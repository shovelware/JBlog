<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="View Profile">
<jsp:body>
	<t:profileView profile="${profile}"/>
	
	<c:choose>
		<c:when test="${not empty blogs}">
			<t:blogViewList blogs = "${blogs}"/>
		</c:when>
		
		<c:when test="${not empty blog}">
			<t:blogView blog = "${blog}"/>
		</c:when>
		
		<c:otherwise>
			<div class="box">This user has no blogs.</div>
		</c:otherwise>
	</c:choose>
	
</jsp:body>
</t:template>