<jsp:include page="/modules/t_template.jsp">
	<jsp:param name="content" value="m_postSingle.jsp"/>
		<jsp:param name="ptitle" value="${post.title}" />
		<jsp:param name="ptext" value="${post.text}"  />
		<jsp:param name="ptimestamp" value="${post.timestamp}" />
	<jsp:param name="title" value="PostView"/>
</jsp:include>