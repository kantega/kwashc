<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<h2>Welcome to the Kantega Web Application Security Hero Challenge blog!</h2>

<p>This blog is NOT secure! That is about to change...</p>
<a href="#bottom">Go to bottom</a>

<c:if test="${sessionScope.user == null}">
    <p>
        Welcome,
        <script>
            var name;
            if(document.URL.indexOf("name=") > 0) {
                var pos = document.URL.indexOf("name=")+5;
                name = document.URL.substring(pos,document.URL.length);
                document.write(name+"!");
            }
            else {
                name = "Anonymous";
                document.write(name+"!");
            }
            $(location.hash);
        </script>
    </p>
</c:if>

<c:if test="${sessionScope.user != null}">
    <p>You are currently browsing the site as admin!</p>
</c:if>

<h3>Posts:</h3>


<c:forEach var="comment" items="${comments}">
    <div class="post">

        <c:if test="${sessionScope.user != null}">
            <div class="buttonsContainer">
                <a class="editButton" id="edit.${comment.title}" title="Edit post" href="/edit?commentID=${comment.ID}">&nbsp;</a>
                <a class="deleteButton" id="delete.${comment.title}" title="Delete post" href="/admin?commentToDelete=${comment.ID}">&nbsp;</a>
            </div>
        </c:if>

        <h4 class="postTitle">${comment.title}</h4>

        <div class="postBody">${comment.comment}</div>
    </div>
</c:forEach>

<form id="commentForm" action="/blog" method="POST">
    <fieldset>
        <legend>Post a comment:</legend>

        <p>
            <label for="title">Title:</label><br/>
            <input class="title" type="text" size="40" id="title" name="title"/>
        </p>

        <p>
            <label for="comment">Comment:</label><br/>
            <textarea class="text" cols="50" rows="6" id="comment" name="comment"></textarea>
        </p>

        <button type="submit" id="commentFormSubmit" class="button positive">
            <img src="/css/blueprint/plugins/buttons/icons/tick.png" alt=""/>Post comment
        </button>
    </fieldset>
</form>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
