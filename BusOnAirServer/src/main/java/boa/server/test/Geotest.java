package boa.server.test;

import java.util.Collection;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import boa.server.domain.*;
import boa.server.domain.utils.GeoUtil;



public class Geotest {

	private static GraphDatabaseService db;


	public static void main(String[] args) {    
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		int range = 4;
		
/*
Station: 
	node: Node[32]
	id: 10
	name: optimes
	latitude: 42.35253
	longitude: 13.34921
 */

		double lat = 88.352;
		double lon = 14.349;
		
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Stations.getStations().getStationById(32).updatePosition(43.352, 14.349);
			tx.success();
		}finally{
			tx.finish();			
		}    	

		
//		Run r = Runs.getRuns().getRunById(597);
		Collection<Station> stations = Stations.getStations().getNearestStations(lat, lon, range);
//		Collection<CheckPoint> cps = r.getNearestCheckPoints(lat, lon, range);
		

		
//		for(Stop s : r.getAllStops()){
//			System.out.print("\n" + s.getStazione().getId());
//		}
		
		
		for(Station s : stations)
			System.out.print("\n" + s);
			

		System.out.print("\n" + stations.size());
		
		
		
		
		DbConnection.turnoff();


	}



}