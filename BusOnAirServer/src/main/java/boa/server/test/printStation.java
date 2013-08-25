/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boa.server.test;

import boa.server.domain.DbConnection;
import boa.server.domain.Station;
import boa.server.domain.Stations;
import boa.server.domain.Stop;




/**
 *
 * @author rashta
 */
public class printStation {
	
 
    
    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		
		Station s = Stations.getStations().getStationById(34);
		
		Stop fs = s.getFirstStopFromTime(0);

		while (fs != null){
			System.out.print("\n\n" + fs + "" + fs.getRun().getId()+ "\n\n");
			fs = fs.getNextInStation();
		}
		
		DbConnection.turnoff();
		
    	
    }
    
}
