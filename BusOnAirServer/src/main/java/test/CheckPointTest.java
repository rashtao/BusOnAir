package test;

import java.util.ArrayList;

import org.neo4j.graphdb.GraphDatabaseService;

import domain.DbConnection;
import domain.Route;
import domain.Routes;
import domain.Run;
import domain.Runs;
import domain.Stop;
import domain.Stops;

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

//		for(Run r : Runs.getRuns().getAll()){
//			System.out.print("\n" + r.getAllCheckPoints().size() + ", " + r.getAllStops().size());
//		}
		
		Run r = Runs.getRuns().getRunById(11);
		System.out.print("\n" + r.getAllCheckPoints().size() + ", " + r.getAllStops().size());
	}
}
