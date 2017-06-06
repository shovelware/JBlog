<%@attribute name="blogId" required="true"%>
<%@attribute name="profileId" required="true"%>

<div class=form id=postform>
	<form method="POST" action="${pageContext.request.contextPath}/post/submit">
	<input type="hidden" name="blogId" value="${blogId}">
	<input type="hidden" name="profileId" value="${profileId}">
		<h2>WRITE A POST</h2>
		<br />
		<input name="title" autofocus></input>
		<br />
		<textarea rows="6" cols="49" name="text"></textarea>
		<br /> <input value="SUBMIT" type="SUBMIT">
	</form>
</div>