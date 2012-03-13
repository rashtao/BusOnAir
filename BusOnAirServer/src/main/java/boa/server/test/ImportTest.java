package boa.server.test;

import java.util.ArrayList;

import org.neo4j.graphdb.GraphDatabaseService;

import boa.server.domain.DbConnection;
import boa.server.domain.Route;
import boa.server.domain.Routes;
import boa.server.domain.Run;
import boa.server.domain.Runs;
import boa.server.domain.Stop;
import boa.server.domain.Stops;


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
		db = DbConnection.getDb();

		int routesCount = 0;
		int runsCount = 0;
		int stopsCount = 0;
		
		
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
        
	}		
	
}
