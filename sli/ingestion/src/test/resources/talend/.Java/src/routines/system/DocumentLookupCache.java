package routines.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * now for unique match result & first match
 * for tXMLMap
 * @author Administrator
 *
 */
public class DocumentLookupCache {
	
	private LookupCache cache;
	
	public DocumentLookupCache(String matchingMode) {
		if("UNIQUE_MATCH".equals(matchingMode)) {
			cache = new UniqueLookupCache();
		} else if("FIRST_MATCH".equals(matchingMode)) {
			cache = new FirstLookupCache();
		} else if("ALL_MATCHES".equals(matchingMode)) {
			cache = new AllMatchLookupCache();
		}
	}
	
	public void put(List<Object> key,Map<String,Object> value) {
		cache.put(key, value);
	}

	public void lookup(List<Object> key) {
		cache.lookup(key);
	}
	
	public boolean hasNext() {
		return cache.hasNext();
	}
	
	public Map<String,Object> next() {
		return cache.next();
	}
	
	abstract class LookupCache {
		protected boolean hasNext = false;
		protected Map<String,Object> currentValue;
		abstract void put(List<Object> key,Map<String,Object> value);
		abstract void lookup(List<Object> key);
		abstract boolean hasNext();
		abstract Map<String,Object> next();
	}
	
	/**
	 * for unique match
	 * @author Administrator
	 *
	 */
	class UniqueLookupCache extends LookupCache {

		private Map<List<Object>,Map<String,Object>> uniqueMap = new HashMap<List<Object>,Map<String,Object>>();
		
		@Override
		void put(List<Object> key,Map<String,Object> value) {
			uniqueMap.put(key,value);
		}

		@Override
		void lookup(List<Object> key) {
			currentValue = uniqueMap.get(key);
			hasNext = currentValue != null ? true : false;
		}

		@Override
		boolean hasNext() {
			return hasNext;
		}

		@Override
		Map<String, Object> next() {
			return currentValue;
		}
		
	}
	
	/**
	 * for first match
	 * it seems that is is the same with UniqueLookupCache,because they are all for only one resultset
	 * @author Administrator
	 *
	 */
	class FirstLookupCache extends LookupCache {

		private Map<List<Object>,Map<String,Object>> uniqueMap = new HashMap<List<Object>,Map<String,Object>>();
		
		@Override
		void put(List<Object> key,Map<String,Object> value) {
			uniqueMap.put(key,value);
		}

		@Override
		void lookup(List<Object> key) {
			currentValue = uniqueMap.get(key);
			hasNext = currentValue != null ? true : false;
		}

		@Override
		boolean hasNext() {
			return hasNext;
		}

		@Override
		Map<String, Object> next() {
			return currentValue;
		}
		
	}
	
	class AllMatchLookupCache extends LookupCache {
		
		//private Map<List<Object>,Map<String,Object>> uniqueMap = new HashMap<List<Object>,Map<String,Object>>();

		@Override
		void put(List<Object> key, Map<String, Object> value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		void lookup(List<Object> key) {
			// TODO Auto-generated method stub
			
		}

		@Override
		boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		Map<String, Object> next() {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	}
}
