package mil.af.rl.jcat.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiMap<K, V> implements Serializable, Iterable<K>
{
	Map<K, List<V>> multiMap = new HashMap<K, List<V>>();
	
	
	public Object clone()
	{
		MultiMap copy = new MultiMap<K, V>();
		
		java.util.Iterator keys = this.keySet().iterator();
		while(keys.hasNext())
		{
			K key = (K)keys.next();
			java.util.Iterator valuesForKey = this.get(key).iterator();
			while(valuesForKey.hasNext())
				copy.put(key, valuesForKey.next());
		}
		
		return copy;
	}
	
	public int size() {
		return multiMap.size();
	}

	public boolean isEmpty() {
		return multiMap.isEmpty();
	}

	public boolean containsKey(K key) {
		return multiMap.containsKey(key);
	}

	/**
	 * Returns false by default. Working out the semantics of this. -CM
	 * 
	 * @param value
	 * @return false always
	 */
	public boolean containsValue(K value) {
		return false;
	}

	public List<V> get(K key) {
		return multiMap.get(key);
	}
	/**
     * Puts value in collection associated with the given key.
     *  Returns false if the given value is already associates.
     * @param key
     * @param value
     * @return
	 */
	public Object put(K key, V value) {
		if(!multiMap.containsKey(key)){
            ArrayList<V> list = new ArrayList<V>();
            multiMap.put(key, list);
            return list.add(value);			
        }
        else {
            if(!multiMap.get(key).contains(value))
                return multiMap.get(key).add(value);
        }            
        return false;
	}

	public List<V> remove(K key) {
		return multiMap.remove(key);
	}

	public void clear() {
		multiMap.clear();
	}

	public Set keySet() {
		return multiMap.keySet();
	}

	public Collection values() {
		return multiMap.values();
	}

	public Set entrySet() {
		return multiMap.entrySet();
	}

    public Iterator<K> iterator()
    {
        return new MultiMapIterator();
    }
    /**
     * Provides an Iterator to iterate over the Set of MultiMap Keys
     * Allowing the use of a java for each loop.
     * 
     * @author craig
     *
     *
     */
    private class MultiMapIterator<K> implements Iterator<K>
    {
        private Iterator<K> keySet = (Iterator<K>) multiMap.keySet().iterator();
        

        public boolean hasNext()
        {
            return keySet.hasNext();
        }

        public K next()
        {
            return keySet.next();
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        
    }
	
}
