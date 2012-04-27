package org.slc.sli.dal;

import java.util.Iterator;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Iterable for paging through a mongo query
 * 
 * @author nbrown
 * 
 */
public class MongoIterable implements Iterable<DBObject> {
    private static final int DEFAULT_PAGE_SIZE = 50;
    
    private final DBCollection collection;
    private final DBObject query;
    private final int pageSize;
    
    public MongoIterable(DBCollection collection, DBObject query, int pageSize) {
        super();
        this.collection = collection;
        this.query = query;
        this.pageSize = pageSize;
    }
    
    public MongoIterable(DBCollection collection, DBObject query) {
        this(collection, query, DEFAULT_PAGE_SIZE);
    }
    
    @Override
    public Iterator<DBObject> iterator() {
        return new PagingIterator();
    }
    
    private final class PagingIterator implements Iterator<DBObject> {
        private int position = 0;
        private Iterator<DBObject> currentPage;
        
        private PagingIterator() {
            currentPage = getNextPage();
        }
        
        @Override
        public boolean hasNext() {
            return currentPage.hasNext() || (collection.count() > position);
        }
        
        @Override
        public DBObject next() {
            if (!currentPage.hasNext()) {
                currentPage = getNextPage();
            }
            return currentPage.next();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        private Iterator<DBObject> getNextPage() {
            Iterator<DBObject> iterator = collection.find(query).skip(position).limit(pageSize).iterator();
            position += pageSize;
            return iterator;
        }
    }
    
}
