package ie.transportdublin.server.plugin.stations;

import domain.*;

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

@Path( "/stations" )
public class StationsResource
{

    private json.Stations stationList;
    private final Database database;
    private BufferedWriter log;

    public StationsResource( @Context Database database,
            @Context HttpServletRequest req, @Context OutputFormat output ) throws IOException 
    {
        this( new SessionFactoryImpl( req.getSession( true ) ), database,
                output );

        String fullURL = req.getRequestURL().append("?").append( 
            req.getQueryString()).toString();        
        log.write("\nHttpServletRequest(" + fullURL +")");
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
    public Response getAll() throws IOException{        
        log.write("\nstations/getall");
        log.flush();
              
        stationList = new json.Stations();

        ArrayList<Station> stations = domain.Stations.getStations().getAll();

        for(Station s : stations){
        	json.Station js = new json.Station(s);  
        	stationList.add(js);        	
        }

        return Response.ok().entity(stationList).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/getneareststations" )
    public Response getNearestStations(
    		@QueryParam("lat") Double lat,
    		@QueryParam("lon") Double lon,
    		@QueryParam("range") Integer range) throws IOException{        
        
    	log.write("\nstations/getneareststations");
        log.flush();

        
        if ( (lat == null) || (lon == null))
            return Response.serverError().entity( "Lat and Lon cannot be blank" ).build();

        Collection<Station> stations;
        
        if(range == null)
        	stations = domain.Stations.getStations().nearestStations(lat, lon);
        else
        	stations = domain.Stations.getStations().nearestStations(lat, lon, range);
        
                
        if(stations.size() == 0){
            return Response.status( 400 ).entity(
                "No Station in specified range (" + range + ")").build();
        }
        
        stationList = new json.Stations();

        for(Station s : stations){
        	json.Station js = new json.Station(s);  
        	stationList.add(js);        	
        }

        return Response.ok().entity(stationList).build();
    }    
    
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}")
    public Response getStation(@PathParam("id") int id) throws IOException{
    	
    	log.write("\nstations/" + id);
        log.flush();
    	
//        if ( id == null)
//            return Response.serverError().entity( "ID cannot be blank" ).build();
        
    	domain.Station s = domain.Stations.getStations().getStationById(id);
    	
    	if (s == null) 
    		return Response.status( 400 ).entity(
                    "No Station having specified id (" + id + ")").build();
    	
    	return Response.ok().entity(s).build();
    }
}
