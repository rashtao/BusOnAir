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

public class StopInStations {	   
    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		
		Station staz = Stations.getStations().getStationById((long) 132);
		
		System.out.print("\n\nstaz.getAllStops():");
		for(Stop s : staz.getAllStops()){
			System.out.print("\n" + s.getId() + "\t --> " + s.getStaticTime());
		}
		
//		System.out.print("\n\nstaz.getAllIncidentStops():");
//		for(Stop s : staz.getAllIncidentStops()){
//			System.out.print("\n" + s.getId());
//		}
//		
//		System.out.print("\n\nstaz.getAllStopsInIndex():");
//		for(Stop s : staz.getAllStopsInIndex()){
//			System.out.print("\n" + s.getId());
//		}
//		
		
		
		DbConnection.turnoff();
		
    	
    }
    
}
