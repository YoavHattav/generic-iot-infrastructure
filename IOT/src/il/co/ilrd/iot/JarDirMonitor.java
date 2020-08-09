package il.co.ilrd.iot;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import il.co.ilrd.iot.Dispatcher.Callback;

public class JarDirMonitor {
	Dispatcher<String> dispatcher = new Dispatcher<String>();
	private String jarDirPath = null;
	private volatile boolean runFlag = true;
	
	public JarDirMonitor(String jarDirPath) {
		this.jarDirPath = jarDirPath;
	}
	
	public void register(Callback<String> cb) {
		dispatcher.register(cb);
	}
	
	public void startMonitoring() {
		Thread monitoringThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				loadBefore(jarDirPath);
				try {
					WatchService watcher = FileSystems.getDefault().newWatchService();
					Path dir = Paths.get(new File(jarDirPath).getParent());
					dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
					
		            while (runFlag) {
		            	WatchKey key = null;
						try {
							key = watcher.take();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		                for (WatchEvent<?> event : key.pollEvents()) {
		                	@SuppressWarnings("unchecked")
							Path jarPath = ((WatchEvent<Path>)event).context();
		                	dispatcher.updateAll(jarPath.toString());
		                }
		                
		                if (!key.reset()) { break; }
		            }
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	public void stopMonitoring() {
		runFlag = false;
	}

	private void loadBefore(String jarDirPath) {
		File file = new File(jarDirPath);
		for (String jarPath : file.list()) {
			dispatcher.updateAll(jarPath);
		}
	}
}
