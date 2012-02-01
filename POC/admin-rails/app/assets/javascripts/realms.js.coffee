# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/

`getTableData = function(json) {
console.log(json);
        toReturn = []
        for (var sliRole in json) {
                for (var j in json[sliRole]) {
                        toReturn.push([json[sliRole][j], sliRole])
                }
        }
        return toReturn
}`


`sortTable = function(data, col, order) {

        data.sort(function(a, b) {
                if (a[col].toLowerCase() > b[col].toLowerCase())
                        return 1 * order;
                if (a[col].toLowerCase() < b[col].toLowerCase())
                        return -1 * order;

                //equal - sort by other col
                if (col == 1)
			col = 0;
		else
			col = 1;
                if (a[col].toLowerCase() > b[col].toLowerCase())
                        return 1 * order;
                if (a[col].toLowerCase() < b[col].toLowerCase())
                        return -1 * so;
                return 0;
                
        });
}`

`drawTable = function(data, editable) {
        var table = $("#mTable"); 
        table.empty();

        for (var i in data) {
		var tr = $("<tr>");
        	tr.append($("<td>" + data[i][0] +  "<td>" + data[i][1] +  "</td>"));
		if (editable) {
			tr.append("<td><button class='db'>X</button>");
        		$(".db").each(function() {
                		$(this).click(function() {
                       		$(this).parent().parent().remove();
                		});
			});
		}	
		table.append(tr);
        }

}
`
