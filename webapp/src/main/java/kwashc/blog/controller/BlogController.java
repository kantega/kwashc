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
import kwashc.blog.model.Comment;
import kwashc.blog.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * This is the main page, and serves the comments get requests, and add comments on post requests.
 */
@Controller
public class BlogController {

    @RequestMapping(value = {"/", "/blog"}, method = RequestMethod.GET)
    public String showBlog(Model model) {
        model.addAttribute("comments", Database.getComments());
        return "blog";
    }

    @RequestMapping(value = "/blog", method = RequestMethod.POST)
    public String addComment(HttpServletRequest request) {
        String title = request.getParameter("title");
        String comment = request.getParameter("comment");
        String homepage = request.getParameter("homepage");

        User user = (User) request.getSession(true).getAttribute("USER");
        Database.addComment(new Comment(user, homepage, title, comment));

        return "redirect:/blog"; //redirect to avoid double postings
    }
}
