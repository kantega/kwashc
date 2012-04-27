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


<div class="contentPadding bigtext">
    <p>Currently registered sites:</p>

    <table>
        <tr>
            <th>Site:</th>
            <th>Team:</th>
        </tr>

        <c:forEach var="site" items="${sites}">

        <tr>
            <td><a href="<c:url value="/site/${site.id}/"/>"><c:out value="${site.name}"/></a>
            </td>
            <td>
                <c:out value="${site.owner}"/>
            </td>

            </c:forEach>
    </table>

</div>

<div class="buttonContainer">

    <a class="button" href="<c:url value="/site/new"/>">Add site</a>
</div>

<br><br><br>

<jsp:include page="/WEB-INF/jsp/serverfooter.jsp"/>
