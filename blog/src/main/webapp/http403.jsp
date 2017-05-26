<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="HTTP 403">
<jsp:body>
	<t:error
	code="HTTP 403"
	name="FORBIDDEN"
	message="That which is forbidden is the most tempting.<br />But the answer is still no."/>
</jsp:body>
</t:template>