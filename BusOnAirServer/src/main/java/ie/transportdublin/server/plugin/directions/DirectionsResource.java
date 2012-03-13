package ie.transportdublin.server.plugin.directions;

import domain.DbConnection;
import domain.Station;
import domain.Stations;
import domain.Stop;
import domain.Stops;
import ie.transportdublin.server.plugin.json.Directions;
import ie.transportdublin.server.plugin.json.DirectionsList;
import ie.transportdublin.server.plugin.json.DirectionsRoute;
import ie.transportdublin.server.plugin.json.DirectionsWalk;
import ie.transportdublin.shortestpath.ShortestPath;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import json.DirectionRoute;
import json.DirectionWalk;
import myShortest.StopMediator;
import myShortest.myShortest;
import myShortest.myShortestGeo;

import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.webadmin.rest.SessionFactoryImpl;
import utils.GeoUtil;

@Path( "/directions" )
public class DirectionsResource
{
    private final Database database;
    private static ShortestPath threeLayeredTraverserShortestPath;
    private Transaction tx;
    private BufferedWriter log;

    public DirectionsResource( @Context Database database,
            @Context HttpServletRequest req, @Context OutputFormat output ) throws IOException
    {
        this( new SessionFactoryImpl( req.getSession( true ) ), database,output );
        
        String fullURL = req.getRequestURL().append("?").append( 
            req.getQueryString()).toString();        
        log.write("\nHttpServletRequest(" + fullURL +")");
        log.flush();         
    }

    public DirectionsResource( SessionFactoryImpl sessionFactoryImpl, Database database, OutputFormat output ) throws IOException
    {
        this.database = database;
//        threeLayeredTraverserShortestPath = new ShortestPath(database.graph);
        FileWriter logFile = new FileWriter("/tmp/trasportaqdirections.log");
        log = new BufferedWriter(logFile);
        DbConnection.createDbConnection(database);
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/getdirections" )
    public Response getDirections( 
    		@QueryParam( "lat1" ) Double lat1,
            @QueryParam( "lon1" ) Double lon1,
            @QueryParam( "lat2" ) Double lat2,
            @QueryParam( "lon2" ) Double lon2, 
            @QueryParam( "departureday" ) Integer departureday,
            @QueryParam( "departuretime" ) Integer departuretime,
            @QueryParam( "minchangetime" ) Integer minchangetime,
            @QueryParam( "criterion" ) String criterion,
            @QueryParam( "timelimit" ) Integer timelimit,
            @QueryParam( "maxwalkdistance" ) Integer maxwalkdistance) throws IOException{

    	log.write("\ngetDirection");
        log.flush();
        
        if ( lat1 == null || lon1 == null || lat2 == null || lon2 == null || departureday == null || departuretime == null)
        	return Response.ok().entity(new json.Response(400, "lat1, lon1, lat2, lon2, departureday, departuretime cannot be blank")).build();
           	

        if(maxwalkdistance == null)
        	maxwalkdistance = new Integer(1000);
        
        if(timelimit == null)
        	timelimit = new Integer(600);
        
    	myShortestGeo mysp = new myShortestGeo(departuretime, timelimit, lat1, lon1, lat2, lon2, maxwalkdistance);
        mysp.shortestPath(); 
        json.Directions directs = mysp.getDirections();      
//        if(directs == null || directs.getDirectionsList() == null || directs.getDirectionsList().size() == 0)
//        	return Response.ok().entity(new json.Response(204, "No path found")).build();
    	
    	return Response.ok().entity(directs).build();
    	
    }
    
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/" )
    public Response directions( 
    		@QueryParam( "lat1" ) double lat1,
            @QueryParam( "lon1" ) double lon1,
            @QueryParam( "lat2" ) double lat2,
            @QueryParam( "lon2" ) double lon2, 
            @QueryParam( "time" ) double time ) throws IOException
    {
    	
    	// metodo utilizzato da web client di transportdublin
    	
    	
        if ( lat1 == 0 || lat1 == 0 || lon1 == 0 || lon2 == 0 || time==0 )
            return Response.serverError().entity( "params cannot be blank" ).build();
        
    	log.write("\ngetDirection?lat1=" + lat1 + "&lon1=" + lon1 + "&lat2=" + lat2 + "&lon2=" + lon2 + "&time=" + time);
        log.flush();
        
    	myShortestGeo mysp = new myShortestGeo((int) time, 1000, lat1, lon1, lat2, lon2, 500);
        StopMediator cache = mysp.shortestPath(); 
        json.Directions directs = mysp.getDirections();      
        
        DirectionsList  directionsList2  = new DirectionsList();

        if(directs.getDirectionsList().size() == 0){
            return Response.ok().entity( null ).build();
        }

        json.Direction sp = directs.getDirectionsList().iterator().next();
        List<json.DirectionRoute> routesDirection = sp.getRoutes();
        List<json.DirectionWalk> walksDirection = sp.getWalks();
//        Collections.reverse(routesDirection);
//        Collections.reverse(walksDirection);
        
        List<DirectionsRoute> routes = new ArrayList<DirectionsRoute>();
        List<DirectionsWalk> walks = new ArrayList<DirectionsWalk>();      

        for(json.DirectionRoute rd : routesDirection){
        	domain.Stop s1 = cache.get(Stops.getStops().getStopById(rd.getDepId()));
        	domain.Stop s2 = cache.get(Stops.getStops().getStopById(rd.getArrId()));
        	log.write("\n" + s1);
        	log.write("\n" + s2);
            log.flush();
        	routes.add(new DirectionsRoute(s1, s2));
        }
        
        for(json.DirectionWalk rw : walksDirection){
            walks.add(new DirectionsWalk(
                    rw.getDeparture().getLat(),
                    rw.getDeparture().getLon(),
                    rw.getArrival().getLat(),
                    rw.getArrival().getLon(),
                    rw.getDuration()));            	
        }
        

            directionsList2.add(new Directions(routes, walks));
            

        Response resp = Response.ok().entity( directionsList2 ).build();
        
        return resp;
    }


//    @GET
//    @Produces( MediaType.APPLICATION_JSON )
//    @Path( "/" )
//    public Response directions( 
//    		@QueryParam( "lat1" ) double lat1,
//            @QueryParam( "lon1" ) double lon1,
//            @QueryParam( "lat2" ) double lat2,
//            @QueryParam( "lon2" ) double lon2, 
//            @QueryParam( "time" ) double time )
//    {
//        if ( lat1 == 0 || lat1 == 0 || lon1 == 0 || lon2 == 0 || time==0 )
//            return Response.serverError().entity( "params cannot be blank" ).build();
//        
//        tx = database.graph.beginTx();
//        //la transaction migliora le performance, anche se non sarebbe necessaria
//        
//        
////        DirectionsList  directionsList =null;
//        DirectionsList  directionsList2  = new DirectionsList();
//        
//        try
//        {            
//            int tempo = (int) time;
//            Station s1 = Stations.getStations().nearestStation(lat1, lon1);
//            Station s2 = Stations.getStations().nearestStation(lat2, lon2);
//            if(s1 == null || s2 == null){
//                return Response.ok().entity( null ).build();
//            }            
//            
//            double dist1 = GeoUtil.getDistance2(
//                    lat1, 
//                    lon1,
//                    s1.getLatitude(), 
//                    s1.getLongitude());
//            
//            double dist2 = GeoUtil.getDistance2(
//                    s2.getLatitude(), 
//                    s2.getLongitude(),
//                    lat2, 
//                    lon2);
//            
//            int walkingtime1 = (int) (dist1 / 4.5 * 60);
//            int walkingtime2 = (int) (dist2 / 4.5 * 60);
//            
//            Stop firstStop = s1.getFirstStopsFromTime(tempo + walkingtime1);
//            
//            if(firstStop == null){
//                //return Response.serverError().entity( "there are no buses" ).build();
//                return Response.ok().entity( null ).build();
//            }
//            myShortest mysp = new myShortest(firstStop, s2, 600);
//            mysp.shortestPath();
//            
//            Stop arrivo = mysp.getShortestPath();
//            if(arrivo == null){
//                //return Response.serverError().entity( "there are no buses" ).build();
//                return Response.ok().entity( null ).build();
//            }
//            
//            Stop s = arrivo;
//            
//            List<DirectionsRoute> routes = new ArrayList<DirectionsRoute>();
//            List<DirectionsWalk> walks = new ArrayList<DirectionsWalk>();      
//            
//            Stack<DirectionsRoute> routesStack = new Stack<DirectionsRoute>();
//            Stack<DirectionsWalk> walkStack = new Stack<DirectionsWalk>();
//
//
//            walks.add(new DirectionsWalk(
//                    lat1,
//                    lon1,
//                    firstStop.getStazione().getLatitude(),
//                    firstStop.getStazione().getLongitude(),
//                    walkingtime1));      
//            
//            
//
//            
//            Stop arr = arrivo;
//
//
//            int i = 0;
//            while(s != null && !s.getStazione().equals(firstStop.getStazione())){
//                
//                while(s.prevSP != null && !s.getStazione().equals(s.prevSP.getStazione())){
//                    s = s.prevSP;
//                }
//            
//                routesStack.push(new DirectionsRoute(s, arr));
//                            
//                if(i > 0){
//                    walkStack.push(new DirectionsWalk(
//                        arr.getStazione().getLatitude(),
//                        arr.getStazione().getLongitude(),
//                        arr.getStazione().getLatitude(),
//                        arr.getStazione().getLongitude(),
//                        0));
//                }
//                
//                while(s.prevSP != null && s.getStazione().equals(s.prevSP.getStazione())){
//                    s = s.prevSP;
//                }
//
//                arr = s;
//                i++;
//            
//            }
//            
//            
//            while(!routesStack.isEmpty()){
//                routes.add(routesStack.pop());
//            }
//            
//            while(!walkStack.isEmpty()){
//                walks.add(walkStack.pop());
//            }
//            
//            walks.add(new DirectionsWalk(
//                    arrivo.getStazione().getLatitude(),
//                    arrivo.getStazione().getLongitude(),
//                    lat2,
//                    lon2,
//                    walkingtime2));            
//
//            
//            directionsList2.add(new Directions(routes, walks));
//            
//            
//            
//            
//            
////            WeightedPath path = threeLayeredTraverserShortestPath.findShortestPath( lat1, lon1, lat2, lon2, time );
////            
////            if ( path != null )
////            {
////                DirectionsGenerator directionsGenerator = new DirectionsGenerator( path );
////                if ( path.length() == 3 )
////                    directionsList = directionsGenerator.convertOneBusPath( path, time );
////                else
////                    directionsList = directionsGenerator.convertTwoBusPath( path, time );
////            }
//            tx.success();
//        }
//        finally
//        {
//            tx.finish();
//        }
//        
//        
////        String respo = "{\"directionslist\":[{\"routes\":[{\"arrStation\":\"viasaragat(aquilone)\",\"arrTime\":\"997\",\"deptStation\":\"terminalbus\",\"deptTime\":\"990\",\"latLon\":[{\"lat\":42.34453,\"lon\":13.4011},{\"lat\":42.35608,\"lon\":13.3415},{\"lat\":42.3504,\"lon\":13.34718}],\"numOfStops\":2,\"routeId\":\"12\"},{\"arrStation\":\"universitÃ coppito\",\"arrTime\":\"1008\",\"deptStation\":\"viasaragat(aquilone)\",\"deptTime\":\"1000\",\"latLon\":[{\"lat\":42.3504,\"lon\":13.34718},{\"lat\":42.33031,\"lon\":13.48218},{\"lat\":42.36211,\"lon\":13.34721},{\"lat\":42.37025,\"lon\":13.34146},{\"lat\":42.3678,\"lon\":13.35246}],\"numOfStops\":4,\"routeId\":\"M5\"}],\"walks\":[{\"distance\":5,\"latLon\":[{\"lat\":42.32673192118198,\"lon\":13.399821635131957},{\"lat\":42.34453,\"lon\":13.4011}]},{\"distance\":5,\"latLon\":[{\"lat\":42.3504,\"lon\":13.34718},{\"lat\":42.3504,\"lon\":13.34718}]},{\"distance\":5,\"latLon\":[{\"lat\":42.3678,\"lon\":13.35246},{\"lat\":42.37259394258441,\"lon\":13.360768671875121}]}]}]}";
////        Response resp = Response.ok(respo).type("text/xml").build();
//
//        Response resp = Response.ok().entity( directionsList2 ).build();
//        
//        return resp;
//    }
    
}
