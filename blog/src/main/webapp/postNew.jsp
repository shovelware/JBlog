<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="Edit Post">
<jsp:body>
	<t:postForm blogId="${blogId}" profileId="${profileId}"/>
</jsp:body>
</t:template>