<%@attribute name="post" required="true" type="nl.cerios.blog.database.PostDTO"%>
<div class="box">
	<h2>${post.title}</h2> <br/>
	<div class="postcontent">${post.text}</div>
	<div class="timestamp" style="float:right">${post.timestamp}</div>
</div>