<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="Edit Blog">
<jsp:body>
	<t:blogEditForm blog="${blog}"/>
</jsp:body>
</t:template>