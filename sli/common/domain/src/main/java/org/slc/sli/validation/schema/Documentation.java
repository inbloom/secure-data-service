package org.slc.sli.validation.schema;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Representation of Documentation annotations.
 * 
 * @author asaarela
 * 
 */
public class Documentation extends Annotation {
    
    private String value;
    
    public Documentation(NodeList nodes) {
        if (nodes == null) {
            value = null;
        }
        
        StringBuilder builder = new StringBuilder();
        
        for (int docNodeIdx = 0; docNodeIdx < nodes.getLength(); ++docNodeIdx) {
            Node node = nodes.item(docNodeIdx);
            
            if (node instanceof Text) {
                Text e = (Text) node;
                builder.append(e.getNodeValue());
            }
        }
        value = builder.toString();
    }
    
    @Override
    public Annotation.AnnotationType getType() {
        return Annotation.AnnotationType.DOCUMENTATION;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
}
