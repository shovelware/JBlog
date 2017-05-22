<div class="error">
	<center>
		<h1>HTTP 401</h1>
		<br />
		<h2>I can't let just <i>anyone</i> in.</h2>
		<br />Try signing in?

		<c:if test="${not empty param.errdetails}">
			<br />${param.errdetails}
		</c:if>
		
		<br/>
	</center>
</div>