package test;

import java.util.Collection;

import org.neo4j.graphdb.GraphDatabaseService;

import utils.GeoUtil;

import domain.*;

public class Geotest {

	private static GraphDatabaseService db;


	public static void main(String[] args) {    
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		int range = 200;
		
/*
Station: 
	node: Node[32]
	id: 10
	name: optimes
	latitude: 42.35253
	longitude: 13.34921
 */

		double lat = 42.352;
		double lon = 13.349;
		Run r = Runs.getRuns().getRunById(597);
		Collection<Station> stations = Stations.getStations().nearestStations(lat, lon, range);
		Collection<CheckPoint> cps = r.getNearestCheckPoints(lat, lon, range);
		

		
//		for(Stop s : r.getAllStops()){
//			System.out.print("\n" + s.getStazione().getId());
//		}
		
		
		Station s1 = stations.iterator().next();
		System.out.print("\n" + stations.size() + "\n" + s1 + "\n");
		
		Double slat= s1.getLatitude();
		Double slon= s1.getLongitude();
		Double distance = GeoUtil.getDistance2(lat, lon, slat, slon);
		
		System.out.print(distance*1000.0 + "\n-------------\n");
		
		CheckPoint cp = cps.iterator().next();
		System.out.print(cps.size() + "\n" + cp + "\n");
		
		slat= cp.getLatitude();
		slon= cp.getLongitude();
		distance = GeoUtil.getDistance2(lat, lon, slat, slon);
		
		System.out.print(distance*1000.0 + "\n-------------\n");
		
		
		
		
		
		DbConnection.turnoff();


	}



}