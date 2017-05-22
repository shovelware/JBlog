<div class="error">
	<strong>HTTP 404<br />NOT FOUND</strong>
	<br /><br />
	We couldn't find what you were looking for.
	<br /> Maybe it's between the couch cushions?

	<c:if test="${not empty param.errdetails}">
		<br/>${param.errdetails}
	</c:if>
	
	<br/>
</div>