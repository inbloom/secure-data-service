
db.tenant.remove();

for(var count = 0; count < total; count++) {
var id = getGuid();
var tenantId = "tenant" + count;
var dbName = tenantId;
var date = new Date();

db.tenant.insert({
	"_id" : id,
	"type": "tenant",
	"body": {
		"tenantId" : tenantId,
		"dbName" : dbName,
		"landingZone" : [{
							"ingestionServer" : "local",
							"educationOrganization" : "STANDARD-SEA",
							"desc" : null,
							"path" : lzPath + tenantId,
							"userNames" : null,
							"preload" : {
											"files" : [dataSet],
											"status": "ready"
							}
		}]
	},
	"metaData" : {
		"tenantId" : tenantId,
		"updated" : date,
		"created" : date,
		"createdBy": tenantId
	}

});
}

function getGuid()
{
    var S4 = function ()
    {
        return Math.floor(
                Math.random() * 0x10000 /* 65536 */
            ).toString(16);
    };

    return (
            S4() + S4() + "-" +
            S4() + "-" +
            S4() + "-" +
            S4() + "-" +
            S4() + S4() + S4()
        );
}