<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@ attribute name="title"%>
<c:set var="title" value="${(empty title) ? 'Cerios Blogging' : title}" />

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<c:url value="/favicon.ico" var="favicon" />
	<c:url value="/static/base.css" var="css" />
	
	<link rel="icon" type="image/x-icon" href="${favicon}"/>
	<link rel="stylesheet" href="${css}" />
	
	<title>${title}</title>
</head>
<body>
	<div id=top></div>
	<t:header/>
	<t:navigation/>
		<jsp:doBody />
	<t:footer/>
	<div id=bottom></div>
</body>
</html>