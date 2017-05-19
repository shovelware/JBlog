<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>${param.title}</title>
<c:url value="/static/base.css" var="css" />
<link rel="stylesheet" href="${css}" />
</head>
<body>

	<jsp:include page="m_header.jsp" />
	<jsp:include page="m_nav.jsp" />
	
	<jsp:include page="${param.content}" />

	<jsp:include page="m_footer.jsp" />

</body>
</html>