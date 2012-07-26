package org.slc.sli.api.selectors.doc;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.selectors.model.*;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.*;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.NeutralSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of a selector document
 *
 * @author srupasinghe
 *
 */
@Component
public class DefaultSelectorDocument implements SelectorDocument {

    @Autowired
    private ModelProvider modelProvider;

    @Autowired
    private SchemaRepository repo;

    @Autowired
    private EntityDefinitionStore entityDefs;

    @Override
    public List<EntityBody> aggregate(Map<Type, SelectorQueryPlan> queryMap, final Constraint constraint) {

        List<EntityBody> results = executeQueryPlan(queryMap, constraint, false);
        System.out.print(results);

        return null;

    }

    protected EntityDefinition getEntityDefinition(Type type) {
        return entityDefs.lookupByEntityType(StringUtils.uncapitalise(type.getName()));
    }

    protected List<EntityBody> executeQueryPlan(Map<Type, SelectorQueryPlan> queryMap, List<EntityBody> results,
                                                final Constraint constraint, final boolean inLine) {

        if (results == null) {
            results = new ArrayList<EntityBody>();
        }

        for (Map.Entry<Type, SelectorQueryPlan> entry : queryMap.entrySet()) {
            Type type = entry.getKey();
            SelectorQueryPlan plan = entry.getValue();

            List<String> ids;

            Constraint previousConstraint = new Constraint();
            previousConstraint.setKey(constraint.getKey());
            previousConstraint.setValue(constraint.getValue());
            Iterable<EntityBody> entities = executeQuery(type, plan.getQuery(), previousConstraint, inLine);

            EntityBody body = new EntityBody();
            body.put(type.getName(), entities);
            results.add(body);

            System.out.println("Type : " + type.getName() + " " + entities);

            ids = extractIds(entities, getKeyName(type));
            previousConstraint.setValue(ids);

            List<Object> childQueryPlans = plan.getChildQueryPlans();

            for (Object obj : childQueryPlans) {
                previousConstraint.setKey(getAssociationKeyName(type));
                List<EntityBody> list = executeQueryPlan((Map<Type, SelectorQueryPlan>) obj, results,
                        previousConstraint, true);

                EntityBody body1 = new EntityBody();
                body1.put(type.getName(), list);

                System.out.println("Type : " + type.getName() + " " + list);
            }
        }

        return results;
    }



    protected Iterable<EntityBody> executeQuery(Type type, NeutralQuery query, final Constraint constraint, final boolean inLine) {
        query.addCriteria(new NeutralCriteria(constraint.getKey(),
                NeutralCriteria.CRITERIA_IN, constraint.getValue(), inLine));

        Iterable<EntityBody> results = getEntityDefinition(type).getService().list(query);

        return results;
    }

    protected String getKeyName(Type type) {
        String key = "_id";

        ClassType classType = modelProvider.getClassType(type.getName());

        if (classType.isAssociation()) {
            AssociationEnd end = classType.getLHS();
            Type endType = modelProvider.getType(end.getType());

            key = StringUtils.uncapitalise(endType.getName()) + "Id";
        }

        return key;
    }

    protected String getAssociationKeyName(Type type) {
        ClassType classType = modelProvider.getClassType(type.getName());

        return StringUtils.uncapitalise(classType.getName()) + "Id";
    }

    protected List<String> extractIds(Iterable<EntityBody> entities, String key) {
        List<String> ids = new ArrayList<String>();

        key = key.equals("_id") ? "id" : key;

        for (EntityBody body : entities) {
            ids.add((String) body.get(key));
        }

        return ids;
    }



}
