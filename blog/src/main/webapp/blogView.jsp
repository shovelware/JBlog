<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<t:template title="View Blog">
<jsp:body>
	<t:blogView blog="${blog}"/>
	<c:choose>
		<c:when test="${not empty posts}">
			<t:postViewList posts="${posts}"/>
		</c:when>
		<c:otherwise>
			<t:messageBox message="No posts to display!"/>
		</c:otherwise>
	</c:choose>
</jsp:body>
</t:template>