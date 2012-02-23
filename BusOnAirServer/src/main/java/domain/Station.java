package domain;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import domain.RelTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.index.lucene.ValueContext;

public class Station {
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String IS_SCHOOL = "is_school";
    private static final String IS_TERMINAL = "is_terminal";
        
    private final Node underlyingNode;
    private Index<Node> stopIndex;
    
    public Station(Node node){
    	underlyingNode = node;
        stopIndex = DbConnection.getDb().index().forNodes("stopIndex" + getId());
    }  

    public Station(Node node, int id, String name, double latitude, double longitude, boolean isSchool, boolean isTerminal){
    	underlyingNode = node;
        setId(id);
        setName(name);	
        setLatitude(latitude);
        setLongitude(longitude);
        setIsSchool(isSchool);
        setIsSchool(isTerminal);
        setType();
        setStopFittizio();
        stopIndex = DbConnection.getDb().index().forNodes("stopIndex" + getId());
    }   

    public Station(Node node, int id, String name, double latitude, double longitude){
        this(node, id, name, latitude, longitude, false, false);
    }   

    public Station(Node node, int id, String name){
        this(node, id, name, 0, 0, false, false);
    }   

    public void setType() {
            underlyingNode.setProperty(Station.TYPE, "Station");		
    }

    public Node getUnderlyingNode(){
        return underlyingNode;
    }

    public Integer getId(){
            return (Integer) underlyingNode.getProperty(ID);
    }

    public String getName(){
            return (String) underlyingNode.getProperty(NAME);
    }

    public Double getLatitude(){
            return (Double) underlyingNode.getProperty(LATITUDE);
    }

    public Double getLongitude(){
            return (Double) underlyingNode.getProperty(LONGITUDE);
    }

    public Boolean getIsSchool(){
            return (Boolean) underlyingNode.getProperty(IS_SCHOOL);
    }

    public Boolean getIsTerminal(){
            return (Boolean) underlyingNode.getProperty(IS_TERMINAL);
    }

    public void setId(int id){
            underlyingNode.setProperty(Station.ID, id);
    }

    public void setName(String name){
            underlyingNode.setProperty(Station.NAME, name);
    }

    public void setLatitude(double lat){
            underlyingNode.setProperty(Station.LATITUDE, lat);
    }

    public void setLongitude(double lng){
            underlyingNode.setProperty(Station.LONGITUDE, lng);
    }

    public void setIsSchool(boolean isSchool){
            underlyingNode.setProperty(Station.IS_SCHOOL, isSchool);
    }

    public void setIsTerminal(boolean isTerminal){
            underlyingNode.setProperty(Station.IS_TERMINAL, isTerminal);
    }

//    public void setFirstStop(Stop first){
//    underlyingNode.createRelationshipTo(first.getUnderlyingNode(), RelTypes.STATION_FIRSTSTOP);		
//    }

//    public Stop getFirstStop(){
//    Relationship rel = underlyingNode.getSingleRelationship(RelTypes.STATION_FIRSTSTOP, Direction.OUTGOING);
//    return new Stop(rel.getEndNode());		
//    }

    public void addStop(Stop s){
        stopIndex.add(s.getUnderlyingNode(), "time", new ValueContext(s.getTime()).indexNumeric());
    }

    public Iterable<Stop> getStopsFromTime(int startTime, int endTime){
        List<Stop> stops = new ArrayList<Stop>();
        IndexHits<Node> result = stopIndex.query(QueryContext.numericRange("time", startTime, endTime));
        for(Node n : result){
            stops.add(new Stop(n));
        }            
        result.close();
        return stops;
    }

    public Stop getFirstStopsFromTime(int startTime){
        List<Stop> stops = new ArrayList<Stop>();
        QueryContext query = QueryContext.numericRange("time", startTime, 1440);
        query.sortNumeric("time", false);
        
        IndexHits<Node> hits =  stopIndex.query(query);
        if((hits == null) || (hits.size() < 1)){
            hits.close();
            return null;
        }
        
        Node n = hits.next();
        hits.close();
                
        if(n == null)
            return null;
        else{
         
            Stop out = new Stop(n);
            while(out.getPrevInStation() != null && out.getPrevInStation().getTime() >= startTime){
                out = out.getPrevInStation();                        
            }
            
            return out;
        }
    }
    
    public Iterable<Stop> getStopsFromTime(int startTime){
        return getStopsFromTime(startTime, 1440);
    }        
    
    public StopFittizio getStopFittizio(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.FITTIZIO_STAZIONE, Direction.INCOMING);
        return new StopFittizio(rel.getStartNode());	
    }
    
    public ArrayList<Stop> getAllStops(){
        ArrayList<Stop> output = new ArrayList<Stop>();
        
        Iterable<Relationship> rels = getStopFittizio()
                .getUnderlyingNode().getRelationships(RelTypes.FITTIZIO, Direction.INCOMING);
        for(Relationship r : rels){
            output.add(new Stop(r.getStartNode()));   
        }
        
        return output;  
        
    }
        
    @Override
    public boolean equals(final Object otherStation){
        if (otherStation instanceof Station){
            return underlyingNode.equals(((Station) otherStation).getUnderlyingNode());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return underlyingNode.hashCode();
    }

    @Override
	public String toString(){
		return ("Station: " +
				"\n\tnode: " + underlyingNode + 
				"\n\tid: " + getId() + 
				"\n\tname: " + getName() +
				"\n\tlatitude: " + getLatitude().toString() +
				"\n\tlongitude: " + getLongitude().toString());
//				"\n\tIsSchool: " + getIsSchool().toString() +
//				"\n\tIsTerminal: " + getIsTerminal().toString());	    
    }

    private void setStopFittizio() {
        StopFittizio sf = new StopFittizio(DbConnection.getDb().createNode(), getId());
        sf.setStazione(this);
    }

	public ArrayList<Route> getAllRoutes() {
		Set<Route> set = new HashSet<Route>();

		for(Stop s : getAllStops()){
			set.add(s.getRun().getRoute());			
		}

		ArrayList<Route> result = new ArrayList<Route>(set);
		return result;
	}

}
