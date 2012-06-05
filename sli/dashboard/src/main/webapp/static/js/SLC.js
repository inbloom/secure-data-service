var SLC = SLC || {}; // Creates SLC global namespace

// Description: This function creates namespaces under SLC.
// Example: SLC.namespace('SLC.modules.module1');
SLC.namespace = function (ns_string, func) {
	var parts = ns_string.split('.'),
		parent = SLC,
		i;
	
	if (parts[0] === "SLC") {
		parts = parts.slice(1);
	}
	
	for (i = 0; i < parts.length; i+=1) {
		if (typeof parent[parts[i]] === "undefined") {
			parent[parts[i]] = {};
			
			if (func) {
				parent[parts[i]] = func;
			}
		}
		parent = parent[parts[i]];
	}
	
	return parent;
};
