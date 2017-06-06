<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@ attribute name="title"%>
<c:set var="title" value="${(empty title) ? 'Cerios Blogging' : title}" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:url value="/static/base.css" var="css" />
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