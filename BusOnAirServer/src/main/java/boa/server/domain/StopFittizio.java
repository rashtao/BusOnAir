package boa.server.domain;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class StopFittizio extends StopAbstract {
	private static final String TYPE = "type";
	
	public StopFittizio(Node node){
		super(node);		
	}

    public StopFittizio(Node node, int id) {
    	this(node);
    	setId(id);
    	setType();
    }  
	        
	public void setType() {
		underlyingNode.setProperty(StopFittizio.TYPE, "StopFittizio");		
	}

	public Station getStazione(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.FITTIZIO_STAZIONE, Direction.OUTGOING);
        return new Station(rel.getEndNode());				
	}
	
	public void setStazione(Station stazione){
		underlyingNode.createRelationshipTo(stazione.getUnderlyingNode(), RelTypes.FITTIZIO_STAZIONE);
	}
	
    @Override
    public boolean equals(final Object otherStopFittizio){
        if (otherStopFittizio instanceof StopFittizio){
            return underlyingNode.equals(((StopFittizio) otherStopFittizio).getUnderlyingNode());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return underlyingNode.hashCode();
    }

    @Override
	public String toString(){
		return ("StopFittizio: " +
				"\n\tid: " + getId() + 
				"\n\ttype: " + getType());	
	}	
}
