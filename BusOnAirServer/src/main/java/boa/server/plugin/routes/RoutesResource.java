package boa.server.plugin.routes;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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



@Path( "/routes" )
public class RoutesResource{
    private final Database database;
    private BufferedWriter log;

    public RoutesResource( @Context Database database,
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

    public RoutesResource( SessionFactoryImpl sessionFactoryImpl,
            Database database, OutputFormat output ) throws IOException 
    {
        FileWriter logFile = new FileWriter("/tmp/trasportaqroutes.log");
        log = new BufferedWriter(logFile);
        this.database = database;
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/getall" )
    public Response getAll() throws IOException{        
        log.write("\nroutes/getall");
        log.flush();
              
        boa.server.json.Routes routeList = new boa.server.json.Routes();

        Iterable<Route> routes = boa.server.domain.Routes.getRoutes().getAll();
         
        for(Route r : routes){
        	boa.server.json.Route jr = new boa.server.json.Route(r);  
        	routeList.add(jr);        	
        }

        return Response.ok().entity(routeList).build();
    }
    
//    @GET
//    @Produces( MediaType.APPLICATION_JSON )    
//    @Path("{id}")
//    public Response getRoute(@PathParam("id") Integer id) throws IOException{
//    	
//    	log.write("\nroutes/" + id);
//        log.flush();
//    	
//        if ( id == null)
//            return Response.status( 400 ).entity( "id cannot be blank" ).build();
//        
//    	domain.Route route = domain.Routes.getRoutes().getRouteById(id);
//    	 
//    	if (route != null){
//    		json.Route jr = new json.Route(route);    	
//    		return Response.ok().entity(jr).build();
//    	} else {
//    		return Response.status( 404 ).entity( "No route having the specified id." ).build();
//    	}
//    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}") 
    public Response getRoute(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\nroutes/" + id);
        log.flush();
    	
        if ( id == null)
        	return Response.ok().entity(new boa.server.json.Response(400, "id cannot be blank")).build();
        
    	boa.server.domain.Route route = boa.server.domain.Routes.getRoutes().getRouteById(id);
    	 
    	if (route != null){
    		boa.server.json.Route jr = new boa.server.json.Route(route);    	
    		return Response.ok().entity(jr).build();
    	} else {
        	return Response.ok().entity(new boa.server.json.Response(404, "No route having the specified id value.")).build();
    	}
    }
        
    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/getallruns")
    public Response getAllRuns(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetAllRuns/" + id);
        log.flush();
        
        if ( id == null)
        	return Response.ok().entity(new boa.server.json.Response(400, "id cannot be blank")).build();
        
    	boa.server.domain.Route route = boa.server.domain.Routes.getRoutes().getRouteById(id);
    	 
    	if (route == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No route having the specified id value.")).build();

    	ArrayList<Run> runs = route.getAllRuns();
    	
        if(runs.size() == 0)
        	return Response.ok().entity("").build();
        	
        boa.server.json.Runs jruns = new boa.server.json.Runs();
        for(boa.server.domain.Run r : runs)
        	jruns.add(r);
        	   
        return Response.ok().entity(jruns).build();
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )    
    @Path("{id}/getallstations")
    public Response getAllStations(@PathParam("id") Integer id) throws IOException{
    	
    	log.write("\ngetAllStations/" + id);
        log.flush();
        
        if ( id == null)
        	return Response.ok().entity(new boa.server.json.Response(400, "id cannot be blank")).build();
        
    	boa.server.domain.Route route = boa.server.domain.Routes.getRoutes().getRouteById(id);
    	 
    	if (route == null)
        	return Response.ok().entity(new boa.server.json.Response(404, "No route having the specified id value.")).build();

    	ArrayList<Station> stations = route.getAllStations();
    	
        if(stations.size() == 0)
        	return Response.ok().entity("").build();
        	
        boa.server.json.Stations jstations = new boa.server.json.Stations();
        for(boa.server.domain.Station s : stations)
        	jstations.add(s);
        	   
        return Response.ok().entity(jstations).build();
    }    
}
