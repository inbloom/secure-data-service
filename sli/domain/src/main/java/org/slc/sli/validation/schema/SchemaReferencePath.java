package org.slc.sli.validation.schema;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

public class SchemaReferencePath {
    private List<SchemaReferenceNode> nodeList;

    private String entityName;

    private String fieldPath;

    private String path;

    private String referent;

    private boolean isArray;

    private boolean isOptional;

    private boolean isRequired;

    private Long minOccurs;

    private Long maxOccurs;

    private String typePath;

    public SchemaReferencePath(String path, String referent, Long min, Long max, boolean array, boolean isOptional, boolean isRequired) {

        this.path = path;
        this.referent = referent;
        minOccurs = min;
        maxOccurs = max;
        isArray = array;
        this.isOptional = isOptional;
        this.isRequired = isRequired;
    }

    public SchemaReferencePath(List<SchemaReferenceNode> list) {
        nodeList = list;
        int listSize = nodeList.size();

        List<String> names =
                Lists.newArrayList(Iterables.transform(nodeList, new Function<SchemaReferenceNode, String>() {
                    @Override
                    public String apply(@Nullable SchemaReferenceNode schemaReferenceNode) {
                        return schemaReferenceNode.getName();
                    }
                }));
        path = Joiner.on(".").join(names);

        entityName = names.remove(0);
        fieldPath = Joiner.on(".").join(names);

        SchemaReferenceNode topNode = nodeList.get(listSize - 1);
        referent = topNode.getReferences();

        minOccurs = topNode.getMinOccurs();
        maxOccurs = topNode.getMaxOccurs();


        isArray = (minOccurs != null && minOccurs > 1) || (maxOccurs != null && maxOccurs > 1);

        isOptional = (
                     ( (minOccurs != null && minOccurs == 0)  && (maxOccurs == null || maxOccurs == 1) )
                                                  ||
                     ( (minOccurs != null && minOccurs == 0)  && (maxOccurs != null && maxOccurs > 1) )
                     );

        isRequired = (
                     ( (minOccurs == null || minOccurs == 1) && (maxOccurs == null || maxOccurs == 1) )
                                                 ||
                     ( (minOccurs == null || minOccurs == 1) && (maxOccurs != null && maxOccurs > 1) )
                     );

        List<String> types =
                Lists.newArrayList(Iterables.transform(nodeList, new Function<SchemaReferenceNode, String>() {
                    @Override
                    public String apply(@Nullable SchemaReferenceNode schemaReferenceNode) {
                        return schemaReferenceNode.getReferences();
                    }
                }));
        typePath = Joiner.on(".").useForNull("{NULL}").join(types);

    }

    public String getPath() {
        return path;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public String getReferent() {
        return referent;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public Long getMaxOccurs() {
        return maxOccurs;
    }

    public Long getMinOccurs() {
        return minOccurs;
    }

    public String getTypePath() {
        return typePath;
    }

    @Override
    public String toString() {
        return "new SchemaReferencePath(" +
                "\"" + path  + "\", " +
                "\"" + referent  + "\", " +
                minOccurs + "L, " + maxOccurs + "L," + isArray + "," + isOptional + ", " + isRequired + ") ";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SchemaReferencePath that = (SchemaReferencePath) o;

        if (isArray != that.isArray) {
            return false;
        }
        if (isOptional != that.isOptional) {
            return false;
        }
        if (isRequired != that.isRequired) {
            return false;
        }
        if (maxOccurs != null ? !maxOccurs.equals(that.maxOccurs) : that.maxOccurs != null) {
            return false;
        }
        if (minOccurs != null ? !minOccurs.equals(that.minOccurs) : that.minOccurs != null) {
            return false;
        }
        if (path != null ? !path.equals(that.path) : that.path != null) {
            return false;
        }
        if (referent != null ? !referent.equals(that.referent) : that.referent != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (referent != null ? referent.hashCode() : 0);
        result = 31 * result + (isArray ? 1 : 0);
        result = 31 * result + (isOptional ? 1 : 0);
        result = 31 * result + (isRequired ? 1 : 0);
        result = 31 * result + (minOccurs != null ? minOccurs.hashCode() : 0);
        result = 31 * result + (maxOccurs != null ? maxOccurs.hashCode() : 0);
        return result;
    }
}
