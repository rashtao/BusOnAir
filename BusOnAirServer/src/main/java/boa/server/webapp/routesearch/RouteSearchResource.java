package boa.server.webapp.routesearch;


import boa.server.domain.DbConnection;
import boa.server.domain.Run;
import boa.server.webapp.webappjson.RouteStop;
import boa.server.webapp.webappjson.Routes;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.webadmin.rest.SessionFactoryImpl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


@Path( "/routesearch" )
public class RouteSearchResource
{

    private BufferedWriter log;

    public RouteSearchResource( @Context Database database,
            @Context HttpServletRequest req, @Context OutputFormat output ) throws IOException 
    {
        this( new SessionFactoryImpl( req.getSession( true ) ), database,
                output );

        String fullURL = req.getRequestURL().append("?").append( 
            req.getQueryString()).toString();        
        log.write("\nHttpServletRequest(" + fullURL +")");
        log.flush(); 
        
        
    }

    public RouteSearchResource( SessionFactoryImpl sessionFactoryImpl,
            Database database, OutputFormat output ) throws IOException 
    {
        FileWriter logFile = new FileWriter("/tmp/trasportaqroutes.log");
        log = new BufferedWriter(logFile);
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/" )
    public Response routeSearch( @QueryParam( "routeId" ) Integer routeId ) throws IOException 
    {        
        log.write("\nROUTESEARCH(" + routeId +")");
        log.flush();

        if ( routeId == null )
            return Response.serverError().entity( "routeId cannot be blank" ).build();

        boa.server.domain.Route route = boa.server.domain.Routes.getRoutes().getRouteById(routeId);
        if(route == null){
            return Response.status( 400 ).entity(
                "No Route Found: " + routeId ).build();
        }
        log.write(route.toString());
        log.flush();
                
        Run run = route.getAllRuns().iterator().next();
        
        boa.server.domain.Stop stop = run.getFirstStop();

        Routes routeList = new Routes();
        while(stop != null){
            routeList.add(new RouteStop(stop));
            stop = stop.getNextInRun();
        }

        return Response.ok().entity(routeList).build();
    }
}
