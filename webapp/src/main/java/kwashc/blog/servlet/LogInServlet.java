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

package kwashc.blog.servlet;

import kwashc.blog.database.Database;
import kwashc.blog.model.User;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogInServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LogInServlet.class);

    public static final String USER_SESSION_ATTRIBUTE = "user";
    public static final String TARGET_PAGE_SESSION_ATTRIBUTE = "TARGET_PAGE";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        // try to log in
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = Database.getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute(USER_SESSION_ATTRIBUTE, user);
            logger.info("User logged in: " + user);
            // a user is logged in, redirect to target:
            if (session.getAttribute(TARGET_PAGE_SESSION_ATTRIBUTE) != null) {
                response.sendRedirect(String.valueOf(session.getAttribute(TARGET_PAGE_SESSION_ATTRIBUTE)));
            } else {
                response.sendRedirect("/");
            }
        } else {
            logger.info("User failed to log in: " + username);
            // show login page
            session.getServletContext().getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
}
