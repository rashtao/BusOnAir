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
public class printStation {
	
 
    
    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		
		Station s = Stations.getStations().getStationById((long) 34);
		
		Stop fs = s.getFirstStopFromTime(0);

		while (fs != null){
			System.out.print("\n\n" + fs + "" + fs.getRun().getId()+ "\n\n");
			fs = fs.getNextInStation();
		}
		
		DbConnection.turnoff();
		
    	
    }
    
}
