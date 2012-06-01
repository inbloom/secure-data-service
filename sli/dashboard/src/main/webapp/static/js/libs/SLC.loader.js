/*global $*/

// 	Description: This function creates "loading" graphic" jquery widget for SLC
//	Example: w_studentListLoader = $("<div></div>").loader();
//			 w_studentListLoader.show();
$.widget( "SLC.loader", {
	
	options: {
		message: "Loading..."
	},
	
    _create: function () {
        var message = this.options.message;
        this.element
            .addClass( "loader" )
            .html("<div class='message'>" + message + "</div>")
            .appendTo("body");
    },
    
    message: function (message) {
        if ( message === undefined || typeof message !== "string" ) {
            return this.options.message;
        } else {
            this.options.message = message;
            this.element.find(".message").html(this.options.message);
        }
    }
});