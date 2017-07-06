<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="blog" required="true" type="nl.cerios.clog.object.BlogDTO"%>

<c:set var="title" value="${(empty blog.title) ? '' : blog.title}" />
<c:set var="description" value="${(empty blog.description) ? '' : blog.description}" />

<div class=form id=blogform>
	<form method="POST"
		action="${pageContext.request.contextPath}/blog/resubmit">
		<input type="hidden" name="profileId" value="${blog.profileId}">
		<input type="hidden" name="blogId" value="${blog.id}">
		
		<h2>EDIT BLOG</h2>
		Title<br/>
		<input placeholder="Title" name="title" value="${title}" autofocus></input> <br />
		Description<br />
		<textarea placeholder="Description" rows="6" cols="49" name="description">${description}</textarea>
		<br /> <input value="SUBMIT" type="SUBMIT">
	</form>
</div>
