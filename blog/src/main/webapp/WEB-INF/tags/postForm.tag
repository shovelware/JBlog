<%@attribute name="blogId" required="true"%><%--Replace with retreival of id's belonging to profile, dropdown --%>
<%@attribute name="profileId" required="true"%>

<div class=form id=postform>
	<form method="POST" action="${pageContext.request.contextPath}/post/submit">
	<input type="hidden" name="profileId" value="${profileId}">
	<input type="hidden" name="blogId" value="${blogId}">
		<h2>WRITE A POST</h2>
		<input placeholder="Title" name="title" autofocus></input>
		<br />
		<textarea placeholder="Post Content" rows="6" cols="49" name="text"></textarea>
		<br /> <input value="SUBMIT" type="SUBMIT">
	</form>
</div>