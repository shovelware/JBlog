<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class=form id=loginform>
	<form method="POST"
		action="${pageContext.request.contextPath}/login/submit">
		<input name="username" placeholder="username" autofocus="true"></input>
		<br /><input type="password" placeholder="password" name="password"></input>
		<br /> <input value="SUBMIT" type="SUBMIT">
	</form>

	<c:if test="${not empty errorMessage}">
		<div class="error">
			${errorMessage}
		</div>
	</c:if>
</div>