<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${empty sessionScope.loggedInUser}">
	<div class=box>
	<p>Identity theft is a serious crime.</p>
	<p>Try logging in first.</p>
	</div>
</c:if>

<%-- Edit motto, username? password? --%>

<div class=form id=profileform>
	<form method="POST"
		action="${pageContext.request.contextPath}/profile/resubmit">
		<br /> <input value="DO NOTHING" type="SUBMIT">
	</form>
</div>
