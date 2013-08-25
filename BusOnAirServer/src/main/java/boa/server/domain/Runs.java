package boa.server.domain;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import java.util.ArrayList;

public class Runs {
    protected Index<Node> runsIndex;
    protected Index<Node> runningBuses;

    protected static Runs instance = null;

    public static synchronized Runs getRuns() {
        if (instance == null)
            instance = new Runs();
        return instance;
    }

    public static void destroy() {
        instance = null;
    }

    protected Runs() {
        runsIndex = DbConnection.getDb().index().forNodes("runsIndex");
        runningBuses = DbConnection.getDb().index().forNodes("runningBuses");
    }

    public void addRun(Run r) {
        runsIndex.add(r.getUnderlyingNode(), "id", r.getId());
    }

    public void deleteAllRuns() {
        for (Run r : getAll()) {
            deleteRun(r);
        }
    }

    public void deleteRun(Run run) {
        for (CheckPoint cp : run.getAllCheckPoints()) {
            run.deleteCheckPoint(cp);
        }

        run.deleteCpSpatialIndex();
        run.deleteCpIndex();

        for (Stop s : run.getAllStops()) {
            Stops.getStops().deleteStop(s);
        }

        run.setFirstStop(null);

        runsIndex.remove(run.getUnderlyingNode());
        runningBuses.remove(run.getUnderlyingNode());
        run.getRoute().removeRun(run);

//		System.out.println("\n\nDELETING Run " + run.getId());
//		for(Relationship rel : run.getUnderlyingNode().getRelationships()){
//    		System.out.println(rel + " (" + rel.getType() + ") :  " +  rel.getStartNode() + " --> " + rel.getEndNode());
//    	}

        run.setRoute(null);
        run.getUnderlyingNode().delete();
    }

    public void addRunningBus(Run r) {
        runningBuses.add(r.getUnderlyingNode(), "id", r.getId());
    }

    public Run getRunById(Integer id) {
        if (id == null)
            return null;

        IndexHits<Node> result = runsIndex.get("id", id);
        Node n = result.getSingle();
        result.close();
        if (n == null) {
            return null;
        } else {
            return new Run(n);
        }
    }

    public Run getRunningBusById(Integer id) {
        IndexHits<Node> result = runningBuses.get("id", id);
        Node n = result.getSingle();
        result.close();
        if (n == null) {
            return null;
        } else {
            return new Run(n);
        }
    }

    public ArrayList<Run> getAll() {
        ArrayList<Run> output = new ArrayList<Run>();
        IndexHits<Node> result = runsIndex.query("id", "*");
        for (Node n : result) {
            output.add(new Run(n));
        }
        result.close();
        return output;
    }

    public ArrayList<Run> getAllRunningBuses() {
        ArrayList<Run> output = new ArrayList<Run>();
        IndexHits<Node> result = runningBuses.query("id", "*");
        for (Node n : result) {
            output.add(new Run(n));
        }
        result.close();
        return output;
    }

    public void removeRunningBus(Run r) {
        runningBuses.remove(r.getUnderlyingNode());
    }

    public void updateIndex(Run r) {
        runsIndex.remove(r.getUnderlyingNode());
        addRun(r);
    }

    public Run createOrUpdateRun(boa.server.domain.json.Run jr) {
        // creates a new Run having the specified id
        // if the id already exists then updates the corresponding db record

        Run r = Runs.getRuns().getRunById(jr.getId());
        if (r != null) {    // update
            Route oldRoute = r.getRoute();
            oldRoute.removeRun(r);
        } else {        // create
            r = new Run(
                    DbConnection.getDb().createNode(),
                    jr.getId());
            addRun(r);
        }

        Route route = Routes.getRoutes().getRouteById(jr.getRoute());
        route.addRun(r);
        r.setRoute(route);
        r.setFirstStop(Stops.getStops().getStopById(jr.getFirstStop()));
        r.setFirstCheckPoint(r.getCheckPointById(jr.getFirstCheckPoint()));
        return r;
    }

    public void createOrUpdateRuns(boa.server.domain.json.Runs runs) {
        // creates new runs having the specified ids
        // if an id already exists then updates the corresponding db record

        for (boa.server.domain.json.Run r : runs.runsObjectsList) {
            Transaction tx = DbConnection.getDb().beginTx();
            try {
                createOrUpdateRun(r);
                tx.success();
            } finally {
                tx.finish();
            }
        }
    }

}
