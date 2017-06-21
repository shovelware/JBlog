<%@attribute name="blog" required="true" type="nl.cerios.clog.database.BlogDTO"%>
<div class="box blog">
	<a href="${pageContext.request.contextPath}/profile?id=${blog.profileId}">profile</a>
	<a href="${pageContext.request.contextPath}/blog?id=${blog.id}"><h2>${blog.title}</h2></a> <br/>
	<div class="description">${blog.description}</div>
</div>