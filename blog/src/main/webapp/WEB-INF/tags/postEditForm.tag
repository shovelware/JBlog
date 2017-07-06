<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="post" required="true" type="nl.cerios.clog.object.PostDTO"%>

<c:set var="title" value="${(empty post.title) ? '' : post.title}" />
<c:set var="text" value="${(empty post.text) ? '' : post.text}" />

<div class=form id=postform>
	<form method="POST"	action="${pageContext.request.contextPath}/post/resubmit">
		<input type="hidden" name="profileId" value="${post.profileId}">
		<input type="hidden" name="blogId" value="${post.blogId}">
		<input type="hidden" name="postId" value="${post.id}">
		<input type="hidden" name="timestamp" value="${post.timestamp}">
		<h2>EDIT POST</h2>
		
		Title<br/>
		<input placeholder="Title" name="title" value="${title}" autofocus></input>
		
		<br /><br />Text<br/>
		<textarea placeholder="Post Content" rows="6" cols="49" name="text">${text}</textarea>
		
		<br /><br /><input value="SUBMIT" type="SUBMIT">
	</form>
</div>
