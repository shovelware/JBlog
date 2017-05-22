<div class="error">
	<strong>HTTP 401<br />AUTH REQUIRED</strong>
	<br /><br />
	We can't let just <em>anyone</em> in.
	<br />Try signing in?

	<c:if test="${not empty param.errdetails}">
		<br />${param.errdetails}
	</c:if>
	
	<br/>
</div>