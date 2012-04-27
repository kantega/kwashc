/*
 * Copyright 2012 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kwashc;


import kwashc.blog.model.User;
import kwashc.blog.servlet.LogInServlet;
import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * TODO andbat: Description/responsibilities.
 *
 * @author Anders Båstrand, (www.kantega.no)
 */
public class SecurityFilter implements Filter {

    private static final Logger logger = Logger.getLogger(SecurityFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
        // ignore
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession(true);

        Object userObject = session.getAttribute(LogInServlet.USER_SESSION_ATTRIBUTE);

        if (userObject != null && userObject instanceof User) {
            // a user is logged in, proceed
            chain.doFilter(req, resp);
        } else {
            session.setAttribute(LogInServlet.TARGET_PAGE_SESSION_ATTRIBUTE, request.getRequestURI() + "?" + request.getQueryString());
            // show login page
            session.getServletContext().getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, response);
        }
    }

    public void destroy() {
        // ignore
    }
}
