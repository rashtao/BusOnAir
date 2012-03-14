/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boa.server.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import boa.server.domain.DbConnection;
import boa.server.domain.Route;
import boa.server.domain.Routes;
import boa.server.domain.utils.AlphanumComparator;
import boa.server.test.printRoutes.RouteComparator;



/**
 *
 * @author rashta
 */
public class printRoutes {
	
    public class RouteComparator implements Comparator<Route>{

		@Override
		public int compare(Route o1, Route o2) {
		 	String l1 = ((Route)o1).getLine();        
		 	String l2 = ((Route)o2).getLine();    
		 	
		 	AlphanumComparator nc = new AlphanumComparator();
		 	

		 	return nc.compare(l1,l2);
    	
		}

    }
    
    public static void main(String[] args) {     
    	printRoutes pr = new printRoutes();
    	pr.runna();    
    }
    
    public printRoutes(){
    	
    }
    
    public void runna(){
		DbConnection.createEmbeddedDbConnection();
		
        List<Route> routes = Routes.getRoutes().getAll();

        Collections.sort(routes, new RouteComparator());
        
        
        int i = 0;
        Route prev = null;
        for(Route r : routes){
//        	if(prev != null && r.getLine().equals(prev.getLine()))
//        		System.out.print("\nA/R ROUTE: ['" + r.getId()  + "','" + r.getId()  + "','" + r.getLine() + "','" + r.getTowards().getName() + "'],");
            System.out.print("\n['" + r.getId()  + "','" + r.getId()  + "','" + r.getLine() + "','" + r.getTowards().getName() + "'],");
            i++;
            prev = r;
        }
        
        
        Route r = Routes.getRoutes().getRouteByLine("M12A");
        
        System.out.print(r);
            	
    }
}
