<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <h2>Getting Started</h2>

    <ul>
        <li>Download <a href="source/KWASHC-webapp.zip">KWASHC Blog Webapp</a> and extract to a local directory</li>
        <li>The rest of the setup can be done in several ways, choose you potion:
        </li>
    </ul>

    <h3>Option 1: Maven</h3>

    <ul>
        <li>Go to <strong>webapp</strong> directory and run:
            <ul>
                <li>mvn clean install</li>
                <li>mvn install jetty:run</li>
            </ul>
        </li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a></li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>

    <h3>Option 2: Maven Wrapper</h3>

    <ul>
        <li>Go to <strong>webapp</strong> directory and install Maven by running:
            <ul>
                <li>mvnw.cmd (Windows)</li>
                <li>./mvnw (Linux/Mac)</li>
            </ul>
        </li>
        <li>The run:
            <ul>
                <li>mvnw(.cmd) clean install</li>
                <li>mvnw(.cmd) -f webapp/pom.xml jetty:run</li>
            </ul>
        </li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a></li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>

    <h3>Option 3: Docker Compose</h3>

    <ul>
        <li>Install Docker Compose:
            <ul>
                <li>pip install -U docker-compose</li>
            </ul>

        </li>
        <li>Go to <strong>webapp</strong> directory and run:
            <ul>
                <li>docker-compose pull</li>
                <li>docker-compose build</li>
                <li>docker-compose up</li>
            </ul>
        </li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a></li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>


    <h3>Option 4: IntelliJ IDEA & Maven</h3>

    <ul>
        <li>Download and install <a target="_blank" href="http://maven.apache.org/download.html">Maven</a></li>
        <li>Download and install <a target="_blank" href="http://www.jetbrains.com/idea/free_java_ide.html">IDEA</a>
        </li>
        <li>Open Project -> select webapp/pom.xml</li>
        <li>Click 'Maven Project' on Right menu bar -> webapp -> Plugins -> Right click jetty:run -> Run Maven Build</li>
        <li>If asked about "No Maven installation..", click 'configuration dialog', and set 'Maven home location' to
            installed Maven home dir
        </li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a>
        </li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>

    <h3>Option 5: Eclipse</h3>

    <ul>
        <li>Download and install <a target="_blank" href="http://www.eclipse.org/downloads/moreinfo/java.php">Eclipse for Java Developers</a></li>
        <li>Import -> Maven -> Existing Maven Projects</li>
        <li>Root Directoy: Select webapp path -> Next -> Finish</li>
        <li>Under 'Project Explorer Window' Select project root, then go to Run Menu -> Run As -> Maven Build (first
            one)
        </li>
        <li>Write 'install jetty:run' in Goals -> Run</li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a>
        </li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>

</div>

<jsp:include page="/WEB-INF/jsp/serverfooter.jsp"/>