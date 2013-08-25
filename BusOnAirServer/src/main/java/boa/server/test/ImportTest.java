package boa.server.test;

import boa.server.domain.*;
import org.neo4j.graphdb.GraphDatabaseService;



public class ImportTest {

	private static GraphDatabaseService db;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DbConnection.createEmbeddedDbConnection();
		importTest();
	}

	public static void importTest(){
    	System.out.print("\n\n ---- IMPORT TEST ---");
		db = DbConnection.getDb();
		
//		Transaction tx = DbConnection.getDb().beginTx();
//		try{
//			int id = 1051;
//			boa.server.domain.importer.Station js = new boa.server.domain.importer.Station(id, "Piazza Duomo", new Coordinate(88.352, 14.349));
//			Stations.getStations().createStation(js);
//			tx.success();
//		}finally{
//			tx.finish();			
//		}   
		
		
		
//		Route addded = null;
//		Transaction tx = DbConnection.getDb().beginTx();
//		try{
//			Integer id = 1051;
//			Integer from = 15;
//			Integer towards = 22;
//			boa.server.importer.json.Route jr = new boa.server.importer.json.Route(id, "199BIS", from, towards);
//			addded = Routes.getRoutes().createOrUpdateRoute(jr);
//			tx.success();
//		}finally{
//			tx.finish();			
//		}   
		
		


		int stationsCount = 0;
		int routesCount = 0;
		int runsCount = 0;
		int stopsCount = 0;
		
		
        for(Station s : Stations.getStations().getAll()){
        	++stationsCount;
        }		
		
        for(Route route : Routes.getRoutes().getAll()){
        	++routesCount;
//        	System.out.print("\n\n" + route);
            for(Run r : route.getAllRuns()){
            	++runsCount;
//            	System.out.print("\n\n" + r);
                for(Stop s : r.getAllStops()){
                	++stopsCount;
//                	System.out.print("\n\n" + s);
                }
                
            }
        }
        
        System.out.println("");
        System.out.println(stationsCount);
        System.out.println(routesCount);
        System.out.println(runsCount);
        System.out.println(stopsCount);

        routesCount = Routes.getRoutes().getAll().size();
        runsCount = Runs.getRuns().getAll().size();
        stopsCount = Stops.getStops().getAll().size();
        
        System.out.println("");
        System.out.println(routesCount);
        System.out.println(runsCount);
        System.out.println(stopsCount);

        
    	System.out.print("\n\n ---- end IMPORT TEST ---");

		DbConnection.turnoff();
	}		
	
}
