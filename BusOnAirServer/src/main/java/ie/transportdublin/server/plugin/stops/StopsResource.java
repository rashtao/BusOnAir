package ie.transportdublin.server.plugin.stops;

import domain.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

@Path( "/stops" )
public class StopsResource{
    private final Database database;
    private BufferedWriter log;

    public StopsResource( @Context Database database,
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

    public StopsResource( SessionFactoryImpl sessionFactoryImpl,
            Database database, OutputFormat output ) throws IOException 
    {
        FileWriter logFile = new FileWriter("/tmp/trasportaqstops.log");
        log = new BufferedWriter(logFile);
        this.database = database;
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/getall" )
    public Response getAll() throws IOException{        
        log.write("\nstops/getall");
        log.flush();
        
        return Response.status( 404 ).entity( "Not implemented yet!" ).build();
              
//        json.Stops stopList = new json.Stops();
//
//        Iterable<Stop> stops = domain.Stops.getStops().getAll();
//         
//        for(Stop r : stops){
//        	json.Stop jr = new json.Stop(r);  
//        	stopList.add(jr);        	
//        }
//
//        return Response.ok().entity(stopList).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}")
    public Response getStop(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\nstops/" + id);
        log.flush();

        return Response.status( 404 ).entity( "Not implemented yet!" ).build();

//        if ( id == null)
//            return Response.status( 400 ).entity( "id cannot be blank" ).build();
//        
//    	domain.Stop stop = domain.Stops.getStops().getStopByLine(line);
//    	 
//    	if (stop != null){
//    		json.Stop jr = new json.Stop(stop);    	
//    		return Response.ok().entity(jr).build();
//    	} else {
//    		return Response.status( 404 ).entity( "No stop having the specified line value." ).build();
//    	}
    }
}
