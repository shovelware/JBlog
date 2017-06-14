<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<div id="navigation" class="bar">
	<a href="${pageContext.request.contextPath}/index">index</a>
	|
	<a href = "${pageContext.request.contextPath}/about">about</a>
	|
	<a href = "${pageContext.request.contextPath}/recent">recent</a>
	|
	<a href = "${pageContext.request.contextPath}/post/find">post</a>
	<t:ifLoggedIn><a href = "${pageContext.request.contextPath}/post/new">new</a></t:ifLoggedIn>
	|
	<a href = "${pageContext.request.contextPath}/blog/find">blog</a>
	<t:ifLoggedIn><a href = "${pageContext.request.contextPath}/blog/me">me</a></t:ifLoggedIn>
	<t:ifLoggedIn><a href = "${pageContext.request.contextPath}/blog/edit">edit</a></t:ifLoggedIn>
	|
	<a href = "${pageContext.request.contextPath}/profile/find">profile</a>
	<t:ifLoggedIn><a href = "${pageContext.request.contextPath}/profile/me">me</a></t:ifLoggedIn>
	<t:ifLoggedIn><a href = "${pageContext.request.contextPath}/profile/edit">edit</a></t:ifLoggedIn>
	|
	<div style="float: right"><a  href = "${pageContext.request.contextPath}/http401.jsp"> 401</a>
		<a href = "${pageContext.request.contextPath}/http403.jsp"> 403</a>
		<a href = "${pageContext.request.contextPath}/http404.jsp"> 404</a>
		<a href = "${pageContext.request.contextPath}/http500.jsp"> 500</a>
		<a href = "${pageContext.request.contextPath}/httperr.jsp"> err</a>
	</div>
</div>

