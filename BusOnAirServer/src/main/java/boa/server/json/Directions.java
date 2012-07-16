package boa.server.json;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import boa.server.domain.Station;
import boa.server.domain.Stop;
import boa.server.domain.utils.GeoUtil;
import boa.server.routing.*;


@XmlRootElement( name = "List" )
public class Directions
{
    @XmlElement( name = "directionlist" )
    List<Direction> directionsList = new ArrayList<Direction>();

    private double lat1;
    private double lon1;
    
    
    public Directions()
    {
    }

    public Directions(com.vividsolutions.jts.geom.Coordinate startPoint, LinkedList<boa.server.routing.Direction> arrivalList){
		lat1 = startPoint.y;
		lon1 = startPoint.x;
		for(boa.server.routing.Direction dir : arrivalList)
			add(dir);
    }
    
//    public void add( Direction direction ){
//        directionsList.add( direction );
//    }

    public void add( boa.server.routing.Direction dir ){
    	Direction output = new Direction();
   	 
 		Stop arrivo = dir.getStop();
 		Station s_arrivo = arrivo.getStation();

	    output.getWalks().addFirst(new DirectionWalk(
	    		false, 
	    		dir.getWalkTime(), 
	    		(int) Math.round(dir.getDistance() * 1000.0), 
	    		new boa.server.json.Coordinate(s_arrivo.getLatitude(), s_arrivo.getLongitude()), 
	    		new boa.server.json.Coordinate(dir.getLat(), dir.getLon()),
	    		s_arrivo.getId()));
	    
    	output.setArrivalTime(dir.getArrivalTime());
    	output.setNumChanges(dir.getNumChanges());
    	output.setWalkingDistance(dir.getWalkDistance());

    	Stop tmp, prevtmp;
    	tmp = arrivo;
    	while(tmp.prevSP != null){
    		tmp = tmp.prevSP;
    	}
    	
    	Station depStat = tmp.getStation();
    	
    	
    	
    	tmp = arrivo;
    	prevtmp = tmp.prevSP;
    	

    	
    	while(prevtmp != null){
	    	while(prevtmp != null && tmp.equals(prevtmp.getNextInRun())){
//	    		System.out.print("\ntmp: " + tmp);
//	    		System.out.print("\nprevtmp: " + prevtmp);
	    		
	    		tmp = prevtmp;
	    		prevtmp = prevtmp.prevSP;
	    	}
	    	
	    	if(prevtmp != null){
	    		output.getRoutes().addFirst(new DirectionRoute(tmp, arrivo));
	    		if(!depStat.equals(tmp.getStation())){		// è un cambio
			    	output.getWalks().addFirst(new DirectionWalk(
			    			true, 
			    			tmp.getTime() - prevtmp.getTime(), 
			    			0, 
			    			new boa.server.json.Coordinate(tmp.getStation().getLatitude(), tmp.getStation().getLongitude()),
			    			new boa.server.json.Coordinate(tmp.getStation().getLatitude(), tmp.getStation().getLongitude()),
			    			tmp.getStation().getId()));
	    		} else {		// è la prima walk
	    			Coordinate coord1 = new boa.server.json.Coordinate(lat1, lon1);
	    			Coordinate coord2 = new boa.server.json.Coordinate(prevtmp.getStation().getLatitude(), prevtmp.getStation().getLongitude());
	    			double dist = GeoUtil.getDistance2(lat1, lon1, prevtmp.getStation().getLatitude(), prevtmp.getStation().getLongitude());
	    			
	    			int walktime = (int) Math.round(dist / Config.WALKSPEED * 60);
	    			
	    			output.getWalks().addFirst(new DirectionWalk(
		    	    		false, 
		    	    		walktime, 
		    	    		(int) Math.round(dist * 1000.0), 
		    	    		coord1, 
		    	    		coord2,
		    	    		prevtmp.getStation().getId()));
	    			
//		    		while(prevtmp != null){
//			    		tmp = prevtmp;
//			    		prevtmp = prevtmp.prevSP;
//		    		}
		    		
	    			output.setDepartureTime(dir.getDepartureTime());
	    			
		    	    prevtmp = null;
	    		}
	    		
	    		while(tmp.prevSP != null && tmp.equals(tmp.prevSP.nextInStation)){
		    		tmp = tmp.prevSP;
	    		}
	    		
	    		if(prevtmp != null){
					prevtmp = tmp.prevSP;
					arrivo = tmp;
	    		}
	    	}
    	}
    	
    	directionsList.add( output );
    }

    public List<Direction> getDirectionsList(){
    	return directionsList;
    }
}
