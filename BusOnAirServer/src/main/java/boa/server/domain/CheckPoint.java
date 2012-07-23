package boa.server.domain;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.vividsolutions.jts.geom.Coordinate;


public class CheckPoint {
    protected static final String TYPE = "type";
    protected static final String DT = "dt";	// in secondi
    protected static final String LATITUDE = "lat";
    protected static final String LONGITUDE = "lon";
        
    protected Node underlyingNode;
	   
    public CheckPoint(Node node){
    	underlyingNode = node;
    }  

	public CheckPoint(Node node, double lat, double lon, long dt) {
		underlyingNode = node;
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
    	// after changing lat/lon call run.updatePosition() to update cpSpatialIndex
        underlyingNode.setProperty(CheckPoint.LATITUDE, lat);
    }

    public void setLongitude(double lng){
    	// after changing lat/lon call run.updatePosition() to update cpSpatialIndex
    	underlyingNode.setProperty(CheckPoint.LONGITUDE, lng);
    }

    public void setDt(long dt) {
    	 underlyingNode.setProperty(CheckPoint.DT, dt);
	}
    
	public Long getDt(){
		return (Long) underlyingNode.getProperty(CheckPoint.DT);
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

    public void setFrom(Stop from){
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.CHECKPOINTFROM, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();    	
    	
    	if(from != null)
    		underlyingNode.createRelationshipTo(from.getUnderlyingNode(), RelTypes.CHECKPOINTFROM);
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
	
    public int getTimeInMinutes(){
    	return (int) (getTowards().getTime() - Math.round(getDt()/60.0));
    }

    public long getTimeInSeconds(){
    	return getTowards().getTime()*60 - getDt();
    }

	@Override
	public String toString() {
		return ("(CPID" + getId() + ":DT" + getDt()+  ":TIME" + getTimeInMinutes() + ":FROM STOPID" + getFrom().getId() + ":TOWARDS STOPID" + getTowards().getId() + ")");
	}

	public CheckPoint getPrevCheckPoint() {
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.NEXTCHECKPOINT, Direction.INCOMING);
        if(rel == null)
            return null;
        else
            return new CheckPoint(rel.getStartNode());			
	}
	
	public Coordinate getCoordinate(){
		return new Coordinate(getLongitude(), getLatitude());
	}    
		
	public String getUrl(){
		return "/runs/" + getFrom().getRun().getId() + "/checkpoints/" + getId();
	}
}
