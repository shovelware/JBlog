<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="Edit Profile">
<jsp:body>
	<t:profileEditForm profile="${profile}"/>
</jsp:body>
</t:template>