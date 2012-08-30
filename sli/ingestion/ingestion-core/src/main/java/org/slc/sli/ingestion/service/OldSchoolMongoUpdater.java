package org.slc.sli.ingestion.service;

import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.normalization.ComplexRefDef;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.RefDef;

/**
 * Async mongo writer (consumer of producer/consumer)
 *
 * @author dduran
 *
 */
public final class OldSchoolMongoUpdater {
    private static DB sliDB;
    static {
        try {
            Mongo mongo = new Mongo("localhost", 27017);
            sliDB = mongo.getDB("sli");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static void giveContext(NeutralRecord item, EntityConfig entityConfig) {
        ComplexRefDef complexRefDef = entityConfig.getComplexReference();
        if (complexRefDef != null) {
            // wat is this?
        }

        if (entityConfig.getReferences() != null) {
            for (RefDef refDef : entityConfig.getReferences()) {

                List<String> givesContextList = refDef.getRef().getGivesContext();
                if (givesContextList != null) {

                    String entityReferenced = refDef.getRef().getEntityType();

                    for (String contextToGive : givesContextList) {

                        Object edOrgs = item.getMetaData().get(contextToGive);
                        if (edOrgs != null) {

                            String bodyPath = refDef.getFieldPath().replaceFirst("body\\.", "");

                            Object normalizedIdValue = item.getAttributes().get(bodyPath);
                            if (normalizedIdValue instanceof String) {

                                updateMongo(entityReferenced, TenantContext.getTenantId(), (String) normalizedIdValue,
                                        contextToGive, edOrgs);

                            } else if (normalizedIdValue instanceof List) {
                                for (String val : (List<String>) normalizedIdValue) {

                                    updateMongo(entityReferenced, TenantContext.getTenantId(), val, contextToGive,
                                            edOrgs);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void updateMongo(String collectionName, String tenantId, String id, String contextField,
            Object context) {
        DBObject query = new BasicDBObject();
        query.put("metaData.tenantId", tenantId);
        query.put("_id", id);

        DBObject update = new BasicDBObject();
        update.put("$addToSet", new BasicDBObject("metaData." + contextField, context));

        sliDB.getCollection(collectionName).update(query, update, false, true, WriteConcern.NORMAL);
    }
}
