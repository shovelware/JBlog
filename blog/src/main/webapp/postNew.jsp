<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="New Post">
<jsp:body>
	<t:postForm blogs="${blogs}"/>
</jsp:body>
</t:template>