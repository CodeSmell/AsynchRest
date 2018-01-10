# Asynch REST
Examine Spring MVC & Jersey as REST API framework within Spring Boot

Playing with various asynchronous & parallel processing options

## Running the examples
Spring MVC will run using the following URL path
	
	localhost:8888/springmvc/hello
	
	localhost:8888/springmvc/invoice/find/?storeNumber=100&trailerNumber=1

Jersey/JAX-RS will run using the following URL path
	
	localhost:8888/jaxrs/hello
	
	localhost:8888/jaxrs/invoice/find/?storeNumber=100&trailerNumber=1

### Testing stream
In order to see the stream in action, update the `SlowInvoiceDao` so that it returns 5000 invoices. Also update the `Thread.sleep` to 100 ms.

	curl localhost:8888/jaxrs/invoice/find/stream?storeNumber=100

The resulting array of JSON objects will start to be returned before the method has completed.
	
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
    
### Issue w/ Jersey and Spring Boot
[Jersey doesn't always work with Spring Boot fat jars (1468)](https://github.com/spring-projects/spring-boot/issues/1468)   

This issue presented when adding the `packages()` to `JerseyConfig`  

	@Configuration
	@ApplicationPath("/jaxrs")
	public class JerseyConfig extends ResourceConfig {
	
	    public JerseyConfig() {
	        register(HelloJerseyController.class);
	        register(JaxRsInvoiceController.class);
	        register(JaxRsAsynchInvoiceController.class);
	        //packages("codesmell.invoice.rest.jaxrs.mappers");
	    }
	}
	
To get around it the `ExceptionMapper` classes were registered directly.	

## building and running the application

The simulator can be run from Maven command line:

    mvn spring-boot:run
    
It can also be built and run as a "fat jar"

    mvn package spring-boot:repackage
    
This builds the JAR file that contains all of the dependencies, including the container.
    
    java -jar target/rx-invoice-[version].jar

## The scenario being solved 
Imagine that we are storing invoices in an Apache Cassandra database.

Assume a keyspace that has two tables. 
One allows queries to retrieve all of the invoices going to the same place and on the same trailer.

	CREATE TABLE IF NOT EXISTS "invoices_by_destination" (
		invoice_id TEXT,
		destination_name TEXT,
		destination_type TEXT,
		trailer TEXT,
		PRIMARY KEY ((destination_name, destination_type), trailer, invoice_id));

Another stores the JSON document with all of the details for a single invoice.
	
	CREATE TABLE IF NOT EXISTS "invoice_detail" (
		invoice_id TEXT,
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

