package org.slc.sli.client;

/**
 * Client for accessing SLI resources from web service
 * This provides a mechanism for making CRUD type operations on any supported resource
 * 
 * @author nbrown
 * 
 */
public interface SliClient {
    
    /**
     * Gets all resources of a given type
     * For example, to get all Schools, call list(School.class)
     * 
     * @param resourceType
     *            The type of the resource to get
     * @return All resources in the db of the given type
     */
    public <T> Iterable<T> list(Class<T> resourceType);
    
    /**
     * Delete a resource from the db
     * 
     * @param resourceType
     *            the type of resource to delete
     * @param id
     *            the id of the resource to delete
     * @return true iff the delete was successful
     */
    public <T> boolean deleteResource(Class<T> resourceType, String id);
    
    /**
     * Delete a resource from the db
     * 
     * @param resource
     * 
     * @return true iff the delete was successful
     */
    public <T> boolean deleteResource(T resource);
    
    /**
     * Add a new resource to the db
     * 
     * @param resource
     *            the resource to add
     * @return the created object, or null if the object couldn't be created
     */
    public <T> T addNewResource(T resource);
    
    /**
     * Update an existing resource to the db
     * 
     * @param resource
     *            the resource to update
     * @return true iff the update was successful
     */
    public boolean updateResource(Object resource);
    
    /**
     * Gets a resource of a given type and a given id
     * 
     * @param resourceType
     *            the type of resource to get
     * @param id
     *            the id of the resource to get
     * @return the actual resource
     */
    public <T> T getResource(Class<T> resourceType, String id);
    
    /**
     * Get resources associated with the given resource. If the relativeType is the type of a
     * resource that is related to the given resource, the results will be the relatives themselves.
     * If it is the class of the association itself, the results will be the associations that link
     * the resources.
     * As an example, give a school mySchool, to get all students one would call
     * getAssociated(mySchool, Student.class)
     * to get all student/school associations, one would call
     * getAssociated(mySchool, StudentSchoolAssociation.class)
     * 
     * @param resource
     *            The resource to get associated resources for
     * @param relativeType
     *            The type of the relative, or the association itself
     * @return
     */
    public <T> Iterable<T> getAssociated(Object resource, Class<T> relativeType);
    
    /**
     * Get the associations between two resources
     * As an example, given a school mySchool, and student myStudent, to get all
     * StudentSchoolAssociations between the two, one would call
     * getAssociations(mySchool, myStudent, StudentSchoolAssociation.class)
     * 
     * @param resource
     *            The first resource
     * @param relative
     *            the resource related to the first resource
     * @return an iterator through the relations between the two resources
     */
    public <T> Iterable<T> getAssociations(Object resource, Object relative, Class<T> assocationType);
    
    /**
     * Associate two resources with each other. Calling this for two resources already related will
     * result in the existing relation changing
     * 
     * @param resource
     *            The first resource
     * @param relative
     *            the resource related to the first resource
     * @param relation
     *            the relation to add
     * @return The relation, or null if the relation could not be added
     */
    public <T> T associate(Object resource, Object relative, T relation);
    
    /**
     * Disassociate a resource from another
     * 
     * @param resource
     *            The first resource
     * @param relative
     *            the resource related to the first resource
     * @param relation
     *            the relation to delete
     * @return whether or not the dissociation was successful
     */
    public boolean disassociate(Object resource, Object relative, Object relation);
    
    /**
     * Update the association between two resources
     * 
     * @param resource
     *            The first resource
     * @param relative
     *            the resource related to the first resource
     * @param relation
     *            the relation to update
     * @return whether or not the reassociation was successful
     */
    public boolean reassociate(Object resource, Object relative, Object relation);
    
    /**
     * Gets the format being used to send and retrieve data to the SLI system
     * Normal usage of this library should be able to just use the default here
     * 
     * @return the default format to be used to communicate with the SLI system
     */
    public Format getDefaultFormat();
    
    /**
     * Sets the format being used to send and retrieve data to the SLI system
     * Normal usage of this library should be able to just use the default here
     * 
     * @param format
     *            the default format to be used to communicate with the SLI system
     */
    public void setDefaultFormat(Format format);
}
