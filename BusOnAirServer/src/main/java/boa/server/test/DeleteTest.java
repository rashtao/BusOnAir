package boa.server.test;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import boa.server.domain.*;



public class DeleteTest {

	private static GraphDatabaseService db;
    

    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();
		//Stations.getStations().deleteAllStations();
		
//		Transaction tx = DbConnection.getDb().beginTx();
//		try{
//			//	Runs.getRuns().deleteRun(run);
//			Runs.getRuns().deleteRun(Runs.getRuns().getRunById(137));
//
//			tx.success();
//		}finally{
//			tx.finish();			
//		}    	
		
		
		Station s = Stations.getStations().getStationById(16);
//		Route route = Routes.getRoutes().getRouteById(7);
//		for(Run r : route.getAllRuns()){
		System.out.println(s);	
//		}

//		Route route = Routes.getRoutes().getRouteById(1);
//		Transaction tx = DbConnection.getDb().beginTx();
//		try{
//			//	Runs.getRuns().deleteRun(run);
//			Routes.getRoutes().deleteRoute(route);
//			tx.success();
//		}finally{
//			tx.finish();			
//		}    	

		
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			//	Runs.getRuns().deleteRun(run);
			Stations.getStations().deleteAllStations();
			tx.success();
		}finally{
			tx.finish();			
		}    	

		
		
//		
////		Run run = Runs.getRuns().getRunById(286);
//
//		Transaction tx = DbConnection.getDb().beginTx();
//		try{
////			Runs.getRuns().deleteRun(run);
//			Routes.getRoutes().deleteRoute(route);
//			tx.success();
//		}finally{
//			tx.finish();			
//		}    	

//		
//		for(Run r : route.getAllRuns()){
//			System.out.println(r);	
//		}
		
		DbConnection.turnoff();
	}

}
