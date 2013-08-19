//mongo localhost:27017/02f7abaa9764db2fa3c1ad852247cd4ff06b2c0aq parentage.js
db.educationOrganization.find({}, {"_id":1, "body.stateOrganizationId":1, "body.parentEducationAgencyReference":1, 'body.organizationCategories':1}).sort({"body.stateOrganizationId":1}).forEach(
    function (eo) {
       print("");
       var id = eo._id;
       var sid     = eo.body.stateOrganizationId;
       var parents = eo.body.parentEducationAgencyReference;
       var oc      = eo.body['organizationCategories'];
       var ocStr = (oc != null)?oc.join(', '):'_OC_';
       var name = " [ " + id + " ][ " + sid + "] "; 
       var plist =  [];
       if(parents != null)
       for(var i = 0; i < parents.length; i++) {
           var pid = parents[i];
           var parent = db.educationOrganization.findOne({"_id":pid}, {"body.stateOrganizationId":1});
           plist.push(parent.body.stateOrganizationId);
       }
       print (name + ' [' + ocStr + '] '  +  " P " + plist.join(", "));
    }
)




