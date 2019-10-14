# Siddhi Mock Data Generator

Java project used to generate mock data for Sales Stream with an Siddhi HTTP Source,

```
@Source(type = 'http', receiver.url = 'http://0.0.0.0:8080/sales', basic.auth.enabled = 'false',
	@map(type = 'json'))
define stream SalesStream(timestamp long, categoryName string, productName string, sellerName string, quantity int);
```

## How to run

```console
java -jar siddhi-mock-data-generator-1.0.0.jar
```
OR

```
java -jar siddhi-mock-data-generator-1.0.0.jar 8006

```
