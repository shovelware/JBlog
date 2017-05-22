<div class="error">
	<strong>HTTP 500<br />SERVER ERROR</strong>
	<br /><br />
	An Unexpected Server Error Occured.
	<br />But who expects errors, really?
	
	<c:if test="${not empty param.errdetails}" >
		<br/>${param.errdetails}
	</c:if>
	
	<br/>
</div>