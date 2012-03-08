package domain;

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

import org.neo4j.gis.spatial.GeometryEncoder;
import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import utils.*;




public class Run {
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String LASTUPDATETIME = "lastupdatetime";
        
    private LayerNodeIndex checkPointsSpatialIndex;
    private Index<Node> cpIndex;
    //private static final int DELAYTH = 1;
    
    private final Node underlyingNode;
    
    public Run(Node node){
    	underlyingNode = node;
    	cpIndex = DbConnection.getDb().index().forNodes("cpIndex" + getId());
    }  

    public Run(Node node, int id){
    	underlyingNode = node;
        setId(id);
        setType();
    	cpIndex = DbConnection.getDb().index().forNodes("cpIndex" + getId());
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

	public void createCheckPointsSpatialIndex() {
		if(checkPointsSpatialIndex == null)
			checkPointsSpatialIndex = new LayerNodeIndex( "checkPointsSpatialIndex" + getId(), DbConnection.getDb(), new HashMap<String, String>() );

		for(CheckPoint cp : getAllCheckPoints()){
			checkPointsSpatialIndex.add(cp.getUnderlyingNode(), "", "" );
		}
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
    
    public Integer getLastUpdateTime(){
        return (Integer) underlyingNode.getProperty(LASTUPDATETIME);
    }

    public void setLastUpdateTime(int time){
        underlyingNode.setProperty(Run.LASTUPDATETIME, time);
    }
    
    public Double getLatitude(){
        return (Double) underlyingNode.getProperty(LATITUDE);
	}
	
	public Double getLongitude(){
	        return (Double) underlyingNode.getProperty(LONGITUDE);
	}    
	
    public void setLatitude(double lat){
        underlyingNode.setProperty(Run.LATITUDE, lat);
	}
	
	public void setLongitude(double lng){
	        underlyingNode.setProperty(Run.LONGITUDE, lng);
	}	

    public void setLastCheckPoint(CheckPoint last){
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_LASTCHECKPOINT, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();
        underlyingNode.createRelationshipTo(last.getUnderlyingNode(), RelTypes.RUN_LASTCHECKPOINT);		
    }
    
    public CheckPoint getLastCheckPoint(){
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_LASTCHECKPOINT, Direction.OUTGOING);
        return new CheckPoint(rel.getEndNode());		
    }
    
    public Stop getLastStop(){
    	return getLastCheckPoint().getFrom();
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
    
    public int getRitardo(){
    	Stop ls = getLastStop(); 
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
				"\n\tlon:" + getLatitude() + 
				"\n\tlat:" + getLongitude() + 				
				"\n\tlastStop:" + getLastStop() + 
				"\n\tlastCheckPoint:" + getLastCheckPoint());	 
		
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
    
    public void restoreRun(){
    	//setta lastvisitedcheckpoit all'ultimo checkpoint della run e reimposta i tempi di tutti gli stop con i tempi originali (static time)
    	
    	CheckPoint cp = getFirstCheckPoint();
    	
    	while(cp.getNextCheckPoint() != null)
    		cp = cp.getNextCheckPoint();		
    	
		Transaction tx = DbConnection.getDb().beginTx();
		try{
	    	setLastCheckPoint(cp);
	    	setLatitude(cp.getLatitude());
	    	setLongitude(cp.getLongitude());
	    	
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
    
    
    public void updateRun(CheckPoint lastCP, int time){
    	//propaga il ritardo da lastCP.getTowards():Stop fino a fine run
    	
    	int ritardo = time - lastCP.getTime();
//    	System.out.print("\nRit: " + ritardo);
    	
		Transaction tx = DbConnection.getDb().beginTx();
		try{	
	    	setLastCheckPoint(lastCP);
	    	setLastUpdateTime(time);
	    	setLatitude(lastCP.getLatitude());
	    	setLongitude(lastCP.getLongitude());
	    	
//	    	if(ritardo > DELAYTH || ritardo < -DELAYTH){
	    		Stop nextStop = lastCP.getTowards();
		    	while(nextStop != null){
		    		nextStop.setTime(nextStop.getTime() + ritardo);
		    		updateStop(nextStop);
		    		nextStop = nextStop.getNextInRun();
		    	}
//	    	}
			tx.success();
		}finally{
			tx.finish();			
		}    	
    }
    
    private void updateStop(Stop s){
    	if(s == null)
    		return;
    	
    	Stop pis = s.getPrevInStation();
    	Stop nis = s.getNextInStation();
    	
    	if(pis != null && pis.getTime() > s.getTime()){
	    	while(pis != null && pis.getTime() > s.getTime()){
	    		nis = pis;
	    		pis = pis.getPrevInStation();
	    	}
	    	spostaStop(pis, s, nis);
    	} else if(nis != null && nis.getTime() < s.getTime()){
	    	while(nis != null && nis.getTime() < s.getTime()){
	    		pis = nis;
	    		nis = nis.getNextInStation();
	    	}
	    	spostaStop(pis, s, nis);    	
    	}
    }
    
    private void spostaStop(Stop prev, Stop s, Stop next){
    	// sposta s tra prev e next
    	
//    	System.out.print("\n\n******\nspostaStop(" + prev + s + next + ")\n******\n");
    	
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

	public void addCheckPointImporter(CheckPoint cp) {
        cpIndex.add(cp.getUnderlyingNode(), "id", cp.getId());
    }
	
	public void addCheckPoint() throws Exception {
		//NB: da invocare dopo update(lat,lon,time), altrimenti il dt potrebbe venire negativo!
		
		CheckPoint lastCP = getLastCheckPoint();
		CheckPoint nextCP = lastCP.getNextCheckPoint();
		
		int dt = nextCP.getTowards().getTime() - getLastUpdateTime();		

		if(dt < 0)
			return;
		
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			Node n = DbConnection.getDb().createNode();
			CheckPoint newCP = new CheckPoint(n, getLatitude(), getLongitude(), dt);
			
			newCP.setTowards(nextCP.getTowards());	
			newCP.setFrom(lastCP.getFrom());
			
			lastCP.setNextCheckPoint(newCP);
			newCP.setNextCheckPoint(nextCP);			

			cpIndex.add(newCP.getUnderlyingNode(), "id", newCP.getId());
			
			if(checkPointsSpatialIndex == null)
				checkPointsSpatialIndex = new LayerNodeIndex( "checkPointsSpatialIndex" + getId(), DbConnection.getDb(), new HashMap<String, String>() );

			checkPointsSpatialIndex.add(newCP.getUnderlyingNode(), "", "" );
			
			System.out.print("\n\n********\n" + newCP + "\n**********\n");
			
			setLastCheckPoint(newCP);
			
			
			tx.success();
		} catch(Exception e) {
			System.out.print("\n\n******** ECCEZIONE **********\n");
			System.out.print(e);
			throw e;
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

	public void update(Double lat, Double lon, int time) {
		// aggiorna la posizione lat,lon della run
		// setta l'ultimo checkpoint visitato (calcolandolo dalla posizione attuale)
		// calcola il rapporto (percentualeAvanzamento) tra la proiezione della segmento posAttuale-cp1 sul segmento cp1-cp2
		// calcola il ritardo relativamente alla percentuale di avanzamento
		// propaga il ritardo ai nodi successivi
		
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			setLatitude(lat);
			setLongitude(lon);
			setLastUpdateTime(time);
			
			calculateAndSetLastCP();
			
			tx.success();
		}finally{
			tx.finish();			
		} 
		
		
		double a,b,c,d,percentualeAvanzamento, dt;
		CheckPoint cp1 = getLastCheckPoint();
		CheckPoint cp2 = cp1.getNextCheckPoint();
		a = GeoUtil.getDistance2(cp1.getLatitude(), cp1.getLongitude(), getLatitude(), getLongitude());
		b = GeoUtil.getDistance2(cp2.getLatitude(), cp2.getLongitude(), getLatitude(), getLongitude());
		c = GeoUtil.getDistance2(cp1.getLatitude(), cp1.getLongitude(), cp2.getLatitude(), cp2.getLongitude());
		
		d = (a*a-b*b+c*c)/(2.0*c);
		
		percentualeAvanzamento = d / c;
		
		System.out.print("\npercentualeAvanzamento: " + percentualeAvanzamento);
		
		dt = (1.0 - percentualeAvanzamento)*(cp2.getTime() - cp1.getTime());	// tempo restante all'arrivo al prossimo cp

		System.out.print("\ndt: " + dt);
				
		updateRunExpected(cp2, (int) Math.round(time + dt));

	}	


    private void calculateAndSetLastCP() {
    	CheckPoint nearestCP = getNearestCheckPoint(getLatitude(), getLongitude());
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
    	
    	double dist1 = GeoUtil.getDistance2(c3.lat, c3.lon, getLatitude(), getLongitude());
    	double dist2 = GeoUtil.getDistance2(p.lat,p.lon,getLatitude(), getLongitude());
    	
    	if(dist1 < dist2){
    		System.out.print("\n----- CP tra: " + nearestCP.getId() + ", " + nextCP.getId());
    		setLastCheckPoint(nearestCP);
    	}else{
    		System.out.print("\n----- CP tra: " + prevCP.getId() + ", " + nearestCP.getId());
    		setLastCheckPoint(prevCP);
    	}
	}

	private void updateRunExpected(CheckPoint nextCP, int time){
    	//propaga il ritardo da nextCP.getTowards():Stop fino a fine run
    	
    	int ritardo = time - nextCP.getTime();
//    	System.out.print("\nRit: " + ritardo);
    	
		Transaction tx = DbConnection.getDb().beginTx();
		try{	
//	    	setLastCheckPoint(lastCP);
//	    	if(ritardo > DELAYTH || ritardo < -DELAYTH){
	    		Stop nextStop = nextCP.getTowards();
		    	while(nextStop != null){
		    		nextStop.setTime(nextStop.getTime() + ritardo);
		    		updateStop(nextStop);
		    		nextStop = nextStop.getNextInRun();
		    	}
//	    	}
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
//            System.out.print("\n\n-----Dist: " + entry.getValue() + "\n" + out);          
            return out;
        } else {
            return null;
        }
    }    

    public Map<Node, Double> queryWithinDistance( Double lat, Double lon){
    	return queryWithinDistance(lat, lon, 10000.0);
    }
	    
	public Map<Node, Double> queryWithinDistance( Double lat, Double lon, Double distance)
	    {       
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
	    static Map<Node, Double> sortByValue( Map<Node, Double> map )
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
	    


}
