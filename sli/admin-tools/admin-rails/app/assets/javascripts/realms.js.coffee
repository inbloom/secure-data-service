# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/

# Converts the data returned by the realm API into a simpler object
# that's basically an array of table rows, the first column being
# the client role and the second being the SLI role.
# e.g. [ ['foo', 'Educator'], ['bar', 'Leader'] ]
`getTableData = function(json) {
        toReturn = [];
        for (var i in json.role) {
          var roleData = json.role[i];
          sliRole = roleData.sliRoleName;
          for (var j in roleData.clientRoleName) {
            toReturn.push([roleData.clientRoleName[j], sliRole]);
          }
        }
        return toReturn
}`

`extractData = function() {
	var map = {};
	$("#mTable > tr").each(function () {
		var cRole = $(this).children("td:eq(0)")[0].innerText;		
		var sRole = $(this).children("td:eq(1)")[0].innerText;		
		if (!map[sRole])
			map[sRole] = [];
		map[sRole].push(cRole);
	});
	return map;
}
`

# Takes our table data and converts it into the mapping object
# needed by the API.
`mapData = function(data) {
	var map = {};
	var role = [];
	map.role = role;
	for (var i in data) {
		var cRole = data[i][0];
		var sliRole = data[i][1];
		var sliRoleArray = getSliRoleObject(sliRole, role);
		if (sliRoleArray == null) {
		  sliRoleArray = {};
		  sliRoleArray.sliRoleName = sliRole;
		  sliRoleArray.clientRoleName = [];
		  role.push(sliRoleArray);
		}
		sliRoleArray.clientRoleName.push(cRole);
	}
	return map;
}`

# Helper function used by mapData
`
getSliRoleObject = function(sliRole, roleData) {
  for (var i in roleData) {
    if (roleData[i].sliRoleName == sliRole) {
      return roleData[i];
    }
  }
  return null;
}
`
`sortTable = function(data, col, order) {

        data.sort(function(a, b) {
                if (a[col].toLowerCase() > b[col].toLowerCase())
                        return 1 * order;
                if (a[col].toLowerCase() < b[col].toLowerCase())
                        return -1 * order;
		return 0;
        });
}`

`drawTable = function(data, editable) {
        var table = $("#mTable"); 
        table.empty();

        for (var i in data) {
		var tr = $("<tr style='display: none'>");
        	tr.append($("<td>" + data[i][0] +  "<td>" + data[i][1] +  "</td>"));
		if (editable) {
			tr.append("<td><button class='deleteButton'>X</button></td>");
		}	
		//fade in last one
		table.append(tr);
		tr.show();
		if (editable) {
        		$(tr).find(".deleteButton").each(function() {
                		$(this).click(function() {
                       		//$(this).parent().parent().remove()
					$(this).trigger("deleteRow");
                		});
			});
		}
        }

}
`
