<div class="error">
	<center>
		<h1>HTTP 500</h1>
		<br />
		<h2>An Unexpected Server Error Occured.</h2>
		<br />But who expects errors, really?
		
		<c:if test="${not empty param.errdetails}" >
			<br/>${param.errdetails}
		</c:if>
		
		<br/>
	</center>
</div>