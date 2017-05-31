<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="code"%>
<%@ attribute name="name"%>
<%@ attribute name="message"%>

<c:set var="code" value="${(empty code) ? 'ERROR' : code}" />
<c:set var="name" value="${(empty name) ? '' : name}" />
<c:set var="message" value="${(empty message) ? 'Something went wrong. <br/> We\\'d rather not say what.' : message}" />

<div class="error">
	<strong>
		${code}<br />
		${name}
	</strong>
	<br /><br />
	${message}<br />
	<c:if test="${not empty errordetails}">
		<br/>
		<hr/>
		${errordetails}
		<hr/>
	</c:if>
	<br/>
</div>
