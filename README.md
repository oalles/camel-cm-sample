# CAMEL CM SMS Component Sample of Usage

This project is a [Spring Boot Project](http://projects.spring.io/spring-boot/) to show how camel-cm-sms component can be integrated in a camel route.

### Description
The application consumes documents from a mongodb capped collection. For each document it tries to create a SMSMessage instance, which is the payload accepted by camel-cm-sms component. 

In order to send messages you just have to insert documents in the capped collection following the rules set in the [Translator](https://github.com/oalles/camel-cm-sample/blob/master/src/main/java/es/omarall/camel/cm/MongoTranslator.java). 

Have a look at the route definition.

```java
	// 1. Consume a document from a capped MONGODB collection.
	from(mongoUri)

			// 2. Translate the document to a SMSMessage instance
			// following an easy rule.
			.bean(MongoTranslator.class, "translate")

			// 3. Send SMSMessage to CMComponent
			.to(cmUri)
			
			.routeId("FROM-MONGO-TO-CM-WITH-LOVE");
```

### Usage:

You need a valid account. [Register for one](https://www.cmtelecom.com/support). They provide some free credits.

1. Clone this project
	
	```
	git clone https://github.com/oalles/camel-cm-sample
	```
	
2. Open pom.xml
	* the camel-cm-sms component is needed to send sms messages via CM.  
	```
		<dependency>
			<groupId>org.apache.camel</groupId>
			<artifactId>camel-cm-sms</artifactId>
			<version>${camel.version}</version>
		</dependency>
	```
	* to consume messages from a capped collection, the camel-mongodb component is needed
	
	```
		<dependency>
			<groupId>org.apache.camel</groupId>
			<artifactId>camel-mongodb</artifactId>
			<version>${camel.version}</version>
		</dependency>
	```
	
4. Open [application.properties](https://github.com/oalles/camel-cm-sample/blob/master/src/main/resources/application.properties) and set the product-token you have been provided by mail. 
5. Start the mongo shell and connect to your MongoDB instance, in this case running on localhost with default port, and create a capped collection in a given database. 
	
	```
	mongo
	use sms-db
	db.createCollection("messages", {capped: true, size: 1000})
	```
	 
	Set db and collection names in the [application.properties](https://github.com/oalles/camel-cm-sample/blob/master/src/main/resources/application.properties) file. 

6. Run the application.
	
	```	
	cd camel-cm-sample
	mvn spring-boot:run
	```
	
7. Go to the mongo shell and insert messages in the capped collection following a simple rule. Every document that has a 'phoneNumber' field and a 'message' is going to be sent to CM SMS GW. See [Translator](https://github.com/oalles/camel-cm-sample/blob/master/src/main/java/es/omarall/camel/cm/MongoTranslator.java) rules to convert from Document to SMSMessage. The payload that camel-cm component expects. 
	
	```
	db.insert({a: 1,b: 2}) #Not CM Sendable
	db.insert({phoneNumber: "+34600000000",message: "Hello CM GW!"}) #To be sent
	```

### Final comments.
As you can see our app trigger is a document being inserted in a capped collection. 
You could choose your own trigger and forget about all the mongo stuff in the project. 
Just provide your custom [Translator](https://github.com/oalles/camel-cm/blob/master/src/main/java/org/apache/camel/component/cm/client/Translator.java) implementation before Camel CM SMS component.
 






