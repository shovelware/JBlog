<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@attribute name="blogs" required="true" type="java.util.List"%>

<c:forEach items="${blogs}" var="blog">
	<t:blogView blog="${blog}"/>
	<br/><br/>
</c:forEach>