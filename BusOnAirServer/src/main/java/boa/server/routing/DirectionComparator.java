/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boa.server.routing;

import java.util.Comparator;


     public class DirectionComparator implements Comparator{
    	 
    	 Criteria criterion;
    	 
    	 public DirectionComparator(Criteria crit){
    		 criterion = crit;
    	 }
        
        @Override 
        public int compare(Object o1, Object o2){

        	Integer time1 = ((Direction)o1).getArrivalTime();
        	Integer time2 = ((Direction)o2).getArrivalTime();

            Integer changes1 = ((Direction)o1).getNumChanges();
            Integer changes2 = ((Direction)o2).getNumChanges();

            Integer depart1 = ((Direction)o1).getDepartureTime();
            Integer depart2 = ((Direction)o2).getDepartureTime();

            Integer walk1 = ((Direction)o1).getWalkDistance();
            Integer walk2 = ((Direction)o2).getWalkDistance();

            
            if(time1 > time2){
                return 1;
            } else if(time1 < time2) {
                return -1;
            } else if(criterion == Criteria.MINCHANGES){
            	return(changes1.compareTo(changes2));
            } else if(criterion == Criteria.LATESTLEAVING){
            	return(depart1.compareTo(depart2));
            } else if(criterion == Criteria.MINWALK){            	
            	return(walk1.compareTo(walk2));
            }

            return 0;            
        }


    }