package importer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import domain.*;
import utils.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import scala.actors.threadpool.Arrays;

public class DBInserter
{


    private GraphDatabaseService db;

    private static Index<Node> hubLayer1;
    private static Index<Node> hubLayer2;
    private static Index<Node> hubTransferLayer1;
    private static Index<Node> hubTransferLayer2;
    private static LayerNodeIndex hubTransferLayer2SpatialIndex;
    private static Index<Node> stopLayer;
    private static LayerNodeIndex stationSpatialIndex;

    private static List<RouteSql> sqlRoutes;
    private static List<StationSql> sqlStations;
    private static List<StopSql> sqlStops;
    
    private static HashMap<String, Integer> runCodeIndex;

    public DBInserter(){
    	this.db = DbConnection.getDb();
    }

    public void addData()
    {


            sqlStations = XMLReader.readStations().getStationList();
//            sqlRoutes = XMLReader.readRoutes().getRouteList();
            sqlStops = XMLReader.readStops().getStopList();
            
            createRunCodeIndex();
            
                    Transaction tx = db.beginTx();
        try{
          addStations();
          addStops();
          linkStopsInRuns();
          linkStopsInStations();
                      tx.success();
        }
        finally
        {
          tx.finish();
        }
        
        
            ArrayList<Station> stazioni = Stations.getStations().getAll();
          for(Station s : stazioni){
              System.out.println( "\n" + s);		    	  
              
          }
              System.out.println( "\nTOT:" + stazioni.size());		    	  
          
          
//          addRoutes();

            
            
//            hubLayer1 = db.index().forNodes( "hubLayer1" );
//            hubLayer2 = db.index().forNodes( "hubLayer2" );
//            hubTransferLayer1 = db.index().forNodes( "hubTransferLayer1" );
//            hubTransferLayer2 = db.index().forNodes( "hubTransferLayer2" );
//            hubTransferLayer2SpatialIndex = new LayerNodeIndex( "hubTransferLayerSpatialIndex", db, new HashMap<String, String>() );
//            stopLayer = db.index().forNodes( "stopLayer" );
//            stopSpatialIndex = new LayerNodeIndex( "stopSpatialIndex", db, new HashMap<String, String>() );
//            addStops();           
//            addHubs();
//            addWalkingConnections();
   

    }

    private void addStations() {
    	  System.out.println( "\nAdding Stations... " );

			  //Stations stations = new Stations(db.createNode(), 0);
		      for (StationSql stationSql : sqlStations){
		    	  //System.out.println( "\n\n\nAdding: " + stationSql);
		    	  Station station = new Station(
		    			  db.createNode(), 
		    			  stationSql.Id_Station, 
		    			  stationSql.name,
		    			  stationSql.lat,
		    			  stationSql.lng,
		    			  stationSql.is_school,
		    			  stationSql.is_terminal);
		    	  Stations.getStations().addStation(station);
		    	  //System.out.println( "\nAs: " + station);		    	  
		      }

		
    	  System.out.println( "OK!" );	
	}

//    private void addRoutes() {
//  	  System.out.println( "\nAdding Routes... " );
//  	  
//		  Transaction tx = db.beginTx();
//		  try{
//			  //Routes routes = new Routes(db.createNode(), 0);
//		      for (RouteSql routeSql : sqlRoutes){
//		    	  System.out.println( "\n\n\nAdding: " + routeSql);
//		    	  Route route = new Route(
//		    			  db.createNode(), 
//		    			  routeSql.Id_Route, 
//		    			  routeSql.line);
//		    	  //routes.addRoute(route);
//		    	  System.out.println( "\nAs: " + route);		    	  
//		      }
//		      tx.success();
//		  }
//		  finally
//		  {
//		      tx.finish();
//		  }
//		
//  	  System.out.println( "OK!" );	
//	}
//

    private void addStops() {
         System.out.println( "\nAdding Stops... " );
         

            for(StopSql inStop : sqlStops){
                //System.out.println( "\n\n\nAdding: " + inStop);
//                System.out.print("\nRUNID: " + (int) runCodeIndex.get(inStop.runcode));

                Stop outStop = new Stop(
	                db.createNode(), 
	                Integer.parseInt(inStop.Id_Stop), 
	                inStop.getMinutesFromMidn(),
	                inStop.src,
	                (int) runCodeIndex.get(inStop.runcode),
	                inStop.line + inStop.note);
                

            }

        System.out.println( "OK!" );	
    }

    private void linkStopsInRuns() {
        for(Route route : Routes.getRoutes().getAll()){
            for(Run r : route.getAllRuns()){
                ArrayList<Stop> stops = r.getAllStops();
                Collections.sort(stops, new TimeComparator());  
                Stop prevStop = null;
                for(Stop s : stops){
                    if(prevStop == null){
                        r.setFirstStop(s);
                    } else {
                        prevStop.setNextInRun(s);
                    }
                    prevStop = s;
                }            
            }
        }
    }

    private void linkStopsInStations() {
          ArrayList<Station> stazioni = Stations.getStations().getAll();
          for(Station staz : stazioni){
            ArrayList<Stop> stops = staz.getAllStops();
            Collections.sort(stops, new TimeComparator());  
            Stop prevStop = null;
            for(Stop s : stops){
                if(prevStop == null){
                    staz.setFirstStop(s);                    
                } else {
                    prevStop.setNextInStation(s);
                }
                prevStop = s;
            }            
          }
    }

    private void createRunCodeIndex() {
        runCodeIndex = new HashMap<String, Integer>();
        int i = 0;
        for(StopSql ss : sqlStops){
            runCodeIndex.put(ss.runcode, i);
            i++;
        }
              
    }

    void addSpatialIndex() {
        LayerNodeIndex stationSpatialIndex = new LayerNodeIndex( "stationSpatialIndex", db, new HashMap<String, String>() );
        
        for(Station s : Stations.getStations().getAll()){
            stationSpatialIndex.add( s.getUnderlyingNode(), "", "" );        
        }
    }

	public void duplicateRoutes() {
        Transaction tx = db.beginTx();
		try{
		
		int routeCircular = 0;
		for(Route route : Routes.getRoutes().getAll()){
//			System.out.print("\n\n\n\n" + route);
			Station s1 = null;
			Station s2 = null;
			for(Run run : route.getAllRuns()){
				Station s = run.getFirstStop().getStazione();
//				System.out.print("\n\n" + run);
//				System.out.print("\n" + run.getFirstStop());				
//				System.out.print("\n" + s);
				if(s1 == null)
					s1 = s;
				
				if(!s.equals(s1)){
					if(s2 == null)
						s2 = s;
//					if(!s.equals(s2))
//						System.out.print("\n\n STRANO! 3 sorgenti per la stessa route");
				}				
			}
			
			if(s2 == null)
				routeCircular++;

			if(s1 != null)
				route.setFrom(s1);
			
			if(s2 != null){
				Route twinRoute = new Route(db.createNode(), route.getLine());
				Routes.getRoutes().addRoute(twinRoute);
				twinRoute.setFrom(s2);

				for(Run run : route.getAllRuns()){
					Station s = run.getFirstStop().getStazione();
					if(s.equals(s2)){
						run.setRoute(twinRoute);
					}
				}
			}
		}
//		System.out.print("\nRoute circolari: " + routeCircular);
		
        tx.success();
		}
		finally
		{
		tx.finish();
		}

	}
    
    
    

}