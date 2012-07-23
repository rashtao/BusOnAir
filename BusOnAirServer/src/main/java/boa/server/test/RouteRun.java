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
public class RouteRun {
	   
    /**
     * @param args
     */
    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		
//		Run r = Runs.getRuns().getRunById(208);

		
		for(Route r: Routes.getRoutes().getAll()){
			System.out.println(r.getAllRuns().size());
		}
		
		
//		Route route = Routes.getRoutes().getRouteById(1039);
//		for(Run run : route.getAllRuns()){
//			System.out.println(run);
//		}
		

		
		
//		r.restoreRun();
		
//		CheckPoint cp = new CheckPoint(DbConnection.getDb().getNodeById(7803));
//		System.out.print(cp);
//		System.out.print(r);
		
		DbConnection.turnoff();
		
    	
    }
    
}
