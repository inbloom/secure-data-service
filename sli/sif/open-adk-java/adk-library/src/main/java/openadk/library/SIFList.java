//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a SIF Repeatable Element list container
 *
 * @param <T> The specific type of SIFElement contained in the list
 */
/**
 * @author Andrew
 *
 * @param <T>
 */
public class SIFList<T extends SIFElement> 
	extends SIFElement
	implements Collection<T>
{
	
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;	
	
	/**
	 * Creates an instance of a SIFList class
	 * @param def
	 */
	public SIFList(ElementDef def) {
		super(def);
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return getChildCount();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return (Iterator<T>)_childList().iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		return _childList().toArray();
	}

	/**
	 * Adds the specified Repeatable child element to this list of elements
	 * @param element The child to add
	 * @return True if the element was added to the list
	 */
	public boolean add( T element ) {
		this.addChild( element );
		return true;
	}

	/**
	 * Remobes the specified Repeatable child element from the list of elements
	 * @param element
	 * @return True if the child was removed from this List. Otherwise, false
	 */
	public boolean remove( T element ) {
		return this.removeChild( element );
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll( Collection<?> elementCollection ) {
		return _childList().containsAll( elementCollection );
	}

	/**
	 * Adds all of the elements in the specified collection to this list
	 * @param elementCollection
	 * @return True if all items were added to this list
	 */
	public boolean addAll( Collection<? extends T> elementCollection ) {
		if( elementCollection == null ){
			return true;
		}
		for( T o : elementCollection ){
			addChild( o );
		}
		return true;
	}
	
	/**
	 * Adds all of the elements i to this list
	 * @param items
	 * @return True if all items were added to this list
	 */
	public boolean addAll( T... items ) {
		if( items == null ){
			return true;
		}
		for( T o : items ){
			addChild( o );
		}
		return true;
	}
	
	
    /**
     * Replaces the contents of this list with the specified array of items
     * @param items
     */
    public  void setChildren(T... items)
    {
    	this.clear();
    	this.addAll(items);
    }
    
    /**
     * Replaces the contents of this list with the contents of the collection
     * @param elementCollection The collection to add. If Null is passed in, the contents
     * of this list will be cleared
     */
    public  void setChildren(Collection<? extends T> elementCollection)
    {
    	this.clear();
    	this.addAll( elementCollection );
    }


	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> elementCollection) {
		_childList().removeAll( elementCollection );
		return true;
	}


	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		synchronized( fSyncLock ) {
			if( getChildCount() > 0 ){
				List<SIFElement> v = _childList();
			    // Go through the vector in reverse order, removing any children of this type
			    for( int i = v.size() -1 ; i >= 0; i-- ){
			        SIFElement child = v.get( i );
			        child.setParent( null );
		            v.remove( i );
			    }
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object item ) {
		 synchronized ( fSyncLock ) {
             if( this.getChildCount() == 0 ){
                 return false;
             }
             else {
                 return this._childList().contains( item  );
             }
         }
	}


	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove( Object child ) {
		return removeChild( (SIFElement)child );
	}
	
	/**
	 * Copies the child elements to the specified array
	 * @param <V> The generic Type of the array
	 * @param array The Array to add the members to
	 * @return The array that the child members were added to
	 */
	public <V> V[] toArray(V[] array) {
		synchronized( fSyncLock ){
			return _childList().toArray( array );
        }
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> arg0) {
		 synchronized( fSyncLock ){
             if( this.getChildCount() == 0 )
             {
                 return arg0.size() == 0;
             }
             else
             {
            	 return _childList().retainAll( arg0 );
             }
         }
	}


	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	 @SuppressWarnings("unchecked")
	public T get(int arg0) {
		return  (T)_childList().get( arg0 );
	}

}
