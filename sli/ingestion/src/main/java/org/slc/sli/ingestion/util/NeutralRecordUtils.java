package org.slc.sli.ingestion.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

public class NeutralRecordUtils {
	   @SuppressWarnings("unchecked")
		public static <T> T scrubEmptyStrings(T obj){
	    	if (Map.class.isInstance(obj)) {
	    		return (T) process((Map<?, ?>) obj);
	    	} else if(List.class.isInstance(obj)){
	    		return (T) process((List<?>) obj);
	    	} else if(String.class.isInstance(obj)){
	    		return (T) process((String) obj);
	    	} else {
	    		return obj;
	    	}
	    }

		private static List<Object> process(List<?> value) {
			List<Object> newList = new ArrayList<Object>();

			boolean isEmpty = true;
			for (Object record : value) {
				record = scrubEmptyStrings(record);

				if (record != null) {
					isEmpty = false;
				}

				newList.add(record);
			}

			if (isEmpty) {
				newList = new ArrayList<Object>();
			}

			return newList;
		}

		private static Map<Object,Object> process(Map<?,?> value) {
			Map<Object, Object> newMap = new HashMap<Object,Object>();

			boolean isEmpty = true;
			for(Map.Entry<?, ?> item : value.entrySet()){
				Object newValue = scrubEmptyStrings(item.getValue());

				if (newValue != null) {
					isEmpty = false;
				}

				newMap.put(item.getKey(), newValue);
			}

			if(isEmpty){
				newMap = null;
			}

			return newMap;
		}

		private static String process(String value) {
			String cmp = value;

			if (!StringUtils.hasText(cmp)) {
				value = null;
			}

			return value;
		}
}
