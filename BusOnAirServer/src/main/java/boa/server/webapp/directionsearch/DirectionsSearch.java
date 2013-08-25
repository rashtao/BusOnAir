package boa.server.webapp.directionsearch;


import boa.server.domain.DbConnection;
import boa.server.domain.Stops;
import boa.server.routing.Criteria;
import boa.server.routing.ShortestPathGeo;
import boa.server.routing.StopMediator;
import boa.server.webapp.webappjson.Directions;
import boa.server.webapp.webappjson.DirectionsList;
import boa.server.webapp.webappjson.DirectionsRoute;
import boa.server.webapp.webappjson.DirectionsWalk;
import com.vividsolutions.jts.geom.Coordinate;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.webadmin.rest.SessionFactoryImpl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/directions")
public class DirectionsSearch {
    private BufferedWriter log;

    public DirectionsSearch(@Context Database database,
                            @Context HttpServletRequest req, @Context OutputFormat output) throws IOException {
        this(new SessionFactoryImpl(req.getSession(true)), database, output);

        String fullURL = req.getRequestURL().append("?").append(
                req.getQueryString()).toString();
        log.write("\nHttpServletRequest(" + fullURL + ")");
        log.flush();
    }

    public DirectionsSearch(SessionFactoryImpl sessionFactoryImpl, Database database, OutputFormat output) throws IOException {
        FileWriter logFile = new FileWriter("/tmp/trasportaqdirections.log");
        log = new BufferedWriter(logFile);
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response directions(
            @QueryParam("lat1") double lat1,
            @QueryParam("lon1") double lon1,
            @QueryParam("lat2") double lat2,
            @QueryParam("lon2") double lon2,
            @QueryParam("time") double time) throws IOException {

        // metodo utilizzato da web client di transportdublin


        if (lat1 == 0 || lat1 == 0 || lon1 == 0 || lon2 == 0 || time == 0)
            return Response.serverError().entity("params cannot be blank").build();

        log.write("\ngetDirection?lat1=" + lat1 + "&lon1=" + lon1 + "&lat2=" + lat2 + "&lon2=" + lon2 + "&time=" + time);
        log.flush();

        ShortestPathGeo mysp = new ShortestPathGeo((int) time, 1000, lat1, lon1, lat2, lon2, 500, Criteria.MINCHANGES);
        StopMediator cache = mysp.shortestPath();
        boa.server.plugin.json.Directions directs = new boa.server.plugin.json.Directions(new Coordinate(lon1, lat1), mysp.getArrivalList());

        DirectionsList directionsList2 = new DirectionsList();

        if (directs.getDirectionsList().size() == 0) {
            return Response.ok().entity(null).build();
        }

        boa.server.plugin.json.Direction sp = directs.getDirectionsList().iterator().next();
        List<boa.server.plugin.json.DirectionRoute> routesDirection = sp.getRoutes();
        List<boa.server.plugin.json.DirectionWalk> walksDirection = sp.getWalks();

        List<DirectionsRoute> routes = new ArrayList<DirectionsRoute>();
        List<DirectionsWalk> walks = new ArrayList<DirectionsWalk>();

        for (boa.server.plugin.json.DirectionRoute rd : routesDirection) {
            boa.server.domain.Stop s1 = cache.get(Stops.getStops().getStopById(rd.getDepId()));
            boa.server.domain.Stop s2 = cache.get(Stops.getStops().getStopById(rd.getArrId()));
            log.write("\n" + s1);
            log.write("\n" + s2);
            log.flush();
            routes.add(new DirectionsRoute(s1, s2));
        }

        for (boa.server.plugin.json.DirectionWalk rw : walksDirection) {
            walks.add(new DirectionsWalk(
                    rw.getDeparture().getLat(),
                    rw.getDeparture().getLon(),
                    rw.getArrival().getLat(),
                    rw.getArrival().getLon(),
                    rw.getDuration()));
        }

        directionsList2.add(new Directions(routes, walks));

        return Response.ok().entity(directionsList2).build();
    }
}
