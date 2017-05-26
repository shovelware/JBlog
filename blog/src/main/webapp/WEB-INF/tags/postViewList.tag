<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="posts" required="true" type="java.util.List"%>

<c:forEach items="${posts}" var="post">
	<t:postView post="${post}"/>
	<br/><br/>
</c:forEach>