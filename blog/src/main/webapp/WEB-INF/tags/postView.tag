<%@attribute name="post" required="true" type="nl.cerios.blog.database.PostDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="box">
	<div class="timestamp" style="float:right"><t:localDate date="${post.timestamp}"/></div>
	<a href="${pageContext.request.contextPath}/blog?id=${post.blogId}">blog</a> 
	<a href="${pageContext.request.contextPath}/post?id=${post.id}">post</a> 
	<t:ifLoggedIn><a href='${pageContext.request.contextPath}/post/edit?id=${post.id}'>edit</a></t:ifLoggedIn>
	<h2>${post.title}</h2> <br/>
	<div class="postcontent">${post.text}</div>
</div>