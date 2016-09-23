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

package kwashc.blog.internal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static kwashc.blog.database.AccountsRepository.getSecret;

/** This servlet is needed for the KWASHC testing server, and is not considered part of the application to be secured.
 *  Do not modify.
 */
public class PasswordTestServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = null;
        try{
            StringBuffer res = new StringBuffer("<html><head></head><body>");
            res.append("P1:").append(getSecret("username")).append(":P1 P2:").append(getSecret("anotheruser")).append(":P2");
            res.append("</body></html>");
            writer = response.getWriter();
            writer.print(res);
            writer.flush();
        } catch (Exception e ){
            // empty catch, but you should not change this code, remember? :-)
        } finally {
            if(writer != null) writer.close();
        }
    }
}
