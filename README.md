# InvenHelper

An inventory management simple application

# Create Database
To run with Docker database use:

`docker run --name invenhelperdb -e MYSQL_ROOT_PASSWORD=password -d mysql:latest*`

The file `databaseinit.sql` inside the `./configfile` directory contains a script to create the database and the tables necessary for the application.

The `application.properties` file must contain the following properties, to connect to the database:

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
The application behavior can be tested using `gradle test`. This command will execute basic logic verification (unit testing) and integration tests using an in-memory H2 database.

The `databaseinit.sql` in the `resources` directory inside the `test` main directory contains a script that creates the database.