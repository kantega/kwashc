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
        <li>Download and install <a href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">Java</a></li>
        <li>Setup Environment variables for Java</li>
        <ul>
            <li>Windows: Follow <a href="http://www.itcsolutions.eu/2010/11/29/set-environment-variables-in-windows-7-for-java/">this guide.</a></li>
            <li>Mac OS X: Run the following:<br>
                <code>echo export "JAVA_HOME=\$(/usr/libexec/java_home)" >> ~/.bash_profile</code><br>
                Then restart your shell. If that did not work <a href="http://www.mkyong.com/java/how-to-set-java_home-environment-variable-on-mac-os-x/">follow this guide</a></li>
            <li>Linux (Ubuntu):<br>
                <code>echo export "JAVA_HOME=/usr/lib/jvm/java-8-oracle" >> ~/.bashrc</code><br>
                Then restart your shell. If that did not work, try Google.
            </li>
        </ul>
        <li>
            Open a (Windows Command Shell (CMD.exe)|Mac OS X Terminal|Linux Bash) and try to run:<br>
            <code>java -version</code><br>
            If that worked, your ready to go. If not go back to <b>Setup Environment variables</b> and try again or ask someone for help.
        </li>
        <li>The rest of the setup can be done in several ways, choose you potion:
        </li>
    </ul>

    <h3>Option 1: Maven NOT installed</h3>

    <ul>
        <li>Using the shell/terminal, go to <strong>webapp</strong> directory and install Maven by running:
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

    <h3>Option 2: Maven already installed</h3>

    <ul>
        <li>Using the shell/terminal, go to <strong>webapp</strong> directory and run:
            <ul>
                <li>mvn clean install</li>
                <li>mvn install jetty:run</li>
            </ul>
        </li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a></li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>


    <h3>Option 3: <a href="https://docs.docker.com/installation/">Docker</a> (Must already be installed)</h3>

    <ul>
        <li>Using the shell/terminal, install <a href="https://docs.docker.com/compose/">Docker Compose</a>:
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
        <li>Download and install <a target="_blank" href="https://maven.apache.org/download.html">Maven</a></li>
        <li>Download and install <a target="_blank" href="https://www.jetbrains.com/idea/download/">IDEA</a>
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
        <li>Download and install <a target="_blank" href="https://www.eclipse.org/ide/">Eclipse for Java</a></li>
        <li>Import -> Maven -> Existing Maven Projects</li>
        <li>Root Directoy: Select <strong>webapp</strong> directory -> Next -> Finish</li>
        <li>Under 'Project Explorer Window' Select project root, then go to Run Menu -> Run As -> Maven Build (first
            one)
        </li>
        <li>Write 'install jetty:run' in Goals -> Run</li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a>
        </li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>

    <h3>Option 6: NetBeans</h3>

    <ul>
        <li>Download and install <a target="_blank" href="https://netbeans.org/downloads/">NetBeans for Java SE</a></li>
        <li>File -> Open Project -> Select <strong>webapp</strong> directory -> Open Project</li>
        <li>Under 'Project' window select webapp, then under 'Navigator' window select 'jetty <strong>run</strong>'</li>
        <li>Access blog application with browser on <a target="_blank" href="http://localhost:8080">localhost:8080</a>
        </li>
        <li><a href="/site/new">Register</a> your site</li>
    </ul>

</div>

<jsp:include page="/WEB-INF/jsp/serverfooter.jsp"/>