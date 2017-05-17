# JBlog
A simple, java-based blogging platform

## tasks
Architecture (by Remco/Rene?)  
jira board  
code!  

## Other info
Java 8  
Tomcat 9.0.0  
MySQL 5.7  
JUnit 3.8.1  
MySQL Connector Java 5.1.42  
JSTL 1.2  
SnakeYAML 1.18  


Adding SQL link $CATALINA_HOME/conf/context.xml
```
<Context>
	<Resource name="jdbc/blog"
	auth="Container"
	type="javax.sql.DataSource"
	username="user"
	password="pwd"
	driverClassName="com.mysql.jdbc.Driver"
	url="jdbc:mysql://localhost:3306/blog"/>
</Context>
```
