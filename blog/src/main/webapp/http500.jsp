<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="HTTP 500">
<jsp:body>
	<t:error
	code="HTTP 500"
	name="SERVER ERROR"
	message="An Unexpected Server Error Occured.<br />But who expects errors, really?"/>
</jsp:body>
</t:template>