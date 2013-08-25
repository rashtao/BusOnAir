package boa.server.test;


import boa.server.domain.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;



public class DelayTest {


    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
        GraphDatabaseService db = DbConnection.getDb();

		Run r = Runs.getRuns().getRunById(11);

		Transaction tx = DbConnection.getDb().beginTx();
		try{
			r.restore();
			tx.success();
		}finally{
			tx.finish();			
		}    	

		CheckPoint cp = r.getFirstCheckPoint().getNextCheckPoint();
		Stop s = cp.getTowards();
		Station staz = s.getStation();
		Stop fsStaz = staz.getFirstStopFromTime(0);

		System.out.print("CheckPoint:\n" + cp + "\n\n\n");
		System.out.print("Stop:\n" + s + "\n\n\n");
		
		
		System.out.print("\nPrima:\n");
		System.out.print("\nRun:\n");
		System.out.print(r.toString());
		System.out.print("\nStation:\n");

		while(fsStaz != null){
			System.out.print("-->" + fsStaz);
			fsStaz = fsStaz.getNextInStation();
		}

		tx = DbConnection.getDb().beginTx();
		try{
			r.checkPointPass(cp, 1160*60);
			tx.success();
		}finally{
			tx.finish();			
		}    	

		System.out.print("\nDopo:\n");
		System.out.print("\nRun:\n");
		System.out.print(r.toString());
		System.out.print("\nStation:\n");

		fsStaz = staz.getFirstStopFromTime(0);
		while(fsStaz != null){
			System.out.print("-->" + fsStaz);
			fsStaz = fsStaz.getNextInStation();
		}
		
		
		
		System.out.print("\n\n\ngetAllCheckPointsInSpatialIndex: ");
		for(CheckPoint c : r.getAllCheckPointsInSpatialIndex()){
			System.out.print("\n" + c  + " NODEID:" + c.getUnderlyingNode().getId());			
		}		
		
		System.out.print("\nFirst CheckPoint: " + r.getFirstCheckPoint());

		
		System.out.print("\nRemoving CheckPoint: " + cp + " NODEID:" + cp.getUnderlyingNode().getId());

		
		tx = DbConnection.getDb().beginTx();
		try{
			r.deleteCheckPoint(cp);
			tx.success();
		}finally{
			tx.finish();			
		}   
		
//		tx = DbConnection.getDb().beginTx();
//		try{
//			
//			tx.success();
//		}finally{
//			tx.finish();			
//		}  		
		DbConnection.turnoff();
	}

}
