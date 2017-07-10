# About the project

This is a very simple project demonstrating how to integrate Spring Boot, JWT, OAuth, MySQL and role-based access. 

A basic understanding of Spring DI, Spring Boot, Spring Security, OAuth flows, REST APIs, JPA Repositories, JWT Concepts and MySQL is required.

## Step 1. Restoring the database dump

For this example we will be using MySQL.

In the project root there is a file named **database_schema.sql**. This file contains a very simple schema with three tables, one for users, one for users' roles and another for users' oauth authentication details. 

There are two registered users: User 1 with username **user1** and password **pass1** and User 2 with username **user2** and password **pass2**.

The User 1 has to roles, **ADMIN** and **USER** whereas User 2 only has the role **USER**.

The users have the same permissions in the OAuth scheme, that means, **user1** also has **ADMIN** and **USER** authorities and **user2** also has **USER** authority. **User1** also has **read** and **write** scopes whereas **user2** only has **read** scope. Both users have **password** and **refresh_token** grant types. For testing purposes, access tokens have validity of 60 seconds, and refresh tokens have validity of 120 seconds. Auto-aproval is enabled for both users.

The passwords are encoded using the services provided by `services.password.PasswordService.java` which uses the secret contained in `src.main.resources.application.properties`.


## Step 2. Connecting to the database

Once the database schema is restored, you will need to change its credentials in `application.properties`.

The dependency necessary to connect to the database is:

`
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
`


## Step 3. Configuring the repository

We will be interacting with the database through JPA by using Hibernate as the implementation provider.

The entities representing the database tables * users * and * users_roles * are located in the package `model`.

The persistence context configuration is done by the class `persistence.PersistenceContext.java`.

The relation between `User` `UsersRoles` is mapped in such a way to load the roles a user has automatically when a user is loaded, thus, we are only creating a repository to access `User` instances. The repository can be found at `repository.UserRepository.java`.  
 
The dependency for this feature is:
 
`
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
`


## Step 4. Creating the API

We are going to create two API entries, one to be accessed in an insecure way (`/api/public/`) and another one in a secure way (`/api/secure/`). The API will work somewhat like an `echo`,  sending back the received value along with the security level of the request.

The methods implementing this API can be found in the class `controller.MainController.java`. 


## Step 5. Securing the API

We are now adding the JWT and OAuth features to secure the API by using RSA keys. 

1. Generate RSA asymmetric keys by running `keytool -genkeypair -alias asymkey -keyalg RSA -keypass asympass -keystore asymkey.jks -storepass asympass`. This command will generate a pair of keys with alias *asymkey* and password *asympass* in the keystore *asymkey.jks*.

2. Export the public key by running `keytool -list -rfc --keystore asymkey.jks | openssl x509 -inform pem -pubkey`. The keys will be shown. Copy the public key (from the line containing *BEGIN PUBLIC KEY* to the line containing *END PUBLIC KEY*) into `src/main/resource/public.txt`.

3. Copy **asymkey.jks** to `src/main/resource/`.

4. Create an implementation of `UserDetailsService` which will provide access to users' data. The code is in `services.UserService.java`.

5. Create a token store which uses the RSA created in the steps above. The code for this is in `security.token.TokenStoreConfiguration.java`. This class also contains an example of a token store signed with a symmetric key.

6. Enable authorization by extending `AuthorizationServerConfigurerAdapter` and annotating the sub-class with `@EnableAuthorizationServer`. The code is in `security.OAuth2AuthorizationConfig.java`. This class also defines a bean for `PasswordEncoder`, and also configures `AuthorizationServerEndpointsConfigurer` to get users' credentials from database. Also, it configures OAuth to look for users' grant types, token expiration times, etc, in the database by configuring `AuthorizationServerEndpointsConfigurer`. This class also provides an example of how to set up OAuth access details in memory. This example is commented in the end of the class.

7. Enable resource serving by extending `ResourceServerConfigurerAdapter` and annotating the sub-class with `@EnableResourceServer`. This class defines the id of the resources being served, the token store to be used and also the API access rules, where **user1** can access both, the private and public endpoints whereas **user2** can only access the public one.

## Step 6. Testing

There are three API entries to be tested, the public, the authorization and the secure.

### Step 6.1. Accessing the public API

*1. Request an authorization token:*

`curl -X POST --user 'userId2:passId2' -d 'grant_type=password&username=user2&password=pass2' http://localhost:8080/oauth/token`

*2. Hit the public endpoint by using the authorization token:*

`curl -i -H "Accept: application/json" -H "Authorization: Bearer 260480f7-3e94-4811-a49d-b385dff83f4a" http://localhost:8080/api/public/request/felipe`

The answer must be: '**felipe' came from an INsecure channel.**.

*3. Try to hit the secure endpoint:*

`curl -i -H "Accept: application/json" -H "Authorization: Bearer 260480f7-3e94-4811-a49d-b385dff83f4a" http://localhost:8080/api/secure/request/felipe`

The response must be HTTP 403 since the access is only allowed for **ADMIN** users.

*4. Access the secure endpoint:*

Repeat the steps above with **user1** credentials and the response must be: '**felipe' came from a secure channel.**.


### Step 6.2. Refreshing the access token

When the access token is expired (in this example, after 60 seconds) a new one can be obtained by asking OAuth and passing the refresh token associated with the expired token:

`curl -X POST --user 'userId2:passId2' -d 'grant_type=refresh_token&refresh_token=48c9f1db-9252-47a0-9a28-091caa775e4c' http://localhost:8080/oauth/token`.

After the a new access token is obtained, it can be used to, once again, access the API.