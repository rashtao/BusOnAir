package boa.server.domain;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.index.lucene.ValueContext;

import boa.server.domain.RelTypes;
import boa.server.json.Coordinate;

public class Station {
    protected static final String ID = "id";
    protected static final String TYPE = "type";
    protected static final String NAME = "name";
    protected static final String LATITUDE = "lat";
    protected static final String LONGITUDE = "lon";
    protected static final String IS_SCHOOL = "is_school";
    protected static final String IS_TERMINAL = "is_terminal";
        
    protected Node underlyingNode;
    protected Index<Node> stopIndex;
    
    public Station(){
    }  
 
    public Station(Node node){
    	underlyingNode = node;
        stopIndex = DbConnection.getDb().index().forNodes("stopIndex" + getId());
    }  

    public void removeStop(Stop s){
    	stopIndex.remove(s.getUnderlyingNode());
    }
    
    public void setType() {
        underlyingNode.setProperty(Station.TYPE, "Station");		
    }

    public Node getUnderlyingNode(){
        return underlyingNode;
    }

    public Long getId(){
            return (Long) underlyingNode.getProperty(ID);
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

    public void setId(long id){
            underlyingNode.setProperty(Station.ID, id);
    }
    
    public void setName(String name){
        underlyingNode.setProperty(Station.NAME, name);
    }
    
    public void setLatitude(double lat){
    	// after changing lat/lon call updatePosition() to update stationSpatialIndex
        underlyingNode.setProperty(Station.LATITUDE, lat);
    }

    public void setLongitude(double lng){
    	// after changing lat/lon call updatePosition() to update stationSpatialIndex
        underlyingNode.setProperty(Station.LONGITUDE, lng);
    }

    public void updatePosition(){
        Stations.getStations().updateSpatialIndex(this);
    }

    public void setIsSchool(boolean isSchool){
            underlyingNode.setProperty(Station.IS_SCHOOL, isSchool);
    }

    public void setIsTerminal(boolean isTerminal){
            underlyingNode.setProperty(Station.IS_TERMINAL, isTerminal);
    }

    public void update(boa.server.importer.json.Station s){
		setName(s.getName());
		setLatitude(s.getLatLon().getLat());
		setLongitude(s.getLatLon().getLon());    	
    }
    
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

    public Stop getFirstStopFromTime(int startTime){
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
        
    public ArrayList<Stop> getAllStops(){
        ArrayList<Stop> output = new ArrayList<Stop>();
        
        Iterable<Relationship> rels = getUnderlyingNode().getRelationships(RelTypes.STOP_STATION, Direction.INCOMING);
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

	public ArrayList<Route> getAllRoutes() {
		Set<Route> set = new HashSet<Route>();

		for(Stop s : getAllStops()){
			set.add(s.getRun().getRoute());			
		}

		ArrayList<Route> result = new ArrayList<Route>(set);
		return result;
	}

	public ArrayList<Run> getAllRuns() {
		Set<Run> set = new HashSet<Run>();

		for(Stop s : getAllStops()){
			set.add(s.getRun());			
		}

		ArrayList<Run> result = new ArrayList<Run>(set);
		return result;
	}
	
    public String getUrl(){
    	return "/stations/" + getId();
    }
}
