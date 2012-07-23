package boa.server.plugin.backend;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.graphdb.Transaction;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.webadmin.rest.SessionFactoryImpl;

import boa.server.domain.*;
import boa.server.importer.domain.StationImporter;



@Path( "/backend" )
public class BackEndResource{
    private final Database database;
    private BufferedWriter log;

    public BackEndResource( @Context Database database,
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

    public BackEndResource( SessionFactoryImpl sessionFactoryImpl,
            Database database, OutputFormat output ) throws IOException 
    {
        FileWriter logFile = new FileWriter("/tmp/trasportaqbackend.log");
        log = new BufferedWriter(logFile);
        this.database = database;
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/cleardb" )
    public Response clearDb() throws IOException{        
    	DbConnection.getDbConnection().clear();
    	DbConnection.createDbConnection(database);
    	
        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/runs/{id}/delete" )
    public Response deleteRun(@PathParam("id") Integer id) throws IOException{        

        Run run = Runs.getRuns().getRunById(id);

        if(run == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No run having the specified id value.")).build();

		Transaction tx = DbConnection.getDb().beginTx();
		try{
	        Runs.getRuns().deleteRun(run);
			tx.success();
		}finally{
			tx.finish();			
		}    	

        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/stations/{id}/delete" )
    public Response deleteStation(@PathParam("id") Integer id) throws IOException{        

        Station staz = Stations.getStations().getStationById(id);

        if(staz == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No station having the specified id value.")).build();

		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Stations.getStations().deleteStation(staz);
			tx.success();
		}finally{
			tx.finish();			
		}    	
		
        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/routes/{id}/delete" )
    public Response deleteRoute(@PathParam("id") Integer id) throws IOException{        

        Route route = Routes.getRoutes().getRouteById(id);

        if(route == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No route having the specified id value.")).build();
        	
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Routes.getRoutes().deleteRoute(route);
			tx.success();
		}finally{
			tx.finish();			
		}    	

        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/routes/deleteall" )
    public Response deleteAllRoutes() throws IOException{        
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Routes.getRoutes().deleteAllRoutes();
			tx.success();
		}finally{
			tx.finish();			
		}    	

        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/runs/deleteall" )
    public Response deleteAllRuns() throws IOException{        
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Runs.getRuns().deleteAllRuns();
			tx.success();
		}finally{
			tx.finish();			
		}    	

        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/stations/deleteall" )
    public Response deleteAllStations() throws IOException{        
		for(Station s : Stations.getStations().getAll()){
			Transaction tx = DbConnection.getDb().beginTx();
			try{
				Stations.getStations().deleteStation(s);
				tx.success();
			}finally{
				tx.finish();			
			}   
		}    	

        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/runs/{id}/checkpoints/{idcp}/delete")
    public Response deleteCheckPoint(@PathParam("id") Integer id, @PathParam("idcp") Long idcp) throws IOException{
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No run having the specified id.")).build();
    	
        CheckPoint cp = run.getCheckPointById(idcp);
        
        if(cp == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No CheckPoint having the specified id.")).build();

		Transaction tx = DbConnection.getDb().beginTx();
		try{
			run.deleteCheckPoint(cp);
			tx.success();
		}finally{
			tx.finish();			
		}                   

        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();          
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/runs/{id}/checkpoints/deleteall" )
    public Response deleteAllCheckPoints(@PathParam("id") Integer id) throws IOException{ 
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No run having the specified id.")).build();

		Transaction tx = DbConnection.getDb().beginTx();
		try{
			run.deleteAllIntermediateCheckPoints();
			tx.success();
		}finally{
			tx.finish();			
		}    	

        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }    
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/stops/{id}/updatestatictime" )
    public Response updateStopStaticTime(@PathParam("id") Integer id, @QueryParam( "time" ) Integer time) throws IOException{        
        Stop stop = Stops.getStops().getStopById(id);

        if(stop == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No stop having the specified id value.")).build();
        
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			stop.updateStaticTime(time);
			tx.success();
		}finally{
			tx.finish();			
		}    	
		
        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }
  

    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/runs/{id}/checkpoints/{idcp}/updatedt")
    public Response updateCheckPointDt(@PathParam("id") Integer id, @PathParam("idcp") Long idcp, @QueryParam( "dt" ) Long dt) throws IOException{
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No run having the specified id.")).build();
    	
        CheckPoint cp = run.getCheckPointById(idcp);
        
        if(cp == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No CheckPoint having the specified id.")).build();

		Transaction tx = DbConnection.getDb().beginTx();
		try{
			cp.updateDt(dt);
			tx.success();
		}finally{
			tx.finish();			
		}                   

        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();          
    }
    

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/runs/{id}/checkpoints/{idcp}/updateposition")
    public Response updateCheckPointPosition(@PathParam("id") Integer id, @PathParam("idcp") Long idcp,  @QueryParam( "lat" ) Double lat, @QueryParam( "lon" ) Double lon) throws IOException{
        boa.server.domain.Run run = boa.server.domain.Runs.getRuns().getRunById(id);
        
        if(run == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No run having the specified id.")).build();
    	
        CheckPoint cp = run.getCheckPointById(idcp);
        
        if(cp == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No CheckPoint having the specified id.")).build();

		Transaction tx = DbConnection.getDb().beginTx();
		try{
			cp.setLatitude(lat);
			cp.setLongitude(lon);
			run.updateCpSpatialIndex(cp);
			tx.success();
		}finally{
			tx.finish();			
		}                   

        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();          
    }
    
    
    @POST @Consumes("application/json")
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("/stations/createorupdate")
    public Response createOrUpdateStation(final boa.server.importer.json.Station  input) throws IOException {        
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Stations.getStations().createOrUpdateStation(input);
			tx.success();
		}finally{
			tx.finish();			
		}   			        
        
        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();   
    }
    
    @POST @Consumes("application/json")
    @Produces( MediaType.APPLICATION_JSON )   
    @Path( "/routes/createorupdate" )
    public Response createOrUpdateRoute(final boa.server.importer.json.Route  input) throws IOException{        
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Routes.getRoutes().createOrUpdateRoute(input);
			tx.success();
		}finally{
			tx.finish();			
		}   			        
        
        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();  
    }    

    @POST @Consumes("application/json")
    @Produces( MediaType.APPLICATION_JSON )   
    @Path( "/runs/createorupdate" )
    public Response createOrUpdateRun(final boa.server.importer.json.Run  input) throws IOException{        
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Runs.getRuns().createOrUpdateRun(input);
			tx.success();
		}finally{
			tx.finish();			
		}   			        
        
        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();  
    }    
    
    @POST @Consumes("application/json")
    @Produces( MediaType.APPLICATION_JSON )   
    @Path( "/stops/createorupdate" )
    public Response createOrUpdateStop(final boa.server.importer.json.Stop  input) throws IOException{        
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Stops.getStops().createOrUpdateStop(input);
			tx.success();
		}finally{
			tx.finish();			
		}   			        
        
        boa.server.json.Response jr = new boa.server.json.Response(200, "OK");
        return Response.ok().entity(jr).build();  
    }    
    
}
