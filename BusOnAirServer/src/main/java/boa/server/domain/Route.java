package boa.server.domain;

import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import boa.server.json.Coordinate;

public class Route {
	protected static final String ID = "id";
	protected static final String TYPE = "type";
    protected static final String LINE = "line";

    
    protected Node underlyingNode;
    
    protected Index<Node> runIndex;
    
    protected Route(){
    }  

    public Route(Node node){
    	underlyingNode = node;
        runIndex = DbConnection.getDb().index().forNodes("runIndex" + getId());
    }  
    
    public void removeRun(Run r){
    	runIndex.remove(r.getUnderlyingNode());
    }

    public void deleteRunIndex(){
    	runIndex.delete();
    }
    
    public Long getId(){
        return (Long) underlyingNode.getProperty(ID);
    }
    
    public void setId(long id){
        underlyingNode.setProperty(Route.ID, id);
    }
        
	public void setType() {
            underlyingNode.setProperty(Route.TYPE, "Route");		
	}

    public void setFrom(Station s){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.ROUTEFROM, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();

    	if(s != null)
    		underlyingNode.createRelationshipTo(s.getUnderlyingNode(), RelTypes.ROUTEFROM);		
    }

    public Station getFrom(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.ROUTEFROM, Direction.OUTGOING);
        return new Station(rel.getEndNode());		
    }    
	
    public void setTowards(Station s){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.ROUTETOWARDS, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();
    	
    	if(s != null)
    		underlyingNode.createRelationshipTo(s.getUnderlyingNode(), RelTypes.ROUTETOWARDS);		
    }

    public Station getTowards(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.ROUTETOWARDS, Direction.OUTGOING);
        return new Station(rel.getEndNode());		
    }    
	    
    public Run getRun(int id){
        IndexHits<Node> result = runIndex.get("id", id);
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
        IndexHits<Node> result = runIndex.query("id", "*");
        for(Node n : result){
            output.add(new Run(n));           
        }        
        result.close();
        return output;
    }

	public ArrayList<Station> getAllStations() {
		ArrayList<Station> result = new ArrayList<Station>();
		if(getAllRuns().size() == 0)
			return result;
		
		Run fr = getAllRuns().iterator().next();
		
		Stop s = fr.getFirstStop();
		
		while(s != null){
			result.add(s.getStation());
			s = s.getNextInRun();
		}
		
		return result;
	}
	public void updateLine(String line){
		setLine(line);
		Routes.getRoutes().addRoute(this);	//aggiorna gli indici
		
	}
	
	public void updateFrom(Station from){
		setFrom(from);
	}
	
	public void updateTowards(Station towards){
		setTowards(towards);
	}
	
    public String getUrl(){
    	return "/routes/" + getId();
    }
}
