package org.slc.sli.api.init;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 */
public class InitializerUtils {
    
    /**
     * This is a fairly simple way of checking whether some arbitrary data has changed.
     * It sums up the hash value of every key and value in the map.
     * 
     * If the resulting checksum aren't the same, it's guaranteed that something has changed.
     * 
     * The one limitation is that it won't recognize structural changes
     * e.g.
     * if a map goes from {foo: bar} to {bar: foo}, they'll hash the same
     * @param map
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected static long checksum(Map map) {
        long hash = 0;
        for (Iterator<Map.Entry> i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = i.next();
            hash += entry.getKey().hashCode();
            if (entry.getValue() instanceof Map) {
                hash += checksum((Map) entry.getValue());
            } else if (entry.getValue() instanceof List) {
                hash += checksum((List) entry.getValue());
            } else {
                hash += entry.getValue() != null ? entry.getValue().hashCode() : -1;
            }
        }
        return hash;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected static long checksum(List list) {
        long hash = 0;
        for (Object o : list) {
            if (o instanceof Map) {
                hash += checksum((Map) o);
            } else if (o instanceof List) {
                hash += checksum((List) o);
            } else {
                hash += o.hashCode();
            }
        }
        return hash;
    }

}
