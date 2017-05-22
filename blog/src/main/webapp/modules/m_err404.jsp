<div class="error">
	<center>
		<h1>HTTP 404</h1>
		<br />
		<h2>We couldn't find what you were looking for.</h2>
		<br /> Maybe it's between the couch cushions?

		<c:if test="${not empty param.errdetails}">
			<br/>${param.errdetails}
		</c:if>
		
		<br/>
	</center>
</div>