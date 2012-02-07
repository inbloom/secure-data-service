package org.slc.sli.repository;

import java.io.Serializable;

/**
 * Provides an interface for giving the mock repository information about what field an entity uses
 * for its ID.
 *
 * @author Sean Melody <smelody@wgen.net>
 *
 * @param <T>
 *            The entity type
 * @param <ID>
 *            The ID type
 */
public interface MockIDProvider<T, ID extends Serializable> {

    public ID getIDForEntity(T entity);

}
