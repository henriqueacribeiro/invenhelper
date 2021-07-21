# InvenHelper

An inventory management simple application

### What does the application do?

Pretty basic stuff. It is possible to register products, increase and decrease their quantity and change their description or nome, if necessary.

### How it was made?
This is a small project that uses Java 11 together with Spring framework to create an API capable of manipulating data on a relation database. 

It is possible to run the application on containers using Docker, isolating it from the system.

# Starting the Application

## General Concerns
To execute this application it is necessary to have a database specified on the `application.properties` file (check the database section for more info).

Using the Docker Container option, the database is generated, and the application automatically connects to it.

## Gradle
To run using Gradle, it is only necessary to execute `gradle bootRun`. The application will be built (if necessary) and then it starts.

## Docker
The project contains a Dockerfile, to create a personalized image with the application, and a Docker Compose file.

Running the compose file creates an environment with a MySQL image with the database already created and generates a container (building the image, if necessary) with the application.

To run the compose file, run the command `docker-compose up`. To build only the image with the application, run `docker build -t invenhelper .` on the project directory.

# Using the application
While running the application, the following paths are available:

## Product

| URL      | Description | JSON Body
| ----------- | ----------- | ------------ |
| /product/getByID?identifier=`x` |  Obtain product by identifier | - 
| /product/getAllIdentifiers   | Obtain all identifiers | -
| /product/create   | Create a product | ```{'requiring_user': AUTHORIZED_USER, 'name': 'x', 'description': 'y', identifier: 'z'} ```
| /product/decreaseQuantity?identifier=`X`&quantity=`Y`&requiring_user=`AUTHORIZED_USER`   | Decrease the product quantity | -
| /product/increaseQuantity?identifier=`X`&quantity=`Y`&requiring_user=`AUTHORIZED_USER`   | Increase the product quantity | -
| /product/updateProduct   | Updates a product | ```{'requiring_user': AUTHORIZED_USER, 'name': 'x', 'description': 'y', identifier: 'z'} ``` (name and description optional)

## API Documentation
While running, navigate to `/doc.html` page will show the Swagger documentation.

# Create Database
The application was designed to work with MySQL databases. To run with a MySQL Docker database instance use:

`docker run --name invenhelperdb -e MYSQL_ROOT_PASSWORD=password -d mysql:latest*`

The file `databaseinit.sql` inside the `./configfile` directory contains a script to create the database and the tables necessary for the application.

The `application.properties` file must contain the following properties to connect to the database:

```
spring.datasource.url=jdbc:mysql://localhost:3306/invenhelper?serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password
```

## Create Data
To create mock data, the `application.property` file must contain the `dev` profile activated, as it follows:

```
spring.profiles.active=dev
```

The data is specified on the `InvenHelperApplication` class, `bootstrap` method, that executes on the application startup.

# Testing
The application behavior can be tested using `gradle test`. This command will execute basic logic verification (unit testing) and integration tests using an in-memory H2 database on MySQL mode.

The `databaseinit.sql` in the `resources` directory inside the `test` main directory contains a script that creates the database.