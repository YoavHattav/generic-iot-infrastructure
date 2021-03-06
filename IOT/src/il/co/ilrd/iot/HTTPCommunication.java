package il.co.ilrd.iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HTTPCommunication implements Communication{
	InetSocketAddress inetSocketAddress;
	TaskManagment taskMannager;
	
	public HTTPCommunication(InetSocketAddress inetSocketAddress, TaskManagment taskMannager) {
		this.inetSocketAddress = inetSocketAddress;
		this.taskMannager = taskMannager;
		listen();
	}
	
	@Override
	public void listen() {
		try {
			HttpServer server = HttpServer.create(inetSocketAddress, 0);
			ExecutorService tpexec = Executors.newFixedThreadPool(10);
			server.createContext("/", new HTTPServerHandler(taskMannager));
			server.setExecutor(tpexec);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static class HTTPServerHandler implements HttpHandler{
		TaskManagment tm;
		HTTPPeer peer;
		
		public HTTPServerHandler(TaskManagment tm) {
			this.tm = tm;
		}

		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			peer = new HTTPPeer(httpExchange);
		    if("GET".equals(httpExchange.getRequestMethod())) { 
		       handleGetRequest();
		    }
		    else if("POST".equals(httpExchange.getRequestMethod())) {
		       handlePostRequest();        
		    }
		}
		
		private void handleGetRequest() {
			peer.send(new Response("- this operation is not supported", 400));
		}
		
		private void handlePostRequest() {
			InputStream inStream = peer.exchange.getRequestBody();

			String jsonString = new BufferedReader(new InputStreamReader(inStream)).lines()
															.collect(Collectors.joining());

			JsonObject json =  new JsonParser().parse(jsonString).getAsJsonObject();
//			System.out.println(json + "in http com");
//			System.out.println(json.get("Key").getAsString());
//			System.out.println(json.get("Data").getAsJsonObject());
			tm.submitTask(peer, json.get("Key").getAsString(), json.get("Data").getAsJsonObject());
		}
	}
}
