package il.co.ilrd.iot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class Factory<K, T, D> {
	
	Map<K, Function<D, ? extends T>> map_types = new HashMap<K, Function<D, ? extends T>>();
	
	public void Add(K key, Function<D, ? extends T> ctorFunc) {
		System.out.println("added to factory map " + ctorFunc);
		map_types.put(key, ctorFunc);
	}
	
	public T Create(K key, D data) {
		Function<D, ? extends T> ctorFunction = map_types.get(key);
		System.out.println("taken out of factory map " + ctorFunction);
		T val = ctorFunction.apply(data);
		
		return val;
	}
}
