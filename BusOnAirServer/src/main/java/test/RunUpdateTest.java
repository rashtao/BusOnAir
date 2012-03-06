package test;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import domain.*;


public class RunUpdateTest {

	private static GraphDatabaseService db;
    

    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		Run r = Runs.getRuns().getRunById(11);
		r.restoreRun();
		
		CheckPoint cp = r.getCheckPointById(0);
		CheckPoint cp2 = r.getCheckPointById(1);
		Stop s = cp.getTowards();
		Station staz = s.getStazione();

		System.out.print("CheckPoint0:\n" + cp + "\n\n\n");
		System.out.print("CheckPoint1:\n" + cp2 + "\n\n\n");

		
		System.out.print("CheckPoint:\n" + cp + "\n\n\n");
		System.out.print("Stop:\n" + s + "\n\n\n");
		
		r.updateRun(cp, 420);
		
		System.out.print("\nPrima:\n");
		System.out.print("\nRun:\n");
		System.out.print(r.toString());
		System.out.print("\nStation:\n");

		Double lati = (cp.getLatitude()*.8 + cp2.getLatitude()*.2);
		Double longi = (cp.getLongitude()*.8 + cp2.getLongitude()*.2);
	
		r.update(lati,longi,424);

		System.out.print("\nDopo:\n");
		System.out.print("\nRun:\n");
		System.out.print(r.toString());


		
		
		DbConnection.turnoff();
	}

}
