# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/

`getTableData = function(json) {
        toReturn = []
        for (var sliRole in json) {
                for (var j in json[sliRole]) {
                        toReturn.push([json[sliRole][j], sliRole])
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
`mapData = function(data) {
	var map = {};
	for (var i in data) {
		var cRole = data[i][0];
		var sliRole = data[i][1];
		if (!map[sliRole])
			map[sliRole] = [];
		map[sliRole].push(cRole);
	}
	return map;
}`

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
