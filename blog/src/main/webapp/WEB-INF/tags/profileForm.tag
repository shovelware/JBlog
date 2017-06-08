<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not empty sessionScope.loggedInUser}">
	<div class=box><p>Why join if you're logged in?</p></div>
</c:if>

<c:if test="${empty sessionScope.loggedInUser}">
	<div class=form id=profileform>
		<form method="POST"
			action="${pageContext.request.contextPath}/profile/submit">
			<h2>JOIN US</h2>
			<input placeholder="Name" name="name" autofocus></input> <br />
			<input placeholder="password" name="password" type="password"></input> <br />
			<br/>
			<input placeholder="Motto" name="motto"></input> <br />
			<input value="SUBMIT" type="SUBMIT">
		</form>
	</div>
</c:if>
