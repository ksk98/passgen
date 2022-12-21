# Password Generation API

Here's a brief summary of the work I've done.

## Endpoints
Endpoints are defined in `controllers/PasswordController.java`. All schemas used by the endpoints are defined in `models/schemas/`.
- **POST** /password/generate - accepts a request with schema `PasswordGenerationRequestDTO.java`. Returns `201` and schema `PasswordGenerationResponseDTO.java` or `400` with error message. The endpoint probably shouldn't be named with a verb but I don't have any better name idea. Maybe just a POST method on endpoint `/password`?
- **POST** /password/complexity - accepts a request with password string in plain text. Returns `200` and schema `PasswordDTO.java` or `400` with error message. If the time value in the response is not null, then the password is present in the database.
- **DELETE** /password/ - accepts a request with password string in plain text. Returns `200` and schema `PasswordDTO.java` or `400` with error message. If the time value in the response is not null, then the password was present in the database and is now deleted.

## Database
This application requires a created PostgreSQL database to be available under `jdbc:postgresql://localhost:5432/passgen` with credentials `postgres`|`s$cret` (as stated in `application.properties`). The database is currently set to wipe every time the application is closed. The application persists password entities with a JPA repository. Password entity is located in `models/entities`.

## Password rules and complexity
General password rules are defined in `PasswordRules.java` and the password complexity categories are defined in `enums/Complexity.java` - they should be able to 
be easily customized and extended but I didn't have enough time to try.

## Security
Passwords kept inside the database are hashed with Argon2 encoder with default spring security values. To find existing password hash a precomputed search hash is used. 

### Search hash
Currently the search hash is computed from the first 33% password characters digested with MD5, although I'm not so sure if it should be kept like that for 
security reasons... I don't have any better idea for this at the moment and my time for the excercise is running low. The idea of a search hash is to narrow down the 
number of password hashes that need to be checked, instead of just checking everything in the database every time the user requests to check if password exists in the DB.

## Tests
I didn't have enough time to cover everything with tests, but I think I did cover the major stuff (controller, service and persistence).
