package boa.server.routing;

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
 * 		int departureDay, 
 * 		int minChangeTime, 
 * 		String criterion, 
 * 		int timeLimit)
 * 
 * 
 * TODO:
 * . ordinare l'output di getdirections secondo in modo lessicografico secondo il criterio di ottimizzazione scelto
*/


import boa.server.domain.*;
import boa.server.domain.utils.GeoUtil;
import org.neo4j.graphdb.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class ShortestPathGeo {
    private StopMediator cache;
    private int startTime;
    private int stopTime;
    private double lat1;
    private double lon1;
    private double lat2;
    private double lon2;
    private int walkLimit;
    private Criteria secondCriterion;
    private Stack<TransientStop> startStack;
    private LinkedList<Direction> arrivalList;

    public ShortestPathGeo(int startTime, int _timeInterval, double lat1, double lon1, double lat2, double lon2, int walkLimit, Criteria criterion) {
        cache = new StopMediator();
        this.startTime = startTime;
        stopTime = startTime + _timeInterval;
        this.lat1 = lat1;
        this.lon1 = lon1;
        this.lat2 = lat2;
        this.lon2 = lon2;
        this.walkLimit = walkLimit;
        secondCriterion = criterion;
        startStack = new Stack<TransientStop>();
        arrivalList = new LinkedList<Direction>();
    }

    public StopMediator shortestPath() {

        createTransientStops();

        for (TransientStop ts : startStack) {
            loadSubgraph(ts.nextWalk);
        }

        multiCriteriaTopologicalVisit();

        linkArrivalPoint();

        return cache;
    }

    private Stop getShortestPath(Station dest, int fromTime) {
        Stop arrivo = dest.getFirstStopFromTime(fromTime);
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
        String strPath = "";
        for (Direction dir : arrivalList) {
            String outPath = "";
            Stop arrivo = dir.getStop();
            String dirstr = "";
            String LASTsTAZ = "";

            if (arrivo != null) {

                outPath = "(WALK: t:" + dir.getWalkTime() + "|arrtime:" + dir.getArrivalTime() + "|distwalk:" + dir.getDistance() + "|numchanges:" + dir.getNumChanges() + ")";
                dirstr = outPath;

                LASTsTAZ = "(" + arrivo.getUnderlyingNode().getId() + ":ID" + arrivo.getId() + ":STAZID" + arrivo.getStation().getId() + ":RUNID" + arrivo.getRun().getId() + ":TIME" + arrivo.getTime() + ")-->";
                outPath = LASTsTAZ + outPath;

                Stop arr = arrivo;
                arr = arr.prevSP;
                while (arr.prevSP != null) {
                    outPath = "(" + arr.getType() + ":" + arr.getId() + ":IDNODE" + arr.getUnderlyingNode().getId() + ":STAZID" + arr.getStation().getId() + ":RUNID" + (arr.getRun() != null ? arr.getRun().getId() : "NN") + ":TIME" + arr.getTime() + ")-->" + outPath;
                    arr = arr.prevSP;
                }
                outPath = "(" + arr.getType() + ":" + arr.getId() + ":STAZID" + arr.getStation().getId() + ":RUNID" + "NN" + ":TIME" + arr.getTime() + ")-->" + outPath;

            }
            strPath = strPath + dir + "\n\n" + dirstr + ":\tARRIVAL:" + LASTsTAZ + "dep:" + dir.getDepartureTime() + "walk:" + dir.getWalkDistance() + "\t\t" + outPath;
        }

        return strPath;

    }

    private void createTransientStops() {
        Collection<Station> startStations = Stations.getStations().getNearestStations(lat1, lon1, walkLimit);

        for (Station s : startStations) {
            double distance = GeoUtil.getDistance2(lat1, lon1, s.getLatitude(), s.getLongitude());
            int walktime = (int) (distance / Config.WALKSPEED * 60);
            Stop startStop = s.getFirstStopFromTime(startTime + walktime);
            while (startStop != null) {
                startStop = cache.get(startStop);

                TransientStop ts = new TransientStop();
                ts.setTime(startStop.getTime() - walktime);
                ts.nextWalk = startStop;
                ts.setStazione(s);

                ts.departureTime = ts.getTime();
                ts.walkDistance = (int) (distance * 1000.0);

                startStop.prevWalk = ts;

                startStack.add(ts);

                startStop = startStop.getNextInStation();
            }
        }
    }

    private void linkArrivalPoint() {
    /*    	 collega l'arrival point agli stop delle stazioni adiacenti tramite archi walk

	    	 collegando tutti gli stop delle stazioni adiacenti (entro WALKLIMIT),
	    	 
	    	 Infine per ogni stazione, per ogni percorso trovato, crea una lista di risultati. I risultati si inseriscono
	    	 in ordine di orario di arrivo, quindi secondo l'ordine dei nodi nelle rispettive stazioni.
	    	 
	    	 Quindi si procede a fare un merge-sort di tutte le liste di tutte le stazioni di arrivo, tenendo ordinati i risultati
	    	 per arrival-time e scartando on-the-fly i risultati che non sono ottimi di pareto. ==> arrivalList
	    	 
	    	 Gli ottimi si valutano in base al secondo criterio di ottimizzazione scelto. Dati 2 Stop di arrivo successivi
	    	 s1 e s2, con t(s1) < t(s2), allora s2 è un ottimo di pareto se SECONDOCRITERIO(s2) < SECONDOCRITERIO(s1) 
	    	 
	    	 NB: nell'attuale implementazione il secondo criterio di ottimizzazzione è la composizione di tutti i criteri ammissibili,
	    	 	 mettendo prima il secondo criterio scelto e, a seguire tutti gli altri criteri
	*/

        LinkedList<Direction> arrivals = new LinkedList<Direction>();
        Collection<Station> arrivalStations = Stations.getStations().getNearestStations(lat2, lon2, walkLimit);

        //		System.out.print("\nLnked arrival stations: " + arrivalStations.size());
        for (Station s : arrivalStations) {
            int time = startTime;
            Stop arrivalStop = getShortestPath(s, time);

            while (arrivalStop != null) {
                //			System.out.print("\nsp: " + arrivalStop);
                arrivals.add(new Direction(arrivalStop, lat2, lon2));

//						while(arrivalStop != null && arrivalStop.nextInStation != null && arrivalStop.equals(arrivalStop.nextInStation.prevSP)){
//							arrivalStop = arrivalStop.nextInStation;
//						}

                if (arrivalStop != null)
                    arrivalStop = arrivalStop.nextInStation;
            }
        }

        DirectionComparator dirC = new DirectionComparator(secondCriterion);
        Collections.sort(arrivals, dirC);


        // --- ELIMINAZIONE ESITI DOMINATI
        Direction prev = null;

        for (Direction d : arrivals) {
//					System.out.print("\n\n---------- Comparing: \nd:\n" + d + "\nprev:\n" + prev + "\nRIS: " +comp.secondCriterionCompare(d, prev) + "\n-------\n\n");
            if (dirC.secondCriterionCompare(d, prev) < 0) {
                arrivalList.add(d);
                prev = d;
            }
        }
        // --- ELIMINAZIONE ESITI DOMINATI (end)

    }


    public void loadSubgraph(Stop source) {
        source = cache.get(source);

        Traverser graphTrav = source.getUnderlyingNode().traverse(
                Traverser.Order.BREADTH_FIRST,
                new StopExplorer(),
                ReturnableEvaluator.ALL,
                RelTypes.NEXTINRUN,
                org.neo4j.graphdb.Direction.OUTGOING,
                RelTypes.NEXTINSTATION,
                org.neo4j.graphdb.Direction.OUTGOING);

        for (Node n : graphTrav) {
            connectNode(cache.get(n));
        }
    }

    private void connectNode(Stop s) {
        Stop nir = s.getNextInRun();
        Stop nis = s.getNextInStation();

//        if(s.getStazione().equals(dest)){
//            nir = null;
//            nis = null;                
//        }

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

    private int compareMinTravelTime(Stop s, Stop next) {
        // confronta s con il suo Next rispetto al criterio di ottimizzazione MINTRAVELTIME
        // ritorna 1 se s rappresenta un prevSP MIGLIORE per il suo Next
        // ritorna -1 se s rappresenta un prevSP PEGGIORE per il suo Next
        // ritorna 0 se s rappresenta un prevSP UGUALE per il suo Next

        if (s.travelTime < next.travelTime)
            return 1;
        else if (s.travelTime > next.travelTime)
            return -1;
        else
            return 0;
    }

    private int compareMinWalk(Stop s, Stop next) {
        // confronta s con il suo Next rispetto al criterio di ottimizzazione MINWALK
        // ritorna 1 se s rappresenta un prevSP MIGLIORE per il suo Next
        // ritorna -1 se s rappresenta un prevSP PEGGIORE per il suo Next
        // ritorna 0 se s rappresenta un prevSP UGUALE per il suo Next

        if (s.walkDistance < next.walkDistance)
            return 1;
        else if (s.walkDistance > next.walkDistance)
            return -1;
        else
            return 0;
    }

    private int compareNIRMinChanges(Stop s, Stop nir) {
        // confronta s con il suo NextInRun rispetto al criterio di ottimizzazione MINCHANGES
        // ritorna 1 se s rappresenta un prevSP MIGLIORE per il suo NextInRun
        // ritorna -1 se s rappresenta un prevSP PEGGIORE per il suo NextInRun
        // ritorna 0 se s rappresenta un prevSP UGUALE per il suo NextInRun

        if (s.numeroCambi < nir.numeroCambi)
            return 1;
        else if (s.numeroCambi > nir.numeroCambi)
            return -1;
        else
            return 0;
    }

    private int compareDuration(Stop s, Stop next) {
        // confronta s con il suo Next rispetto al criterio di ottimizzazione DURATION
        // ritorna 1 se s rappresenta un prevSP MIGLIORE per il suo Next
        // ritorna -1 se s rappresenta un prevSP PEGGIORE per il suo Next
        // ritorna 0 se s rappresenta un prevSP UGUALE per il suo Next

        if (s.departureTime > next.departureTime)
            return 1;
        else if (s.departureTime < next.departureTime)
            return -1;
        else
            return 0;
    }

    private int compareNISMinChanges(Stop s, Stop nis) {
        // confronta s con il suo NextInStation rispetto al criterio di ottimizzazione MINCHANGES
        // ritorna 1 se s rappresenta un prevSP MIGLIORE per il suo NextInStation
        // ritorna -1 se s rappresenta un prevSP PEGGIORE per il suo NextInStation
        // ritorna 0 se s rappresenta un prevSP UGUALE per il suo NextInStation

        int cambio = 0;
        if ((s.prevSP != null) && (s.prevSP.equals(s.getPrevInRun()))) {
            cambio = 1;
        }

        int cambiPerNis = s.numeroCambi + cambio;

        if (cambiPerNis < nis.numeroCambi)
            return 1;
        else if (cambiPerNis > nis.numeroCambi)
            return -1;
        else
            return 0;
    }

    private boolean compareNIR(Stop s, Stop nir) {
        // confronta s con il suo NextInRun
        // ritorna TRUE se s rappresenta un prevSP migliore per il suo NextInRun

        // di default l'ordine dei criteri di ottimizzazione è:
        // - EARLIESTARRIVAL
        // - MINCHANGES
        // - DURATION
        // - MINWALK
        // - MINTRAVELTIME

        int result = 0;

        if (secondCriterion == Criteria.DURATION) {
            if (result == 0)
                result = compareDuration(s, nir);
            if (result == 0)
                result = compareNIRMinChanges(s, nir);
            if (result == 0)
                result = compareMinWalk(s, nir);
        } else if (secondCriterion == Criteria.MINWALK) {
            if (result == 0)
                result = compareMinWalk(s, nir);
            if (result == 0)
                result = compareNIRMinChanges(s, nir);
            if (result == 0)
                result = compareDuration(s, nir);
        } else {    // secondCriterion == Criteria.MINCHANGES
            if (result == 0)
                result = compareNIRMinChanges(s, nir);
            if (result == 0)
                result = compareDuration(s, nir);
            if (result == 0)
                result = compareMinWalk(s, nir);
        }

        // Scelta minTravelTime (in caso di indecisione rispetto ai criteri precedenti)
        // utile per evitare paths che passano più volte per la stessa stazione
        if (result == 0)
            result = compareMinTravelTime(s, nir);

        return result != -1;
    }

    private boolean compareNIS(Stop s, Stop nis) {
        // confronta s con il suo NextInStation
        // ritorna TRUE se s rappresenta un prevSP migliore per il suo NextInStation

        // di default l'ordine dei criteri di ottimizzazione è:
        // - EARLIESTARRIVAL
        // - MINCHANGES
        // - DURATION
        // - MINWALK
        // - MINTRAVELTIME

        int result = 0;

        if (secondCriterion == Criteria.DURATION) {
            if (result == 0)
                result = compareDuration(s, nis);
            if (result == 0)
                result = compareNISMinChanges(s, nis);
            if (result == 0)
                result = compareMinWalk(s, nis);
        } else if (secondCriterion == Criteria.MINWALK) {
            if (result == 0)
                result = compareMinWalk(s, nis);
            if (result == 0)
                result = compareNISMinChanges(s, nis);
            if (result == 0)
                compareDuration(s, nis);
        } else {    // secondCriterion == Criteria.MINCHANGES
            if (result == 0)
                result = compareNISMinChanges(s, nis);
            if (result == 0)
                result = compareDuration(s, nis);
            if (result == 0)
                result = compareMinWalk(s, nis);
        }

        // Scelta minTravelTime (in caso di indecisione rispetto ai criteri precedenti)
        // utile per evitare paths che passano più volte per la stessa stazione
        if (result == 0)
            result = compareMinTravelTime(s, nis);

        return result == 1;
    }

    private void linkNIW(Stop s, Stop niw) {
        if (niw.prevSP == null || compareNIR(s, niw)) {
            // linka s al suo nextInRun
            niw.prevSP = s;

            // aggiornamento valori nir
            niw.departureTime = s.departureTime;
            niw.numeroCambi = s.numeroCambi;
            niw.walkDistance = s.walkDistance;
            niw.travelTime = s.travelTime;
            niw.waitTime = s.waitTime;
            niw.walkTime = s.walkTime + niw.getTime() - s.getTime();
        }
    }

    private void linkNIR(Stop s, Stop nir) {
        if (nir.prevSP == null || compareNIR(s, nir)) {
            // linka s al suo nextInRun
            nir.prevSP = s;

            // aggiornamento valori nir
            nir.departureTime = s.departureTime;
            nir.numeroCambi = s.numeroCambi;
            nir.walkDistance = s.walkDistance;
            nir.travelTime = s.travelTime + nir.getTime() - s.getTime();
            nir.waitTime = s.waitTime;
            nir.walkTime = s.walkTime;
        }
    }

    private void linkNIS(Stop s, Stop nis) {
        if (nis.prevSP == null || compareNIS(s, nis)) {
            // linka s al suo nextInStation
            nis.prevSP = s;

            int cambio = 0;
            if ((s.prevSP != null) && (s.prevSP.equals(s.getPrevInRun()))) {
                cambio = 1;
            }

            // aggiornamento valori nis
            nis.departureTime = s.departureTime;
            nis.numeroCambi = s.numeroCambi + cambio;
            nis.walkDistance = s.walkDistance;
            nis.travelTime = s.travelTime;
            nis.waitTime = s.waitTime + nis.getTime() - s.getTime();
            nis.walkTime = s.walkTime;
        }
    }

    public void multiCriteriaTopologicalVisit() {

        Stack<Stop> toVisit = new Stack<Stop>();

        for (Stop s : startStack) {
            toVisit.push(s);
        }

        while (!toVisit.isEmpty()) {
            Stop s = toVisit.pop();
            Stop nir = s.nextInRun;
            Stop nis = s.nextInStation;
            Stop niw = s.nextWalk;

            if (nir != null) {    // Next In Run
                linkNIR(s, nir);

                // Gestione visita topologica
                nir.prevInRun = null;
                if (nir.prevInStation == null && nir.prevWalk == null) {
                    toVisit.push(nir);
                }
            }

            if (nis != null) {    // Next In Station
                linkNIS(s, nis);

                // Gestione visita topologica
                nis.prevInStation = null;
                if (nis.prevInRun == null && nis.prevWalk == null) {
                    toVisit.push(nis);
                }
            }


            if (niw != null) {    // Next In Walk
                linkNIW(s, niw);

                // Gestione visita topologica
                niw.prevWalk = null;
                if (niw.prevInStation == null && niw.prevInRun == null) {
                    toVisit.push(niw);
                }
            }

            if (s.prevSP != null) {
                s.departureTime = s.prevSP.departureTime;
                s.walkDistance = s.prevSP.walkDistance;
            }
        }
    }

    public class StopExplorer implements StopEvaluator {
//        int count = 0;

        @Override
        public boolean isStopNode(TraversalPosition tp) {
//            System.out.println( "\nVisited nodes: " + count++);
            Stop currentStop = cache.get(tp.currentNode());
            if (currentStop.nextInRun != null || currentStop.nextInStation != null) {
                return true;
            } else return (currentStop.getTime() > stopTime);
        }
    }

    public LinkedList<Direction> getArrivalList() {
        return arrivalList;
    }

}
