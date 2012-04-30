<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>

<body OnLoad="OnLoadEvent();">
<form action="${samlResponse.redirectUri}" method="post" name="form1">
    <input type="hidden" name="SAMLResponse" value="${samlResponse.samlResponse}"  />
</form>
<script>

function OnLoadEvent() {
   document.form1.submit();
}
</script>