package boa.server.plugin.stations;

import boa.server.domain.DbConnection;
import boa.server.domain.Route;
import boa.server.domain.Station;
import boa.server.domain.Stop;
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
import java.util.Collection;

@Path("/stations")
public class StationsResource {
    private BufferedWriter log;

    public StationsResource(@Context Database database,
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

    public StationsResource(Database database) throws IOException {
        FileWriter logFile = new FileWriter("/tmp/trasportaqstations.log");
        log = new BufferedWriter(logFile);
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getall")
    public Response getAll(@QueryParam("objects") Boolean obj) throws IOException {

        ArrayList<Station> stations = boa.server.domain.Stations.getStations().getAll();

        if (obj != null && obj) {
            boa.server.plugin.json.StationsObjects stationList = new boa.server.plugin.json.StationsObjects();
            for (Station s : stations) {
                boa.server.plugin.json.Station js = new boa.server.plugin.json.Station(s);
                stationList.add(js);
            }
            return Response.ok().entity(stationList).build();
        } else {
            boa.server.plugin.json.Stations stationList = new boa.server.plugin.json.Stations();
            for (Station s : stations) {
                boa.server.plugin.json.Station js = new boa.server.plugin.json.Station(s);
                stationList.add(js);
            }
            return Response.ok().entity(stationList).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getneareststations")
    public Response getNearestStations(
            @QueryParam("lat") Double lat,
            @QueryParam("lon") Double lon,
            @QueryParam("range") Integer range,
            @QueryParam("objects") Boolean obj) throws IOException {

        log.write("\nstations/getneareststations");
        log.flush();

        if ((lat == null) || (lon == null))
            return Response.ok().entity(new boa.server.plugin.json.Response(400, "Lat and Lon cannot be blank")).build();

        Collection<Station> stations;

        if (range == null)
            stations = boa.server.domain.Stations.getStations().getNearestStations(lat, lon);
        else
            stations = boa.server.domain.Stations.getStations().getNearestStations(lat, lon, range);

        if (stations.size() == 0)
            return Response.ok().entity(null).build();

        if (obj != null && obj) {
            boa.server.plugin.json.StationsObjects stationList = new boa.server.plugin.json.StationsObjects();

            for (Station s : stations) {
                boa.server.plugin.json.Station js = new boa.server.plugin.json.Station(s);
                stationList.add(js);
            }

            return Response.ok().entity(stationList).build();
        } else {
            boa.server.plugin.json.Stations stationList = new boa.server.plugin.json.Stations();

            for (Station s : stations) {
                boa.server.plugin.json.Station js = new boa.server.plugin.json.Station(s);
                stationList.add(js);
            }

            return Response.ok().entity(stationList).build();
        }
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getStation(@PathParam("id") Integer id) throws IOException {

        log.write("\nstations/" + id);
        log.flush();

        if (id == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(400, "Id cannot be blank")).build();

        boa.server.domain.Station s = boa.server.domain.Stations.getStations().getStationById(id);

        if (s != null) {
            boa.server.plugin.json.Station js = new boa.server.plugin.json.Station(s);
            return Response.ok().entity(js).build();
        } else {
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No station having the specified id.")).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/getfirststopfrom")
    public Response getFirstStopFrom(
            @PathParam("id") Integer id,
            @QueryParam("time") Integer time) throws IOException {

        log.write("\ngetfirststopfrom/" + id);
        log.flush();

        boa.server.domain.Station s = boa.server.domain.Stations.getStations().getStationById(id);

        if (s == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No station having the specified id.")).build();

        if (time == null)
            time = new Integer(0);

        boa.server.domain.Stop fs = s.getFirstStopFromTime(time);

        if (fs == null)
            return Response.ok().entity("").build();

        boa.server.plugin.json.Stop js = new boa.server.plugin.json.Stop(fs);
        return Response.ok().entity(js).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/getallroutes")
    public Response getAllRoutes(@PathParam("id") Integer id, @QueryParam("objects") Boolean obj) throws IOException {

        boa.server.domain.Station s = boa.server.domain.Stations.getStations().getStationById(id);

        if (s == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No station having the specified id.")).build();

        Collection<Route> routes = s.getAllRoutes();

        if (routes.size() == 0)
            return Response.ok().entity("").build();

        if (obj != null && obj) {
            boa.server.plugin.json.RoutesObjects jroutes = new boa.server.plugin.json.RoutesObjects();
            for (boa.server.domain.Route r : routes)
                jroutes.add(r);

            return Response.ok().entity(jroutes).build();
        } else {
            boa.server.plugin.json.Routes jroutes = new boa.server.plugin.json.Routes();
            for (boa.server.domain.Route r : routes)
                jroutes.add(r);

            return Response.ok().entity(jroutes).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/getallstops")
    public Response getAllStops(@PathParam("id") Integer id, @QueryParam("objects") Boolean obj) throws IOException {

        boa.server.domain.Station s = boa.server.domain.Stations.getStations().getStationById(id);

        if (s == null)
            return Response.ok().entity(new boa.server.plugin.json.Response(404, "No station having the specified id.")).build();

        Collection<Stop> stops = (Collection<Stop>) s.getAllStops();

        if (stops.size() == 0)
            return Response.ok().entity("").build();

        if (obj != null && obj) {
            boa.server.plugin.json.StopsObjects jstops = new boa.server.plugin.json.StopsObjects();
            for (boa.server.domain.Stop stop : stops)
                jstops.add(stop);

            return Response.ok().entity(jstops).build();
        } else {
            boa.server.plugin.json.Stops jstops = new boa.server.plugin.json.Stops();
            for (boa.server.domain.Stop stop : stops)
                jstops.add(stop);

            return Response.ok().entity(jstops).build();
        }
    }
}
