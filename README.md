# InvenHelper

An inventory management simple application

# Starting the Application

## General Concerns
To execute this application it is necessary to have a database specified on the `application.properties` file (check the database section for more info) available.

## Gradle
To run using Gradle, it is only necessary to execute `gradle bootRun`. The application will be built (if necessary) and then it starts.

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