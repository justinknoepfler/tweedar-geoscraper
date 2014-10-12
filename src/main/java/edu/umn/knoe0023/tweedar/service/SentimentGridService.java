package edu.umn.knoe0023.tweedar.service;

import javax.ws.rs.core.Request;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

public class SentimentGridService {

	ProducerTemplate camelProduder;

	public void test(Exchange exchange) throws Exception {
		Request request = (Request) exchange.getIn().getBody();
		assert(request != null);
		exchange.getOut().setBody("<p>hello, hammy</p>");
	}
}
