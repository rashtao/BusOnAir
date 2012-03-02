package test;

import java.util.Collection;

import org.neo4j.graphdb.GraphDatabaseService;

import domain.DbConnection;
import domain.Station;

public class Geotest {

	private static GraphDatabaseService db;


	public static void main(String[] args) {    
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		int range = 1000;
		double lat = 42.33585;
		double lon = 13.47126;
		Collection<Station> stations = domain.Stations.getStations().nearestStations(lat, lon, range);
//		Collection<Station> stations = domain.Stations.getStations().nearestStations(lat, lon);

		System.out.print(stations.size());
		DbConnection.turnoff();


	}



}