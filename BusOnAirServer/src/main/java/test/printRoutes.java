/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import domain.DbConnection;
import domain.Route;
import domain.Routes;

/**
 *
 * @author rashta
 */
public class printRoutes {
    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		
        Iterable<Route> routes = Routes.getRoutes().getAll();
        int i = 0;
        for(Route r : routes){
            System.out.print("\n['" + r.getId()  + "','" + r.getLine() + "','" + r.getLine() + "','" + r.getFrom().getName() + "'],");
            i++;
        }
        
        
        Route r = Routes.getRoutes().getRouteByLine("M12A");
        
        System.out.print(r);
        
        
    
    }
}
