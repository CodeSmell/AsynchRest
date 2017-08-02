# Asynch REST

Imagine that we are storing invoices in an Apache Cassandra database.

Assume a keyspace that has two tables. 
One allows queries by the receiver of the invoice items and trailer
	
	CREATE TABLE IF NOT EXISTS "invoice_by_destination" (
		invoice_id TIMEUUID,
		json_doc TEXT,
		
		PRIMARY KEY ((invoice_id)));


Another stores the JSON document
	
	CREATE TABLE IF NOT EXISTS "invoice_doc" (
		invoice_id TIMEUUID,
		destination_name TEXT,
		destination_type TEXT,
		trailer TEXT,
		PRIMARY KEY ((destination_name, destination_type), trailer, invoice_id));


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

Sample code does this with
* Java 8 Streams
* RxJava Observable
