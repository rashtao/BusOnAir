package boa.server.test;

import boa.server.domain.DbConnection;
import boa.server.domain.Run;
import boa.server.domain.Runs;
import org.neo4j.graphdb.GraphDatabaseService;


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
