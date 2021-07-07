# InvenHelper

An inventory management simple application

# Create Database
docker run --name invenhelperdb -e MYSQL_ROOT_PASSWORD=password -d mysql:latest

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