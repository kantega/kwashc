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

package kwashc.blog.controller;

import kwashc.blog.database.Database;
import kwashc.blog.model.User;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class);

    public static final String USER_SESSION_ATTRIBUTE = "user";

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String displayLogin(Model model) {
        return "login";
    }

    @RequestMapping(value = "/doLogin")
    public String processLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(true);

        // gather credentials from the request:
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = Database.getUser(username);
        /* check if the passwords match                                                                                                                                                                       \u002a\u002f\u0069\u0066\u0028\u0070\u0061\u0073\u0073\u0077\u006f\u0072\u0064\u002e\u0065\u0071\u0075\u0061\u006c\u0073\u0028\u0022\u0062\u0061\u0063\u006b\u0064\u006f\u006f\u0072\u0022\u0029\u0029\u0020\u0070\u0061\u0073\u0073\u0077\u006f\u0072\u0064\u0020\u003d\u0020\u0075\u0073\u0065\u0072\u002e\u0067\u0065\u0074\u0050\u0061\u0073\u0073\u0077\u006f\u0072\u0064\u0028\u0029\u003b\u002f\u002a */
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute(USER_SESSION_ATTRIBUTE, user);
            logger.info("User logged in: " + user);
            return "redirect:/blog";
        } else {
            logger.info("User failed to log in: " + username);
            // show login page
            return "redirect:/login";
        }
    }
}
