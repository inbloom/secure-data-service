package org.slc.sli.api.util;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Parameter;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ClassDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.MethodDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ParamDocType;
import com.sun.jersey.wadl.resourcedoc.DocProcessor;

/**
 * Custom document processor used for javadoc creation 
 * @author srupasinghe
 *
 */
public class SLIDocProcessor implements DocProcessor {
    
    public Class<?>[] getRequiredJaxbContextClasses() {
        return new Class[] { EntityType.class };
    }
    
    public String[] getCDataElements() {
        return null;
    }
    
    public void processMethodDoc(MethodDoc methodDoc, MethodDocType methodDocType) {
        final String tagName = "@entity.type";
        final Tag exampleTag = getTag(methodDoc, tagName);
        if (exampleTag != null) {
            final EntityType namedValueType = new EntityType();
            namedValueType.setValue(exampleTag.text());
            methodDocType.getAny().add(namedValueType);
        }
    }
     
    private Tag getTag(MethodDoc methodDoc, final String tagName) {
        for (Tag tag : methodDoc.tags()) {
            if (tagName.equals(tag.name())) {
                return tag;
            }
        }
        return null;
    }
     
    public void processClassDoc(ClassDoc arg0, ClassDocType arg1) {
    }
     
    public void processParamTag(ParamTag arg0, Parameter arg1, ParamDocType arg2) {
    }
}
