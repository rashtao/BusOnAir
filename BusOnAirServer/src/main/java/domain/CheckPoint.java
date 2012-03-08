package domain;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import utils.Coordinate;

public class CheckPoint {
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String DT = "dt";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
        
    private final Node underlyingNode;
	   
    public CheckPoint(Node node){
    	underlyingNode = node;
    }  

	public CheckPoint(Node node, double lat, double lon, int dt) {
		underlyingNode = node;
//        setId(id);
        setLatitude(lat);
        setLongitude(lon);
        setDt(dt);
        setType();
    }   

	public Long getId(){
        return underlyingNode.getId();
    }

    public String getType() {
    	return (String) underlyingNode.getProperty(TYPE);
    }

    public void setType() {
            underlyingNode.setProperty(CheckPoint.TYPE, "CheckPoint");		
    }

    public Node getUnderlyingNode(){
        return underlyingNode;
    }

    public Double getLatitude(){
            return (Double) underlyingNode.getProperty(LATITUDE);
    }

    public Double getLongitude(){
            return (Double) underlyingNode.getProperty(LONGITUDE);
    }

    public void setLatitude(double lat){
            underlyingNode.setProperty(CheckPoint.LATITUDE, lat);
    }

    public void setLongitude(double lng){
            underlyingNode.setProperty(CheckPoint.LONGITUDE, lng);
    }

    public void setDt(int dt) {
    	 underlyingNode.setProperty(CheckPoint.DT, dt);
	}
    
	public Integer getDt(){
//		System.out.print("\n-----------\nCheckPoint.getDt()\n" + underlyingNode);
//		for(Object obj : underlyingNode.getPropertyKeys())
//			System.out.print("\n" + obj);
//			
//		System.out.print("\n-----------\n");
//		System.out.flush();
		return (Integer) underlyingNode.getProperty(CheckPoint.DT);
//		return 0;
    }

    public CheckPoint getNextCheckPoint(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.NEXTCHECKPOINT, Direction.OUTGOING);
        if(rel == null)
            return null;
        else
            return new CheckPoint(rel.getEndNode());						
    }

    public void setNextCheckPoint(CheckPoint next){
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.NEXTCHECKPOINT, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();    	
    	
    	if(next != null)
    		underlyingNode.createRelationshipTo(next.getUnderlyingNode(), RelTypes.NEXTCHECKPOINT);
    }
	

    public Stop getFrom(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.CHECKPOINTFROM, Direction.OUTGOING);
        if(rel == null)
            return null;
        else
            return new Stop(rel.getEndNode());						
    }

    public void setFrom(Stop towards){
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.CHECKPOINTFROM, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();    	
    	
    	if(towards != null)
    		underlyingNode.createRelationshipTo(towards.getUnderlyingNode(), RelTypes.CHECKPOINTFROM);
    }
	
    public Stop getTowards(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.CHECKPOINTTOWARDS, Direction.OUTGOING);
        if(rel == null)
            return null;
        else
            return new Stop(rel.getEndNode());						
    }

    public void setTowards(Stop towards){
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.CHECKPOINTTOWARDS, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();    	
    	
    	if(towards != null)
    		underlyingNode.createRelationshipTo(towards.getUnderlyingNode(), RelTypes.CHECKPOINTTOWARDS);
    }
	
    public int getTime(){
    	return getTowards().getTime() - getDt();
    }

	@Override
	public String toString() {
		return ("(CPID" + getId() + ":DT" + getDt()+  ":TIME" + getTime()+ ":FROM" + getFrom().getId() + ":TOWARDS" + getTowards().getId() + ")");
	}

	public CheckPoint getPrevCheckPoint() {
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.NEXTCHECKPOINT, Direction.INCOMING);
        if(rel == null)
            return null;
        else
            return new CheckPoint(rel.getStartNode());			
	}
	
	public Coordinate getCoordinate(){
		return new Coordinate(getLatitude(), getLongitude());
	}
    
}
