package boa.server.plugin.runs;


import boa.server.domain.CheckPoint;
import boa.server.domain.DbConnection;
import boa.server.domain.Run;
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

@Path( "/runs" )
public class RunsResource{
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
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/getall" )
    public Response getAll(@QueryParam( "objects" ) Boolean obj) throws IOException{        
        Iterable<Run> runs = boa.server.domain.Runs.getRuns().getAll();

        if(obj != null && obj){
        	boa.server.plugin.json.RunsObjects runList = new boa.server.plugin.json.RunsObjects();        	
            for(Run r : runs){
            	boa.server.plugin.json.Run jr = new boa.server.plugin.json.Run(r);  
            	runList.add(jr);        	
            }
            return Response.ok().entity(runList).build();   
        } else {
            boa.server.plugin.json.Runs runList = new boa.server.plugin.json.Runs();
            for(Run r : runs){
            	boa.server.plugin.json.Run jr = new boa.server.plugin.json.Run(r);  
            	runList.add(jr);        	
            }
            return Response.ok().entity(runList).build();              	
        }
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/getallrunningbuses" )
    public Response getAllRunningBuses(@QueryParam( "objects" ) Boolean obj) throws IOException{        
        Iterable<Run> runs = boa.server.domain.Runs.getRuns().getAllRunningBuses();

        if(obj != null && obj){
        	boa.server.plugin.json.RunsObjects runList = new boa.server.plugin.json.RunsObjects();        	
            for(Run r : runs){
            	boa.server.plugin.json.Run jr = new boa.server.plugin.json.Run(r);  
            	runList.add(jr);        	
            }
            return Response.ok().entity(runList).build();   
        } else {
            boa.server.plugin.json.Runs runList = new boa.server.plugin.json.Runs();
            for(Run r : runs){
            	boa.server.plugin.json.Run jr = new boa.server.plugin.json.Run(r);  
            	runList.add(jr);        	
            }
            return Response.ok().entity(runList).build();              	
        }
    }
        
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}")
    public Response getRun(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\nruns/" + id);
        log.flush();

        if ( id == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(400, "id cannot be blank")).build();
        
    	boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
    	 
    	if (run != null){
    		boa.server.plugin.json.Run jr = new boa.server.plugin.json.Run(run);    	
    		return Response.ok().entity(jr).build();
    	} else {
    		return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id value.")).build();
    	}
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/checkpointpass")
    public Response checkPointPass(
    		@PathParam("id") Integer id,
    		@QueryParam( "checkpointid" ) Integer checkpointid,
    		@QueryParam( "time" ) Integer time) throws IOException{
        
        if ( id == null || checkpointid == null || time == null )
        	return Response.ok().entity(new boa.server.plugin.json.Response(400, "id, checkpointid, time cannot be blank")).build();

        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
        
        boa.server.domain.CheckPoint cp = run.getCheckPointById(checkpointid);
        
        if(cp == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No checkpoint having the specified id.")).build();
                
        run.checkPointPass(cp, time);
        	   
        boa.server.plugin.json.Response jr = new boa.server.plugin.json.Response(200, "OK");
        
        return Response.ok().entity(jr).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/getallstops")
    public Response getAllStops(@PathParam("id") Integer id, @QueryParam( "objects" ) Boolean obj) throws IOException{
        
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();

        
        if(obj != null && obj){
        	boa.server.plugin.json.StopsObjects jstops = new boa.server.plugin.json.StopsObjects();
            for(Stop s : run.getAllStops())
            	jstops.add(s);
            	        	   
            return Response.ok().entity(jstops).build();
        	
        } else {
        	boa.server.plugin.json.Stops jstops = new boa.server.plugin.json.Stops();
            for(Stop s : run.getAllStops())
            	jstops.add(s);
            	        	   
            return Response.ok().entity(jstops).build();
        }        
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/checkpoints/getall")
    public Response getAllCheckPoints(@PathParam("id") Integer id, @QueryParam( "objects" ) Boolean obj) throws IOException{
        
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();

        if(obj != null && obj){
        	boa.server.plugin.json.CheckPointsObjects cps = new boa.server.plugin.json.CheckPointsObjects();
            for(CheckPoint cp : run.getAllCheckPoints())
            	cps.add(cp);
            	        	   
            return Response.ok().entity(cps).build();        	
        } else {       
	    	boa.server.plugin.json.CheckPoints cps = new boa.server.plugin.json.CheckPoints();
	        for(CheckPoint cp : run.getAllCheckPoints())
	        	cps.add(cp);
	        	        	   
	        return Response.ok().entity(cps).build();
        }
    }
        
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/checkpoints/{idcp}")
    public Response getCheckPointById(@PathParam("id") Integer id, @PathParam("idcp") Integer idcp) throws IOException{
    	
    	log.write("\ngetCheckPointById/" + id);
        log.flush();
        
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
    	
        CheckPoint cp = run.getCheckPointById(idcp);
        
        if(cp == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No CheckPoint having the specified id.")).build();
        
        boa.server.plugin.json.CheckPoint jscp = new boa.server.plugin.json.CheckPoint(cp);

        return Response.ok().entity(jscp).build();
    }

    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/checkpoints/{idcp}/gettime")
    public Response getCheckPointTime(@PathParam("id") Integer id, @PathParam("idcp") Integer idcp) throws IOException{
        
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
    	
        CheckPoint cp = run.getCheckPointById(idcp);
        
        if(cp == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No CheckPoint having the specified id.")).build();
        
        boa.server.plugin.json.Time jt = new boa.server.plugin.json.Time(cp.getTimeInSeconds());

        return Response.ok().entity(jt).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/restore")
    public Response restore(
    		@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\nrestore/" + id);
        log.flush();
                
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
        
        run.restore();
                	   
        boa.server.plugin.json.Response jr = new boa.server.plugin.json.Response(200, "OK");
        return Response.ok().entity(jr).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/getlastgpsstop")
    public Response getLastGPSStop(@PathParam("id") Integer id) throws IOException{
        
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
        
    	Stop stop = run.getLastGPSStop();
                	
        boa.server.plugin.json.Stop jstop = new boa.server.plugin.json.Stop(stop);
        	   
        return Response.ok().entity(jstop).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/calculatelaststop")
    public Response calculateLastStop(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetlaststop/" + id);
        log.flush();
        
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
        
    	Stop stop = run.calculateLastStop();
                	
        boa.server.plugin.json.Stop jstop = new boa.server.plugin.json.Stop(stop);
        	   
        return Response.ok().entity(jstop).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/getlastgpscheckpoint")
    public Response getLastGPSCheckPoint(@PathParam("id") Integer id) throws IOException{
        
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
     
        CheckPoint cp = run.getLastGPSCheckPoint();
        
        if(cp == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(500, "GRAVE: No CheckPoint found.")).build();
        
        boa.server.plugin.json.CheckPoint jscp = new boa.server.plugin.json.CheckPoint(cp);
        
        return Response.ok().entity(jscp).build();
    }    
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/calculatelastcheckpoint")
    public Response calculateLastCheckPoint(@PathParam("id") Integer id) throws IOException{
        
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
     
        CheckPoint cp = run.calculateLastCheckPoint();
        
        if(cp == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(500, "GRAVE: No CheckPoint found.")).build();
        
        boa.server.plugin.json.CheckPoint jscp = new boa.server.plugin.json.CheckPoint(cp);
        
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
    	
        if ( id == null || lat == null || lon == null )
        	return Response.ok().entity(new boa.server.plugin.json.Response(400, "id, lat, lon cannot be blank")).build();
        
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
                
        run.updatePosition(lat,lon,time);
        	   
        return Response.ok().entity(new boa.server.plugin.json.Response(200, "OK")).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/addcheckpoint")
    public Response addCheckPoint(
    		@PathParam("id") Integer id,
    		@QueryParam( "time" ) Integer time,
    		@QueryParam( "lat" ) Double lat,
    		@QueryParam( "lon" ) Double lon) throws IOException{
    	
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        if(run == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id.")).build();
                
        run.addCheckPoint(lat,lon,time);
        
        	   
        boa.server.plugin.json.Response jr = new boa.server.plugin.json.Response(200, "OK");
        return Response.ok().entity(jr).build();
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/restoreall")
    public Response restoreAll() throws IOException{

        for(Run r : boa.server.domain.Runs.getRuns().getAll())
        	r.restore();
        	   
        boa.server.plugin.json.Response jr = new boa.server.plugin.json.Response(200, "OK");
        return Response.ok().entity(jr).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/{id}/rt/getlastgpsposition")
    public Response getLastGPSPosition(@PathParam("id") Integer id) throws IOException{
    	
        if ( id == null)
        	return Response.ok().entity(new boa.server.plugin.json.Response(400, "id cannot be blank")).build();
        
    	boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
    	 
    	if (run != null){
    		boa.server.plugin.json.Position p = new boa.server.plugin.json.Position(new boa.server.plugin.json.Coordinate(run.getLastGPSLatitude(), run.getLastGPSLongitude()), run.getLastUpdateTime());    	
    		return Response.ok().entity(p).build();
    	} else {
        	return Response.ok().entity(new boa.server.plugin.json.Response(404, "No run having the specified id value.")).build();
    	}
    }        


}
