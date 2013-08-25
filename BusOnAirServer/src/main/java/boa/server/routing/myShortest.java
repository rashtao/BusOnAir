
package boa.server.routing;

import boa.server.domain.RelTypes;
import boa.server.domain.Station;
import boa.server.domain.Stop;
import org.neo4j.graphdb.*;

import java.util.ArrayList;
import java.util.Stack;

public class myShortest {
    private StopMediator cache;
    private Stop source;
    private int stopTime;
    private Station dest;

    public myShortest(Stop _source, Station _dest, int _timeInterval) {
        cache = new StopMediator();
        source = cache.get(_source);
        source.numeroCambi = 0;
        stopTime = source.getTime() + _timeInterval;
        dest = _dest;
    }

    public StopMediator shortestPath() {
        loadSubgraph();

        if (source.getStation().equals(dest)) { // Gestione partenza == arrivo
            source.prevSP = source;
            source.numeroCambi = 0;
        } else {
            topologicalVisit();
        }
        return cache;
    }

    public Stop getShortestPath() {
        Stop arrivo = dest.getFirstStopFromTime(source.getTime());
        if (arrivo != null)
            arrivo = cache.get(arrivo);

        while (arrivo != null && arrivo.prevSP == null) {
            arrivo = arrivo.getNextInStation();
            if (arrivo != null)
                arrivo = cache.get(arrivo);
        }

        return arrivo;
    }

    public String toString() {
        Stop arrivo = getShortestPath();
        String outPath = "";
        if (arrivo != null) {
            outPath = "(" + arrivo.getUnderlyingNode().getId() + ":ID" + arrivo.getId() + ":STAZID" + arrivo.getStation().getId() + ":TIME" + arrivo.getTime() + ")";

            Stop arr = arrivo;
            while (!arr.equals(source)) {
                arr = arr.prevSP;
                outPath = "(" + arr.getUnderlyingNode().getId() + ":ID" + arr.getId() + ":STAZID" + arr.getStation().getId() + ":TIME" + arr.getTime() + ")-->" + outPath;
            }
        }
        return outPath;

    }

    public ArrayList<Stop> getWeightedPath() {
        ArrayList<Stop> stopList = new ArrayList<Stop>();

        Stop s = getShortestPath();

        while (s != null) {
            stopList.add(s);
            s = s.prevSP;
        }

        return stopList;
    }


    public void loadSubgraph() {

        Traverser graphTrav = source.getUnderlyingNode().traverse(
                Traverser.Order.BREADTH_FIRST,
                new StopExplorer(),
                ReturnableEvaluator.ALL,
                RelTypes.NEXTINRUN,
                org.neo4j.graphdb.Direction.OUTGOING,
                RelTypes.NEXTINSTATION,
                org.neo4j.graphdb.Direction.OUTGOING);

        for (Node n : graphTrav) {
            Stop s = cache.get(n);
            Stop nir = s.getNextInRun();
            Stop nis = s.getNextInStation();

            if (s.getStation().equals(dest)) {
                nir = null;
                nis = null;
            }

            if (nir != null) {
                nir = cache.get(nir);
                s.nextInRun = nir;
                nir.prevInRun = s;
            }

            if (nis != null) {
                nis = cache.get(nis);
                s.nextInStation = nis;
                nis.prevInStation = s;
            }
        }
    }

    public void topologicalVisit() {

        Stack<Stop> toVisit = new Stack<Stop>();
        toVisit.push(source);

        while (!toVisit.isEmpty()) {
            Stop s = toVisit.pop();
            Stop nir = s.nextInRun;
            Stop nis = s.nextInStation;


            if (nir != null) {
                // UPDATE Shortest path e cambi
                if (nir.prevSP == null) {
                    nir.prevSP = s;
                    nir.numeroCambi = s.numeroCambi;
                } else if (s.numeroCambi < nir.numeroCambi) {
                    nir.prevSP = s;
                    nir.numeroCambi = s.numeroCambi;
                }

                // Gestione visita topologica
                nir.prevInRun = null;
                if (nir.prevInStation == null) {
                    toVisit.push(nir);
                }
            }

            if (nis != null) {
                // UPDATE Shortest path e cambi
                int cambio = 0;
                if ((s.prevSP != null) && (s.prevSP.equals(s.getPrevInRun()))) {
                    cambio = 1;
                }

                int cambiPerNis = s.numeroCambi + cambio;
                if (nis.prevSP == null) {
                    nis.prevSP = s;
                    nis.numeroCambi = cambiPerNis;
                } else if (cambiPerNis <= nis.numeroCambi) {
                    nis.prevSP = s;
                    nis.numeroCambi = cambiPerNis;
                }

                // Gestione visita topologica
                nis.prevInStation = null;
                if (nis.prevInRun == null) {
                    toVisit.push(nis);
                }
            }
        }
    }

    public class StopExplorer implements StopEvaluator {
//        int count = 0;

        @Override
        public boolean isStopNode(TraversalPosition tp) {
//            System.out.println( "\nVisited nodes: " + count++);
            Stop currentStop = cache.get(tp.currentNode());
            if (currentStop.getStation().equals(dest)) {
                return true;
            } else return (currentStop.getTime() > stopTime);
        }
    }
}
