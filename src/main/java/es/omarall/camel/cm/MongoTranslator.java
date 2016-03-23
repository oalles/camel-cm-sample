package es.omarall.camel.cm;

import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.cm.client.Translator;

import com.mongodb.DBObject;

public class MongoTranslator implements Translator<DBObject> {

    public SMSMessage translate(DBObject document) {
        // Is it CM Sendable?
        if (!document.containsField("phoneNumber")
            || !document.containsField("message")) {
            throw new RuntimeException("This document is not CM sendable. Meaning, i cannot create a SMSMessage instance from it cause it must contain both 'phoneNumber' field and a 'message' field");
        }
        // OK. it is CM Sendable.
        return new SMSMessage((String) document.get("message"),
                              (String) document.get("phoneNumber"));
    }

}
