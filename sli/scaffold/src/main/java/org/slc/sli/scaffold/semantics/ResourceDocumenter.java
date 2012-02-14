package org.slc.sli.scaffold.semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.Documentation;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.NeutralSchema;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Creates a resource documentation object that holds a list of all top level resources, the
 * semantic documentation for each resource, and the semantic documentation for each 
 * field on that resource.
 * 
 * @author jstokes
 *
 */
public class ResourceDocumenter {
    
    private static ApplicationContext ctx;
    private List<ResourceDocumentation> documentation;
    private SchemaRepository schemaRepo;
    
    public static void main(String[] args) {
        ResourceDocumenter rd = new ResourceDocumenter();
        List<NeutralSchema> topLevelResources = rd.getResourceSchemas();
        rd.generate(topLevelResources);
    }
    
    /**
     * Constructor for resource documenter
     * Initializes the application context and resolves the Schema Repository 
     */
    public ResourceDocumenter() {
        ctx = new ClassPathXmlApplicationContext(new String[] {"spring/application-context.xml"});
        documentation = new ArrayList<ResourceDocumentation>();
        schemaRepo = (SchemaRepository) ctx.getBean("xsdToNeutralSchemaRepo");
    }
    
    /**
     * Generates the resource documentation for a list of resource schemas
     * @param resources The list of top level resources
     * @return ResourceDocumentation list object containing each of the top level resources and their fields documentation
     */
    public List<ResourceDocumentation> generate(List<NeutralSchema> resources) {
        generateDocumentation(resources);
        debugOuput();
        return documentation;
    }
    
    private void debugOuput() {
        for (ResourceDocumentation doc : documentation) {
            System.out.println(" --- ");
            System.out.println(doc);
            System.out.println(" --- ");
        }
    }

    private void generateDocumentation(List<NeutralSchema> resources) {
        //Top level resource
        for (NeutralSchema resource : resources) {
            ResourceDocumentation rDoc = new ResourceDocumentation();
            rDoc.setResourceName(resource.getType());
            rDoc.setResourceDocumentation(getDocumentation(resource));
            
            generateFieldDocumentation(resource, rDoc);
            
            documentation.add(rDoc);
        }
    }

    private void generateFieldDocumentation(NeutralSchema resource, ResourceDocumentation rDoc) {
        Map<String, NeutralSchema> resourceMap = resource.getFields();
        //Fields on resource
        for (Map.Entry<String, NeutralSchema> entry : resourceMap.entrySet()) {
            String key = entry.getKey();
            NeutralSchema schema = entry.getValue();
            
            rDoc.addFieldDocumentation(key, getDocumentation(schema));
        }
    }
    
    /**
     * Gets the documentation annotation for a given Neutral schema
     * @param neutralSchema Schema to retrieve documentation for
     * @return Documentation associated with the given Neutral schema
     */
    private String getDocumentation(NeutralSchema neutralSchema) {
        String ret = "";
        Documentation doc = neutralSchema.getDocumentation();
        
        if (doc != null) {
            ret = doc.toString();
        } else if (neutralSchema instanceof ListSchema)  {
            ListSchema lSchema = (ListSchema) neutralSchema;
            NeutralSchema first = lSchema.getList().get(0);
            ret = first.getDocumentation().toString();
        } else {
            ret = "No documentation found";
        }
        
        ret = ret.replaceAll("\\s+", " ");
        return ret;
    }
    
    /**
     * Gets a list of all top level resource Neutral schemas
     * @return a list of all top level resource Neutral schemas
     */
    private List<NeutralSchema> getResourceSchemas() {
        ArrayList<NeutralSchema> resources = new ArrayList<NeutralSchema>();
        //TODO: get from wadl via xpath
        resources.add(schemaRepo.getSchema("school"));
        resources.add(schemaRepo.getSchema("student"));
        return resources;
    }
    
    public List<ResourceDocumentation> getDocumentation() {
        return documentation;
    }
}
