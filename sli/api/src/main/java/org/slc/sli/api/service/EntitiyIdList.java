package org.slc.sli.api.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EntitiyIdList implements Iterable<String> {
    
    List<String> ids = new LinkedList<String>();
    long totalCount;
    
    public EntitiyIdList(Collection<String> ids, long totalCount) {
        this.ids.addAll(ids);
        this.totalCount = totalCount;
    }
    
    @Override
    public Iterator<String> iterator() {
        return ids.iterator();
    }
    
}
