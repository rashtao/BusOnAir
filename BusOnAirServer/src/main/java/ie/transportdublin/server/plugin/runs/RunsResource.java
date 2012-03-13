package ie.transportdublin.server.plugin.runs;

import domain.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
    @Path("/{id}")
    public Response getRun(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\nruns/" + id);
        log.flush();

        if ( id == null)
        	return Response.ok().entity(new json.Response(400, "id cannot be blank")).build();
        
    	domain.Run run = domain.Runs.getRuns().getRunById(id);
    	 
    	if (run != null){
    		json.Run jr = new json.Run(run);    	
    		return Response.ok().entity(jr).build();
    	} else {
    		return Response.ok().entity(new json.Response(404, "No run having the specified id value.")).build();
    	}
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/checkpointpass")
    public Response checkPointPass(
    		@PathParam("id") Integer id,
    		@QueryParam( "checkpointid" ) Integer checkpointid,
    		@QueryParam( "time" ) Integer time) throws IOException{
    	
    	log.write("\ncheckPointPass/" + id);
        log.flush();
        
        if ( id == null || checkpointid == null || time == null )
        	return Response.ok().entity(new json.Response(400, "id, checkpointid, time cannot be blank")).build();

        domain.Run run = domain.Runs.getRuns().getRunById(id);
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
        
        domain.CheckPoint cp = run.getCheckPointById(checkpointid);
        
        if(cp == null)
        	return Response.ok().entity(new json.Response(404, "No checkpoint having the specified id.")).build();
                
        run.checkPointPass(cp, time);
        	   
        json.Response jr = new json.Response(200, "OK");
        
        return Response.ok().entity(jr).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/getallstops")
    public Response getAllStops(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetallstops/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
    	
    	json.Stops jstops = new json.Stops();
        for(Stop s : run.getAllStops())
        	jstops.add(s);
        	        	   
        return Response.ok().entity(jstops).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/checkpoints/getall")
    public Response getAllCheckPoints(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetallcheckpoints/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
    	
    	json.CheckPoints cps = new json.CheckPoints();
        for(CheckPoint cp : run.getAllCheckPoints())
        	cps.add(cp, run.getId());
        	        	   
        return Response.ok().entity(cps).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/checkpoints/{idcp}")
    public Response getCheckPointById(@PathParam("id") Integer id, @PathParam("idcp") Integer idcp) throws IOException{
    	
    	log.write("\ngetCheckPointById/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
    	
        CheckPoint cp = run.getCheckPointById(idcp);
        
        if(cp == null)
        	return Response.ok().entity(new json.Response(404, "No CheckPoint having the specified id.")).build();
        
        json.CheckPoint jscp = new json.CheckPoint(cp);

        return Response.ok().entity(jscp).build();
    }

    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/checkpoints/{idcp}/gettime")
    public Response getCheckPointTime(@PathParam("id") Integer id, @PathParam("idcp") Integer idcp) throws IOException{
    	
    	log.write("\ngetCheckPointTime/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
    	
        CheckPoint cp = run.getCheckPointById(idcp);
        
        if(cp == null)
        	return Response.ok().entity(new json.Response(404, "No CheckPoint having the specified id.")).build();
        
        json.Time jt = new json.Time(cp.getTime());

        return Response.ok().entity(jt).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/restore")
    public Response restore(
    		@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\nrestore/" + id);
        log.flush();
                
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
        
        run.restore();
                	   
        json.Response jr = new json.Response(200, "OK");
        return Response.ok().entity(jr).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/getlaststop")
    public Response getLastStop(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetlaststop/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
        
    	Stop stop = run.getLastStop();
                	
        json.Stop jstop = new json.Stop(stop);
        	   
        return Response.ok().entity(jstop).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/getlastcheckpoint")
    public Response getLastCheckPoint(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetlastcheckpoint/" + id);
        log.flush();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
     
        CheckPoint cp = run.getLastCheckPoint();
        
        if(cp == null)
        	return Response.ok().entity(new json.Response(500, "GRAVE: No CheckPoint found.")).build();
        
        json.CheckPoint jscp = new json.CheckPoint(cp);
        
        return Response.ok().entity(jscp).build();
    }    
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/updateposition")
    public Response updatePosition(
    		@PathParam("id") Integer id,
    		@QueryParam( "time" ) Integer time,
    		@QueryParam( "lat" ) Double lat,
    		@QueryParam( "lon" ) Double lon) throws IOException{
    	
    	log.write("\nupdateposition/" + id);
        log.flush();
        
        if ( id == null || lat == null || lon == null )
        	return Response.ok().entity(new json.Response(400, "id, lat, lon cannot be blank")).build();
        
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
                
        run.updatePosition(lat,lon,time);
        	   
        return Response.ok().entity(new json.Response(200, "OK")).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/addcheckpoint")
    public Response addCheckPoint(
    		@PathParam("id") Integer id,
    		@QueryParam( "time" ) Integer time,
    		@QueryParam( "lat" ) Double lat,
    		@QueryParam( "lon" ) Double lon) throws IOException{
    	
    	log.write("\naddcheckpoint/");
        log.flush();
                
        domain.Run run = domain.Runs.getRuns().getRunById(id);
        if(run == null)
        	return Response.ok().entity(new json.Response(404, "No run having the specified id.")).build();
                
        run.addCheckPoint(lat,lon,time);
        
        	   
        json.Response jr = new json.Response(200, "OK");
        return Response.ok().entity(jr).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/restoreall")
    public Response restoreAll() throws IOException{
    	
    	log.write("\ninit/");
        log.flush();
                
        for(Run r : domain.Runs.getRuns().getAll())
        	r.restore();
        	   
        json.Response jr = new json.Response(200, "OK");
        return Response.ok().entity(jr).build();
    }

    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/getposition")
    public Response getPosition(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetposition/" + id);
        log.flush();

        if ( id == null)
        	return Response.ok().entity(new json.Response(400, "id cannot be blank")).build();
        
    	domain.Run run = domain.Runs.getRuns().getRunById(id);
    	 
    	if (run != null){
    		json.Position p = new json.Position(new json.Coordinate(run.getLatitude(), run.getLongitude()), run.getLastUpdateTime());    	
    		return Response.ok().entity(p).build();
    	} else {
        	return Response.ok().entity(new json.Response(404, "No run having the specified id value.")).build();
    	}
    }        
}
