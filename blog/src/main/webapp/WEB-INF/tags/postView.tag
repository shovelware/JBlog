<%@attribute name="post" required="true" type="nl.cerios.blog.database.PostDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="box">
	<div class="timestamp" style="float:right">${post.timestamp}</div>
	<a href="${pageContext.request.contextPath}/blog?id=${post.blogId}">blog</a> 
	<a href="${pageContext.request.contextPath}/post?id=${post.id}">post</a> 
	<a href="${pageContext.request.contextPath}/post/edit?id=${post.id}">edit</a>
	<h2>${post.title}</h2> <br/>
	<div class="postcontent"><c:out value="${post.text}"></c:out></div>
</div>