package boa.server.domain;

import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

public class Routes {
    protected Index<Node> routesIndex;

    protected static Routes instance = null;
    
    public static synchronized Routes getRoutes() {
        if (instance == null) 
            instance = new Routes();
        return instance;
    }    
    
    protected Routes(){
        routesIndex = DbConnection.getDb().index().forNodes("routesIndex");
    }
    
    public void addRoute(Route r){
    	routesIndex.remove(r.getUnderlyingNode());
    	routesIndex.add(r.getUnderlyingNode(), "id", r.getId());
    	routesIndex.add(r.getUnderlyingNode(), "line", r.getLine());
    }
    
    public void deleteAllRoutes(){
        for(Route route : getAll()){
        	deleteRoute(route);	
        }
    }
    
    public void deleteRoute(Route r){
	    for(Run run : r.getAllRuns()){
			Runs.getRuns().deleteRun(run);
		}

    	for(Relationship rel : r.getUnderlyingNode().getRelationships(RelTypes.RUN_ROUTE, Direction.INCOMING)){
    		Run run = new Run(rel.getStartNode());
    		Runs.getRuns().deleteRun(run);
    	}
	    
	    r.deleteRunIndex();	
		routesIndex.remove(r.getUnderlyingNode());
		r.setFrom(null);
		r.setTowards(null);

//    	for(Relationship rel : r.getUnderlyingNode().getRelationships()){
//    		System.out.println(rel + " (" + rel.getType() + ") :  " +  rel.getStartNode() + " --> " + rel.getEndNode());
//    	}
		
		r.getUnderlyingNode().delete();
    }
    
    public Route getRouteById(int id){
        IndexHits<Node> result = routesIndex.get("id", id);
        Node n = result.getSingle();
        result.close();
        if(n == null){
            return null;
        } else {
            return new Route(n);                
        }
    }

    public Route getRouteByLine(String line){
        IndexHits<Node> result = routesIndex.get("line", line);
        Node n = result.getSingle();
        result.close();
        if(n == null){
            return null;
        } else {
            return new Route(n);                
        }
    }
    
    public ArrayList<Route> getAll() {
        ArrayList<Route> output = new ArrayList<Route>();
        IndexHits<Node> result = routesIndex.query("id", "*");
        for(Node n : result){
            output.add(new Route(n));           
        }     
        result.close();
        return output;
    }
    
}
