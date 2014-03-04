jQuery(function($) {
  $(document).on('click', '.count_link', function(event) {
	  var element = event.target;
	  var url = $(event.target).data("url");
	  var count = "";
	  
	  // Set text to nothing to be ready for appending
	  element.text = "";
	  element.innerHTML = "<img class=loader src='/assets/ajax-loader.gif' />";
	  
	  // Get the total count
	  count = count + get_counts(element, url);
	  
	  // Add a spacing marker
	  count = count + " / ";
	  
	  // Change url to get currentOnly and then get current counts
	  url = url + "&currentOnly=true";
	  count = count + get_counts(element, url);
	  
	  element.innerHTML = count;
  })
});

/*
 * Used to perform the ajax query to get the count data back from the api
 * using Databrowser as a proxy.
 */
function get_counts(element, url) {
	var text = null;
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        dataType: "JSON" 
    }).success(function(data) {
    	//var text = null;
    	if (data.entities === undefined) {
    		text = data.alert
    	} else {
    		text = data.headers.totalcount;
    	}
    	//$(element).append(text);
    }).error(function(data) {
    	if (data.alert === undefined) {
    		text = "error"
    		$(element).append("error");
    	} else {
    		text = "alert"
    		$(element).append(data.alert);
    	}
    });
    return text;
}