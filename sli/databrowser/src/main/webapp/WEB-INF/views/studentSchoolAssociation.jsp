<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page session="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript"
	src="/databrowser/resources/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript">
	
	function post_action(action, associationId, studentId, schoolId, entryGradeLevel) {
		var form = $('<form action="studentschoolassociationmenu.html" method="POST">' + 
			'<input type="hidden" name="action" value="' + action + '">' +
			'<input type="hidden" name="schoolId" value="' + schoolId + '">' +
			'<input type="hidden" name="studentId" value="' + studentId + '">' +
			'<input type="hidden" name="entryGradeLevel" value="' + entryGradeLevel + '">' +
			'<input type="hidden" name="associationId" value="' + associationId + '">' +
			'<input type="hidden" name="filterSchool" value="${filterSchool}">' +
			'<input type="hidden" name="filterStudent" value="${filterStudent}">' +
			'</form>');
		form.appendTo('body');
		form.submit();
	}
	
	function click_add() {
		var studentId = $('#addStudentId').val();
		var schoolId = $('#addSchoolId').val();
		var entryGrade = $('#addEntryGradeLevel').val();
		post_action('add', -1, studentId, schoolId, entryGrade);
	}
	
	function click_update(associationId) {
		var studentId = $('#' + associationId + "_studentId").val();
		var schoolId = $('#' + associationId + "_schoolId").val();
		var entryGrade = $('#' + associationId + "_entryGradeLevel").val();
		post_action('update', associationId, studentId, schoolId, entryGrade);
	}
	
	function click_delete(associationId) {
		var studentId = $('#' + associationId + "_studentId").val();
		var schoolId = $('#' + associationId + "_schoolId").val();
		var entryGrade = $('#' + associationId + "_entryGradeLevel").val();
		post_action('delete', associationId, studentId, schoolId, entryGrade);
	}
	
	function refresh_filter() {
		var studentId = $("#filterStudent").val();
		var schoolId = $("#filterSchool").val();
		var query = "?schoolId=" + schoolId + "&studentId=" + studentId;
		window.location = query;
	}
	
	$(document).ready(function() {
		
	});
</script>

<style type="text/css">
body {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
}

#associationTable {
	padding: 2px;
	text-align: center;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
	font-weight: normal;
	border: 2px solid #000000;
	margin-top: 10px;
}

.studentId,.schoolId,.associationId,.entryGradeLevel,.entryDate {
	height: auto;
	width: auto;
	padding: 5px;
	border: 1px solid #000000;
}

select.add {
	color: blue;	
}


input {
	padding: 3px;
	width: 300px;
	text-align: center;
	border: 1px solid #ffffff;
	vertical-align: middle;
}

#schoolfullname,#shortname {
	border: 1px solid #000000;
}

input:focus {
	background-color: yellow;
	border: 1px solid #000000;
}

.id {
	height: auto;
	width: 300px;
	padding: 5px;
	text-align: center;
	border: 1px solid #000000;
}

.action {
	height: auto;
	width: 150px;
	padding: 5px;
	text-align: center;
	border: 1px solid #000000;
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
}

.action a {
	text-decoration: none;
}

.action a:hover {
	color: red;
	text-decoration: underline;
}

#error {
	color: red;
}
</style>
<title>Student School Association</title>
</head>
<body>
	<h1>Student School Association Menu</h1>

	<h2 id="error">${errorMessage}</h2>

	<table class="filterTable">
		<tr>
			<td><label for="filterStudent">Filter by Student:</label> <select onchange="refresh_filter()" id="filterStudent">
					<option value='-1'></option>
					<c:forEach items="${students}" var="student">
						<option id="${student.studentId}_filterStudent" value="${student.studentId}"
							${student.studentId==filterStudentId ? "SELECTED" : ""}>${student.firstName}
							${student.lastSurname} : ${student.studentId}</option>
					</c:forEach>
			</select></td>
			<td><label for="filterSchool">Filter by School:</label> <select id="filterSchool" onchange="refresh_filter()">
					<option value='-1'></option>
					<c:forEach items="${schools}" var="school">
						<option id="${school.schoolId}_filterSchool" value="${school.schoolId}"
							${school.schoolId==filterSchoolId ? "SELECTED" : ""}>${school.fullName} : ${school.schoolId}</option>
					</c:forEach>
			</select></td>
		</tr>
	</table>

	<table id="associationTable">
		<tr>
			<th class="studentId">Student</th>
			<th class="schoolId">School</th>
			<th class="entryGradeLevel">Grade Level</th>
			<th class="entryDate">Entry Date</th>
			<th class="associationId">Association ID</th>
			<th class="action">Action</th>
		</tr>
		<c:forEach items="${associations}" var="association">
			<tr class="associationRecord">
				<td class="studentId">
					<select id="${association.associationId}_studentId">
						<c:forEach items="${students}" var="student">
							<option value="${student.studentId}" ${student.studentId==association.studentId ? "SELECTED" : ""}>
							${student.firstName} ${student.lastSurname} : ${student.studentId}</option>
						</c:forEach>
					</select>
				</td>
				<td class="schoolId">
					<select id="${association.associationId}_schoolId">
						<c:forEach items="${schools}" var="school">
							<option value="${school.schoolId}" ${school.schoolId==association.schoolId ? "SELECTED" : ""}>
							${school.fullName} : ${school.schoolId}</option>
						</c:forEach>
					</select>	
				</td>
				<td class="entryGradeLevel">
					<select id="${association.associationId}_entryGradeLevel">
						<c:forEach items="${gradeLevels}" var="grade">
							<option value="${grade}" ${grade==association.entryGradeLevel ? "SELECTED" : ""}>${grade.value}</option>
						</c:forEach>
					</select>
				</td>
				<td class="entryDate" id="${association.associationId}_entryDate" />
					<fmt:formatDate value="${association.entryDate.time}" dateStyle="SHORT"/>
				</td>
				<td class="associationId" id="${association.associationId}_associationId" />
					${association.associationId}
				</td>
				<td class="action">
					<a id="${association.associationId}_update" href="#" onclick="click_update(${association.associationId})">Update</a>
					/
					<a id="${association.associationId}_delete" href="#" onclick="click_delete(${association.associationId})">Delete</a>
				</td>
			</tr>
		</c:forEach>

		<tr>
			<td class="studentId">
				<select class="add" id="addStudentId">
					<c:forEach items="${students}" var="student">
						<option value="${student.studentId}" ${student.studentId==filterStudentId ? "SELECTED" : ""}>
						${student.firstName} ${student.lastSurname} : ${student.studentId}</option>
					</c:forEach>
				</select>
			</td>
			<td class="schoolId">
				<select class="add" id="addSchoolId">
					<c:forEach items="${schools}" var="school">
						<option value="${school.schoolId}"  ${school.schoolId==filterSchoolId ? "SELECTED" : ""}>
						${school.fullName} : ${school.schoolId}</option>
					</c:forEach>
				</select>	
			</td>
			<td class="entryGradeLevel">
				<select class="add" id="addEntryGradeLevel">
					<c:forEach items="${gradeLevels}" var="grade">
						<option value="${grade}">${grade.value}</option>
					</c:forEach>
				</select>
			</td>
			<td>
			</td>
			<td>
			</td>
			<td class="action">
				<a class="add" id="addAssociation" href="#" onclick="click_add()">Add</a>
			</td>
		</tr>
	</table>
<br>
<br>
<div id="siteNav">
	<button id="navHome" type="button" onClick="window.location.href='/databrowser'">Back to Home</button>
</div>
</body>
</html>