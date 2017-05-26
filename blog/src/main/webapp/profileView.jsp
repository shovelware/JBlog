<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="View Profile">
<jsp:body>
	<t:profileView profile="${profile}"/>
</jsp:body>
</t:template>