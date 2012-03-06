package test;

import java.util.Collection;

import org.neo4j.graphdb.GraphDatabaseService;

import utils.GeoUtil;

import domain.DbConnection;
import domain.Station;

public class Geotest {

	private static GraphDatabaseService db;


	public static void main(String[] args) {    
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		int range = 8;
		double lat = 42.3358;
		double lon = 13.4712;
		Collection<Station> stations = domain.Stations.getStations().nearestStations(lat, lon, range);
//		Collection<Station> stations = domain.Stations.getStations().nearestStations(lat, lon);

		System.out.print(stations.size() + "\n");
		Station s1 = stations.iterator().next();
		
		Double slat= s1.getLatitude();
		Double slon= s1.getLongitude();
		Double distance = GeoUtil.getDistance2(lat, lon, slat, slon);
		
		System.out.print(distance*1000.0 + "\n");
		
		
		DbConnection.turnoff();


	}



}