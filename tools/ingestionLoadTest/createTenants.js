
var prefix = null;
if ( typeof tenantIdPrefix != 'undefined') {
    prefix = tenantIdPrefix;
}

var count = null;
if ( typeof tenantCount != 'undefined') {
    count = tenantCount;
}

var landingZoneRoot = null;
if ( typeof tenantLandingZoneRoot != 'undefined') {
    landingZoneRoot = tenantLandingZoneRoot;
}

var iserver = null;
if ( typeof server != 'undefined') {
    iserver = server;
}

print ("Parameter Prefix, Count, LandingZoneRoot, IngestionServer [" + prefix + ", " + count + ", " + landingZoneRoot + ", " + server + "]" );
if( prefix == null || count == null || landingZoneRoot == null || iserver == null){
    quit(0);
} else {
}

var thisInstance = new Date();
var tenantTemplate = { "_id" : "-------" ,
	"type" : "tenant" ,
	"body" : { "tenantId" :  "--------" ,
		"landingZone" : [ { "ingestionServer" : "---------" ,
			"educationOrganization" : "------" ,
			"desc" : "-------" ,
			"path" : "-------"} 
		]} ,
	"metaData" : { "updated" : thisInstance , "created" : thisInstance}
		};


var baseId = "2012nc-aaec7f59-e237-11e1-bacc-e4115bf51018";
for(var i = 0; i < count; i++) {
    var nextYear                                                      = new String(2012 + i);
    var newId                                                         = baseId.replace("2012", nextYear);
    var landingZone                                                   = landingZoneRoot + prefix +  i;
    tenantTemplate["_id"]                                             = newId; 
    tenantTemplate["body"]["tenantId"]                                = "TEST-TENANT-" + prefix  + i;
    tenantTemplate["body"]["landingZone"][0]["educationOrganization"] = "Load-Testing-EdOrg-" + i; 
    tenantTemplate["body"]["landingZone"][0]["desc"]                  = "Load Testing Tenant";
    tenantTemplate["body"]["landingZone"][0]["path"]                  = landingZone;
    tenantTemplate["body"]["landingZone"][0]["ingestionServer"]       = iserver;
    print(tenantTemplate["body"]["landingZone"][0]["path"]);
    tenantTemplate["metaData"]["created"]                             = thisInstance; 
    tenantTemplate["metaData"]["updated"]                             = thisInstance;
    db.tenant.insert(tenantTemplate);
}


