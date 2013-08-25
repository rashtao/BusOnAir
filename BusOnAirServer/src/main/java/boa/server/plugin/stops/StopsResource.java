package boa.server.plugin.stops;


import boa.server.domain.DbConnection;
import boa.server.domain.Stop;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.webadmin.rest.SessionFactoryImpl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Path("/stops")
public class StopsResource {
    private BufferedWriter log;

    public StopsResource(@Context Database database,
                         @Context HttpServletRequest req, @Context OutputFormat output) throws IOException {
        this(new SessionFactoryImpl(req.getSession(true)), database,
                output);

        StringBuffer fullURL = req.getRequestURL();
        StringBuffer queryString = new StringBuffer();
        queryString.append(req.getQueryString());
        if (!queryString.toString().equals("null"))
            fullURL.append("?").append(queryString);

        log.write("\nHttpServletRequest(" + fullURL.toString() + ")");
        log.flush();
    }

    public StopsResource(SessionFactoryImpl sessionFactoryImpl,
                         Database database, OutputFormat output) throws IOException {
        FileWriter logFile = new FileWriter("/tmp/trasportaqstops.log");
        log = new BufferedWriter(logFile);
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getall")
    public Response getAll(@QueryParam("objects") Boolean obj) {

        Iterable<Stop> stops = boa.server.domain.Stops.getStops().getAll();

        if (obj != null && obj) {
            boa.server.plugin.json.StopsObjects stopList = new boa.server.plugin.json.StopsObjects();
            for (Stop r : stops) {
                boa.server.plugin.json.Stop jr = new boa.server.plugin.json.Stop(r);
                stopList.add(jr);
            }

            return Response.ok().entity(stopList).build();
        } else {
            boa.server.plugin.json.Stops stopList = new boa.server.plugin.json.Stops();
            for (Stop r : stops) {
                boa.server.plugin.json.Stop jr = new boa.server.plugin.json.Stop(r);
                stopList.add(jr);
            }

            return Response.ok().entity(stopList).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getStop(@PathParam("id") Integer id) {

        if (id == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(400, "id cannot be blank")).build();

        boa.server.domain.Stop stop = boa.server.domain.Stops.getStops().getStopById(id);

        if (stop != null) {
            boa.server.plugin.json.Stop jr = new boa.server.plugin.json.Stop(stop);
            return Response.ok().entity(jr).build();
        } else {
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No stop having the specified id value.")).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/getnextinstation")
    public Response getNextInStation(@PathParam("id") Integer id) throws IOException {

        log.write("\ngetNextInStation/" + id);
        log.flush();

        if (id == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(400, "id cannot be blank")).build();

        boa.server.domain.Stop stop = boa.server.domain.Stops.getStops().getStopById(id);

        if (stop == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No stop having the specified id value.")).build();

        Stop ns = stop.getNextInStation();

        if (ns == null)
            return Response.ok().entity("").build();

        boa.server.plugin.json.Stop jstop = new boa.server.plugin.json.Stop(ns);
        return Response.ok().entity(jstop).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/getprevinstation")
    public Response getPrevInStation(@PathParam("id") Integer id) throws IOException {

        log.write("\ngetPrevInStation/" + id);
        log.flush();

        if (id == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(400, "id cannot be blank")).build();

        boa.server.domain.Stop stop = boa.server.domain.Stops.getStops().getStopById(id);

        if (stop == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No stop having the specified id value.")).build();

        Stop ns = stop.getPrevInStation();

        if (ns == null)
            return Response.ok().entity("").build();

        boa.server.plugin.json.Stop jstop = new boa.server.plugin.json.Stop(ns);
        return Response.ok().entity(jstop).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/gettime")
    public Response getTime(@PathParam("id") Integer id) throws IOException {

        log.write("\ngetTime/" + id);
        log.flush();

        if (id == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(400, "id cannot be blank")).build();

        boa.server.domain.Stop stop = boa.server.domain.Stops.getStops().getStopById(id);

        if (stop == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No stop having the specified id value.")).build();

        boa.server.plugin.json.Time jt = new boa.server.plugin.json.Time(stop.getTime());
        return Response.ok().entity(jt).build();
    }

}
