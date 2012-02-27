package importer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
//                    staz.setFirstStop(s);                    
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
		
		checkEmptyRoutes();
		for(Route route : Routes.getRoutes().getAll()){
			HashSet<Station> partenze = new HashSet<Station>(); 
			HashSet<Station> arrivi = new HashSet<Station>(); 
			
			for(Run run : route.getAllRuns()){
				Stop stop = run.getFirstStop();
				Station s = stop.getStazione();
				partenze.add(s);
				while(stop.getNextInRun() != null)
					stop = stop.getNextInRun();
				arrivi.add(stop.getStazione());				
			}								

			Object[] arrpartenze = partenze.toArray();
			Object[] arrarrivi = arrivi.toArray();

			route.setFrom((Station) arrpartenze[0]);
			route.setTowards((Station) arrarrivi[0]);
			
			if(partenze.size() > 2 || arrivi.size() > 2)
				System.out.print("\nRoute Anomala!");
			
			if(partenze.size() > 1 && arrivi.size() > 1){	// Route A/R
				System.out.print("\nA/R Route founded... " + route.getLine() + ": " + route.getId() + ", ");
				Route twinRoute = new Route(db.createNode(), route.getLine());
				Routes.getRoutes().addRoute(twinRoute);
				System.out.print(twinRoute.getId());
				
				twinRoute.setFrom((Station) arrpartenze[1]);
				twinRoute.setTowards((Station) arrarrivi[1]);

				ArrayList<Run> runs = route.getAllRuns();
				route.clearIndex();
				for(Run run : runs){
					Station s = run.getFirstStop().getStazione();
					if(!s.equals(arrpartenze[0])){
						run.setRoute(twinRoute);
						twinRoute.addRun(run);
					} else {
						run.setRoute(route);
						route.addRun(run);						
					}
				}
			}
		}
		
		checkEmptyRoutes();

        tx.success();
		}
		finally
		{
		tx.finish();
		}

	}
	
	public void checkEmptyRoutes(){
		System.out.print("\n-----\ncheckEmptyRoutes()\n-----\n");
		for(Route route : Routes.getRoutes().getAll()){
			ArrayList<Run> runs = route.getAllRuns();
			if(runs.size() == 0)
				System.out.print("\nEmptyRoute: " + route);
		}
		System.out.print("\n*****\nEND checkEmptyRoutes()\n*****\n");
	}

	public void generateRunsId() {
		Transaction tx = db.beginTx();
		try{
			int i = 0;
			for(Run r : Runs.getRuns().getAll()){
				r.setId(i++);
				Runs.getRuns().updateIndex(r);
			}
			tx.success();
		}
		finally
		{
			tx.finish();
		}
		
	}

	public void checkStopStations() {
		Transaction tx = db.beginTx();
		try{
			for(Stop s : Stops.getStops().getAll()){
				Station staz = s.getStazione();
				if(staz == null)
					System.out.print("\nNULL STAZION FOR NODE: " + s.getUnderlyingNode().getId());
			}
			tx.success();
		}
		finally
		{
			tx.finish();
		}
		
		
	}

	public void setStaticTimes() {
		Transaction tx = db.beginTx();
		try{		
			for(Stop s : Stops.getStops().getAll()){
				s.setStaticTime(s.getTime());
			}		
			
			tx.success();
		}finally{
			tx.finish();
		}
	}

	public void setLastVisitedStops() {
		Transaction tx = db.beginTx();
		try{		
			for(Run r : Runs.getRuns().getAll()){
				Stop s = r.getFirstStop();
				while(s.getNextInRun() != null)
					s = s.getNextInRun();
				
				r.setLastStop(s);				
			}
			
			
			tx.success();
		}finally{
			tx.finish();
		}
	}

	public void linkCheckPoints() {

		
			for(Run r : Runs.getRuns().getAll()){
				Transaction tx = db.beginTx();
				try{		
						
					Stop s = r.getFirstStop();
					Stop prev = null;
					CheckPoint prevCp = null;
					int id = 0;
					
					while (s != null){
						if(prev != null){	//iterazioni successive
							double lat = (s.getStazione().getLatitude() + prev.getStazione().getLatitude()) / 2.0;
							double lon = (s.getStazione().getLongitude() + prev.getStazione().getLongitude()) / 2.0;
							int dt = (s.getTime() - prev.getTime()) / 2; 
							CheckPoint cp = new CheckPoint(db.createNode(), id, lat, lon, dt);
							cp.setTowards(s);	
							cp.setFrom(prev);
							prevCp.setNextCheckPoint(cp);
							prevCp = cp;
							r.addCheckPoint(cp);
							id++;
						} 
						
						double lat = s.getStazione().getLatitude();
						double lon = s.getStazione().getLongitude();
						CheckPoint cp = new CheckPoint(db.createNode(), id, lat, lon, 0);
						cp.setTowards(s);		
						cp.setFrom(s);
						if(prevCp != null)
							prevCp.setNextCheckPoint(cp);
						prevCp = cp;
						r.addCheckPoint(cp);
						
						prev = s;
						s = s.getNextInRun();
						id++;
					}
					tx.success();
				}finally{
					tx.finish();
				}	
			}	
	}
    
    
    

}