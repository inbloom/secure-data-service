//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A fast and very light-weight Map implementation for very small collections of items (less than 10)<p>
 * This implementation is much more lightweight than a HashMap in that it uses less objects. It is also
 * very fast for small lists because it uses a linked list lookup rather than a bucket-based approach.
 * This list is not synchronized.
 * @author Andrew Elmhorst
 *
 * @param <K> The Type of the key for this Map
 * @param <V> The Type of the value for this Map
 */
public class LinkedListMap<K,V> implements Map<K,V> {

	private Entry<K,V> first;
	private Entry<K,V> last;
	private final boolean fCaseInsensitive;

	public LinkedListMap(){
		fCaseInsensitive = false;
	}

	/**
	 * Allows a case-insensitive instance to be created. If this Map is created
	 * with a String type as the key, all values will be created in a case-insensitive
	 * manner.
	 * @param isCaseInsensitive
	 */
	public LinkedListMap(Boolean isCaseInsensitive ){
		fCaseInsensitive = isCaseInsensitive;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		int a = 0;
		Entry next = first;
		while( next != null ){
			a++;
			next = next.nextEntry;
		}
		return a;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return first == null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object arg0) {
		return getEntry( arg0 ) != null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object arg0) {
		Entry<K,V> next = first;
		while( next != null ){
			if( next.fValue.equals( arg0 )){
				return true;
			}
			next = next.nextEntry;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object arg0) {
		Entry<K,V> value = getEntry( arg0 );
		if( value == null ){
			return null;
		}
		return value.fValue;
	}

	private Entry<K,V> getEntry( Object key ){
		Entry<K,V> next = first;
		while( next != null ){
			if( next.fKey.equals( key )){
				return next;
			}
			next = next.nextEntry;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(K, V)
	 */
	@SuppressWarnings("unchecked")
	public V put(K key, V putValue ) {
		K usedKey = key;
		if( fCaseInsensitive && usedKey != null && usedKey instanceof String ){
			usedKey = (K)usedKey.toString().toLowerCase();
		}
		Entry<K,V> savedValue = getEntry( usedKey );
		if( savedValue == null ){

			Entry<K,V> candidate = new Entry<K,V>( last, null );
			candidate.fKey = usedKey;
			candidate.fValue = putValue ;
			if( first == null ){
				first = candidate;
			} else {
				last.nextEntry = candidate;
			}
			last = candidate;
		} else {
			savedValue.fValue = putValue ;
		}
		return putValue ;

	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public V remove(Object arg0) {
		Entry<K,V> value = getEntry( arg0 );
		if( value == null ){
			return null;
		}

		// Remove the item from the linked list
		if( value.previousEntry != null ){
			value.previousEntry.nextEntry = value.nextEntry;
		}
		if( value.nextEntry != null ){
			value.nextEntry.previousEntry = value.previousEntry;
		}

		if( value == first ){
			first = value.nextEntry;
		}
		if( value == last ){
			last = value.previousEntry;
		}

		return value.fValue;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends K, ? extends V> arg0) {
		for( Map.Entry<? extends K,? extends V> entry : arg0.entrySet() ){
			this.put( entry.getKey(), entry.getValue() );
		}

	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		first = null;
		last = null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<K> keySet() {
		HashSet<K> set = new HashSet<K>();
		Entry<K,V> next = first;
		while( next != null ){
			set.add( next.getKey() );
			next = next.nextEntry;
		}
		return set;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<V> values() {
		Collection<V> list = new ArrayList<V>();
		Entry<K,V> next = first;
		while( next != null ){
			list.add( next.getValue() );
			next = next.nextEntry;
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<Map.Entry<K,V>> entrySet() {
		HashSet<Map.Entry<K,V>> set = new HashSet<Map.Entry<K,V>>();
		// iterate the list
		Entry<K,V> next = first;
		while( next != null ){
			set.add( next );
			next = next.nextEntry;
		}
		return set;
	}

	/**
	 * The Map.Entry implementation for the LinkedListMap
	 * @author Andrew
	 *
	 * @param <K>
	 * @param <V>
	 */
	private static class Entry<K,V> implements Map.Entry<K,V>
	{
		protected K fKey;
		protected V fValue;
		protected Entry<K,V> nextEntry;
		protected Entry<K,V> previousEntry;

		public Entry( Entry<K,V> previous, Entry<K,V> next )
		{
			previousEntry = previous;
			nextEntry = next;
		}

		public K getKey() {
			return fKey;
		}
		public V getValue() {

			return fValue;
		}
		public V setValue(V arg0) {
			fValue = arg0;
			return fValue;
		}
	}


}

