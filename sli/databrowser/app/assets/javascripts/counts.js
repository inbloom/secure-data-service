jQuery(function($) {
  $(document).on('click', '.count_link', function(event) {
	  var element = event.target;
	  var url = $(event.target).data("url");
	  var include_current = $(event.target).data("include_current");
	  var count = "";
	  
	  // Set text to nothing to be ready for appending
	  element.innerHTML = "<img class=loader src='/assets/ajax-loader.gif' />";
	  
	  // Get the total count
	  count = count + get_count(element, url);
alert(url);	  
	  if (include_current) {
		  // Add a spacing marker
		  count = count + " / ";
		  
		  // Change url to get currentOnly and then get current counts
		  url = url + "&currentOnly=true";
		  count = count + get_count(element, url);
          }
	
	  element.innerHTML = count;
  })
});

/*
 * Used to perform the ajax query to get the count data back from the api
 * using Databrowser as a proxy.
 */
function get_count(element, url) {

	var text = null;
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        dataType: "json" 
    }).success(function(data) {
    	//var text = null;
    	if (data.entities === undefined) {
    		text = "N/A"
        } else if (data.total != undefined) {
            text = data.total
    	} else {
    		text = data.headers.totalcount;
    	}
    	//$(element).append(text);
    }).error(function(data) {
    	text = "Error";
    });
    return text;
}
