package boa.server.plugin.routes;


import boa.server.domain.DbConnection;
import boa.server.domain.Route;
import boa.server.domain.Run;
import boa.server.domain.Station;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

@Path("/routes")
public class RoutesResource {
    private BufferedWriter log;

    public RoutesResource(@Context Database database,
                          @Context HttpServletRequest req, @Context OutputFormat output) throws IOException {
        this(database
        );

        StringBuffer fullURL = req.getRequestURL();
        StringBuffer queryString = new StringBuffer();
        queryString.append(req.getQueryString());
        if (!queryString.toString().equals("null"))
            fullURL.append("?").append(queryString);

        log.write("\nHttpServletRequest(" + fullURL.toString() + ")");
        log.flush();
    }

    public RoutesResource(Database database) throws IOException {
        FileWriter logFile = new FileWriter("/tmp/trasportaqroutes.log");
        log = new BufferedWriter(logFile);
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getall")
    public Response getAll(@QueryParam("objects") Boolean obj) throws IOException {

        Iterable<Route> routes = boa.server.domain.Routes.getRoutes().getAll();

        if (obj != null && obj) {
            boa.server.plugin.json.RoutesObjects routeList = new boa.server.plugin.json.RoutesObjects();
            for (Route r : routes) {
                boa.server.plugin.json.Route jr = new boa.server.plugin.json.Route(r);
                routeList.add(jr);
            }

            return Response.ok().entity(routeList).build();
        } else {
            boa.server.plugin.json.Routes routeList = new boa.server.plugin.json.Routes();
            for (Route r : routes) {
                boa.server.plugin.json.Route jr = new boa.server.plugin.json.Route(r);
                routeList.add(jr);
            }

            return Response.ok().entity(routeList).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getRoute(@PathParam("id") Integer id) throws IOException {

        log.write("\nroutes/" + id);
        log.flush();

        if (id == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(400, "id cannot be blank")).build();

        boa.server.domain.Route route = boa.server.domain.Routes.getRoutes().getRouteById(id);

        if (route != null) {
            boa.server.plugin.json.Route jr = new boa.server.plugin.json.Route(route);
            return Response.ok().entity(jr).build();
        } else {
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No route having the specified id value.")).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/getallruns")
    public Response getAllRuns(@PathParam("id") Integer id, @QueryParam("objects") Boolean obj) throws IOException {
        if (id == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(400, "id cannot be blank")).build();

        boa.server.domain.Route route = boa.server.domain.Routes.getRoutes().getRouteById(id);

        if (route == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No route having the specified id value.")).build();

        ArrayList<Run> runs = route.getAllRuns();

        if (runs.size() == 0)
            return Response.ok().entity("").build();

        if (obj != null && obj) {
            boa.server.plugin.json.RunsObjects jruns = new boa.server.plugin.json.RunsObjects();
            for (boa.server.domain.Run r : runs)
                jruns.add(r);

            return Response.ok().entity(jruns).build();
        } else {
            boa.server.plugin.json.Runs jruns = new boa.server.plugin.json.Runs();
            for (boa.server.domain.Run r : runs)
                jruns.add(r);

            return Response.ok().entity(jruns).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/getallstations")
    public Response getAllStations(@PathParam("id") Integer id, @QueryParam("objects") Boolean obj) throws IOException {
        if (id == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(400, "id cannot be blank")).build();

        boa.server.domain.Route route = boa.server.domain.Routes.getRoutes().getRouteById(id);

        if (route == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No route having the specified id value.")).build();

        ArrayList<Station> stations = route.getAllStations();

        if (stations.size() == 0)
            return Response.ok().entity("").build();

        if (obj != null && obj) {
            boa.server.plugin.json.StationsObjects jstations = new boa.server.plugin.json.StationsObjects();
            for (boa.server.domain.Station s : stations)
                jstations.add(s);

            return Response.ok().entity(jstations).build();
        } else {
            boa.server.plugin.json.Stations jstations = new boa.server.plugin.json.Stations();
            for (boa.server.domain.Station s : stations)
                jstations.add(s);

            return Response.ok().entity(jstations).build();
        }
    }
}
