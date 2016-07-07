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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * The edit function is really simple.
 *
 * If an admin finds a comment that needs editing, he replaces the text with a short notice:
 *
 * 'Comment is edited and anonymised!'
 */
@Controller
public class EditController {

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editComment(HttpServletRequest req) throws ServletException, IOException {
        int commentID = Integer.parseInt(req.getParameter("commentID"));
        Comment c = Database.getComment(commentID);
        String title = c.getTitle();
        c.setTitle(title + " - edited");
        c.setComment("Comment is edited and anonymised!");


        return "redirect:/blog"; //redirect to BlogController
    }
}
