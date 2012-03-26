package boa.server.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import boa.server.domain.utils.*;

public class Run {
	protected static final String ID = "id";
    protected static final String TYPE = "type";
    protected static final String LATITUDE = "lat";		// ultima posizione rilevata con GPS e comunicata
    protected static final String LONGITUDE = "lon";	// ultima posizione rilevata con GPS e comunicata	
    protected static final String LASTUPDATETIME = "lastupdatetime";	// in secondi dalla mezzanotte

    protected static final int SERVER_TIMEOUT = 30;
    
    protected LayerNodeIndex checkPointsSpatialIndex;
    protected Index<Node> cpIndex;
    
    protected Node underlyingNode;
    
    public Run(){
    }  

    public Run(Node node){
    	underlyingNode = node;
    	cpIndex = DbConnection.getDb().index().forNodes("cpIndex" + getId());
    }  

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
    
    public Long getLastUpdateTime(){
        return (Long) underlyingNode.getProperty(LASTUPDATETIME);
    }

    protected void setLastUpdateTime(long time){
        underlyingNode.setProperty(Run.LASTUPDATETIME, time);
    }
    
    public Double getLastGPSLatitude(){
        return (Double) underlyingNode.getProperty(LATITUDE);
	}
	
	public Double getLastGPSLongitude(){
	        return (Double) underlyingNode.getProperty(LONGITUDE);
	}    
	
    protected void setLatitude(double lat){
        underlyingNode.setProperty(Run.LATITUDE, lat);
	}
	
    protected void setLongitude(double lng){
	        underlyingNode.setProperty(Run.LONGITUDE, lng);
	}	

    protected void setLastCheckPoint(CheckPoint last){
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_LASTCHECKPOINT, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();
        underlyingNode.createRelationshipTo(last.getUnderlyingNode(), RelTypes.RUN_LASTCHECKPOINT);		
    }
    
    public CheckPoint getLastGPSCheckPoint(){
    	// Ritorna l'ultimo CheckPoint (lastCheckPoint memorizzato) aggiornato da 
    	// updatePosition o da checkPointPass (chiamate dalla tracking app)
    	
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_LASTCHECKPOINT, Direction.OUTGOING);
        return new CheckPoint(rel.getEndNode());
    }
    
    public CheckPoint calculateLastCheckPoint(){
    	// Ritorna l'ultimo CheckPoint visitato se la tracking app ha problemi di (comunicazione / ricezione GPS)
    	// viene cmq restituito l'ultimo CheckPoint che ipoteticamente dovrebbe essere stato visitato al tempo corrente, 
    	// basandosi sull'ultima comunicazione della tracking app e sul tempo corrente
    	
    	long currentTime = boa.server.domain.utils.DateUtil.getSecondsSinceMidnight();
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_LASTCHECKPOINT, Direction.OUTGOING);
        CheckPoint lastCP = new CheckPoint(rel.getEndNode());
        CheckPoint nextCP = lastCP.getNextCheckPoint(); 
        
    	while(nextCP != null && currentTime > nextCP.getTimeInSeconds()){
    		lastCP = nextCP;
    		nextCP = nextCP.getNextCheckPoint();    		
    	}
    	
    	return lastCP;    	
    }
    
    public Stop getLastGPSStop(){
    	return getLastGPSCheckPoint().getFrom();
    }

    public Stop calculateLastStop(){
    	return calculateLastCheckPoint().getFrom();
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
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_FIRSTSTOP, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();
    	
    	if(first != null)
    		underlyingNode.createRelationshipTo(first.getUnderlyingNode(), RelTypes.RUN_FIRSTSTOP);		
    }

    public Stop getFirstStop(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_FIRSTSTOP, Direction.OUTGOING);
        return new Stop(rel.getEndNode());		
    }


    public void setFirstCheckPoint(CheckPoint first){
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_FIRSTCHECKPOINT, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();
    	
    	if(first != null)
    		underlyingNode.createRelationshipTo(first.getUnderlyingNode(), RelTypes.RUN_FIRSTCHECKPOINT);		
    }

    public CheckPoint getFirstCheckPoint(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_FIRSTCHECKPOINT, Direction.OUTGOING);
        return new CheckPoint(rel.getEndNode());		
    }
    
    public double getLength(){
        double length = 0;
        Stop s1 = getFirstStop();
        Stop s2 = s1.getNextInRun();
        
        while(s2 != null){
            double dist = GeoUtil.getDistance2(
                    s1.getStation().getLatitude(), 
                    s1.getStation().getLongitude(),
                    s2.getStation().getLatitude(), 
                    s2.getStation().getLongitude());
            length += dist;
            s1 = s2;
            s2 = s1.getNextInRun();        
        } 
        
        return length;
    }
    
    public int getDelay(){
    	Stop ls = getLastGPSStop(); 
    	Stop ns = ls.getNextInRun();

    	if(ns == null)		// la Run non sta circolando
    		return 0;
    	
    	return ns.getTime() - ns.getStaticTime();
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
    	String output = "";
		output += ("Run: " +
				"\n\tid: " + getId() + 
				"\n\tlon:" + getLastGPSLatitude() + 
				"\n\tlat:" + getLastGPSLongitude() + 				
				"\n\tlastGPSStop:" + getLastGPSStop() + 
				"\n\tlastGPSCheckPoint:" + getLastGPSCheckPoint() +
				"\n\tlastCalculatedStop:" + calculateLastStop() + 
				"\n\tlastCalculatedCheckPoint:" + calculateLastCheckPoint());	 
		
		output += "\nStops:\n";
		Stop s = getFirstStop();
		while(s != null){
			output += "-->" + s.toString();
			s = s.getNextInRun();
		}
		
		output += "\nCheckPoints:\n";
		CheckPoint cp = getFirstCheckPoint();
		while(cp != null){
			output += "-->" + cp.toString();
			cp = cp.getNextCheckPoint();
		}
		
		return output;
    }

    public ArrayList<Stop> getAllStops() {
        ArrayList<Stop> output = new ArrayList<Stop>();
        
        Iterable<Relationship> rels = getUnderlyingNode().getRelationships(RelTypes.STOP_RUN, Direction.INCOMING);
        for(Relationship r : rels){
            output.add(new Stop(r.getStartNode()));   
        }
        
        return output;  
        
    }
    
    public void restore(){
    	// setta lastvisitedcheckpoit all'ultimo checkpoint della run e reimposta i tempi di tutti gli stop con i tempi originali (static time)
    	// setta lastUpdateTime = 1440*60
    	// rimuove la run dall'indice Runs.runningBuses
    	
    	CheckPoint cp = getFirstCheckPoint();
    	
    	while(cp.getNextCheckPoint() != null)
    		cp = cp.getNextCheckPoint();		
    	
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Runs.getRuns().removeRunningBus(this);
	    	setLastCheckPoint(cp);
	    	setLatitude(cp.getLatitude());
	    	setLongitude(cp.getLongitude());
	    	setLastUpdateTime(1440*60);
	    	
	    	Stop s = getFirstStop();
	    	
	    	while(s != null){
	    		s.setTime(s.getStaticTime());
	    		updateStop(s);
	    		s = s.getNextInRun();
	    	}
			tx.success();
		}finally{
			tx.finish();			
		}   
    }
    
    
    public void checkPointPass(CheckPoint lastCP, long time){
    	//propaga il ritardo da lastCP.getTowards():Stop fino a fine run
    	// aggiunge la run dall'indice Runs.runningBuses
    	
    	int timeInMinutes = (int) Math.round(time/60.0);
    	int ritardo = timeInMinutes - lastCP.getTimeInMinutes();
    	
    	if(lastCP.getNextCheckPoint() == null){		// fine RUN
    		restore();    		
    	} else {    	
			Transaction tx = DbConnection.getDb().beginTx();
			try{	
				Runs.getRuns().addRunningBus(this);
		    	setLastCheckPoint(lastCP);
		    	setLastUpdateTime(time);
		    	setLatitude(lastCP.getLatitude());
		    	setLongitude(lastCP.getLongitude());
	    	
	    		Stop nextStop = lastCP.getTowards();
		    	while(nextStop != null){
		    		nextStop.setTime(nextStop.getTime() + ritardo);
		    		updateStop(nextStop);
		    		nextStop = nextStop.getNextInRun();
		    	}
				tx.success();
			}finally{
				tx.finish();			
			}    	
    	}
    }
    
    protected void updateStop(Stop s){
    	if(s == null)
    		return;
    	
    	Stop pis = s.getPrevInStation();
    	Stop nis = s.getNextInStation();
    	
    	if(pis != null && pis.getTime() > s.getTime()){
	    	while(pis != null && pis.getTime() > s.getTime()){
	    		nis = pis;
	    		pis = pis.getPrevInStation();
	    	}
	    	moveStop(pis, s, nis);
    	} else if(nis != null && nis.getTime() < s.getTime()){
	    	while(nis != null && nis.getTime() < s.getTime()){
	    		pis = nis;
	    		nis = nis.getNextInStation();
	    	}
	    	moveStop(pis, s, nis);    	
    	}
    }
    
    protected void moveStop(Stop prev, Stop s, Stop next){
    	// sposta s tra prev e next
    	
    	if(prev == null && s.getNextInStation() != null && s.getNextInStation().equals(next))
    		return;
    	
    	if(prev != null && prev.getNextInStation() != null && prev.getNextInStation().equals(s) && 
    			s.getNextInStation() != null && s.getNextInStation().equals(next))
    		return;	
    	
    	Stop pis = s.getPrevInStation();
    	Stop nis = s.getNextInStation();
		
		if(prev != null)
			prev.setNextInStation(s);
		s.setNextInStation(next);
		if(pis != null){
			pis.setNextInStation(nis);
		}
    }
	
	public void addCheckPoint(Double lat, Double lon, long time){
		updatePosition(lat, lon, time);
		
		CheckPoint lastCP = getLastGPSCheckPoint();
		CheckPoint nextCP = lastCP.getNextCheckPoint();
		
		long dt = nextCP.getTowards().getTime()*60 - getLastUpdateTime();		

		if(dt < 0)
			return;
		
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Node n = DbConnection.getDb().createNode();
			CheckPoint newCP = new CheckPoint(n, getLastGPSLatitude(), getLastGPSLongitude(), dt);
			
			newCP.setTowards(nextCP.getTowards());	
			newCP.setFrom(lastCP.getFrom());
			
			lastCP.setNextCheckPoint(newCP);
			newCP.setNextCheckPoint(nextCP);			

			cpIndex.add(newCP.getUnderlyingNode(), "id", newCP.getId());
			
			if(checkPointsSpatialIndex == null)
				checkPointsSpatialIndex = new LayerNodeIndex( "checkPointsSpatialIndex" + getId(), DbConnection.getDb(), new HashMap<String, String>() );

			checkPointsSpatialIndex.add(newCP.getUnderlyingNode(), "", "" );
			
//			System.out.print("\n\n********\n" + newCP + "\n**********\n");
			
			setLastCheckPoint(newCP);
			tx.success();

		}finally{
			tx.finish();			
		} 
    }
	
    public ArrayList<CheckPoint> getAllCheckPoints() {
        ArrayList<CheckPoint> output = new ArrayList<CheckPoint>();
        IndexHits<Node> result = cpIndex.query("id", "*");
        for(Node n : result){
            output.add(new CheckPoint(n));           
        }        
        result.close();
        return output;
    }

	public CheckPoint getCheckPointById(long id) {
	        IndexHits<Node> result = cpIndex.get("id", id);
	        Node n = result.getSingle();
	        result.close();
	        if(n == null){
	            return null;
	        } else {
	            return new CheckPoint(n);                
	        }
	}

	public void updatePosition(Double lat, Double lon, long time) {
		// aggiorna la posizione lat,lon della run
		// setta l'ultimo checkpoint visitato (calcolandolo dalla posizione attuale)
		// calcola il rapporto (percentualeAvanzamento) tra la proiezione della segmento posAttuale-cp1 sul segmento cp1-cp2
		// calcola il ritardo relativamente alla percentuale di avanzamento
		// propaga il ritardo ai nodi successivi
    	// aggiunge la run dall'indice Runs.runningBuses
		
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Runs.getRuns().addRunningBus(this);
			setLatitude(lat);
			setLongitude(lon);
			setLastUpdateTime(time);
			
			calculateAndSetLastCP();
			
			tx.success();
		}finally{
			tx.finish();			
		} 
		
//		System.out.print("\n\n------ DEBUG --------");
//		System.out.print("\n\nLASTCP: " + getLastCheckPoint());		
//		System.out.print("\n------ DEBUG --------\n\n");
		
		double a,b,c,d,percentualeAvanzamento;
		long dt;
		
		CheckPoint cp1 = getLastGPSCheckPoint();
		CheckPoint cp2 = cp1.getNextCheckPoint();
		a = GeoUtil.getDistance2(cp1.getLatitude(), cp1.getLongitude(), getLastGPSLatitude(), getLastGPSLongitude());
		b = GeoUtil.getDistance2(cp2.getLatitude(), cp2.getLongitude(), getLastGPSLatitude(), getLastGPSLongitude());
		c = GeoUtil.getDistance2(cp1.getLatitude(), cp1.getLongitude(), cp2.getLatitude(), cp2.getLongitude());
		
		d = (a*a-b*b+c*c)/(2.0*c);
		
		percentualeAvanzamento = d / c;
		dt = (long) ((1.0 - percentualeAvanzamento)*(cp2.getTimeInSeconds() - cp1.getTimeInSeconds()));	// tempo restante all'arrivo al prossimo cp
		
//		System.out.print("\npercentualeAvanzamento: " + percentualeAvanzamento);
//		System.out.print("\ndt: " + dt);
				
		checkPointPassExpected(cp2, time + dt);
	}	

    protected void calculateAndSetLastCP() {
    	CheckPoint nearestCP = getNearestCheckPoint(getLastGPSLatitude(), getLastGPSLongitude());
    	CheckPoint prevCP = nearestCP.getPrevCheckPoint();
    	CheckPoint nextCP = nearestCP.getNextCheckPoint();
		
    	if(prevCP == null){
    		setLastCheckPoint(nearestCP);
    		return;
    	}
    	
    	if(nextCP == null){
    		setLastCheckPoint(prevCP);
    		return;
    	}
    	
    	
    	Coordinate c1 = nearestCP.getCoordinate();
    	Coordinate c2 = prevCP.getCoordinate();
    	Coordinate c3 = nextCP.getCoordinate();
    	
    	
    	Coordinate p = GeomUtil.proiezione(c1, c2, c3);
    	
    	double dist1 = GeoUtil.getDistance2(c3.lat, c3.lon, getLastGPSLatitude(), getLastGPSLongitude());
    	double dist2 = GeoUtil.getDistance2(p.lat,p.lon,getLastGPSLatitude(), getLastGPSLongitude());
    	
    	if(dist1 < dist2){
    		System.out.print("\n----- CP tra: " + nearestCP.getId() + ", " + nextCP.getId());
    		setLastCheckPoint(nearestCP);
    	}else{
    		System.out.print("\n----- CP tra: " + prevCP.getId() + ", " + nearestCP.getId());
    		setLastCheckPoint(prevCP);
    	}
	}

	protected void checkPointPassExpected(CheckPoint nextCP, long time){
    	//propaga il ritardo da nextCP.getTowards():Stop fino a fine run
    	
    	int ritardo = (int) (Math.round(time/60.0) - nextCP.getTimeInMinutes());
    	
		Transaction tx = DbConnection.getDb().beginTx();
		try{	
    		Stop nextStop = nextCP.getTowards();
	    	while(nextStop != null){
	    		nextStop.setTime(nextStop.getTime() + ritardo);
	    		updateStop(nextStop);
	    		nextStop = nextStop.getNextInRun();
	    	}
			tx.success();
		}finally{
			tx.finish();			
		}    	
    }	

    public Collection<CheckPoint> getNearestCheckPoints( double lat1, double lon1){
		return getNearestCheckPoints(lat1, lon1, 100000);
    }
    	
    public Collection<CheckPoint> getNearestCheckPoints( double lat1, double lon1, int range){    
    	//range in meters

    	double kmrange = (double) range / 1000.0;
    	    	
    	Collection<CheckPoint> result = new ArrayList<CheckPoint>(); 
        Map<Node, Double> hits = queryWithinDistance( lat1, lon1, (double) kmrange);

        Iterator it = hits.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Node, Double> entry = (Map.Entry<Node, Double>)it.next();
            
            if(entry != null && entry.getKey() != null && entry.getValue() < (double) range){
            	result.add(new CheckPoint(entry.getKey()));            	
            }
        }

		return result;
    }
    
    public CheckPoint getNearestCheckPoint( double lat1, double lon1){
        Map<Node, Double> hits = queryWithinDistance( lat1, lon1 );
        Map.Entry<Node, Double> entry = hits.entrySet().iterator().next();
        
        if(entry != null && entry.getKey() != null){
        	CheckPoint out = new CheckPoint(entry.getKey());
            return out;
        } else {
            return null;
        }
    }    

    protected Map<Node, Double> queryWithinDistance( Double lat, Double lon){
    	return queryWithinDistance(lat, lon, 10000.0);
    }
	    
	protected Map<Node, Double> queryWithinDistance( Double lat, Double lon, Double distance){       
		if(checkPointsSpatialIndex == null)
			checkPointsSpatialIndex = new LayerNodeIndex( "checkPointsSpatialIndex" + getId(), DbConnection.getDb(), new HashMap<String, String>() );

        Map<String, Object> params = new HashMap<String, Object>();
        params.put( LayerNodeIndex.DISTANCE_IN_KM_PARAMETER, distance);
        params.put( LayerNodeIndex.POINT_PARAMETER, new Double[] { lat, lon } );              
        Map<Node, Double> results = new HashMap<Node, Double>();
        for ( Node spatialRecord : checkPointsSpatialIndex.query( LayerNodeIndex.WITHIN_DISTANCE_QUERY, params ) )
          results.put( DbConnection.getDb().getNodeById( (Long) spatialRecord.getProperty( "id" )), (Double) spatialRecord.getProperty( "distanceInKm" ) );               
        return sortByValue( results );    
	}
	
    @SuppressWarnings( "unchecked" )
    protected static Map<Node, Double> sortByValue( Map<Node, Double> map )
    {
        List<Map.Entry<Node, Double>> list = new LinkedList<Map.Entry<Node, Double>>( map.entrySet() );
        Collections.sort( list, new Comparator()
        {
            public int compare( Object o1, Object o2 )
            {
                return ( (Comparable) ( (Map.Entry) ( o1 ) ).getValue() ).compareTo( ( (Map.Entry) ( o2 ) ).getValue() );
            }
        } );
        Map<Node, Double> result = new LinkedHashMap();
        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();
            result.put( (Node) entry.getKey(), (Double) entry.getValue() );
        }
        return result;
    }        	    

    public String getUrl(){
    	return "/runs/" + getId();
    }
}
