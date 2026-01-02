# Anvil - A Functional Relational Mapping (FRM) API for Databases in Scala

Anvil is a Scala library for working with databases that provides a functional API. Anvil has the following aims:

1. To provide a functional relational mapping (FRM) library or as close as possible to a FRM library.
2. To provide composible functions giving control on what interactions should take place with the database.
3. To provide a performant library for database CRUD.
4. To provide type safety and compile time checking with FRM.
5. To provide security from threats such as SQL injection where possible.
6. To provide an extensible library that can be extended for any databases that support JDBC.

Please the [Wiki page](https://github.com/TheDiscProg/Anvil/wiki) for more details about how to use Anvil.

## Current Status
* ANSI/ISO SQL is supported for major CRUD commands for single tables.
* PostgreSQL 14 and above is supported including PostgreSQL data types.
* MySQL 8 and above is supported including MySQL 8 specific data types.

