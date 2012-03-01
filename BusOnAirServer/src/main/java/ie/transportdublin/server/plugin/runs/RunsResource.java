package ie.transportdublin.server.plugin.runs;

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

@Path( "/runs" )
public class RunsResource{
    private final Database database;
    private BufferedWriter log;

    public RunsResource( @Context Database database,
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

    public RunsResource( SessionFactoryImpl sessionFactoryImpl,
            Database database, OutputFormat output ) throws IOException 
    {
        FileWriter logFile = new FileWriter("/tmp/trasportaqruns.log");
        log = new BufferedWriter(logFile);
        this.database = database;
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/getall" )
    public Response getAll() throws IOException{        
        log.write("\nruns/getall");
        log.flush();
                      
        json.Runs runList = new json.Runs();

        Iterable<Run> runs = domain.Runs.getRuns().getAll();
         
        for(Run r : runs){
        	json.Run jr = new json.Run(r);  
        	runList.add(jr);        	
        }

        return Response.ok().entity(runList).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}")
    public Response getrun(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\nruns/" + id);
        log.flush();

        if ( id == null)
            return Response.status( 400 ).entity( "id cannot be blank" ).build();
        
    	domain.Run run = domain.Runs.getRuns().getRunById(id);
    	 
    	if (run != null){
    		json.Run jr = new json.Run(run);    	
    		return Response.ok().entity(jr).build();
    	} else {
    		return Response.status( 404 ).entity( "No run having the specified id value." ).build();
    	}
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/getroute")
    public Response getRoute(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetroute/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.status( 404 ).entity( "No run having the specified id." ).build();
        
    	Route route = run.getRoute();
                	
        json.Route jroute = new json.Route(route);
        	   
        return Response.ok().entity(jroute).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/update")
    public Response updateRun(
    		@PathParam("id") Integer id,
    		@QueryParam( "checkpointid" ) Integer checkpointid,
    		@QueryParam( "time" ) Integer time) throws IOException{
    	
    	log.write("\nupdate/" + id);
        log.flush();
        
        if ( id == null || checkpointid == null || time == null )
            return Response.status( 400 ).entity( "id, checkpointid, time cannot be blank" ).build();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        if(run == null)
        	return Response.status( 404 ).entity( "No run having the specified id." ).build();
        
        domain.CheckPoint cp = run.getCheckPointById(checkpointid);
        
        if(cp == null)
        	return Response.status( 400 ).entity( "No checkpoint having the specified id." ).build();
                
        run.updateRun(cp, time);
        	   
        return Response.ok().entity("DONE").build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/getfirststop")
    public Response getFirstStop(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetfirststop/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.status( 404 ).entity( "No run having the specified id." ).build();
        
    	Stop stop = run.getFirstStop();
                	
        json.Stop jstop = new json.Stop(stop);
        	   
        return Response.ok().entity(jstop).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/getallstops")
    public Response getAllStops(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetallstops/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.status( 404 ).entity( "No run having the specified id." ).build();
    	
    	json.Stops jstops = new json.Stops();
        for(Stop s : run.getAllStops())
        	jstops.add(s);
        	        	   
        return Response.ok().entity(jstops).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/checkpoints/getall")
    public Response getAllCheckPoints(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetallcheckpoints/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.status( 404 ).entity( "No run having the specified id." ).build();
    	
    	json.CheckPoints cps = new json.CheckPoints();
        for(CheckPoint cp : run.getAllCheckPoints())
        	cps.add(cp, run.getId());
        	        	   
        return Response.ok().entity(cps).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/checkpoints/{idcp}")
    public Response getCheckPointById(@PathParam("id") Integer id, @PathParam("idcp") Integer idcp) throws IOException{
    	
    	log.write("\ngetCheckPointById/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.status( 404 ).entity( "No run having the specified id." ).build();
    	
        CheckPoint cp = run.getCheckPointById(idcp);
        
        if(cp == null)
        	return Response.status( 404 ).entity( "No CheckPoint having the specified id." ).build();
        
        json.CheckPoint jscp = new json.CheckPoint(cp);
        jscp.setFrom("/stops/" + cp.getFrom().getId());
        jscp.setTowards("/stops/" + cp.getTowards().getId());
        jscp.setNext("/runs/" + run.getId() + "/checkpoints/" + cp.getNextCheckPoint().getId());
        return Response.ok().entity(jscp).build();
    }

}
