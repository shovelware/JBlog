<div class="error">
	<strong>HNGGG</strong>
	<br /><br />
	There was definitely an error somewhere.
	<br />We're not sure about anything else.

	<c:if test="${not empty param.errdetails}">
		<br/>${param.errdetails}
	</c:if>
	
	<br/>
</div>
