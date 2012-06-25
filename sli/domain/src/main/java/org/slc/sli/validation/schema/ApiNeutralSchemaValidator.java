package org.slc.sli.validation.schema;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.ValidationError;

/**
 * This validator is used to get the naturalKeyFields for an entity. 
 * This validator applies specifically to the API CRUD operations, 
 * as the natural keys are not currently applicable for ingestion.
 *
 * @author John Cole <jcole@wgen.net>
 *
 */
@Component
public class ApiNeutralSchemaValidator extends NeutralSchemaValidator {

    // Methods

    @Override
    public List<String> getNaturalKeyFields(Entity entity) {

        List<String> naturalKeyFields = new ArrayList<String>();

        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());
        if (schema != null) {

            AppInfo appInfo = schema.getAppInfo ();
            if (appInfo != null) {
                if (appInfo.applyNaturalKeys()) {
                    Map<String, NeutralSchema> fields = schema.getFields();
                    for (Iterator<Map.Entry<String, NeutralSchema>> i = fields.entrySet().iterator(); i.hasNext();) {
                        Map.Entry entry = i.next();
                        String field = (String)entry.getKey();
                        NeutralSchema fieldSchema = (NeutralSchema)entry.getValue();

                        AppInfo fieldsAppInfo = fieldSchema.getAppInfo ();
                        if (fieldsAppInfo != null) {
                            if (fieldsAppInfo.isNaturalKey()) {
                                naturalKeyFields.add (field);
                            }
                        }
                    }
                }
            }
        }

        return naturalKeyFields;
    }

}
