/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import test.printRoutes.RouteComparator;
import utils.AlphanumComparator;

import domain.DbConnection;
import domain.Route;
import domain.Routes;

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
        for(Route r : routes){
            System.out.print("\n['" + r.getId()  + "','" + r.getId()  + "','" + r.getLine() + "','" + r.getTowards().getName() + "'],");
            i++;
        }
        
        
        Route r = Routes.getRoutes().getRouteByLine("M12A");
        
        System.out.print(r);
            	
    }
}
