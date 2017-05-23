<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="header" class="bar" style="color: #220088">

	<!-- Logo -->
	<a style="text-align: left"
		href="${pageContext.request.contextPath}/index"> Go Home! <!-- evt. home, my blog, my page links etc. -->
	</a>

	<!-- Login/My Page/Logout -->
	<c:if test="${not empty sessionScope.loggedInUser}">
	<a style="float: right" 
		href="${pageContext.request.contextPath}/logout">Log Out ${sessionScope.loggedInUser}
	</a>
	</c:if>

	<c:if test="${empty sessionScope.loggedInUser}">
		<a style="float: right"
			href="${pageContext.request.contextPath}/login"> <!-- probably /login.do/ or something -->
			Login
		</a>
	</c:if>

</div>