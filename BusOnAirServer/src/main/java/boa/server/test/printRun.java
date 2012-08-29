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

import boa.server.domain.*;
import boa.server.domain.utils.AlphanumComparator;




/**
 *
 * @author rashta
 */
public class printRun {
	   
    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		

		ArrayList<Route> routes = Routes.getRoutes().getAll();
		for(Route r : routes){
			System.out.print(r);
		}
		
	
		
		Run r = Runs.getRuns().getRunById(208);

		
		
		
		Route route = Routes.getRoutes().getRouteById((long) 1039);
		for(Run run : route.getAllRuns()){
			System.out.print(run);
		}
		
		for(Station s : route.getAllStations()){
			System.out.print(s);
		}
		
		
//		r.restoreRun();
		
//		CheckPoint cp = new CheckPoint(DbConnection.getDb().getNodeById(7803));
//		System.out.print(cp);
		System.out.print(r);
		
		DbConnection.turnoff();
		
    	
    }
    
}
