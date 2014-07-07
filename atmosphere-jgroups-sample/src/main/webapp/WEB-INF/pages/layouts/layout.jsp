<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
		<title><tiles:insertAttribute name="title" /></title>
		<tiles:insertAttribute name="header" />	
	</head>
	<body>
		<tiles:insertAttribute name="body" />
		<tiles:insertAttribute name="footer" />
	</body>
</html>