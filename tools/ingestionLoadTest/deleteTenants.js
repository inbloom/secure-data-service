var prefix = "PLACEHOLDER PLACEHOLDE ";
if ( typeof tenantIdPrefix != 'undefined') {
    prefix = '.*' + tenantIdPrefix + '.*';
}
print("Deleting tenants with [" + prefix + "]");
db.tenant.remove( { "body.tenantId" : { $regex :prefix, $options: 'i' } } );
