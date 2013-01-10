//mongo --eval "var collectionName='student';" localhost:27017/sli csv.js

if( typeof collectionName == 'undefined' || 
           collectionName == null)
	   collectionName = "student";
var unresolvedRefs    = [];
var resolvedRefsCount = 0;
var documentCount     = 0;

var criteria = {};
if( typeof tenant != 'undefined' 
      &&  tenant  != null 
      && tenant.length > 0) 
    criteria['metaData.tenantId']=tenant;

if( typeof filter != 'undefined' 
      &&  filter  != null 
      && filter.length > 0
) {
    var nvPair = filter.match(/(\S+)\s*=\s*(\S+)/);
    if(nvPair != null) {
        var name  = nvPair[1];
        var value = nvPair[2];
	criteria[name] = value;
    }
}

var selectFields = {};
if( typeof select != 'undefined' 
&&  select  != null 
&&  select.length > 0
) 
    selectFields[select] = 1;

var fieldNameRegex = null;
if( typeof regex != 'undefined' 
&&  regex  != null 
&&  regex.length > 0
) 
    fieldNameRegex = new RegExp(regex, "i");;

if( typeof limit == 'undefined' || 
           limit == null)
	   limit = 0;
db.getCollection(collectionName).find(criteria, selectFields).limit(limit).batchSize(50).forEach(walker);

function  walker(node, stack, collector, depth) { 
	if(stack     == null) {  stack     = new Array(); stack.push(collectionName);}
	if(collector == null)    collector = new Object();
	if(depth     == null)    depth     = 0;
	for(attrName in node) {
            var attrValue    =  node[attrName];
	    if(attrName      == null || attrValue == null ) continue;
	    else if(isString(attrValue) && isRef(attrName, attrValue)) {
                var object = getRef(attrName, attrValue);
                //stack.push(attrName + "[" + attrValue + "]");
		if(object == null) object = {"unresolved" :  "[" + attrValue + "]"};
                stack.push(attrName);
		walker(object, stack , collector, depth + 1);
	        stack.pop();
	    }
	    else if(isString(attrValue) || isNumeric(attrValue) || isBoolean(attrValue)|| isDate(attrValue) ) { 
	            var qualifiedAttrName = stack.join(".") + "." + attrName;
	            if(attrName != 'padding' && (fieldNameRegex == null || fieldNameRegex.test(qualifiedAttrName)))
		        collector[qualifiedAttrName] = attrValue;
	    }
	    else if(attrValue instanceof Array) {
		    var copyAttrName = attrName;
		    for(var i = 0; i < attrValue.length; i++) {
			    var arrayElementName = copyAttrName + "[" + i + "]";
			    var arrayElement = {};
			    arrayElement[arrayElementName] = attrValue[i]; 
			    walker(arrayElement, stack, collector, depth + 1);
		    }
	    } else if(attrValue instanceof Object) {
                    stack.push(attrName);
		    walker(attrValue, stack , collector, depth + 1);
	            stack.pop();
	    }
	    else {
	        //not interested!
		//print ("Ignoring:" + attrName + " " + attrValue + "\n");
	    }
	}
	if(depth == 0) {
		var valueList = new Array();
		for(var name in collector) {
			valueList.push(name + "=" + collector[name]);
			//valueList.push(collector[name]);
		}
		valueList.sort();
		print(valueList.join("\n"));
		//print(valueList.join(","));
		print();
		documentCount++;
	}
}

function isRef(attrName, attrValue) {
	var isRef = false;
	if(attrName != "_id" 
	 && attrValue.indexOf('-') == 6
	 && attrValue.replace(/-/g,"").length == 38) {
		isRef = true;
 	} 
	return isRef;
}

function getRef(collectionName, objectId) {
	collectionName=collectionName.replace(/Id.*/, "");
	collectionName=collectionName.replace(/Reference.*/, "");
	collectionName=collectionName.replace(/learningStandards.*/, "learningStandard");
        var collectionMapping = {
	                        'teacher':'staff', 
              	                 'school':'educationOrganization', 
		           'educationOrg':'educationOrganization',
                   'responsibilitySchool':'educationOrganization',
		       'assignmentSchool':'educationOrganization',
                  'parentEducationAgency':'educationOrganization',
                      'learningStandards':'learningStandard'
		                };
        var renamed = collectionMapping[collectionName];
	if(renamed != null) collectionName = renamed;
        var found = null;
	try{
		found = db.getCollection(collectionName).findOne({'_id' : objectId}); 
	}catch (err) {unresolvedRefs.push(collectionName + ":" + objectId)}
	if(found == null) unresolvedRefs.push(collectionName + ":" +  objectId);
	else resolvedRefsCount++;
	return found;
}

function isNumeric(input) {
	return !isNaN(parseFloat(input));
}

function isString(input) {
	return input.substring != null;
}

function isBoolean(input) {
	return typeof input == "boolean" ;
}

function isDate(input) {
	return input instanceof Date;
}


print(unresolvedRefs.join("\n"));
print ("stats,"   + collectionName 
                  + ",Count:" + documentCount  
                  + ",Internal Refs. Resolved:" 
		  + resolvedRefsCount 
		  + ",Internal Refs. Unresolved:" 
		  + unresolvedRefs.length ); 

