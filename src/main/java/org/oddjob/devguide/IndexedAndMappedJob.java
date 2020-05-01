package org.oddjob.devguide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexedAndMappedJob implements Runnable {

	private List<Object> indexed = new ArrayList<Object>();
	private Map<String, Object> mapped = new HashMap<String, Object>();
		
	public void setIndexed(int index, Object something) {
		if (something == null) {
			indexed.remove(index);
		}
		else {
			indexed.add(index, something);
		}
	}

	public void setMapped(String key, Object something) {
		if (something == null) {
			mapped.remove(key);
		}
		else {
			mapped.put(key, something);
		}
	}
	
	public void run() {
	}
}
