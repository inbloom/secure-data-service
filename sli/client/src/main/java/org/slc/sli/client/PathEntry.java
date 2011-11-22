package org.slc.sli.client;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;

// TODO: should this be defined in common?
// That way they can be reused by the client and api, and no (or very little) refactoring
// will be needed when if^H^Hwhen one of these changes
// or they will be discovered from a root URI
final class PathEntry {
    
    private final String path;
    private final Map<Class<?>, PathEntry> children;
    
    public static PathEntry getRootPath() {
        Map<Class<?>, PathEntry> root = new HashMap<Class<?>, PathEntry>();
        Map<Class<?>, PathEntry> studentResources = new HashMap<Class<?>, PathEntry>();
        studentResources.put(School.class, new PathEntry("schools", null));
        studentResources.put(StudentSchoolAssociation.class, new PathEntry("schools", null));
        root.put(Student.class, new PathEntry("students", studentResources));
        Map<Class<?>, PathEntry> schoolResources = new HashMap<Class<?>, PathEntry>();
        schoolResources.put(Student.class, new PathEntry("students", null));
        schoolResources.put(StudentSchoolAssociation.class, new PathEntry("students", null));
        root.put(School.class, new PathEntry("schools", schoolResources));
        return new PathEntry("/api/rest/", root);
    }
    
    public PathEntry(String path, Map<Class<?>, PathEntry> children) {
        super();
        this.path = path;
        this.children = children;
    }
    
    public String getPath() {
        return path;
    }
    
    public Map<Class<?>, PathEntry> getChildren() {
        return children;
    }
    
}
