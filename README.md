[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=u-society%3Amanager&metric=alert_status)](https://sonarcloud.io/dashboard?id=u-society%3Amanager)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=u-society%3Amanager&metric=security_rating)](https://sonarcloud.io/dashboard?id=u-society%3Amanager)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=u-society%3Amanager&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=u-society%3Amanager)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=u-society%3Amanager&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=u-society%3Amanager)

# Welcome to U Society - REST Manager!

RESTful API Orchestrator created for the **U Society** project.

## Description
This microservice has the function of the resource server, so, if it’s the head of the whole ecosystem.

## Getting started

### Dependencies
- _Spring boot_ (Open source microservice-based Java web framework)  
- _Docker_ (Used for package the application in containers)  
- _Docker compose_ (Tool for defining and running multi-container applications)  
- _MVN_ (Maven manager dependency, used to build and manage the entire project)  
- _MySQL_ (Relational database management system)  
- _OAUTH2_ (Open standard for access delegation)  
- _JWT_ (Java Web Tokens, open standard for creating signed and encrypted data)  
- _Spring Security_ (It provides the authentication and authorization mechanisms)  
- _Webflux_ (Reactive REST client used to interconnect all the microservices' ecosystem)  
- _SpringDoc OpenAPI_ (User for automating the generation of API documentation)  
- _Spring mail_ (It provides the mail support)  
- _Gmail SMTP_ (Email provider used to send emails)  
- _Apache tomcat_ (Web server container)  
- _Jackson databind_ (High-performance JSON processor)  
- _Apache commons lang_ (Reusable static utility methods)  
- _JUnit + Mockito_ (Unit testing stuff)  
- _JaCoCo_ (Code coverage report generator)  
- _Redis_ (In-memory key-value data structure store)  
- _Lettuce client_ (Fully non-blocking Redis client)  
- _Slugify_ (Uniques identifying part address generator)

### Prerequisites
- [Java 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html).
- [Docker](https://docs.docker.com/get-docker/).
- [Docker-compose](https://docs.docker.com/compose/install/).


### Installation

1. Clone repository:
	```sh
	git clone git@github.com:renderjaviii/u-society-api.git
	```
2. Recreate database:
	```sh
	docker-compose -f src/main/resources/docker-db.yml up --build
	```
3. Recreate in-memory store (cache):   
	```sh  
	docker-compose -f src/main/resources/docker-cache.yml up --build
	```  
4. Compile Spring project and generate deployable file:  
	```sh  
	mvn clean install
	```  
5. Build docker image:  
	```sh  
	docker build -t u-society/manager .
	```  
6. Run docker container:  
	```sh  
	docker-compose up --build -d
	```  
7. Additional utilities:

- Show container logs:  
	```sh  
	docker-compose logs -f
	```  
- Stop container:  
	```sh  
	docker-compose down -v
	```  
- Run tests and check code coverage:  
	```sh  
	mvn clean test
	```  
- Clean Redis cache:  
	```sh  
	docker exec company-redis redis-cli flushall
	```
	
> **ProTips**: 
> - _If you need to change some server configuration locally (like port), update the environment variables located in file  `src/main/resources/development.env`_.
> - _When you run the database's docker container, it will be populated using the seeders files:  `src/main/resources/data.sql`  and  `src/main/resources/import.sql`  (to avoid it, remove the entry points)_.

### Usage
Refer to the Swagger documentation: `https://localhost:8443/manager/swagger-ui/index.html`.
 
If you want to interact with the API, please download [Postman](https://www.postman.com/) (or whatever API client that you prefer), this tool will help us to consume the endpoints, e.g., when the user's [bearer token](https://datatracker.ietf.org/doc/html/rfc6750) is required.

> Please, have in mind that a _**self-signed SSL certificate**_ was configured to fit out _HTTPS security communication_, so, you may need to add it into the CA store (or ignore the security warning).

## Application bootstrapping

There are different Spring profiles (application versions) in order to have the suitable application's behaviors.

|Stage           |Profile                        |Features                                             |
|----------------|-------------------------------|-----------------------------------------------------|
|Development     |`dev`                          |Development tasks: local machine's resources (complete logging)|
|Testing         |`qa`                           |E2E check: cloud resources (limited logging)|
|Live            |`prod`                         |Production deployment: cloud instances (optimal perfomarnce)|
