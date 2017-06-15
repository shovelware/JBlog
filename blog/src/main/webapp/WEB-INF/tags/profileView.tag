<%@attribute name="profile" required="true" type="nl.cerios.blog.database.ProfileDTO"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<div class="box profile">
	<div class="timestamp" style="float:right">Joined: <t:localDate date="${profile.joinDate}"/></div>
	<h2>${profile.name}</h2> <br/>
	<div class="motto">${profile.motto}</div>
</div>