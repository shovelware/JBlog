<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty loggedInUser}"><jsp:doBody/></c:if>