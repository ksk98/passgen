# Password Generation API

## Database
This application requires a created PostgreSQL database to be available under `jdbc:postgresql://localhost:5432/passgen`. The database is currently set to wipe every 
time the application is closed. The application persists password entities with a JPA repository.

## Password rules and complexity
General password rules are defined in `PasswordRules.java` and the password complexity categories are defined in `enums/Complexity.java` - they should be able to 
be customized and extended.

## Security
Passwords kept inside the database are hashed with Argon2 encoder with default spring security values. To find existing password hash a precomputed search hash is used. 

### Search hash
Currently the search hash is computed from the first 33% password characters digested with MD5, although I'm not so sure that it should be kept like that for 
security reasons... I don't have any better idea for this at the moment and my time for the excercise is running low. The idea of a search hash is to narrow down the 
number of password hashes that need to be checked, instead of just checking everything in the database every time the user requests to check if password exists in the DB.
