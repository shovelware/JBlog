<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:template title="About">
<jsp:body>
	<div class="box">
			 <code> ${testString} </code>
		<br/><code> ${testString1} </code>
		<br/><code> ${testString2} </code>
		<br/><code> ${testString3} </code>
		<br/><code> ${testString4} </code>
		<br/><code> ${testString5} </code>
		<br/><code> ${testString6} </code>
		<br/><code> ${testString7} </code>
		<br/><code> ${testString8} </code>
		<br/><code> ${testString9} </code>
	</div>
	
	<div class="box">
		<form method="POST" action="${pageContext.request.contextPath}/test/submit">
				<input placeholder="String" name="testInput" autofocus/>
			<br/><input placeholder="HashThis" name="testInput1" autofocus/>
			<br/><input placeholder="String2" name="testInput2" autofocus/>
			<br/><input placeholder="String3" name="testInput3" autofocus/>
			<br/><input placeholder="String4" name="testInput4" autofocus/>
			<br/><input placeholder="String5" name="testInput5" autofocus/>
			<br/><input placeholder="String6" name="testInput6" autofocus/>
			<br/><input placeholder="String7" name="testInput7" autofocus/>
			<br/><input placeholder="Password" name="testInput8" autofocus/>
			<br/><input placeholder="Hash" name="testInput9" autofocus/>
			<br/><input value="SUBMIT" type="SUBMIT"/>
		</form>
	</div>
</jsp:body>
</t:template>