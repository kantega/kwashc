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
<h2>${test.name}</h2>

<div class="description contentPadding floatContainer rounded">
    <h4>Info</h4>
    ${test.description}
</div>

<c:if test="${test.exploit != null}">
    <div class="exploit contentPadding floatContainer rounded">
    <h4>Exploit</h4>
    ${test.exploit}
    </div>
</c:if>
<c:if test="${test.hint != null}">
    <div class="floatContainer wide">
        <input type="button" class="button positive leftFloat noMargin" onclick="javascript:$('#hint').toggle();" value="Toggle hint"/>
    </div>
    <div class="hint contentPadding floatContainer rounded" id="hint" style="display:none;">
        <h4>Hint</h4>
        ${test.hint}
    </div>
</c:if>
<c:if test="${test.informationURL != null}">
<br>
<p>To learn more about this threat, google it, or read at this site: <a target="_blank" href="${test.informationURL}">${test.informationURL}</a></p>
</c:if>
<br><br>
<c:if test="${result != null}">
    <div class="rounded contentPadding floatContainer">
        <c:choose>
            <c:when test="${result.passed}">
                <img class="modelight" src="/gfx/modelight_green.png" title="Passed"/>
            </c:when>
            <c:otherwise>
                <img class="modelight" src="/gfx/modelight_red.png" title="Failed"/>
            </c:otherwise>
        </c:choose>
        <a class="bigtext" href="<c:url value="/test/site/${site.id}/execute=${result.test.identifikator}"/>"
           title="Run this test only!">${result.test.name} (${result.duration} seconds)</a>
        <br> ${result.message}
    </div>
    </div>
    <div class="buttonContainer">
        <a class="button positive" href="<c:url value="/test/site/${site.id}/execute=${result.test.identifikator}"/>"
           title="Run this test only!">Re-run the test!</a>

    </div>
    <br>
    <div class="contentPadding">
        <p>OBS: Individual test runs are not saved, and do not affect your score. To get a new score, <a
                href="<c:url value="/test/site/${site.id}/executeAll"/>">run all tests.</a></p>
        <br>
    </div>
</c:if>


<jsp:include page="/WEB-INF/jsp/serverfooter.jsp"/>
