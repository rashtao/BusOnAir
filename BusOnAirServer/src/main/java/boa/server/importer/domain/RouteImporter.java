package boa.server.importer.domain;

import org.neo4j.graphdb.Node;

import boa.server.domain.DbConnection;
import boa.server.domain.Route;
import boa.server.domain.Run;

public class RouteImporter extends Route {
    
    protected static int count = 0;
    protected static int runCount = 0;
    
	public RouteImporter(Node node, String line){
		super();
        underlyingNode = node;
	    setLine(line);
	    setType();
	    setId(count++);
        runIndex = DbConnection.getDb().index().forNodes("runIndex" + getId());
	}   
	
    public RouteImporter(Route r) {
		super(r.getUnderlyingNode());
	}

	public void addRun(Run r){
        runIndex.add(r.getUnderlyingNode(), "id", runCount++);
    }

    public void clearIndex(){
    	for(Run r : getAllRuns()){
    		runIndex.remove(r.getUnderlyingNode());
    	}    	
    }

}
