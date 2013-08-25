package boa.server.webapp.config;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/populatecache")
public class PopulateCacheResource {

    private final Database database;

    public PopulateCacheResource(@Context Database database,
                                 @Context HttpServletRequest req, @Context OutputFormat output) {
        this(database);
    }

    public PopulateCacheResource(Database database) {
        this.database = database;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response populateCache() {
        int i = 0;
        for (Node node : this.database.graph.getAllNodes()) {
            node.getPropertyKeys();
            node.getPropertyValues();
            for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
                relationship.getPropertyKeys();
                relationship.getPropertyValues();
            }
        }
        return Response.ok().entity(i + "").build();
    }
}




