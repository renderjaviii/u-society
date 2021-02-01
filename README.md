# USociety - REST Manager
REST API Orchestrator created to the USociety project.

This RESTful microservice has the function of the Resource server, so, if it’s the head of all the ecosystem. There are a certain group of public endpoints (no requires authentication) and other privates, then if you receive a 401 (Unauthorized HTTP code) from as response of the API, you first need to get the authorization token and add it to the `Authorization` header.

#### How to run locally

1. Recreate a local database: `docker-compose -f src/main/resources/docker-db.yml up --build`
1. Compile Spring project and generate jar file: `mvn clean install`
3. Build docker image: `docker build -t u-society/manager .`.
4. Run docker container: `docker-compose up --build -d`
5. Show container logs: `docker-compose logs -f`
6. Stop container: `docker-compose down -v`

#### Notes
- Base API path: `http://localhost:8080/manager`
- Swagger documentation: `http://localhost:8080/manager/swagger-ui.html`.

#### Technologies used
- Spring boot.
- Docker.
- Docker compose.
- Maven manager dependency.
- MySQL.
- OAUTH2.
- Spring Security + JWT.
- Webflux.
- Swagger UI.
- Model mapper.
- Spring mail.
- Gmail SMTP.
- Apache tomcat.
- Jackson databind.
- Apache commons lang3.
- JUnit 4 + Mockito.

#### Additional considerations
1. If you want to change the general server configurations (as port), you can change the environment variables from this file `src/main/resources/development.env`.
2. When you run the docker db container, the database is going to be populated using the seeders files `src/main/resources/data.sql` and `src/main/resources/import.sql` (If you want to avoid it remove the entry points localized in the .yaml file). 
