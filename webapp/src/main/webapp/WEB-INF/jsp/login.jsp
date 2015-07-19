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

<h2>Log in</h2>

<p>You asked for a protected resource. Please log in:</p>

<form action="/j_security_check" method="POST">
    <fieldset>

        <p>
            <label for="username">Username</label><br/>
            <input class="text" type="text" id="username" name="username"/>
        </p>

        <p>
            <label for="password">Password</label><br/>
            <input class="text" type="password" id="password" name="password"/>
        </p>

        <button type="submit" id="formSubmitButton" class="button positive">
            <img src="/css/blueprint/plugins/buttons/icons/tick.png" alt=""/>Log in
        </button>

    </fieldset>
</form>

<hr>

<p>Two users should exist: </p>

<pre>username/password</pre>
<pre>anotheruser/anotherpassword</pre>


<jsp:include page="/WEB-INF/jsp/footer.jsp"/>