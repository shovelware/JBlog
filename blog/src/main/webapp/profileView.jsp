<jsp:include page="/modules/t_template.jsp">
	<jsp:param name="content" value="m_profile.jsp"/>
		<jsp:param name="pname" value="${profile.name}" />
		<jsp:param name="pmotto" value="${profile.motto}"  />
		<jsp:param name="ptimestamp" value="${profile.joinDate}" />
	<jsp:param name="title" value="ProfileView"/>
</jsp:include>