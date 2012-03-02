package domain;

import java.util.ArrayList;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import domain.RelTypes;
import utils.GeoUtil;



public class Run {
    private static final String ID = "id";
    private static final String TYPE = "type";
    private Index<Node> cpIndex;
    private static final int DELAYTH = 1;
    
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
    
//    public void setRitardo(int ritardo){
//    	underlyingNode.setProperty(Run.RITARDO, ritardo);
//    }
//    
//    public int getRitardo(){
//    	return (Integer) underlyingNode.getProperty(RITARDO);
//    }
//    
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
				"\n\tlastStop:" + getLastStop() + "\n" + 
				"\n\tlastCheckPoint:" + getLastCheckPoint() + "\n");	 
		
		Stop s = getFirstStop();
		while(s != null){
			output += "-->" + s.toString();
			s = s.getNextInRun();
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
    	
    	CheckPoint cp = getCheckPointById(0);
    	
    	while(cp.getNextCheckPoint() != null)
    		cp = cp.getNextCheckPoint();		
    	
		Transaction tx = DbConnection.getDb().beginTx();
		try{
	    	setLastCheckPoint(cp);
	    	
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

	public void addCheckPoint(CheckPoint cp) {
        cpIndex.add(cp.getUnderlyingNode(), "order", cp.getId());
    }
	
    public ArrayList<CheckPoint> getAllCheckPoints() {
        ArrayList<CheckPoint> output = new ArrayList<CheckPoint>();
        IndexHits<Node> result = cpIndex.query("order", "*");
        for(Node n : result){
            output.add(new CheckPoint(n));           
        }        
        result.close();
        return output;
    }

	public CheckPoint getCheckPointById(int id) {
	        IndexHits<Node> result = cpIndex.get("order", id);
	        Node n = result.getSingle();
	        result.close();
	        if(n == null){
	            return null;
	        } else {
	            return new CheckPoint(n);                
	        }
	}	
}
