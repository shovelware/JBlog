<div class=form id=blogform>
	<form method="POST" action="${pageContext.request.contextPath}/blog/submit">
		<input type="hidden" name="profileId" value="${loggedInUser.id}">
		<h2>ADD A BLOG</h2>
				
		Title<br />
		<input placeholder="Blog Title" name="title" autofocus></input>
		
		<br /><br />Description<br />
		<textarea placeholder="Blog Description" rows="6" cols="49" name="description"></textarea>
		
		<br /><br /><input value="SUBMIT" type="SUBMIT">
	</form>
</div>