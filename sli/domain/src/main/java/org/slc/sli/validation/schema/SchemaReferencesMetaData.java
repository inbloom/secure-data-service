package org.slc.sli.validation.schema;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.validation.SchemaRepository;

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

    /**
     * Some virtual fields in the schema are derived from other fields.
     * Deleting a virtual field requires deletion of underlying fields.
     */
    private BiMap<String, String> fieldMapping =
    ImmutableBiMap.of(
            "assessment.assessmentPeriodDescriptorId", "assessment.assessmentPeriodDescriptor",
            "assessment.assessmentFamilyReference", "assessment." + ParameterConstants.ASSESSMENT_FAMILY_HIERARCHY
            );

    @Autowired
    private SchemaRepository schemaRepo;

    @PostConstruct
    public void init(){
        aggregateForeignKeyReferences();

        refPaths =
        Multimaps.transformValues(refNodeLists, new Function<List<SchemaReferenceNode>, SchemaReferencePath>() {
            @Override
            public SchemaReferencePath apply(@Nullable List<SchemaReferenceNode> schemaReferenceNodes) {
                SchemaReferencePath path = new SchemaReferencePath(schemaReferenceNodes);
                String fullPath = path.getPath();
                String mappedPath = fieldMapping.get(fullPath);
                if(mappedPath != null) {
                    path.setMappedPath(mappedPath);
                }
                return path;
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
            LOG.debug("Introspecting {} for References.", schemaType);
            SchemaReferenceNode root = new SchemaReferenceNode(schemaType);
            stack.clear();
            stack.push(root);
            collectReferences(schema, stack, refNodeLists);
        }

        //Below code block can be enabled for debugging!
        /*
        NeutralSchema schema = schemaRepo.getSchema("session");
        String schemaType = schema.getType();
        LOG.info("Introspecting {} for References.", schemaType);
        SchemaReferenceNode root = new SchemaReferenceNode(schemaType);
        stack.clear();
        stack.push(root);
        collectReferences(schema, stack, refNodeLists);
        */
    }

    /**
     * A tree walker implementation that searches for leaf nodes of type ReferenceSchema within a NeutralSchema tree.
     *
     * @param schema
     * @param current nesting level e.g. studentCompetency.objectiveId.studentCompetencyObjectiveId
     * @param refMap  A map from entityType to a List of SchemaReferenceNode, each of which represents
     *                a path like studentCompetency.objectiveId.studentCompetencyObjectiveId
     */
    public void collectReferences(NeutralSchema schema, final Stack<SchemaReferenceNode> currentPath, ListMultimap refMap) {
        if(schema instanceof ReferenceSchema) {
            ReferenceSchema reference = (ReferenceSchema)schema;
            AppInfo appInfo = (AppInfo)reference.getAnnotation(Annotation.AnnotationType.APPINFO);
            if(appInfo != null) {

                Stack<SchemaReferenceNode> refPath = new Stack<SchemaReferenceNode>();
                refPath.addAll(currentPath);
                if(refPath.peek().getName().equals("reference")) {
                    refPath.pop();
                }
                String type = appInfo.getReferenceType();
                refPath.peek().setReferences(type);
                refMap.put(type, refPath);
                LOG.debug("Found a Reference from {}->{}", refPath, type);
            } else {
                LOG.warn("No AppInfo for {}. Cannot determine what EntityType it points to!", currentPath);
            }

        } else {
            Map<String, NeutralSchema> fields = schema.getFields();
            for(String fieldName:fields.keySet()) {

                NeutralSchema fieldSchema = fields.get(fieldName);
                Map<String, Object> properties = fieldSchema.getProperties();
                Long minOccurs           =       (Long)  properties.get("minCardinality");
                Long maxOccurs           =       (Long)  properties.get("maxCardinality");
                final String elementType =       (String)properties.get("elementType");

                boolean loop = Iterables.any(currentPath, new Predicate<SchemaReferenceNode>() {
                    @Override
                    public boolean apply(@Nullable SchemaReferenceNode schemaReferenceNode) {
                        String refs =  schemaReferenceNode.getReferences();
                        if(refs != null && elementType != null && refs.equals(elementType)){
                            LOG.debug("Cycle found. Repeating [" + elementType + "]" + " in [" + getTypePath(currentPath) + "]");
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                if(loop) {
                    continue;
                }

                SchemaReferenceNode pathNode = new SchemaReferenceNode(fieldName);
                pathNode.setMinOccurs(minOccurs); pathNode.setMaxOccurs(maxOccurs);
                pathNode.setReferences(elementType);
                currentPath.push(pathNode);

                if(fieldSchema.getType().equals("list")) {
                    Map<String, NeutralSchema> listFields = fieldSchema.getFields();
                    for(NeutralSchema listFieldSchema:listFields.values()) {
                        collectReferences(listFieldSchema, currentPath, refMap);
                        currentPath.pop();
                    }
                } else {
                    collectReferences(fieldSchema, currentPath, refMap);
                    currentPath.pop();
                }

            }
        }
    }

    private String getTypePath(Stack<SchemaReferenceNode> currentPath) {
        List<String> types =
                Lists.newArrayList(Iterables.transform(currentPath, new Function<SchemaReferenceNode, String>() {
                    @Override
                    public String apply(@Nullable SchemaReferenceNode schemaReferenceNode) {
                        String type = schemaReferenceNode.getReferences();
                        String name = schemaReferenceNode.getName();
                        if(type != null) {
                            return type;
                        }
                        else if(name != null) {
                            return "N{" + name + "}";
                        }
                        else {
                            return null;
                        }
                    }
                }));
        String typePath = Joiner.on(".").useForNull("{NULL}").join(types);
        return typePath;
    }

}
