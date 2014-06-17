<?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/assets/bootstrap/3.1.1/css/bootstrap.min.css" media="all"/>
</head>
<body>
<nav class="navbar navbar-inverse navbar-static-top" role="navigation">
    <div class="container">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle Navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="/">inBloom Portal</a>
    </div>
</nav>

<div class="container">

    <div class="row">
        <div class="col-lg-12">
            <h2>Admin Tools</h2>
            <ul>
                <li><a href="${adminUrl}">Admin Tools</a></li>
            </ul>
            <h2>Data Tools</h2>
            <ul>
                <li><a href="${dataBrowserUrl}">Data Browser</a></li>
                <li><a href="${dashboardUrl}">Dashboards</a></li>
            </ul>
        </div>
    </div><!--/row-->

    <footer>
        <hr/>
        <p>&copy; Company 2014</p>
    </footer>

</div> <!-- /container -->


</body>
</html>
