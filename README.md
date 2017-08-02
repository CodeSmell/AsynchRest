# Asynch REST
Examine Spring MVC & Jersey as REST API framework within Spring Boot
Play with various asynchronous options 

## Using Jersey as JAX-RS provider instead of Spring MVC
Typically a Spring Boot application will use the web starter, which sets up the container for Spring MVC REST development.

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>


Switch that starter out for the following Jersey one (or in our case leave them both in, since we are comparing them)

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-jersey</artifactId>
    </dependency>

## Invoice Data
Imagine that we are storing invoices in an Apache Cassandra database.

Assume a keyspace that has two tables. 
One allows queries by the destination of the invoice items and trailer

	CREATE TABLE IF NOT EXISTS "invoice_doc" (
		invoice_id TIMEUUID,
		destination_name TEXT,
		destination_type TEXT,
		trailer TEXT,
		PRIMARY KEY ((destination_name, destination_type), trailer, invoice_id));

Another stores the JSON document
	
	CREATE TABLE IF NOT EXISTS "invoice_by_destination" (
		invoice_id TIMEUUID,
		json_doc TEXT,
		PRIMARY KEY ((invoice_id)));	


Now we have a scenario where we are searching for a list of invoices and then want to return JSON that contains an array of invoice documents. Note: the sample code uses a small JSON representation of an invoice.

	[
		{"invoice": {
			"id": "1",
			"foo": "bar"}
		},
		{"invoice": {
			"id": "2",
			"foo": "fighters"}
		}
	]

Sample code in `src/main/test` does this with
* Java 8 Streams
* RxJava Observable
