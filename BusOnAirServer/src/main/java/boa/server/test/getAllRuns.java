/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boa.server.test;

import boa.server.domain.DbConnection;
import boa.server.domain.Run;
import boa.server.domain.Runs;

import java.util.ArrayList;




/**
 *
 * @author rashta
 */
public class getAllRuns {
	   
    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		

		Run run = Runs.getRuns().getRunById(11);
		System.out.print("\n" + run);
//		Runs.getRuns().deleteRun(run);
		ArrayList<Run> runs = Runs.getRuns().getAll();
		for(Run r : runs){
			System.out.print("\n" + r.getId());
		}
		
		
		DbConnection.turnoff();
		
    	
    }
    
}
