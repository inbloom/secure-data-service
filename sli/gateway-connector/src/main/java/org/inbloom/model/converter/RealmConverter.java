package org.inbloom.model.converter;

import com.mongodb.DBObject;
import org.inbloom.model.Realm;
import org.springframework.core.convert.converter.Converter;

/**
 * @author ben morgan
 */
public class RealmConverter extends BaseConverter implements Converter<DBObject, Realm> {

    public Realm convert(DBObject dbObj)
    {
        Realm realm = new Realm();

        realm.setId(getString(dbObj, "_id"));

        realm.setUniqueIdentifier(getString(dbObj, "body.uniqueIdentifier"));
        realm.setName(getString(dbObj, "body.name"));
        realm.setIdpRedirect(getString(dbObj, "body.idp.redirectEndpoint"));
        realm.setTenantId(getString(dbObj, "body.tenantId"));

        return realm;
    }




}
