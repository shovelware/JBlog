<%@attribute name="blog" required="true" type="nl.cerios.clog.object.BlogDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:url value="/static/img/blog.png" var="blogimg" />
<div class="box blog">
	<img src="${blogimg}" alt="Blog image"/><br/>
	<a href="${pageContext.request.contextPath}/profile?id=${blog.profileId}">profile</a>	
	<t:ifLoggedInOwner ownerId="${blog.profileId}"> | <a href='${pageContext.request.contextPath}/blog/edit?id=${blog.id}'>edit</a></t:ifLoggedInOwner>
	<h2><a href="${pageContext.request.contextPath}/blog?id=${blog.id}">${blog.title}</a></h2>
	 <br/>
	<div class="description">${blog.description}</div>
</div>