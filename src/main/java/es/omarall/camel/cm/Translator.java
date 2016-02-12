package es.omarall.camel.cm;

import org.apache.camel.component.cm.client.SMSMessage;

public interface Translator<T> {

	public SMSMessage translate(T e);

}
