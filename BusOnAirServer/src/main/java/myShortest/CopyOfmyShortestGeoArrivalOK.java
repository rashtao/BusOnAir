/* Versione di shortest path che calcola il percorso da un pto di partenza a uno di arrivo (non stazioni). 
 * Quindi include le walk per raggiungere la stazione di partenza dal pto di partenza e per raggiundere il
 * pto di destinazione dalla stazione di arrivo.
 * 
 * Specifiche da implementare: 
 * List<Direction> getDirections(
 * 		double lat1, 
 * 		double lon1, 
 * 		double lat2, 
 * 		double lon2, 
 * 		int departureTime, 
 * 		int minChangeTime, 
 * 		String criterion, 
 * 		int timeLimit)

*/

package myShortest;

import domain.*;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Stack;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.*;

/**
 *
 * @author rashta
 */
public class CopyOfmyShortestGeoArrivalOK {
    private StopMediator cache;
    private Stop source;
    private int startTime;
    private int stopTime;
    private double lat1;
    private double lon1;
    private double lat2;
    private double lon2;
    private int walkLimit;
    private  PriorityQueue<Direction> startQueue;
    private  PriorityQueue<Direction> arrivalQueue;
   
    public CopyOfmyShortestGeoArrivalOK(Stop _source, int _timeInterval, double lat1, double lon1, double lat2, double lon2, int walkLimit){
        cache = new StopMediator();   
        source = cache.get(_source);
        source.numeroCambi = 0;
        stopTime = source.getTime() + _timeInterval;
        this.lat1 = lat1;
        this.lon1 = lon1;
        this.lat2 = lat2;
        this.lon2 = lon2;
        this.walkLimit = walkLimit;
        startQueue = new PriorityQueue<Direction>(10, new DirectionComparator());
        arrivalQueue = new PriorityQueue<Direction>(10, new DirectionComparator());        
    }
    
    public StopMediator shortestPath(){
        loadSubgraph();
        
        linkStartPoint();
            
        topologicalVisit();
        
        linkArrivalPoint();
        
        return cache;        
    }
    


	public Stop getShortestPath(Station dest){
        Stop arrivo = dest.getFirstStopsFromTime(source.getTime());
        if(arrivo != null)
            arrivo = cache.get(arrivo);
        
        while(arrivo != null && arrivo.prevSP == null){
            arrivo = arrivo.getNextInStation();
            if(arrivo != null)
                arrivo = cache.get(arrivo);            
        }
    
        return arrivo;
    }
    
    public String toString() {
    	String strPath = "";    
    	for(Direction dir : arrivalQueue){
	        String outPath = "";
    		Stop arrivo = dir.getStop();
	        if(arrivo != null){
	        	outPath = "(WALK: t:" + dir.getWalkTime() + "|arrtime:" + dir.getArrivalTime() + "|distwalk:" + dir.getDistance() + "|numchanges:" + dir.getNumChanges() + ")";
	            outPath = "(" + arrivo.getUnderlyingNode().getId() + ":ID" + arrivo.getId() + ":STAZID" + arrivo.getStazione().getId() + ":TIME" + arrivo.getTime() + ")-->" + outPath;
	
	            Stop arr = arrivo;
	            while(!arr.equals(source)){
	                arr = arr.prevSP;
	                outPath = "(" + arr.getUnderlyingNode().getId() + ":ID" + arr.getId()  + ":STAZID" + arr.getStazione().getId() + ":TIME" + arr.getTime() + ")-->" + outPath;                
	            }
	        }
	        strPath = strPath + "\n\n" + outPath; 
    	}
    	
        return strPath;

    }
    
    
//    public json.Directions getDirections(){
//    	json.Directions output = new json.Directions();
//
//    	Stop arrivo = getShortestPath();
//
//    	while(arrivo != null){
//    		output.add(getDirection(arrivo));
//    		
//    		//prende il successivo arrivalstop
//    		while(arrivo != null && arrivo.prevSP == null){
//	            arrivo = arrivo.getNextInStation();
//	            if(arrivo != null)
//	                arrivo = cache.get(arrivo);            
//	        }
//    	}
//    	
//    	return output;
//    }
//    
//    public json.Direction getDirection(Stop arrivo){
//    	json.Direction output = new json.Direction();
//    	
//    	
//        outPath = "(" + arrivo.getUnderlyingNode().getId() + ":ID" + arrivo.getId() + ":STAZID" + arrivo.getStazione().getId() + ":TIME" + arrivo.getTime() + ")";
//
//        Stop arr = arrivo;
//        while(!arr.equals(source)){
//            arr = arr.prevSP;
//            outPath = "(" + arr.getUnderlyingNode().getId() + ":ID" + arr.getId()  + ":STAZID" + arr.getStazione().getId() + ":TIME" + arr.getTime() + ")-->" + outPath;                
//        }
//    }
    
    
    
//    public ArrayList<Stop> getWeightedPath() {
//        ArrayList<Stop> stopList = new ArrayList<Stop>();
//        
//        Stop s = getShortestPath();
//
//        while(s != null){
//            stopList.add(s);
//            s = s.prevSP;
//        }
//
//        return stopList;
//    }    
    
    
    private void linkArrivalPoint() {
    	// al momento linka solo il primo stop di ogni stazione più vicina di walkLimit meters
		Collection<Station> arrivalStations = Stations.getStations().nearestStations(lat2, lon2, walkLimit);
		
		System.out.print("\nLnked stations: " + arrivalStations.size());
		for(Station s : arrivalStations){
			Stop arrivalStop = getShortestPath(s);
			System.out.print("\nsp: " + arrivalStop);
			arrivalQueue.add(new Direction(arrivalStop, lat2, lon2));			
		}		
	}

    private void linkStartPoint() {
//		Collection<Station> startStations = Stations.getStations().nearestStations(lat1, lon1, walkLimit);
//		
//		System.out.print("\nLnked start stations: " + startStations.size());
//		for(Station s : startStations){
//			Stop startStop = s.getFirstStopsFromTime(startTime);
//			System.out.print("\nsp: " + arrivalStop);
//			arrivalQueue.add(new Direction(arrivalStop, lat2, lon2));			
//		}	
		
	}
    
	public void loadSubgraph(){
        
        Traverser graphTrav = source.getUnderlyingNode().traverse(
                Traverser.Order.BREADTH_FIRST,
                new StopExplorer(),
                ReturnableEvaluator.ALL, 
                RelTypes.NEXTINRUN,
                org.neo4j.graphdb.Direction.OUTGOING,
                RelTypes.NEXTINSTATION,
                org.neo4j.graphdb.Direction.OUTGOING);
        
        for (Node n : graphTrav){
            Stop s = cache.get(n);
            Stop nir = s.getNextInRun();
            Stop nis = s.getNextInStation();
            
//            if(s.getStazione().equals(dest)){
//                nir = null;
//                nis = null;                
//            }
            
            if(nir != null){
                nir = cache.get(nir);      
                s.nextInRun = nir;
                nir.prevInRun = s;
            }
            
            if(nis != null){
                nis = cache.get(nis);
                s.nextInStation = nis;
                nis.prevInStation = s;                    
            }
        }        
    }
    
    public void topologicalVisit(){

        Stack<Stop> toVisit = new Stack<Stop>();
        toVisit.push(source);
        
        while(!toVisit.isEmpty()){
            Stop s = toVisit.pop();
            Stop nir = s.nextInRun;
            Stop nis = s.nextInStation;
            
            
                        
            if(nir != null){
                // UPDATE Shortest path e cambi
                if(nir.prevSP == null){
                    nir.prevSP = s;
                    nir.numeroCambi = s.numeroCambi;
                } else if(s.numeroCambi < nir.numeroCambi){
                    nir.prevSP = s;
                    nir.numeroCambi = s.numeroCambi;
                }

                // Gestione visita topologica
                nir.prevInRun = null;
                if(nir.prevInStation == null){
                    toVisit.push(nir);
                }
            }
            
            if(nis != null){
                // UPDATE Shortest path e cambi
                int cambio = 0;
                if((s.prevSP != null) && (s.prevSP.equals(s.getPrevInRun()))){
                    cambio = 1;
                }
                
                int cambiPerNis = s.numeroCambi + cambio;                
                if(nis.prevSP == null){
                    nis.prevSP = s;
                    nis.numeroCambi = cambiPerNis;
                } else if(cambiPerNis <= nis.numeroCambi){
                    nis.prevSP = s;
                    nis.numeroCambi = cambiPerNis;
                }

                // Gestione visita topologica
                nis.prevInStation = null;
                if(nis.prevInRun == null){
                    toVisit.push(nis);
                }            
            }
        }
    }            
    
     public class StopExplorer implements StopEvaluator{
//        int count = 0;

        @Override
        public boolean isStopNode(TraversalPosition tp) {            
//            System.out.println( "\nVisited nodes: " + count++);
            Stop currentStop = cache.get(tp.currentNode());   
            if(currentStop.nextInRun != null || currentStop.nextInStation != null){
            	return true;
            } else if((currentStop.getTime() > stopTime)){
                return true;    
            }else{
                return false;
            }
        }
    }   
}
