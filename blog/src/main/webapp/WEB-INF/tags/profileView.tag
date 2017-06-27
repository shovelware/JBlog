<%@attribute name="profile" required="true" type="nl.cerios.clog.domain.ProfileDO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:url value="/static/img/avatar.png" var="avatarimg" />
<div class="box profile">
	<img src="${avatarimg}" alt="Avatar"/>
	<div class="timestamp" style="float:right">Joined: <t:localDate date="${profile.joinDate}"/></div>
	<h2>${profile.name}</h2> <br/>
	<div class="motto">${profile.motto}</div>
</div>