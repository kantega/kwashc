<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%--
  ~ Copyright 2012 Kantega AS
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

<div class="contentPadding">


    <h2><c:out value="${site.name}"/></h2>

    <dl id="siteDefinition">
        <dt>Owner:</dt>
        <dd><c:out value="${site.owner}"/></dd>
        <dt>Site URL:</dt>
        <dd><c:url value="${site.address}"/></dd>
        <c:if test="${site.secureport != ''}">
            <dt>HTTPS Port:</dt>
            <dd><c:out value="${site.secureport}"/></dd>
        </c:if>
        <dt>Secret:</dt>
        <dd><c:out value="${site.secret}"/></dd>
    </dl>
</div>

<div class="buttonContainer">
    <a id="deleteSite" class="button negative leftFloat" href="<c:url value="/site/${site.id}/delete"/>">Delete</a>
    <a class="button leftFloat" href="<c:url value="/site/${site.id}/edit"/>">Edit</a>
	<a class="button positive" href="<c:url value="/test/site/${site.id}/executeAll"/>">Run tests!</a>
</div>

<br>
<div class="contentPadding">

    <h2>Test results:</h2>

	<p>We suggest starting from the top, and solving the tasks in roughly the same order as they appear on the list.</p>

	<p>Hint: To save time, you can run individual tests by clicking the test name.</p>
	<c:choose>

	<c:when test="${empty site.testResults}">

	<div class="rounded contentPadding floatContainer">
		To run the tests, please press the button below.
		<div class="clear"></div>

		</c:when>
		<c:otherwise>
			<c:forEach var="result" items="${site.testResults}">
				<div class="rounded contentPadding floatContainer linkBox"
				onmouseover="$(this).addClass('highlight');" onmouseout="$(this).removeClass('highlight');">
					<c:choose>
						<c:when test="${result.resultEnum == 'passed'}">
							<img class="modelight" src="/gfx/modelight_green.png" title="Passed"/>
						</c:when>
                        <c:when test="${result.resultEnum == 'partial'}">
                            <img class="modelight" src="/gfx/modelight_yellow.png" title="Partial"/>
                        </c:when>
						<c:otherwise>
							<img class="modelight" src="/gfx/modelight_red.png" title="Failed"/>
						</c:otherwise>
					</c:choose>

                    <span class="heading">${result.test.name} (${result.duration} seconds)</span>
					<a id="execute${result.test.identifikator}Link" href="<c:url value="/test/site/${site.id}/execute=${result.test.identifikator}"/>"></a>
                    <span class="testCategoryHeader rounded ${result.test.testCategory}">${result.test.testCategory.name}</span><br>
                    ${result.message}
				</div>
				<div class="clear"></div>
			</c:forEach>
		</c:otherwise>
		</c:choose>

	</div>


	<div class="buttonContainer">
    To clear old messages that might interfere with the tests, you can restart your webapp.<a class="button positive" id="executeAllTestsLink" href="<c:url value="/test/site/${site.id}/executeAll"/>">Run tests!</a>
</div>

<jsp:include page="/WEB-INF/jsp/serverfooter.jsp"/>
