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

        ClassType type2 = modelProvider.getClassType("StudentSchoolAssociation");

        NeutralSchema schema = repo.getSchema(StringUtils.uncapitalise(type2.getName()));
        System.out.print(schema);

        EntityDefinition def = getEntityDefinition(type2);

        System.out.print(def);

        executeQueryPlan(queryMap, constraint);

        return null;

        //executeQueryPlan(queries, constraint);
    }

    protected EntityDefinition getEntityDefinition(Type type) {
        return entityDefs.lookupByEntityType(StringUtils.uncapitalise(type.getName()));
    }

    protected void executeQueryPlan(Map<Type, SelectorQueryPlan> queryMap, final Constraint constraint) {
        for (Map.Entry<Type, SelectorQueryPlan> entry : queryMap.entrySet()) {
            Type type = entry.getKey();
            SelectorQueryPlan plan = entry.getValue();

            List<String> ids;

            Constraint previousConstraint = new Constraint();
            previousConstraint.setKey(getKeyName(type));
            previousConstraint.setValue(constraint.getValue());
            ids = executeQuery(type, plan.getQuery(), previousConstraint);
            previousConstraint.setValue(ids);

            List<Object> childQueryPlans = plan.getChildQueryPlans();

            for (Object obj : childQueryPlans) {
                executeQueryPlan((Map<Type, SelectorQueryPlan>) obj, previousConstraint);
            }
        }
    }

    protected List<String> executeQuery(Type type, NeutralQuery query, final Constraint constraint) {
        query.addCriteria(new NeutralCriteria(constraint.getKey(),
                NeutralCriteria.CRITERIA_IN, constraint.getValue()));

        Iterable<EntityBody> results = getEntityDefinition(type).getService().list(query);

        System.out.println("Running Query : [" + type.getName() + "], " + query);

        //TODO stubbing for now
        List<String> result = new ArrayList<String>();
        result.add(type.getName() + "_34");
        result.add(type.getName() + "_56");

        return result;
    }

    protected String getKeyName(Type type) {
        String key = "id";

        ClassType classType = modelProvider.getClassType(type.getName());

        if (classType.isAssociation()) {
            AssociationEnd end = classType.getRHS();

            key = StringUtils.uncapitalise(end.getName()) + "Id";
        }

        return key;
    }



}
