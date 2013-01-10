<html>
<body>
 <table border="1">
      <tr>
        <th>Name</th>
        <th>Aggregate</th>
        <th>Individual Record Data</th>
        <th>General Student Data</th>
        <th>Restricted Student Data</th>
      </tr>
	<g:each in="${roles}" status="i" var="role">
        <tr>
          <td>${role.name}</td>
          <td>
                <g:if test="${role.aggregate==true}">
                        <span>Yes</span>
                </g:if>
                <g:else>
                        <span>No</span>
                </g:else>
          </td>
          <td></td>
          <td>${role.general}</td>
          <td>${role.restricted}</td>
        </tr>

	</g:each>
</body>
</html>
