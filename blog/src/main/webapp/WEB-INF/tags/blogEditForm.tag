<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${empty sessionScope.loggedInUser}">
	<div class=box>
	<p>How do we know this belongs to you?</p>
	<p>Try logging in first.</p>
	</div>
</c:if>

<div class=form id=blogform>
	<form method="POST"
		action="${pageContext.request.contextPath}/blog/resubmit">
		<br /> <input value="DO NOTHING" type="SUBMIT">
	</form>
</div>
