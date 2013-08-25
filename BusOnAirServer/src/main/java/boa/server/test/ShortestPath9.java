package boa.server.test;

import boa.server.domain.DbConnection;
import boa.server.domain.Station;
import boa.server.domain.Stations;
import boa.server.domain.Stop;
import boa.server.routing.myShortest;
import org.neo4j.graphdb.GraphDatabaseService;


/**
 *
 * @author rashta
 */
public class ShortestPath9 {
	private static GraphDatabaseService db;

    

        public static void main(String[] args) {     
       		DbConnection.createEmbeddedDbConnection();
    		db = DbConnection.getDb();
    		
    		
            int time = 330;     //9h00
            Station s1 = Stations.getStations().getStationById(70);
            Station s2 = Stations.getStations().getStationById(1);
                
            System.out.print("\ns1: " + s1);
            System.out.print("\ns2: " + s2);
            
//            shortestpath.BreadthTraverser.shortestPath(s1, s2, time);
//            Path foundPath = shortestpath.ShortestPath.shortestPath(s1, s2, time);
//            
//                
//            if(foundPath != null){
//            System.out.println( "ShortestPath: "
//                                + Traversal.simplePathToString( foundPath) );
//            } else {
//            System.out.println( "ShortestPath: NULL");
//
//            }
            Stop firstStop = s1.getFirstStopFromTime(time);
            int prevTime = time;
            
            while(time < 570){            
                
                //System.out.print(firstStop);
                myShortest mysp = new myShortest(firstStop, s2, 1440);
                mysp.shortestPath();

                Stop arrivo = mysp.getShortestPath();
                //System.out.print("\n\nSTOP ARRIVO" + arrivo);
                System.out.print("\n-------\ndt: " + (arrivo.getTime() - prevTime));


                System.out.println( "\t" + mysp.toString());
                System.out.println( "\n" + firstStop.getPrevInStation());
//                String outPath = "";
//                for(Stop s : mysp.getWeightedPath()){
//                    outPath = "(" + s.getUnderlyingNode().getId() + ":ID" + s.getId()  + ":STAZID" + s.getStazione().getId() + ":TIME" + s.getTime() + ")-->" + outPath;                
//
//                }
                
                prevTime = firstStop.getTime();
                firstStop = firstStop.getNextInStation();
//                dt = firstStop.getTime() - time;
                time = firstStop.getTime();
                
                
            }
//            Stop arrivo = s2.getFirstStopsFromTime(time);
//            
//            while(arrivo != null && !cache.check(arrivo)){
//                arrivo = arrivo.getNextInStation();
////                System.out.println( "\n\nNot reachable node: " + arrivo);
//            }
//
//            arrivo = cache.get(arrivo);
//            while(arrivo != null){
//                arrivo = cache.get(arrivo);
//
//                System.out.println("\n\n#cambi: " + arrivo.numeroCambi);
//
//                String outPath = "(" + arrivo.getUnderlyingNode().getId() + ":ID" + arrivo.getId() + ":STAZID" + arrivo.getStazione().getId() + ":TIME" + arrivo.getTime() + ")";
//                
//
//                Stop arr = arrivo;
//                while(!arr.equals(firstStop)){
//                    arr = arr.prevSP;
//                    outPath = "(" + arr.getUnderlyingNode().getId() + ":ID" + arr.getId()  + ":STAZID" + arr.getStazione().getId() + ":TIME" + arr.getTime() + ")-->" + outPath;                
//                }
//
//                System.out.println( "myShortestPath: " + outPath);
//
//   
//                do{
//                    arrivo = arrivo.getNextInStation();   
//                    if(arrivo != null)
//                        arrivo = cache.get(arrivo);
//                } while(arrivo != null && arrivo.prevSP == null);
//            }
//            
            
            DbConnection.turnoff();
        }
    
    
}
