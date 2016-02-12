package es.omarall.camel.cm;

import org.apache.camel.component.cm.client.SMSMessage;
import org.bson.Document;

public class MongoTranslator implements Translator<Document>{

	public SMSMessage translate(Document document) {

		// Is it CM Sendable?
		if (!document.containsKey("phoneNumber") || !document.containsKey("message"))
			throw new RuntimeException(
					"This document is not CM sendable. Meaning, i cannot create a SMSMessage instance from it cause it must contain both 'phoneNumber' field and a 'message' field");

		// OK. it is CM Sendable.
		return new SMSMessage(document.getString("message"), document.getString("phoneNumber"));
	}

}
