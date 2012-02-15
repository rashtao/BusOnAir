package domain;

import java.util.ArrayList;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import domain.RelTypes;
import utils.GeoUtil;

public class Run {
    private static final String ID = "id";
    private static final String TYPE = "type";
//    private static final String ORDER = "order";
        
    private final Node underlyingNode;
    
    public Run(Node node){
    	underlyingNode = node;
    }  

    public Run(Node node, int id){
        this(node);
        setId(id);
        setType();
    }   

//    public Run(Node node, int id, int order){
//        this(node, id);
//        setOrder(order);
//    }   

//    public Integer getOrder() {
//    	return (Integer) underlyingNode.getProperty(ORDER);	
//	}

//    public void setOrder(int order) {
//    	underlyingNode.setProperty(Run.ORDER, order);		
//	}

	public void setType() {
        underlyingNode.setProperty(Run.TYPE, "Run");		
    }

    public Node getUnderlyingNode(){
        return underlyingNode;
    }

    public Integer getId(){
        return (Integer) underlyingNode.getProperty(ID);
    }

    public void setId(int id){
        underlyingNode.setProperty(Run.ID, id);
    }

    public void setRoute(Route route){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_ROUTE, Direction.OUTGOING);
        if(rel == null){
            underlyingNode.createRelationshipTo(route.getUnderlyingNode(), RelTypes.RUN_ROUTE);		
        }
    }

    public Route getRoute(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_ROUTE, Direction.OUTGOING);
        return new Route(rel.getEndNode());		
    }

    public void setFirstStop(Stop first){
        underlyingNode.createRelationshipTo(first.getUnderlyingNode(), RelTypes.RUN_FIRSTSTOP);		
    }

    public Stop getFirstStop(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_FIRSTSTOP, Direction.OUTGOING);
        return new Stop(rel.getEndNode());		
    }

    public double getLength(){
        double length = 0;
        Stop s1 = getFirstStop();
        Stop s2 = s1.getNextInRun();
        
        while(s2 != null){
            double dist = GeoUtil.getDistance2(
                    s1.getStazione().getLatitude(), 
                    s1.getStazione().getLongitude(),
                    s2.getStazione().getLatitude(), 
                    s2.getStazione().getLongitude());
            length += dist;
            s1 = s2;
            s2 = s1.getNextInRun();        
        } 
        
        return length;
    }
    
    
    @Override
    public boolean equals(final Object otherRun){
        if (otherRun instanceof Run){
            return underlyingNode.equals(((Run) otherRun).getUnderlyingNode());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return underlyingNode.hashCode();
    }

    @Override
	public String toString(){
		return ("Run: " +
				"\n\tid: " + getId());	    
    }

    public ArrayList<Stop> getAllStops() {
        ArrayList<Stop> output = new ArrayList<Stop>();
        
        Iterable<Relationship> rels = getUnderlyingNode().getRelationships(RelTypes.STOP_RUN, Direction.INCOMING);
        for(Relationship r : rels){
            output.add(new Stop(r.getStartNode()));   
        }
        
        return output;  
        
    }
}
