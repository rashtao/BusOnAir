package boa.server.routing;

import java.util.Comparator;

public class DirectionComparator implements Comparator<Direction>{
	
	    @Override
	    public int compare(Direction o1, Direction o2)
	    {
	    	if(o1 == null || o2 == null)
	    		return 0;
	    	
	        if (o1.getArrivalTime() < o2.getArrivalTime())
	        {
	            return -1;
	        }
	        
	        if (o1.getArrivalTime() > o2.getArrivalTime())
	        {
	            return 1;
	        }
	        
	        if (o1.getNumChanges() < o2.getNumChanges())
	        {
	            return -1;
	        }

	        if (o1.getNumChanges() > o2.getNumChanges())
	        {
	            return 1;
	        }
	        
	        return 0;
	    }
}

