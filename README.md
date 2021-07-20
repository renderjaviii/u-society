# USociety - REST Manager
REST API Orchestrator created for the USociety project.

This RESTful micro service has the function of the Resource server, so, if it’s the head of all the ecosystem (the orchestrator).\
There are a certain group of public endpoints (no requires authentication) and other privates; then if you receive a 401 (Unauthorized HTTP code) as status response, you first need to get the authorization token and add it to the `Authorization` request header.

#### Change list
- _2021-07-20: "Improvements"._ 
- _2021-07-18: General refactor._
- _2020-08-15: Project initialization._

#### How to run locally

1. Recreate database locally - MySQL: `docker-compose -f src/main/resources/docker-db.yml up --build`
2. Recreate in-memory store (cache) locally - Redis: `docker-compose -f src/main/resources/docker-cache.yml up --build` 
3. Compile Spring project and generate jar file: `mvn clean install`
4. Build docker image: `docker build -t u-society/manager .`.
5. Run docker container: `docker-compose up --build -d`
6. Show container logs: `docker-compose logs -f`
7. Stop container: `docker-compose down -v`
8. Run and check tests coverage: `jacoco:report`
9. Clean Redis cache: `docker exec company-redis redis-cli flushall`

#### Notes
- Base API path: `https://localhost:8443/manager`
- Swagger documentation: `https://localhost:8443/manager/swagger-ui.html`.

#### Technologies used
- **Spring boot** (Open source microservice-based Java web framework)
- **Docker** (Used for package the application into containers)
- **Docker compose** (Tool for defining and running multi-container applications)
- **MVN** (Maven manager dependency, used to build and manage the entire project)
- **MySQL** (Relational database management system)
- **OAUTH2** (Open standard for access delegation)
- **JWT** (Java Web Tokens, open standard for creating signed and encrypted data)
- **Spring Security** (It provides the authentication and authorization mechanisms)
- **Webflux** (Reactive REST client used to interconnect all the micro services ecosystem)
- **Swagger UI** (Used to document and use the web service)
- **Model mapper** (Used to convert between Java objects and matching JSON structures)
- **Spring mail** (It provides the mail support)
- **Gmail SMTP** (Email provider used to send emails)
- **Apache tomcat** (Web server container)
- **Jackson databind** (High-performance JSON processor)
- **Apache commons lang3** (Reusable static utility methods)
- **JUnit 4 + Mockito** (Unit testing stuff)
- **JaCoCo** (Code coverage report generator)
- **Redis** (In-memory key-value data structure store)
- **Lettuce client** (Fully non-blocking Redis client)
- **Slugify** (Uniques identifying part address generator)

#### Additional considerations
1. If you want to change the general server configurations (like port), you can change the environment variables from this file `src/main/resources/development.env`.
2. When you run the docker db container, the database is going to be populated using the seeders files: `src/main/resources/data.sql` and `src/main/resources/import.sql` (If you want to avoid it remove the entry points localized in the .yaml file).
3. This API uses a self-signed SSL certificate, you could need to ignore the security warning in your API client (e.g. Postman) in order to be able to consume it.

###### Version: 2.0
###### Date: 2021-07-20
