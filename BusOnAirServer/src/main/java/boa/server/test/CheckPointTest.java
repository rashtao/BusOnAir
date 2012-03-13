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


public class CheckPointTest {

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

		for(Run r : Runs.getRuns().getAll()){
			System.out.print("\n" + r.getAllCheckPoints().size() + ", " + r.getAllStops().size());
		}
		
//		Run r = Runs.getRuns().getRunById(11);
//		System.out.print("\n" + r.getAllCheckPoints().size() + ", " + r.getAllStops().size());
	}
}
