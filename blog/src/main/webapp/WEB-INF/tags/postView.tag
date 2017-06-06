<%@attribute name="post" required="true" type="nl.cerios.blog.database.PostDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="box">
	<div class="timestamp" style="float:right">${post.timestamp}</div>
	<h2>${post.title}</h2> <br/>
	<div class="postcontent"><c:out value="${post.text}"></c:out></div>
</div>