<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="blogs" required="true" type="java.util.List"%>

<div class=form id=postform>
	<form method="POST" action="${pageContext.request.contextPath}/post/submit">
		<input type="hidden" name="profileId" value="${loggedInUser.id}">
		<h2>WRITE A POST</h2>
		
		Blog<br />
		<select name=blogId>
			<c:forEach items="${blogs}" var="blog">
				<option value='${blog.id}'>${blog.title}</option>
			</c:forEach>	
		</select>
		
		<br /><br />Title<br />
		<input placeholder="Post Title" name="title" autofocus></input>
		
		<br /><br />Text<br />
		<textarea placeholder="Post Content" rows="6" cols="49" name="text"></textarea>
		
		<br /><br/><input value="SUBMIT" type="SUBMIT">
	</form>
</div>