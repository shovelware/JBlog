<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="HTTP 404">
<jsp:body>
	<t:error
	code="HTTP 404"
	name="NOT FOUND"
	message="We couldn't find what you were looking for.<br /> Maybe it's between the couch cushions?"/>
</jsp:body>
</t:template>