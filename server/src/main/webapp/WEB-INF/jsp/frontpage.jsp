<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  ~ Copyright 2013 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~  http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<jsp:include page="/WEB-INF/jsp/serverheader.jsp"/>

<div class="contentPadding bigtext">
<h2>Score board</h2>
  <p class="graytext">
    Click a Site to inspect test results
     </p>
<table id="scoreTable">
	<tr>
		<th>Site:</th>
		<th>Team:</th>
		<th>Score:</th>
	</tr>

    <c:forEach var="site" items="${sites}">
		<tr>
			<td><a id="viewSite${site.id}" href="<c:url value="/site/${site.id}/"/>"><c:out value="${site.name}"/></a>
			</td>
			<td>
				<c:out value="${site.owner}"/>
			</td>
			<td>
				<c:out value="${site.score}"/>/<c:out value="${numberOfTests}"/>
			</td>
		</tr>
	</c:forEach>
</table>
</div>

<div class="buttonContainer">
<a class="button positive" id="addSiteLink" href="<c:url value="/site/new"/>">Add site</a>
</div>

<br>

<div class="contentPadding bigtext">
	<h3>Links you will need</h3>
	<ul>
		<li><a href="http://<%= request.getLocalAddr() %>:<%= request.getLocalPort() %>">This server
            (http://<%= request.getLocalAddr() %>:<%= request.getLocalPort() %>)</a></li>
		<li><a href="source/KWASHC-webapp.zip">KWASHC Blog Webapp</a>. This is your test blog, packaged as a Maven
            project.</li>
        <li>Follow the <a href="/gettingstarted">getting started guide</a>, or ask one of the tutors if you need help.</li>
	</ul>
	<h3>Resources</h3>
	<ul>
		<li><a target="_blank" href="http://maven.apache.org/download.html">Maven</a>
			<a target="_blank" href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">JVM</a>
			<a target="_blank" href="http://www.eclipse.org/downloads/moreinfo/java.php">Eclipse for Java Developers</a>
			<a target="_blank" href="http://www.jetbrains.com/idea/free_java_ide.html">IDEA</a>
		</li>
		<li><a target="_blank" href="https://www.owasp.org/index.php/Top_10_2010">OWASP Top 10 2010</a></li>
		<li><a target="_blank" href="http://courses.coreservlets.com/Course-Materials/csajsp2.html">Beginning & Intermediate Servlet & JSP
			Tutorials</a></li>
	</ul>

	<h3>Sites will be tested against ${fn:length(tests)} tests:</h3>


	<c:forEach var="test" items="${tests}" varStatus="status">
		<a href="<c:url value="/test/${test.key}"/>"><c:out value="${test.value.name}"/></a>
        <c:if test="${!status.last}"> | </c:if>
	</c:forEach>

  <br><br>


<p class="graytext centeredtext">This server is not part of the security tests, and no testing of the server's security is allowed.</p>
</div>

<jsp:include page="/WEB-INF/jsp/serverfooter.jsp"/>
