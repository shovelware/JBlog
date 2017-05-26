<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

	<c:if test="${not empty errorMessage}">
		<div class="error">
			${errorMessage}
			<br/>
		</div>
	</c:if>
	
<div class=form id=loginform>
	<form method="POST"	action="${pageContext.request.contextPath}/login/submit">
		<input name="username" placeholder="username" autofocus></input>
		<br /><input type="password" placeholder="password" name="password"></input>
		<br /> <input value="Login" type="SUBMIT">
	</form>
</div>