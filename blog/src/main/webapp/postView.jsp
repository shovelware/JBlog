<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="View Post">
<jsp:body>
	<t:postView post="${post}"/>
</jsp:body>
</t:template>