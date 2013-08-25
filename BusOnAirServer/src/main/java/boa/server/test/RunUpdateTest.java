package boa.server.test;


import boa.server.domain.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;



public class RunUpdateTest {

	private static GraphDatabaseService db;
    

    public static void main(String[] args) throws Exception {     
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		
		Run r = Runs.getRuns().getRunById(11);

		Transaction tx = DbConnection.getDb().beginTx();
		try{		
			r.restore();
			tx.success();
		}finally{
			tx.finish();			
		}    	

		System.out.print("\n\n\nRunning Buses:\n");
		for(Run rr : Runs.getRuns().getAllRunningBuses())
			System.out.println(rr);
		
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

		tx = DbConnection.getDb().beginTx();
		try{		
			r.checkPointPass(cp2, 547*60);
			tx.success();
		}finally{
			tx.finish();			
		}    	
		

//		r.updatePosition(lati,longi,500);
//		r.addCheckPoint(lati,longi,500*60);

		System.out.print("\nDopo:\n");
		System.out.print(r.toString());
		
//		r.updatePosition(lati, longi, 505);		
//		System.out.print("\n\n\nRunning Buses:\n");
//		for(Run rr : Runs.getRuns().getAllRunningBuses())
//			System.out.println(rr);


		
		
		DbConnection.turnoff();
	}

}
