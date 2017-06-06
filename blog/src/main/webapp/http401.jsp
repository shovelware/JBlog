<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="HTTP 401">
<jsp:body>
	<t:error
	code="HTTP 401"
	name="AUTH REQUIRED"
	message="We can't let just <em>anyone</em> in. <br />Try signing in?"/>
</jsp:body>
</t:template>
