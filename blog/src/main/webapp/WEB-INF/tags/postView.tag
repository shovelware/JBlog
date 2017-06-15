<%@attribute name="post" required="true" type="nl.cerios.blog.database.PostDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="box post">
	<div class="timestamp" style="float:right">Posted at <t:localDate date="${post.timestamp}"/></div>
	<a href="${pageContext.request.contextPath}/blog?id=${post.blogId}">blog</a> 
	<t:ifLoggedIn><a href='${pageContext.request.contextPath}/post/edit?id=${post.id}'>edit</a></t:ifLoggedIn>
	<a href="${pageContext.request.contextPath}/post?id=${post.id}"><h2>${post.title}</h2></a><br/>
	<div class="postcontent">${post.text}</div>
</div>