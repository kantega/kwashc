<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
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

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>

    <link rel="stylesheet" href="<c:url value="/css/blueprint/plugins/link-icons/screen.css"/>" type="text/css"
          media="screen, projection">
    <link rel="stylesheet" href="<c:url value="/css/blueprint/plugins/buttons/screen.css"/>" type="text/css" media="screen, projection">
    <link rel="stylesheet" href="<c:url value="/css/blueprint/screen.css"/>" type="text/css" media="screen, projection">
    <link rel="stylesheet" href="<c:url value="/css/blueprint/print.css"/>" type="text/css" media="print">
    <!--[if lt IE 8]>
    <link rel="stylesheet" href="<c:url value="/css/blueprint/ie.css"/>" type="text/css" media="screen, projection">
    <![endif]-->
    <link rel="stylesheet" href="<c:url value="/css/jqcloud.css"/>" type="text/css" media="screen, projection">

    <link rel="stylesheet" href="<c:url value="/css/style.css"/>" type="text/css" media="all"/>
    <script type="text/javascript" src="<c:url value="/js/jquery-1.7.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jqcloud-0.2.10.min.js"/>"></script>
    <title>Kantega Web Application Security Hero Challenge server</title>
</head>

<body>

<div id="header">

    <a id="logo" href="<c:url value="/"/>">

        <div id="logo-sweep"></div>
        <div id="logo-mask"></div>
        <div id="tagline">
            Kantega Web Application Security Hero Challenge
        </div>

    </a>

    <script type="text/javascript">

        $(function(){
            doLogoSweep();
        });

        function doLogoSweep(){
            $("#logo-sweep").css("top", "-40px");
            $("#logo-sweep").animate(
                    {
                        top: '+=150px'
                    }, 7000, "linear", function(){
                        setTimeout("doLogoSweep()", 1000);
                    }
            );
        }
    </script>
</div>
<div id="content" class="bigtext">

    <table class="alternatingGray">
        <tr>
            <th>Site:</th>
            <th>Test:</th>
            <th>Last passed:</th>
        </tr>
        <c:forEach var="site" items="${helpMap}">
            <tr>
                <td>
                    <c:out value="${site.key}"/>
                </td>
                <td></td>
                <td></td>
            </tr>
            <c:forEach var="test" items="${site.value}">
                <tr>
                    <td></td>
                    <td>
                        <c:out value="${test.key}"/>
                    </td>
                    <td>
                        <c:out value="${test.value}"/> minutes ago
                    </td>
                </tr>
            </c:forEach>
        </c:forEach>
    </table>

</div>

</body>
</html>