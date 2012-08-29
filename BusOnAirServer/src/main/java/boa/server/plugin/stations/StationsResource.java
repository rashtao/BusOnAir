package boa.server.plugin.stations;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.webadmin.rest.SessionFactoryImpl;

import boa.server.domain.*;

@Path( "/stations" )
public class StationsResource
{
    private final Database database;
    private BufferedWriter log;

    public StationsResource( @Context Database database,
            @Context HttpServletRequest req, @Context OutputFormat output ) throws IOException 
    {
        this( new SessionFactoryImpl( req.getSession( true ) ), database,
                output );

        StringBuffer fullURL = req.getRequestURL();
        StringBuffer queryString = new StringBuffer();
        queryString.append(req.getQueryString());
        if(!queryString.toString().equals("null"))
        	fullURL.append("?").append(queryString);
        
        log.write("\nHttpServletRequest(" + fullURL.toString() +")");
        log.flush(); 
    }

    public StationsResource( SessionFactoryImpl sessionFactoryImpl,
            Database database, OutputFormat output ) throws IOException 
    {
        FileWriter logFile = new FileWriter("/tmp/trasportaqstations.log");
        log = new BufferedWriter(logFile);
        this.database = database;
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/getall" )
    public Response getAll(@QueryParam( "objects" ) Boolean obj) throws IOException{        

        ArrayList<Station> stations = boa.server.domain.Stations.getStations().getAll();

        if(obj != null && obj){
	        boa.server.json.StationsObjects stationList = new boa.server.json.StationsObjects();        
	        for(Station s : stations){
	        	boa.server.json.Station js = new boa.server.json.Station(s);  
	        	stationList.add(js);        	
	        }
	        return Response.ok().entity(stationList).build();
        } else {
	        boa.server.json.Stations stationList = new boa.server.json.Stations();        
	        for(Station s : stations){
	        	boa.server.json.Station js = new boa.server.json.Station(s);  
	        	stationList.add(js);        	
	        }
	        return Response.ok().entity(stationList).build();
        }
    }
        
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/getneareststations" )
    public Response getNearestStations(
    		@QueryParam("lat") Double lat,
    		@QueryParam("lon") Double lon,
    		@QueryParam("range") Integer range,
    		@QueryParam( "objects" ) Boolean obj) throws IOException{        
        
    	log.write("\nstations/getneareststations");
        log.flush();
        
        if ( (lat == null) || (lon == null))
        	return Response.ok().entity(new boa.server.json.Response(400, "Lat and Lon cannot be blank")).build();

        Collection<Station> stations;
        
        if(range == null)
        	stations = boa.server.domain.Stations.getStations().getNearestStations(lat, lon);
        else
        	stations = boa.server.domain.Stations.getStations().getNearestStations(lat, lon, range);
              
//        if(stations.size() == 0){
//            return Response.status( 204 ).entity(
//                "No Station in specified range (" + range + ")").build();
//        }
        
        if(stations.size() == 0)
        	return Response.ok().entity(null).build();
        
        
        if(obj != null && obj){
            boa.server.json.StationsObjects stationList = new boa.server.json.StationsObjects();
            
            for(Station s : stations){
            	boa.server.json.Station js = new boa.server.json.Station(s);  
            	stationList.add(js);        	
            }

            return Response.ok().entity(stationList).build();        	
        } else {
            boa.server.json.Stations stationList = new boa.server.json.Stations();
            
            for(Station s : stations){
            	boa.server.json.Station js = new boa.server.json.Station(s);  
            	stationList.add(js);        	
            }

            return Response.ok().entity(stationList).build();        	
        }
    }    
    
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}")
    public Response getStation(@PathParam("id") Long id) throws IOException{
    	
    	log.write("\nstations/" + id);
        log.flush();
    	
        if ( id == null)
        	return Response.ok().entity(new boa.server.json.Response(400, "Id cannot be blank")).build();
        
    	boa.server.domain.Station s = boa.server.domain.Stations.getStations().getStationById(id);
    	
//    	if (s == null) 
//    		return Response.status( 204 ).entity(
//                    "No Station having specified id (" + id + ")").build();
    	
    	if (s != null){ 
    		boa.server.json.Station js = new boa.server.json.Station(s);
    		return Response.ok().entity(js).build();
    	} else {
        	return Response.ok().entity(new boa.server.json.Response(404, "No station having the specified id.")).build();
    	}
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/getfirststopfrom")
    public Response getFirstStopFrom(
    		@PathParam("id") Long id,
    		@QueryParam("time") Integer time) throws IOException{
    	
    	log.write("\ngetfirststopfrom/" + id);
        log.flush();
        
        boa.server.domain.Station s = boa.server.domain.Stations.getStations().getStationById(id);
        
        if(s == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No station having the specified id.")).build();
        
        if (time == null)
        	time = new Integer(0);
        
        boa.server.domain.Stop fs = s.getFirstStopFromTime(time);
        
        if(fs == null)
        	return Response.ok().entity("").build();
        
        boa.server.json.Stop js = new boa.server.json.Stop(fs);        
        return Response.ok().entity(js).build();
    }

    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/getallroutes")
    public Response getAllRoutes(@PathParam("id") Long id, @QueryParam( "objects" ) Boolean obj) throws IOException{

        boa.server.domain.Station s = boa.server.domain.Stations.getStations().getStationById(id);
        
        if(s == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No station having the specified id.")).build();
        
        Collection<Route> routes = s.getAllRoutes();
        
        if(routes.size() == 0)
        	return Response.ok().entity("").build();
        	
        if(obj != null && obj){
        	boa.server.json.RoutesObjects jroutes = new boa.server.json.RoutesObjects();
            for(boa.server.domain.Route r : routes)
            	jroutes.add(r);
            	   
            return Response.ok().entity(jroutes).build();
        } else {
        	boa.server.json.Routes jroutes = new boa.server.json.Routes();
            for(boa.server.domain.Route r : routes)
            	jroutes.add(r);
            	   
            return Response.ok().entity(jroutes).build();        	
        }
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/getallstops")
    public Response getAllStops(@PathParam("id") Long id, @QueryParam( "objects" ) Boolean obj) throws IOException{

        boa.server.domain.Station s = boa.server.domain.Stations.getStations().getStationById(id);
        
        if(s == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No station having the specified id.")).build();
        
        Collection<Stop> stops = (Collection<Stop>) s.getAllStops();
        
        if(stops.size() == 0)
        	return Response.ok().entity("").build();
        	
        if(obj != null && obj){
        	boa.server.json.StopsObjects jstops = new boa.server.json.StopsObjects();
            for(boa.server.domain.Stop stop : stops)
            	jstops.add(stop);
            	   
            return Response.ok().entity(jstops).build();
        } else {
        	boa.server.json.Stops jstops = new boa.server.json.Stops();
            for(boa.server.domain.Stop stop : stops)
            	jstops.add(stop);
            	   
            return Response.ok().entity(jstops).build();        	
        }
    }
    
    
}
