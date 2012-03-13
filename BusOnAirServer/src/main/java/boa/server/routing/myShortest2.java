//VERSIONE DI SHORTEST PATH CHE NON CONSIDERE I NEXTINSTATION NELLA STAZIONE DI PARTENZA
// per versione iniziale delle tabelle statistiche di Flammini

package boa.server.routing;

import java.util.Stack;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Direction;

import boa.server.domain.*;

/**
 *
 * @author rashta
 */
public class myShortest2 {
    private StopMediator cache;
    private Stop source;
    private int stopTime;
    private Station dest;
    
    public myShortest2(Stop _source, Station _dest, int _timeInterval){
        cache = new StopMediator();   
        source = cache.get(_source);
        source.numeroCambi = 0;
        stopTime = source.getTime() + _timeInterval;
        dest = _dest;
    }
    
    public StopMediator shortestPath(){
        loadSubgraph();
        topologicalVisit();
        return cache;        
    }
    
    public Stop getShortestPath(){
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
        
    public void loadSubgraph(){
        
        Traverser graphTrav = source.getUnderlyingNode().traverse(
                Traverser.Order.BREADTH_FIRST,
                new StopExplorer(),
                ReturnableEvaluator.ALL, 
                RelTypes.NEXTINRUN,
                Direction.OUTGOING,
                RelTypes.NEXTINSTATION,
                Direction.OUTGOING);
        
        for (Node n : graphTrav){
            Stop s = cache.get(n);
            Stop nir = s.getNextInRun();
            Stop nis = s.getNextInStation();
            
            if(s.equals(source.getNextInStation())){
                nir = null;
                nis = null;    
                source.nextInStation = null;
            }
            
            if(s.getStazione().equals(dest)){
                nir = null;
                nis = null;                
            }
            
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

    public String toString() {
        Stop arrivo = getShortestPath();
        String outPath = "";    
        if(arrivo != null){
            outPath = "(" + arrivo.getUnderlyingNode().getId() + ":ID" + arrivo.getId() + ":STAZID" + arrivo.getStazione().getId() + ":TIME" + arrivo.getTime() + ")";

            Stop arr = arrivo;
            while(!arr.equals(source)){
                arr = arr.prevSP;
                outPath = "(" + arr.getUnderlyingNode().getId() + ":ID" + arr.getId()  + ":STAZID" + arr.getStazione().getId() + ":TIME" + arr.getTime() + ")-->" + outPath;                
            }
        }
        return outPath;

    }
    
     public class StopExplorer implements StopEvaluator{
//        int count = 0;

        @Override
        public boolean isStopNode(TraversalPosition tp) {            
//            System.out.println( "\nVisited nodes: " + count++);
            Stop currentStop = cache.get(tp.currentNode());         
            if(currentStop.prevInStation != null && currentStop.prevInStation.equals(source)){  //esclude l'attesa nella stazione di partenza!
                return true;
            }else if(currentStop.getStazione().equals(dest)){
                return true;
            }else if((currentStop.getTime() > stopTime))
                return true;    
            else
                return false;            
        }
    }   
}
