<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:forEach items="${posts}" var="post">
	<jsp:include page="/modules/m_post.jsp">
		<jsp:param name="ptitle" value="${post.title}" />
		<jsp:param name="ptext" value="${post.text}"  />
		<jsp:param name="ptimestamp" value="${post.timestamp}" />
	</jsp:include>
	<br/><br/>
</c:forEach>