package boa.server.test;

import java.util.Collection;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import com.vividsolutions.jts.geom.Coordinate;

import boa.server.domain.*;
import boa.server.domain.utils.GeoUtil;



public class Geotest {

	private static GraphDatabaseService db;


	public static void main(String[] args) {    
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		int range = 829;
		
/*
Station: 
	node: Node[32]
	id: 10
	name: optimes
	latitude: 42.35253
	longitude: 13.34921
 */

		double lat = 42.36;
		double lon = 13.35;
		Coordinate query = new Coordinate(lon,lat);
		
		
		Collection<Station> stations = Stations.getStations().getNearestStations(lat, lon, range);

		
//		Collection<CheckPoint> cps = r.getNearestCheckPoints(lat, lon, range);
		

		
//		for(Stop s : r.getAllStops()){
//			System.out.print("\n" + s.getStazione().getId());
//		}
		
		
		for(Station s : stations)
			System.out.print("\n-------\nDIST: " + GeoUtil.getDistance2(query, new Coordinate(s.getLongitude(), s.getLatitude())) + "\n" + s);
			

		System.out.print("\n" + stations.size());

		
		
		Station staz = Stations.getStations().getStationById((long) 10);
		System.out.print("\n\nSTAZIONE: " + staz);
		Transaction tx = DbConnection.getDb().beginTx();
		try{
//			staz.setLatitude(43.352);
//			staz.setLongitude(14.349);
			staz.setLatitude(42.353);
			staz.setLongitude(13.35);
			Stations.getStations().updateSpatialIndex(staz);
			tx.success();
		}finally{
			tx.finish();			
		}    	

		stations = Stations.getStations().getNearestStations(lat, lon, range);

		
//		Collection<CheckPoint> cps = r.getNearestCheckPoints(lat, lon, range);
		

		
//		for(Stop s : r.getAllStops()){
//			System.out.print("\n" + s.getStazione().getId());
//		}
		
		
		for(Station s : stations)
			System.out.print("\n-------\nDIST: " + GeoUtil.getDistance2(query, new Coordinate(s.getLongitude(), s.getLatitude())) + "\n" + s);
			

		System.out.print("\n" + stations.size());

//		System.out.print("\n\nSTAZIONE: " + staz  + "\n\n\nALL:\n");
//
//		for(Station s : Stations.getStations().getAllStationsInSpatialIndex())
//			System.out.print("\n" + s);
//	
		
		DbConnection.turnoff();


	}



}