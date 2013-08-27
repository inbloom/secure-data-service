//mongo localhost:27017/02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a parentage.js


var staffMapping = {};

//mongo localhost:27017/02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a staffAssociations.js
db.staffEducationOrganizationAssociation.find({}, {}).forEach(function (seo){

        try {
            var body = seo.body;     
        }catch(E) {}

        var staffClassification =body.staffClassification ;
        var educationOrganizationReference =body.educationOrganizationReference ;
        var positionTitle = body.positionTitle ;
        var staffReference = body.staffReference ;
        var endDate =body.endDate ;
        var beginDate =body.beginDate ;
        var eo = "";
        try {
            eo = db["educationOrganization"].findOne({"_id":educationOrganizationReference}).body.stateOrganizationId;
        } catch(ex) {}

        var st = "";
        try {
            st = db["staff"].findOne({"_id":staffReference}).body.staffUniqueStateId;
        } catch (ex) {
            print("Cannot find staff [" + staffReference + "]");
        }
        var mapping = ['[', st, staffClassification, positionTitle, beginDate, endDate, ']'];
        if( ! (eo in staffMapping)) {
            staffMapping[eo] = [];
        }
        staffMapping[eo].push(mapping.join(", "));
});



db.educationOrganization.find({}, {"_id":1, "body.stateOrganizationId":1, "body.parentEducationAgencyReference":1, 'body.organizationCategories':1}).sort({"body.stateOrganizationId":1}).forEach(
    function (eo) {
       var id = eo._id;
       var sid     = eo.body.stateOrganizationId;
       var parents = eo.body.parentEducationAgencyReference;
       var oc      = eo.body['organizationCategories'];
       var ocStr = (oc != null)?oc.join(', '):'_OC_';
       var name = "[ " + id + " ][ " + sid + "] "; 
       var plist =  [];
       if(parents != null)
       for(var i = 0; i < parents.length; i++) {
           var pid = parents[i];
           var parent = db.educationOrganization.findOne({"_id":pid}, {"body.stateOrganizationId":1});
           plist.push(parent.body.stateOrganizationId);
       }
       if(sid in staffMapping) {
           print("");
           print (name + ' [' + ocStr + '] '  +  " P " + plist.join(", ") + "\n" + staffMapping[sid].join("\n"));
       } else {
           print("");
           print (name + ' [' + ocStr + '] '  +  " P " + plist.join(", ") + "\n" );
       }
 
    }
)




