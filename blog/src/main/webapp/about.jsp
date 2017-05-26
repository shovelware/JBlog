<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="About">
<jsp:body>
	<div class="box">
	This site is a simple blogging platform, built with Java, SQL and HTML.
	
	At the moment it utilizes a Tomcat Server, JDBC driver, and MySQL.
	
	<br/>===
	<table>
	<tr><td>===</td></tr>
	<tr><td>TODO: Finish writing SQL interfaces</td></tr>
	<tr><td>TODO: Make SQL interfaces airtight</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Add unit tests of all sorts</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Finish writing display modules for profile and blog (how display blog)</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Add URI mappings for /post?id=xxxxx (and blog, and profile)</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Investigate removing the /blog/ suffix from everything (WebApp root?)</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Add a favicon (Can't be that hard?)</td></tr>
	<tr><td>TODO: Fix that damned SQL SSL warning one way or another.</td></tr>
	</table>
	</div>
</jsp:body>
</t:template>