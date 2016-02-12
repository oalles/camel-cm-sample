package es.omarall.camel.cm;

import java.net.UnknownHostException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.mongodb.MongoClient;

@SpringBootApplication
public class MongoCmApplication implements EnvironmentAware {

	private final Logger LOG = LoggerFactory.getLogger(MongoCmApplication.class);

	// Both componentes are URI based COMPONENTS.
	private String mongoUri;
	private String cmUri;

	public static void main(String[] args) {
		SpringApplication.run(MongoCmApplication.class, args);
	}

	@Bean
	RoutesBuilder myRouter() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {

				// specify the error handling per exception type
				onException(RuntimeException.class).handled(true).logExhausted(true).logStackTrace(false)
						.logExhaustedMessageHistory(true).process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {

						LOG.error("Document: {} is not CM Sendable. ",
								exchange.getIn().getBody(Document.class).toString());
					}
				});

				// 1. Consume a document from a Tailable MONGODB collection.
				from(mongoUri)

						// 2. Translate the document to a SMSMessage instance
						// following an easy rule.
						.bean(MongoTranslator.class, "translate")

						// 3. Send SMSMessage to CMComponent
						.to(cmUri).routeId("FROM-MONGO-TO-CM-WITH-LOVE");
			}
		};
	}

	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		return new MongoClient();
	}

	@Bean
	public Translator translator() {
		return new MongoTranslator();
	}

	/**
	 * Build component uris from the environment
	 */
	public void setEnvironment(Environment env) {

		// 1. Build CM SMS GW Uri
		final String host = env.getRequiredProperty("cm.url");
		final String productTokenString = env.getRequiredProperty("cm.product-token");
		final String sender = env.getRequiredProperty("cm.default-sender");

		final StringBuffer cmUri = new StringBuffer("cm:" + host).append("?productToken=").append(productTokenString);
		if (sender != null && !sender.isEmpty()) {
			cmUri.append("&defaultFrom=").append(sender);
		}

		// Defaults to 8
		final Integer defaultMaxNumberOfParts = Integer.parseInt(env.getProperty("defaultMaxNumberOfParts", "8"));
		cmUri.append("&defaultMaxNumberOfParts=").append(defaultMaxNumberOfParts.toString());

		this.cmUri = cmUri.toString();

		// 2. Build Mongo Uri
		final String dbName = env.getRequiredProperty("mongotc.db");
		final String collectionName = env.getRequiredProperty("mongotc.collection");
		this.mongoUri = String.format("mongotc:mongoClient?database=%s&collection=%s", dbName, collectionName);
	}
}
