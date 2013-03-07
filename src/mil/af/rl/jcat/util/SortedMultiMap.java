package mil.af.rl.jcat.util;

import java.util.ArrayList;
import java.util.Vector;

public class SortedMultiMap<K extends Comparable<K>, V> {
	ArrayList<java.util.Map.Entry<K, V>> map = null;
	Vector<K> key = null;
	Vector<V> value = null;
	public SortedMultiMap(){
		map = new ArrayList<java.util.Map.Entry<K, V>>();
		key = new Vector<K>();
		value = new Vector<V>();
	}
	public void insertDescending(K k, V v){
		int j= 0;
		for(; j < key.size(); j++){
			if(key.elementAt(j).compareTo(k)  < 0){
				break;
			}
		}
		key.insertElementAt(k, j);
		value.insertElementAt(v, j);
	}
	public V getValueAt(int j){
		return value.elementAt(j);
	}
	public K getKeyAt(int j){
		return key.elementAt(j);
	}
	public V getValue(K searchKey){
		V theValue = null;
		int lim = key.size();
		for(int j = 0;  j < lim; j++){
			if(searchKey.compareTo(key.elementAt(j)) == 0.0f){ // somewhat risky for floats
				theValue = value.elementAt(j);
				break;
			}
		}
		return theValue;
	}
	public int size(){
		return key.size();
	}
}
