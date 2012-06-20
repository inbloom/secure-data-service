/*  
 * SLC global namespace
 * http://www.wirelessgeneration.com/
 * Proprietary Information (c) 2012 SLC, LLC
 */


var SLC = SLC || {};

/*
 * Description: This function creates namespaces under SLC.
 * @param ns_string - namespace string
 * @param assignee - the function which can be assigned to the namespace
 * @return parent object
 * Example: SLC.namespace('SLC.modules.module1');
 */
SLC.namespace = function (ns_string, assignee) {
	var parts = ns_string.split('.'),
		parent = SLC,
		i;
	
	if (parts[0] === "SLC") {
		parts = parts.slice(1);
	}
	
	for (i = 0; i < parts.length; i+=1) {
		if (typeof parent[parts[i]] === "undefined") {
			parent[parts[i]] = {};
			
			if (assignee) {
				parent[parts[i]] = assignee;
			}
		}
		parent = parent[parts[i]];
	}
	
	return parent;
};

