<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="ownerId" required="true" type="java.lang.Integer"%>
<c:if test="${(not empty loggedInUser) and(loggedInUser.id == ownerId)}"><jsp:doBody/></c:if>