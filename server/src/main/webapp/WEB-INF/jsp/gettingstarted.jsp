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

    <br>To get started you need to do the following:<br>

    <ul>
        <li>Download <a href="source/KWASHC-webapp.zip">KWASHC Blog Webapp</a></li>
        <li>Extract KWASHC-webapp.zip to a local directory</li>
        <li>The rest of the setup can be done in several ways, choose you potion:
        </li>
    </ul>

    <h3>Option 1: Native Maven</h3>

    <ul>
        <li>Download and install <a target="_blank" href="http://maven.apache.org/download.html">Maven</a></li>
        <li>cd webapp</li>
        <li>mvn clean install</li>
        <li>mvn install jetty:run</li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a>
        </li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>


    <h3>Option 2: IntelliJ IDEA & Maven</h3>

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

    <h3>Option 3: Stand-alone Eclipse</h3>

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

    <h3>Option 4: Eclipse & Maven installed</h3>

    <ul>
        <li>cd webapps</li>
        <li>mvn -Declipse.workspace="path to your Eclipse Workspace" eclipse:configure-workspace</li>
        <li>mvn eclipse:eclipse</li>
        <li>Start Eclipse</li>
        <li>File > Import > Existing Projects into Workspace</li>
        <li>Root Directoy: Select webapp path -> Finish</li>
        <li>Under Project Explorer Window Select project root, then go to Run Menu -> Run Configurations, double click Maven Build</li>
        <li>Under Base directory -> Browser Workspace -> Select webapp</li>
        <li>Write 'install jetty:run' in Goals -> Run</li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a>
        </li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>

</div>

<jsp:include page="/WEB-INF/jsp/serverfooter.jsp"/>