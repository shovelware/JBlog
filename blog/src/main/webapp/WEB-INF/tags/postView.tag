<%@attribute name="post" required="true" type="nl.cerios.clog.object.PostDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="box post">
	<div class="timestamp" style="float:right">Posted at <t:localDate date="${post.timestamp}"/></div>
	<a href="${pageContext.request.contextPath}/blog?id=${post.blogId}">blog</a> | <a href="${pageContext.request.contextPath}/profile?id=${post.profileId}">profile</a> 
	<t:ifLoggedInOwner ownerId="${post.profileId}"> | <a href='${pageContext.request.contextPath}/post/edit?id=${post.id}'>edit</a></t:ifLoggedInOwner>
	<h2><a href="${pageContext.request.contextPath}/post?id=${post.id}">${post.title}</a></h2><br/>
	<div class="postcontent">${post.text}</div>
</div>