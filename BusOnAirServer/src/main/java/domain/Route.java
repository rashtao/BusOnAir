package domain;

import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

public class Route {
	private static final String ID = "id";
	private static final String TYPE = "type";
    private static final String LINE = "line";
    
    private static int count = 0;
    private static int runCount = 0;
    
    private final Node underlyingNode;
    
    private Index<Node> runIndex;
    
    public Route(Node node){
    	underlyingNode = node;
        runIndex = DbConnection.getDb().index().forNodes("runIndex" + getId());
    }  

	public Route(Node node, String line){
        underlyingNode = node;
	    setLine(line);
	    setType();
	    setId(count++);
        runIndex = DbConnection.getDb().index().forNodes("runIndex" + getId());
	}   

    public Integer getId(){
        return (Integer) underlyingNode.getProperty(ID);
    }
    
    public void setId(int id){
        underlyingNode.setProperty(Route.ID, id);
    }
    
    public void clearIndex(){
    	for(Run r : getAllRuns()){
    		runIndex.remove(r.getUnderlyingNode());
    	}    	
    }
    
	public void setType() {
            underlyingNode.setProperty(Route.TYPE, "Route");		
	}

    public void setFrom(Station s){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.FROM, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();
    	
    	underlyingNode.createRelationshipTo(s.getUnderlyingNode(), RelTypes.FROM);		
    }

    public Station getFrom(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.FROM, Direction.OUTGOING);
        return new Station(rel.getEndNode());		
    }    
	
    public void setTowards(Station s){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.ROUTETOWARDS, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();
    	
		underlyingNode.createRelationshipTo(s.getUnderlyingNode(), RelTypes.ROUTETOWARDS);		
    }

    public Station getTowards(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.ROUTETOWARDS, Direction.OUTGOING);
        return new Station(rel.getEndNode());		
    }    
	
    public void addRun(Run r){
        runIndex.add(r.getUnderlyingNode(), "order", runCount++);
    }
    
    public Run getRun(int order){
        IndexHits<Node> result = runIndex.get("order", order);
        Node n = result.getSingle();
        result.close();
        if(n == null){
            return null;
        } else {
            return new Run(n);    
        }
    }
        
    public Node getUnderlyingNode(){
        return underlyingNode;
    }
		
	public String getLine(){
		return (String) underlyingNode.getProperty(LINE);
	}
		
	public void setLine(String line){
		underlyingNode.setProperty(Route.LINE, line);
	}

    @Override
    public boolean equals(final Object otherRoute){
        if (otherRoute instanceof Route){
            return underlyingNode.equals(((Route) otherRoute).getUnderlyingNode());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return underlyingNode.hashCode();
    }

    @Override
	public String toString(){
		return ("Route: " +
				"\n\tid: " + getId() +	    
				"\n\tline: " + getLine());	    
    }

    public ArrayList<Run> getAllRuns() {
        ArrayList<Run> output = new ArrayList<Run>();
        IndexHits<Node> result = runIndex.query("order", "*");
        for(Node n : result){
            output.add(new Run(n));           
        }        
        result.close();
        return output;
    }

	public ArrayList<Station> getAllStations() {
		ArrayList<Station> result = new ArrayList<Station>();
		
		Run fr = getAllRuns().iterator().next();
		
		Stop s = fr.getFirstStop();
		
		while(s != null){
			result.add(s.getStazione());
			s = s.getNextInRun();
		}
		
		return result;
	}
}
