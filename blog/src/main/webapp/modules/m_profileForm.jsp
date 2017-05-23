<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${empty sessionScope.loggedInUser}">
	<div class=form id=profileform>
		<form method="POST"
			action="${pageContext.request.contextPath}/profile/submit">
			<h2>JOIN US</h2>
			<br /> <input placeholder="name" name="name" autofocus></input> <br />
			<input placeholder="motto" name="motto"></input> <br /> <input
				value="SUBMIT" type="SUBMIT">
		</form>
	</div>
</c:if>

<c:if test="${not empty sesionScope.loggedInUser}">
	<div class=box>Why join if you're logged in?</div>
</c:if>