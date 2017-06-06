<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="View Recent Posts">
<jsp:body>
	<t:postViewList posts="${posts}"/>
</jsp:body>
</t:template>