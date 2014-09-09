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
	
	private static final int QUEUE_WORKER_COUNT = 5;
	
	Client client;
	BlockingQueue<String> messages;
	
	private class QueueWorker implements Runnable {
		ProducerTemplate producer;
		BlockingQueue<String> queue;
		Client client;
		QueueWorker(Client client, BlockingQueue queue, ProducerTemplate producer){
			this.client = client;
			this.queue = queue;
			this.producer = producer;
		}
		
		public void run() {
			while (!client.isDone()) {
				try{
					String message = queue.take();
					producer.sendBody("seda://sendTweetToDatabase", message);
				}
				catch (Exception ex){
					//NOOP
				}
			}
		}
	}


	public void start(Exchange exchange) throws Exception {

		// Initialize a queue to collect messages from the stream
		 messages = new LinkedBlockingQueue<String>(100000);
		
		Hosts host = new HttpHosts(com.twitter.hbc.core.Constants.STREAM_HOST);
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		ArrayList<String> terms = new ArrayList<String>();
		terms.add("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z");
		endpoint.trackTerms(terms);
		
		// Drop in the oauth credentials
		Authentication auth = new OAuth1("1gkVqBVs3DMmYkctTtWVEUn1v", "I2nNOSydVaH7Y0G8nEqM1wv8hvc3RIlM1zjAGYeRCh2z1mc5Ko",
				"2659905841-EwKVLwzMzfkcp63tXGqZVDqtNLcAETwdDXgJPek", "4Ngn7PY6vi5gtZrqlOTUpNkq5d8t79UEP7Pvc1R6onOis");

		// Build a client and read messages until the connection closes.
		ClientBuilder builder = new ClientBuilder()
				.name("TweedarGeoscraper").hosts(host)
				.authentication(auth).endpoint(endpoint)
				.processor(new StringDelimitedProcessor(messages));
		client = builder.build();
		client.connect();

		}
		
		public void offloadMessageToStorage(Exchange exchange) throws Exception {
			CamelContext context = exchange.getContext();
			for(int x = 0; x < QUEUE_WORKER_COUNT; x++){
				ProducerTemplate producer = context.createProducerTemplate();
				Thread thread = new Thread(new QueueWorker(client, messages, producer));
				thread.start();
			}
			while(!client.isDone()){
				Thread.currentThread().sleep(5000);
			}
		}
	}

