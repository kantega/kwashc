<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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

    <c:choose>
        <c:when test="${site.id == null}">
            <h2>Add new site</h2>

        </c:when>
        <c:otherwise>
            <h2>Edit site '<c:out value="${site.name}"/>'</h2>
        </c:otherwise>
    </c:choose>


    <p>Before creating a site you must prove that you own it. This is done by adding the following to the header element of your page (header.jsp):</p>

<pre>
    <c:out value="<meta name=\"no.kantega.kwashc\" content=\"${site.secret}\"/>"/>
</pre>

    <p>The site URL should point to your front page, where this meta tag is present [http://&#60;host&#62;:&#60;port&#62;/...].</p>

</div>

<form:form modelAttribute="site" action="${pageContext.request.contextPath}/site/save" method="POST">

    <fieldset>
        <div class="contentPadding">
            <div class="rounded contentPadding halfWidth">
            <p>
                <form:label path="Name">Name:</form:label><br/>
                <form:input path="name" cssErrorClass="title error" cssClass="title" /><br/>
                <form:errors path="name" cssStyle="color: red"/>
            </p>

            <p>
                <form:label path="owner">Owner:</form:label><br/>
                <form:input path="owner" cssErrorClass="text error" cssClass="text" /><br/>
                <form:errors path="owner" cssStyle="color: red"/>
            </p>

            <p>
                <form:label path="address">Site URL:</form:label><br/>
                <form:input path="address" cssErrorClass="text error" cssClass="text" /><br/>
                <form:errors path="address" cssStyle="color: red"/>
            </p>

            <p>
                <form:label path="secureport">HTTPS port:</form:label><br/>
                <form:input path="secureport" cssErrorClass="error" cssClass="text" /><br/>
                <form:errors path="secureport" cssStyle="color: red"/>
            </p>
            </div>
        </div>

        <div class="buttonContainer">
            <button type="submit" id="saveSiteButton" class="button positive">

                <c:choose>
                    <c:when test="${site.id == null}">
                        Add site
                    </c:when>
                    <c:otherwise>
                        Save site
                    </c:otherwise>
                </c:choose>
            </button>
        </div>

    </fieldset>

</form:form>

<br><br><br>

<jsp:include page="/WEB-INF/jsp/serverfooter.jsp"/>
