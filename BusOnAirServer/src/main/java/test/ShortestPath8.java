package test;

import domain.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import myShortest.StopMediator;
import myShortest.myShortest;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.kernel.Traversal;


/**
 *
 * @author rashta
 */
public class ShortestPath8 {
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
            Stop firstStop = s1.getFirstStopsFromTime(time);
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
