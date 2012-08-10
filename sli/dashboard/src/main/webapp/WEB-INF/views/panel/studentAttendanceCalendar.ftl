<#assign id = getDivId(panelConfig.id)>

<div id="${id}" class="attendanceCalendar"></div>

<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/SLC.attendanceCalendar.js"></script>
<script>
	$(function() {
	    SLC.attendanceCalendar.create("${id}", SLC.dataProxy.getData("studentAttendanceCalendar"));
	    $("#${id}").appendTo(".repeatHeaderTable1");
	});
</script>