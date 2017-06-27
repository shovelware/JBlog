<%@attribute name="blog" required="true" type="nl.cerios.clog.domain.BlogDO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url value="/static/img/blog.png" var="blogimg" />
<div class="box blog">
	<img src="${blogimg}" alt="Blog image"/>
	<a href="${pageContext.request.contextPath}/profile?id=${blog.profileId}">profile</a>
	<a href="${pageContext.request.contextPath}/blog?id=${blog.id}"><h2>${blog.title}</h2></a> <br/>
	<div class="description">${blog.description}</div>
</div>