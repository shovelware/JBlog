<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="owner" required="true" type="java.lang.String"%>
<c:if test="${(not empty loggedInUser) and(loggedInUser == owner)}"><jsp:doBody/></c:if>