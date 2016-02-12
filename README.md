# camel-cm-sample

This is a [Spring Boot Project](http://projects.spring.io/spring-boot/) to show an integration example for [camel-cm component](https://github.com/oalles/camel-cm).

The application consumes documents from a capped collection and tries to create a SMSMessage instance in order to send it to CM SMS GW.

Having a look at the project camel route w

```java
	// 1. Consume a document from a Tailable MONGODB collection.
	from(mongoUri)

			// 2. Translate the document to a SMSMessage instance
			// following an easy rule.
			.bean(Translator.class, "translate")

			// 3. Send SMSMessage to CMComponent
			.to(cmUri).routeId("FROM-MONGO-TO-CM-WITH-LOVE");
```

### Usage:

You need a valid account. [Register for one](https://www.cmtelecom.com/support). They provide some free credits.

* **clone and install** [camel-cm](https://github.com/oalles/camel-cm) component
```bash
https://github.com/oalles/camel-cm
cd camel-cm
mvn install
```
* In order to consume messages from a mongodb capped collection, **clone and install** [camel-mongotc](https://github.com/oalles/camel-mongotc) component
```bash
https://github.com/oalles/camel-mongotc
cd camel-mongotc
mvn install
```
* Clone [camel-cm-sample](https://github.com/oalles/camel-cm-sample) component
```bash
https://github.com/oalles/camel-cm-sample
```

* Open [application.properties](https://github.com/oalles/camel-cm-sample/blob/master/src/main/resources/application.properties) and the product-token you have been provided by mail. 

* Start the mongo shell and connect to your MongoDB instance, in this case running on localhost with default port, and create a capped collection in a given database. 
```
mongo
use sms-db
db.createCollection("messages", {capped: true, size: 1000})
``` 
Set db and collection names in the [application.properties](https://github.com/oalles/camel-cm-sample/blob/master/src/main/resources/application.properties) file. 

* Run the application
```bash	
mvn spring-boot:run
```

* Go to the mongo shell and insert messages in the capped collection following a simple rule. Every document that has a 'phoneNumber' field and a 'message' is going to be sent to CM SMS GW. See [Translator](https://github.com/oalles/camel-cm-sample/blob/master/src/main/java/es/omarall/camel/cm/Translator.java) rules to convert from Document to SMSMessage. The payload that camel-cm component expects. 

```bash
db.insert({a: 1,b: 2}) #Not CM Sendable
db.insert({phoneNumber: "+34600000000",message: "Hello CM GW!"}) #To be sent
```


### Final comments.
As you can see our app trigger is a document being inserted in a capped collection. 
You could choose your own trigger and forget about all the mongo stuff in the project. 
Just provide your custom Translator implementation before Camel CM component.
 






