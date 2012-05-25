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
		if (data[i][1] == 'SLI Administrator') {
			continue	
		}
		var tr = $("<tr style='display: none'>");
        	tr.append($("<td>" + data[i][0] +  "<td>" + data[i][1] +  "</td>"));

		if (editable) {
			tr.append("<td><button class='btn btn-danger deleteButton'>X</button></td>");
		}	
		//fade in last one
		table.append(tr);
		// We typically wouldnt expect a user to remap sli admin, so dont show in list
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

`initRealmEdit = function() {
	//check first radio button by default
        $('input:radio[name=role]')[0].click();

	drawTable(rMap, true);
        
        save = function(success, failure) {
                var mData = mapData(rMap);      
                if (!failure) {
                        failure = jQuery.noop();
                }
                $.ajax({
                        url: UPDATE_URL,
                        type: 'PUT',
                        contentType: 'application/json',
                        data: JSON.stringify({"mappings": mData}),
                        dataType: 'json',
                        success: success,
                        error: function (resp) {
                                notifyError(jQuery.parseJSON(resp.responseText).response);
                                failure.call();
                        }
                
                });
        }


       $("#addButton").click(function() {
                var cRoleName = $("#clientRole").val().trim();
                if (!cRoleName.match(/^\w+(\s\w+)*$/)) { //at least one non-space characters, spaces allowed elsewhere
                  notifyError("Role name must contain only alphanumeric characters.");
                  return;
                }
                rMap.push([cRoleName, $('input:radio[name=role]:checked').val()]);
                var success = function(data) {
                        rMap = getTableData(data.mappings);
                        $("#clientRole").val("")
                        sortTable(rMap, sortCol, sortOrder[sortCol]);
                        drawTable(rMap, true);
                }
                var failure = function() {
                        rMap.pop(); //it failed, so undo it     
                }
                save(success, failure);
        });

	//Called when a delete button is clicked
        //Figure out which item was deleted, remove it from the rMap, attempt to save
        $(document).bind("deleteRow", function(e) {
                var cRole = $(e.target).closest("tr").children("td:eq(0)").text();
                var sliRole = $(e.target).closest("tr").children("td:eq(1)").text();
                for (var i in rMap) {
                        if (rMap[i][0] == cRole) {
                                rMap.splice(i, 1);
                                break;
                        }
                }
                var success = function(data) {
                        rMap = getTableData(data.mappings);
                        sortTable(rMap, sortCol, sortOrder[sortCol]);
                        drawTable(rMap, true);
                }
                var error = function() {
                        //put value back in the data
                        rMap.push([cRole, sliRole]); 
                        sortTable(rMap, sortCol, sortOrder[sortCol]);
                        drawTable(rMap, true);
                }
                save(success, error);
        });

        refreshSortIcon = function(idx) {
                if (sortOrder[idx] == 1) {
                        $('.sort_desc').hide();
                        $('.sort_asc').hide();
                        $('.sort_desc:eq(' + idx + ')').fadeIn();
                } else {
                        $('.sort_desc').hide();
                        $('.sort_asc').hide();
                        $('.sort_asc:eq(' + idx + ')').fadeIn()
                }
        }

        $("#cRole").click(function() {
                sortCol = 0;
                sortOrder[0] *= -1;
                refreshSortIcon(0);
                sortTable(rMap, sortCol, sortOrder[0]);
                drawTable(rMap, true);    
        });

        $("#sRole").click(function() {
                sortCol = 1;
                sortOrder[1] *= -1;
                refreshSortIcon(1);
                sortTable(rMap, sortCol, sortOrder[1]);
                drawTable(rMap, true);    
        });

        $("#resetButton").click(function() {
		if (confirm("Are you sure you want to reset the role mappings?")) {
                rMap = [];
                for (var i in SLI_ROLES) {
                      rMap.push([SLI_ROLES[i], SLI_ROLES[i]]);
                }
                var success = function(data) {
                        rMap = getTableData(data.mappings);
                        sortTable(rMap, sortCol, sortOrder[sortCol]);
                        drawTable(rMap, true);
                }
                save(success);
		}
        });

}

`
