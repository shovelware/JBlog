<%@attribute name="profile" required="true" type="nl.cerios.blog.database.ProfileDTO"%>
<div class="box">
	<div class="timestamp" style="float:right">${profile.joinDate}</div>
	<h2>${profile.name}</h2> <br/>
	<div class="motto">${profile.motto}</div>
</div>