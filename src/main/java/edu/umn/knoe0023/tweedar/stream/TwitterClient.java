package edu.umn.knoe0023.tweedar.stream;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.*;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterClient {
	
	Client client;
	BlockingQueue<String> messages;

	public void start(Exchange exchange) throws Exception {

		// Initialize a queue to collect messages from the stream
		 messages = new LinkedBlockingQueue<String>(100000);
		
		// Connect to the filter endpoint, tracking the term "twitterapi"
		Hosts host = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		ArrayList<String> terms = new ArrayList<String>();
		terms.add("a");
		endpoint.trackTerms(terms);
		
		// Drop in the oauth credentials for your app, available on
		// dev.twitter.com
		Authentication auth = new OAuth1("1gkVqBVs3DMmYkctTtWVEUn1v", "I2nNOSydVaH7Y0G8nEqM1wv8hvc3RIlM1zjAGYeRCh2z1mc5Ko",
				"2659905841-EwKVLwzMzfkcp63tXGqZVDqtNLcAETwdDXgJPek", "4Ngn7PY6vi5gtZrqlOTUpNkq5d8t79UEP7Pvc1R6onOis");

		// Build a client and read messages until the connection closes.
		ClientBuilder builder = new ClientBuilder()
				.name("FooBarBaz-StreamingClient").hosts(host)
				.authentication(auth).endpoint(endpoint)
				.processor(new StringDelimitedProcessor(messages));
		client = builder.build();
		client.connect();

		}
		
		public void offloadMessageToStorage(Exchange exchange) throws Exception {
			CamelContext context = exchange.getContext();
			ProducerTemplate producer = context.createProducerTemplate();
			while (!client.isDone()) {
				String message = messages.take();
				producer.sendBody("seda://sendTweetToDatabase", message);
			}
		}
	}

