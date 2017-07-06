<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="About">
<jsp:body>
	<div class="box">
	This site is a simple blogging platform.
	<br/>
	<br/>At the moment it runs a Java Servlet on Tomcat. 
	<br/>For persistence we use a JDBC driver and a MySQL Database.
	<br/>For security we have XSS prevention and SQL injection protection.
	
	<br/>
	
	<table>
	<tr><td>===</td></tr>
	<tr><td>TODO: ViewProfile is POST, we get that resend message when refreshing.</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Authentication needs a rewrite, hashing, salting, authdb.</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Edit forms still use old system</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: SQL API usage: in POM or database independent?</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Finish implementing blog and profile editing</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Unit Test SQL interfaces</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Finish Implementing/Unit Test Biz layer</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Investigate removing the /blog/ suffix from everything (WebApp root?)</td></tr>
	<tr><td>===</td></tr>
	<tr><td>TODO: Implement Pictures in posts and SQL (How tho?)</td></tr>
	<tr><td>===</td></tr>
	
	</table>
	</div>
</jsp:body>
</t:template>