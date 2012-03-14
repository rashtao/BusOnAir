package boa.server.domain;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class Stop extends StopAbstract{
    private static final String TYPE = "type";
    private static final String TIME = "time";
    private static final String STATICTIME = "staticTime";
        
    // TRANSIENT ATTRIBUTES 
    public boolean visited = false;
    public Stop nextInRun = null;
    public Stop nextInStation = null;
    public Stop prevInRun = null;
    public Stop prevInStation = null;
    public Stop nextWalk = null;
    public Stop prevWalk = null;
    
    public Stop prevSP = null;
    public int numeroCambi = 0;
    public int departureTime = 0;
    public int minChangeTime = 1400;
    public int walkDistance = 0;
        
    public Stop(){
    	super();
    }
    
    public Stop(Node node){
    	super(node);
    }  

    public Stop(Node node, int id, int time, int idStation, int idRun, String line){
        super(node, id);
        setTime(time);	    
        setType();
        setStopFittizio(idStation);
        setRun(idRun, line);          
        Stops.getStops().addStop(this);
    }   
    
    public void setType() {
            underlyingNode.setProperty(Stop.TYPE, "Stop");		
    }

    public Integer getTime(){
            return (Integer) underlyingNode.getProperty(TIME);
    }

    public void setTime(int time){
            underlyingNode.setProperty(Stop.TIME, time);		
    }

    public Integer getStaticTime(){
        return (Integer) underlyingNode.getProperty(STATICTIME);
}

	public void setStaticTime(int staticTime){
	        underlyingNode.setProperty(Stop.STATICTIME, staticTime);		
	}

    public Station getStation(){
            return getStopFittizio().getStation();
    }

    public StopAbstract getStopFittizio(){
    Relationship rel = underlyingNode.getSingleRelationship(RelTypes.FITTIZIO, Direction.OUTGOING);
    return new StopFittizio(rel.getEndNode());						
    }

    public void setStopFittizio(StopFittizio sf){
            underlyingNode.createRelationshipTo(sf.getUnderlyingNode(), RelTypes.FITTIZIO);		
            getStation().addStop(this);
    }

    public Run getRun(){
    Relationship rel = underlyingNode.getSingleRelationship(RelTypes.STOP_RUN, Direction.OUTGOING);
    return new Run(rel.getEndNode());						
    }


    public void setRun(Run r){
        underlyingNode.createRelationshipTo(r.getUnderlyingNode(), RelTypes.STOP_RUN);
    }

    public Stop getNextInStation(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.NEXTINSTATION, Direction.OUTGOING);
        if(rel == null)
            return null;
        else
            return new Stop(rel.getEndNode());						
    }

    public Stop getPrevInStation(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.NEXTINSTATION, Direction.INCOMING);
        if(rel == null)
            return null;
        else
            return new Stop(rel.getStartNode());						
    }

    public void setNextInStation(Stop stop){
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.NEXTINSTATION, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();    	
    	
    	if(stop != null)
    		underlyingNode.createRelationshipTo(stop.getUnderlyingNode(), RelTypes.NEXTINSTATION);
    }

    public void setPrevInStation(Stop stop){
    	stop.setNextInStation(this);
    }

    public Stop getNextInRun(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.NEXTINRUN, Direction.OUTGOING);
        if(rel == null)
            return null;
        else
            return new Stop(rel.getEndNode());								
    }

    public Stop getPrevInRun(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.NEXTINRUN, Direction.INCOMING);
        if(rel == null)
            return null;
        else
            return new Stop(rel.getStartNode());								
    }

    public void setNextInRun(Stop stop){
        underlyingNode.createRelationshipTo(stop.getUnderlyingNode(), RelTypes.NEXTINRUN);
    }

    @Override
    public boolean equals(final Object otherStop){
    	if(otherStop == null)
    		return false;
    	
    	if (otherStop instanceof TransientStop)
    		return false;
    	
        if (otherStop instanceof Stop){
            return underlyingNode.equals(((Stop) otherStop).getUnderlyingNode());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return underlyingNode.hashCode();
    }

    @Override
	public String toString(){
    	return ("(STOPID" + getId() + ":NODEID" + getUnderlyingNode().getId() + ":TIME" + getTime()+ ":STATICTIME" + getStaticTime() + ")");
	}

    private void setStopFittizio(int idStation) {
        StopFittizio sf = Stations.getStations().getStationById(idStation).getStopFittizio();
        setStopFittizio(sf);
    }

    private void setRun(int idRun, String line) {
        Route route = Routes.getRoutes().getRouteByLine(line);
        if(route == null){
            route = new Route(DbConnection.getDb().createNode(), line);
        }
        Routes.getRoutes().addRoute(route);
        
        Run run = Runs.getRuns().getRunById(idRun);

        if(run == null){
            run = new Run(DbConnection.getDb().createNode(), idRun);
        }
        run.setRoute(route);
        Runs.getRuns().addRun(run);
        route.addRun(run);        
        setRun(run);
    }
    
    public String getUrl(){
    	return "/stops/" + getId();
    }
}
