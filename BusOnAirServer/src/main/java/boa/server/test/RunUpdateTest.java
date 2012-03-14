package boa.server.test;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import boa.server.domain.*;



public class RunUpdateTest {

	private static GraphDatabaseService db;
    

    public static void main(String[] args) throws Exception {     
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		
		Run r = Runs.getRuns().getRunById(11);

		r.restore();

		
		CheckPoint cp = r.getFirstCheckPoint();
		CheckPoint cp2 = cp.getNextCheckPoint();
		CheckPoint cp3 = cp2.getNextCheckPoint();
		CheckPoint cp4 = cp3.getNextCheckPoint();
		CheckPoint cp5 = cp4.getNextCheckPoint();
		CheckPoint cp6 = cp5.getNextCheckPoint();
		Stop s = cp.getTowards();

		Station staz = s.getStation();

//		System.out.print("CheckPoint0:\n" + cp + "\n\n\n");
//		System.out.print("CheckPoint1:\n" + cp2 + "\n\n\n");
//		System.out.print("CheckPoint2:\n" + cp3 + "\n\n\n");

		
//		System.out.print("CheckPoint:\n" + cp + "\n\n\n");
//		System.out.print("Stop:\n" + s + "\n\n\n");
		
		
		
		System.out.print("\nPrima:\n");
		System.out.print(r.toString());


//		Double lati = (cp.getLatitude()*.8 + cp2.getLatitude()*.2);
//		Double longi = (cp.getLongitude()*.8 + cp2.getLongitude()*.2);
	
		Double lati = (cp4.getLatitude()*.8 + cp6.getLatitude()*.2);
		Double longi = (cp4.getLongitude()*.8 + cp6.getLongitude()*.2);
	
//		r.updateRun(cp, 420);
//		r.updatePosition(lati,longi,500);
		r.addCheckPoint(lati,longi,500);

		System.out.print("\nDopo:\n");
		System.out.print(r.toString());


		
		
		DbConnection.turnoff();
	}

}
