package org.slc.sli.validation.schema;

import com.google.common.base.Function;
import com.google.common.collect.*;
import org.slc.sli.validation.SchemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class SchemaReferencesMetaData {

    private static final Logger LOG = LoggerFactory.getLogger(SchemaReferencesMetaData.class);

    /*
      A map from entityType to a list containing the fields within other entities that refer to the entityType
      {
          student -> [ [attendance, studentId], [studentSchoolAssociation, studentId], [grade, studentId], ...]
          educationOrganization -> [[studentCompetencyObjective, educationOrganizationId], [gradingPeriod, gradingPeriodIdentity, schoolId], ...]
          .
          .
      }
     */
    private ListMultimap<String, List<SchemaReferenceNode>> refNodeLists = ArrayListMultimap.create();

    /*
       A simplified version of  refNodeLists
     */
    private ListMultimap<String, SchemaReferencePath> refPaths;

    @Autowired
    private SchemaRepository schemaRepo;

    @PostConstruct
    public void init(){
        aggregateForeignKeyReferences();

        refPaths =
        Multimaps.transformValues(refNodeLists, new Function<List<SchemaReferenceNode>, SchemaReferencePath>() {
            @Override
            public SchemaReferencePath apply(@Nullable List<SchemaReferenceNode> schemaReferenceNodes) {
                return new SchemaReferencePath(schemaReferenceNodes);
            }
        });
    }

    public List<SchemaReferencePath> getReferencesTo(String type) {
        return refPaths.get(type);
    }

    public Set<String> getReferredEntityTypes() {
        return refNodeLists.keySet();
    }

    public void aggregateForeignKeyReferences() {
        List<NeutralSchema> schemas = schemaRepo.getSchemas();
        Stack<SchemaReferenceNode> stack = new Stack<SchemaReferenceNode>();
        for(NeutralSchema schema: schemas) {
            String schemaType = schema.getType();
            LOG.info("Introspecting {} for References.", schemaType);
            SchemaReferenceNode root = new SchemaReferenceNode(schemaType);
            stack.clear();
            stack.push(root);
            collectReferences(schema, stack, refNodeLists);
        }
    }

    public void collectReferences(NeutralSchema schema, Stack<SchemaReferenceNode> currentPath, ListMultimap refMap) {
        if(schema instanceof ReferenceSchema) {
            ReferenceSchema reference = (ReferenceSchema)schema;
            AppInfo appInfo = (AppInfo)reference.getAnnotation(Annotation.AnnotationType.APPINFO);
            if(appInfo != null) {
                String type = appInfo.getReferenceType();
                currentPath.peek().setReferences(type);
                List<SchemaReferenceNode> refPath = new ArrayList<SchemaReferenceNode>(currentPath);
                refMap.put(type, refPath);
                LOG.info("Found a Reference from {}->{}", refPath, type);
            } else {
                LOG.warn("No AppInfo for {}. Cannot determine what EntityType it points to!", currentPath);
            }

        } else {
            Map<String, NeutralSchema> fields = schema.getFields();
            for(String fieldName:fields.keySet()) {
                NeutralSchema fieldSchema = fields.get(fieldName);
                Map<String, Object> properties = fieldSchema.getProperties();
                Long minOccurs = (Long)properties.get("minCardinality");
                Long maxOccurs = (Long)properties.get("maxCardinality");

                SchemaReferenceNode pathNode = new SchemaReferenceNode(fieldName);
                pathNode.setMinOccurs(minOccurs); pathNode.setMaxOccurs(maxOccurs);
                currentPath.push(pathNode);
                collectReferences(fieldSchema, currentPath, refMap);
                currentPath.pop();
            }
        }
    }

}
