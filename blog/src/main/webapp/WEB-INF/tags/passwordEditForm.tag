<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

	<c:if test="${not empty errorMessage}">
		<div class="error">
			${errorMessage}
			<br/>
		</div>
	</c:if>
	
<div class=form id=passwordform>
	<form method="POST"	action="${pageContext.request.contextPath}/password/submit">
		<input type="password" name="oldPassword" placeholder="Old Password" autofocus></input>
		<br /><input type="password" placeholder="New Password" name="newPassword"></input>
		<br /><input type="password" placeholder="Repeat New Password" name="newPasswordRepeat"></input>
		<br /> <input value="SUBMIT" type="SUBMIT">
	</form>
</div>