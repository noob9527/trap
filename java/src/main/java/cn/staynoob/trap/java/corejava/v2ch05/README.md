# 5 Database Programming

## 1 The Design of JDBC
### JDBC Driver Types
skipped
### Typical Uses of JDBC
skipped
## 2 The Structured Query Language
skipped
## 3 JDBC Configuration
### Database URLs
When connecting to a database, you must use various database-specific parameters such as host names, port numbers, and database names. JDBC uses a syntax similar to that of ordinary URLs to describe data sources. The general syntax is
```
jdbc:subprotocal:other stuff
```
where a subprotocol selects the specific driver for connecting to the database. The format for the "other stuff" parameter depends on the subprotocal used. You will need to loop up your vendor's documentation for the specific format.
### Driver JAR Files
Include the driver JAR file on the class path when running a program that accesses the database.(You don't need the JAR file for compiling.)\
When you launch programs from the command line, simply use the command
```bash
java -classpath driverPath:. ProgramName
```
### Starting the Database
skipped
### Registering the Driver Class
Many JDBC JAR files(such as the Derby driver included with Java SE 8) automatically register the driver class. In that case, you can skip the manual registration step that we describe in this section. A JAR file can automatically register the driver class if it contains a file META-INF/services/java.sql.Driver. You can simply unzip your driver's JAR file to check.
> Note: This registration mechanism uses a little-known part of the JAR specification; Automatic registration is a requirement for a JDBC4-compliant driver.

If your driver's JAR file doesn't support automatic registration, you need to find out the name of the JDBC driver classes used by your vendor. There are two ways to register the driver with the DriverManager. One way is to load the driver class in your Java program. For example,
```java
Class.forName("org.postgresql.Driver"); // force loading of driver class
```
This statement causes the driver class to be loaded, thereby executing a static initializer that registers the driver. Alternatively, you can set the jdbc.drivers property. You can specify the property with a command-line argument, such as
```java
java -Djdbc.drivers=org.postgresql.Driver ProgramName
```
Or, your application can set the system property with a call such as
```java
System.setProperty("jdbc.drivers", "org.postgresql.Driver");
```
You can also supply multiple drivers; separate them with colons.

### Connecting to the Database
In your Java program, open a database connection like this:
```
String url = "jdbc:postgresql:COREJAVA";
String username = "dbuser";
String password = "secret";
Connection conn = DriverManager.getConnection(url, username, password);
```
The driver manager iterates through the registered drivers to find a driver that can use the subprotocol specified in the database URL.

## 4 Working with JDBC Statements
### Executing SQL Statements
To execute a SQL statement, you first create a Statement Object. To create statement objects, use the Connection object that you obtained from the call to DriverManager.getConnection. Next, place the statement that you want to execute into a string. Then call the executeXXX method of the Statement interface.
### Managing Connections, Statements, and Result Sets
Every Connection object can create one or more Statement objects. You can use the same Statement object for multiple unrelated commands and queries. However, a statement has at most one open result set. If you issue multiple queries whose results you analyze concurrently, you need multiple Statement objects.
When you are done using a ResultSet, Statement, or Connection, you should call the close method immediately. These objects use large data structures that draw on the finite resources of the database server. The close method of a Statement object automatically closes the associated result set if the statement has an open result set. Similarly, the close method of the Connection class closes all statements of the connection. If your connections are short-lived, you don't have to worry about closing statements and result sets. To make absolutely sure that a connection object cannot possibly remain open, use a try-with-resources statement.
> Tip: Use the try-with-resources block just to close the connection, and use a separate try/catch block to handle exceptions. Separating the try blocks makes your code easier to read and maintain.
### Analyzing SQL Exceptions
skipped
### Populating a Database
skipped

## 5 Query Execution
### Prepared Statements
Instead of building a separate query statement every time the user launches such a query, we can prepare a query with a host variable and use it many times, each time filling in a different string for the variable. That technique benefits performance. Whenever the database executes a query, it first computes a strategy of how to do it efficiently. By preparing the query and reusing it, you ensure that the planning step is done only once. Each host variable in a prepared query is indicated with a '?'. If there is more that one variable, you must keep track of the positions of the '?' when setting the values. Before executing the prepared statement, you must bind the host variables to actual values with a set method. As with the get methods of the ResultSet interface, there are different set methods for the various types. Once all variables have been bound to values, you can execute the prepared statement.

> Note: A PreparedStatement object becomes invalid after the associated Connection object is closed. However, many databases automatically cache prepared statements. If the same query is prepared twice, the database simply reuses the query strategy. Therefore, don't worry about the overhead of calling prepareStatement.

> Tip: Some Java programmers avoid complex SQL statements. A surprisingly common, but very inefficient, workaround is to write lots of Java code that iterates through multiple result sets. But the database is a lot better at executing query code than a Java program can be--that's the core competency of a database. A rule of thumb: If you can do it in SQL, don't do it in Java.

### Reading and Writing LOBs
skipped
### SQL Escapes
skipped
### Multiple Results
skipped
### Retrieving Autogenerated Keys
skipped

## 6 Scrollable and Updatable Result Sets
### Scrollable Result Sets
skipped
### Updatable Result Sets
The updateRow, insertRow, and deleteRow methods of the ResultSet interface give you the same power as executing UPDATE, INSERT, and DELETE SQL statements. However, Java programmers might find it more natural to manipulate the database contents through result sets than by constructing SQL statements.

> CAUTION: If you are not careful, you can write staggeringly inefficient code with updatable result sets. It is much more efficient to execute an UPDATE statement than to make a query and iterate through the result, changing data along the way. Updatable result sets make sense for interactive programs in which a user can make arbitrary changes, but for most programmatic changes, a SQL update is more appropriate.

## 7 Row Sets
Scrollable result sets are powerful, but they have a major drawback. You need to keep the database connection open during the entire user interaction. However, a user can walk away  from the computer for a long time, leaving the connection occupied. That is not good--database connections are scarce resources. In the situation, use a row set. The RowSet interface extends the ResultSet interface, but row sets don't have to be tied to a database connection.

### Constructing Row Sets
skipped
### Cached Row Sets
skipped

## 8 Metadata
In SQL, data that describe the database or one of its parts are called metadata. You can get three kinds of metadata: about a database, about a result set, and about parameters of prepared statements.
## 9 Transactions
### Programming Transactions with JDBC
By default, a database connection is in autocommit mode, and each SQL statement is committed to the database as soon as it is executed. Once a statement is committed, you cannot roll it back. Turn off this default so you can use transactions.
### Save Points
With some databases and drivers, you can gain finer-grained control over the rollback process by using save points. Creating a save point marks a point to which you can later return without having to abandon the entire transaction.
### Batch Updates
Suppose a program needs to execute many INSERT statements to populate a database table. You can improve the performance of the program by using a batch update. In a batch update, a sequence of statements is collected and submitted as a batch.\
For proper error handling in batch mode, treat the batch execution as a single transaction. If a batch fails in the middle, you want to roll back to the state before the beginning of the batch.

## 10 Advanced SQL Types
skipped
## 11 Connection Management in Web and Enterprise Applications
Database connections are a finite resource. If a user walks away from an application for some time, the connection should not be left open. Conversely, obtaining a connection for each query and closing it afterward is very costly. The solution is to pool connections. This means that database connections are not physically closed but are kept in a queue and reused. Connection pooling is an important service, and the JDBC specification provides hooks for implementors to supply it. However, the JDK itself does not provide any implementations, and database vendors don't usually include one with their JDBC drivers either. Instead, vendors of web containers and application servers supply connection pool implementations.
