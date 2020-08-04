package il.co.ilrd.iot;

import com.google.gson.JsonObject;

public class Task implements Runnable{
	Peer peer;
	String Key;
	String company;
	JsonObject data;
	
	public Task(Peer peer, String key, JsonObject data) {
		this.peer = peer;
		Key = key;
		this.data = data;
	}
	public String getKey() {
		return Key;
	}
	public String getCompany() {
		return company;
	}
	public JsonObject getData() {
		return data;
	}

	@Override
	public void run() {
		SingletonCommandFactory factory = SingletonCommandFactory.getInstance();
		Command command = factory.Create(Key, data);
		Response response = command.execute();
		System.out.println("before send" + Key);
		System.out.println(data);
		System.out.println("factory" + factory);

		peer.send(response);
	}
}