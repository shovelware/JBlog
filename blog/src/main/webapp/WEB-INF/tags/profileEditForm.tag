<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name="profile" required="true" type="nl.cerios.blog.database.ProfileDTO"%>

<%-- Edit motto, username? password? --%>

<c:set var="motto" value="${(empty profile.motto) ? '' : profile.motto}" />

<div class=form id=profileform>
	<form method="POST"
		action="${pageContext.request.contextPath}/profile/resubmit">
		<input type="hidden" name="profileId" value="${profile.id}">
		<input type="hidden" name="name" value="${profile.name}">
		<input type="hidden" name="joinDate" value="${profile.joinDate}">
		
		Motto<br/>
		<input placeholder="Motto" name="motto" value="${motto}" autofocus></input> <br />
		<br /> <input value="SUBMIT" type="SUBMIT">
	</form>
</div>
