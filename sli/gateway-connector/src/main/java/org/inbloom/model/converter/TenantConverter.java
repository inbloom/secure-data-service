package org.inbloom.model.converter;

import com.mongodb.DBObject;
import org.inbloom.model.Tenant;
import org.springframework.core.convert.converter.Converter;

/**
 * @author ben morgan
 */
public class TenantConverter extends BaseConverter implements Converter<DBObject, Tenant> {

    @Override
    public Tenant convert(DBObject dbObj) {
        Tenant tenant = new Tenant();

        tenant.setId(getString(dbObj, "_id"));
        tenant.setTenantId(getString(dbObj, "body.tenantId"));
        tenant.setDbName(getString(dbObj, "body.dbName"));

        return tenant;
    }
}
