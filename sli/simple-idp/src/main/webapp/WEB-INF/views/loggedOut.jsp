<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title>Simple IDP</title>
<link rel="icon" type="image/x-icon" href="resources/favicon.ico"/>
<style type="text/css">

.error-container {
	margin-left: auto;
	margin-right: auto;
	width: 800px;
}

.error-header {
	padding-left: 0;
	padding-top: 12px;
	line-height: 28px;
	border-bottom: 2px solid #CCCCCC;
	color: #D8D8D8;
	font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;
	font-size: 36px;
	font-weight: normal;
}

.error-content {

}
</style>
<link href="resources/bootstrap.css" rel="stylesheet"/>
</head>

<body>
		<div class="error-container">
		<div class="error-header">Message</div>
		<div class="error-content">
			<div class="panel-header">
				You are now logged out.
			</div>
		</div>
	</div>
</body>
</html>
