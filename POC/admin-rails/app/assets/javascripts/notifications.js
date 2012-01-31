
//Wire up the 'clear' button in the notification area
$(document).ready(function() {
	$("#messageContainer > a").click(function() {
		$("#messages > div").remove();
               	$("#messageContainer").hide();
        });
});

//append message to notification area
function notify(text, type) {
	$("#messageContainer").show();
	$("#messages").append( $("<div style='display: none'></div>").addClass(type).text(text).fadeIn() );
}

function notifyError(text) {
	notify(text, "errorNotification");
}

function clearNotifications() {

}
