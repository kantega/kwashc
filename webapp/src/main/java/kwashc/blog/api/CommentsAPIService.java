package kwashc.blog.api;

import kwashc.blog.database.Database;
import kwashc.blog.model.Comment;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * All self-respecting applications must have a RESTful API! We wouldn't want it to be bullied by all the other
 * applications in the repository! Besides, someone might want to write a mobile app or something.
 *
 */
@Path("comments")
public class CommentsAPIService {

    /**
     * Lists all available comments at /blog/api/comments/list
     *
     * @param response
     * @return the comments list serialized as JSON
     */
    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Comment> listComments(@Context HttpServletResponse response){
        response.setHeader("Content-Type", "text/html;charset=ISO-8859-1"); //force charset

        List<Comment> result = new ArrayList<Comment>();
        result.addAll(Database.getComments());
        return result;
    }
}
