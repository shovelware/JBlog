<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="header" class="bar">
	<a href="${pageContext.request.contextPath}/index"> Go Home! <%-- evt. home, my blog, my page links etc. --%></a>

	<%-- Login/My Page/Logout --%>
	<c:if test="${not empty sessionScope.loggedInUser}">
		<form style="float: right"  method="post" action="${pageContext.request.contextPath}/logout" class="inline">
			Welcome, <a href="${pageContext.request.contextPath}/profile/me">${sessionScope.loggedInUser}</a>.
			<button type="submit" name="submit_param" value="submit_value" class="linkbutton">Logout</button>
		</form>
	</c:if>
	<c:if test="${empty sessionScope.loggedInUser}">
		<div style="float: right">
			<a href="${pageContext.request.contextPath}/login">Login</a> or <a href="${pageContext.request.contextPath}/profile/new">Sign Up</a>
			</div>
	</c:if>
</div>

