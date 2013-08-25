/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boa.server.test;

import boa.server.domain.DbConnection;
import boa.server.domain.Station;
import boa.server.domain.Stations;
import boa.server.domain.Stop;

public class StopInStations {	   
    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		
		Station staz = Stations.getStations().getStationById(132);
		
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
