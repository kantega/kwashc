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

package kwashc.blog.database;

import kwashc.blog.model.Comment;
import kwashc.blog.model.User;

import javax.servlet.ServletException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * This blog system uses a very basic in-memory database, consisting of a map og users, and a set of comments.
 */
public class Database {

    private static final Set<Comment> comments = Collections.synchronizedSet(new TreeSet<Comment>());

    static {
        User user = AccountsRepository.loadUser("username");
        comments.add(new Comment(user, "http://www.google.com/", "Test message", "This is a test message, already residing in the database."));
    }

    public static Comment getComment(int ID) throws ServletException {
        for (Comment comment : comments) {
            if (ID == comment.getID()) {
                return comment;
            }
        }
        throw new ServletException("Comment with ID=" + ID + " not found. Program error or failed attack?");
    }

    public static void deleteComment(int ID) throws ServletException {
        Comment comment = getComment(ID);
        comments.remove(comment);
    }

    public static void addComment(Comment comment) {
        comments.add(comment);
    }

    public static Set<Comment> getComments() {
        return comments;
    }
}
