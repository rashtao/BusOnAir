/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boa.server.test;

import boa.server.domain.DbConnection;
import boa.server.domain.Route;
import boa.server.domain.Routes;


/**
 * @author rashta
 */
public class RouteRun {

    /**
     * @param args
     */
    public static void main(String[] args) {
        DbConnection.createEmbeddedDbConnection();

//		Run r = Runs.getRuns().getRunById(208);


        for (Route r : Routes.getRoutes().getAll()) {
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
