package org.slc.sli.validation.schema;

import com.google.common.collect.ArrayListMultimap;
import org.slc.sli.validation.SchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class SchemaReferencesMetaData {

    @Autowired
    private SchemaRepository schemaRepo;

    @PostConstruct
    public void init(){
        aggregateForeignKeyReferences();
    }

    private ArrayListMultimap<String, List<SchemaReferenceNode>> refMap = ArrayListMultimap.create();

    public List<List<SchemaReferenceNode>> getReferencesTo(String type) {
        return refMap.get(type);
    }

    public void aggregateForeignKeyReferences() {

        List<NeutralSchema> schemas = schemaRepo.getSchemas();
        for(NeutralSchema schema: schemas) {
            String type = schema.getType();
            Stack<SchemaReferenceNode> stack = new Stack<SchemaReferenceNode>();
            SchemaReferenceNode root = new SchemaReferenceNode();
            root.setName(type);
            stack.push(root);
            collectReferences(schema, stack, refMap);
        }
        Set<String > keys = refMap.keySet();
        for(String key:keys) {
            List<List<SchemaReferenceNode>> lists = refMap.get(key);
            System.out.print("\n" + key + " is referred from ");
            for(List list: lists){
                System.out.print(list +  "   AND   ") ;
            }

        }
    }

    public void collectReferences(NeutralSchema schema, Stack<SchemaReferenceNode> currentPath, ArrayListMultimap refMap) {
        if(schema instanceof ReferenceSchema) {
            ReferenceSchema reference = (ReferenceSchema)schema;
            AppInfo appInfo = (AppInfo)reference.getAnnotation(Annotation.AnnotationType.APPINFO);
            if(appInfo != null) {
                String type = appInfo.getReferenceType();
                String name = reference.getType();
                SchemaReferenceNode leafNode = currentPath.peek();
                List<SchemaReferenceNode> refPath = new ArrayList<SchemaReferenceNode>(currentPath);
                refMap.put(type, refPath);
            } else {
                System.out.println("Could not find AppInfo for [" + currentPath + "]");
            }

        } else {
            Map<String, NeutralSchema> fields = schema.getFields();
            for(String fieldName:fields.keySet()) {


                Map<String, Object> properties = schema.getProperties();
                Long minOccurs = (Long)properties.get("minOccurs");
                Long maxOccurs = (Long)properties.get("maxOccurs");
                boolean isArray = isArray(minOccurs, maxOccurs);


                if(schema instanceof ListSchema) {//replace instanceof with NeutralSchema.getType()
                    ListSchema listSchema = (ListSchema)schema;
                    for(NeutralSchema listSchemaElement:listSchema.getList()) {
                        SchemaReferenceNode pathNode = new SchemaReferenceNode();
                        pathNode.setName(listSchemaElement.getType());
                        currentPath.push(pathNode);
                        pathNode.setArray(isArray);
                        collectReferences(listSchemaElement, currentPath, refMap);
                        currentPath.pop();
                    }
                }   else if(schema instanceof ChoiceSchema) {
                    for(NeutralSchema choiceSchemaElement:schema.getFields().values()) {
                        SchemaReferenceNode pathNode = new SchemaReferenceNode();
                        pathNode.setName(choiceSchemaElement.getType());
                        currentPath.push(pathNode);
                        pathNode.setArray(isArray);
                        collectReferences(choiceSchemaElement, currentPath, refMap);
                        currentPath.pop();
                    }
                } else {
                    NeutralSchema fieldSchema = fields.get(fieldName);
                    SchemaReferenceNode pathNode = new SchemaReferenceNode();
                    pathNode.setName(fieldName);
                    currentPath.push(pathNode);
                    pathNode.setArray(isArray);
                    collectReferences(fieldSchema, currentPath, refMap);
                    currentPath.pop();
                }

            }
        }
    }


    boolean isArray(Long minOccurs, Long maxOccurs) {
        if(maxOccurs != null){
            if(maxOccurs.equals(Long.MAX_VALUE)) {
                return true;
            }
            if(minOccurs > 1) {
                return true;
            }
        }
        if(minOccurs != null){
            if(maxOccurs > 1) {
                return true;
            }
        }
        return false;
    }
}
