/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.File;
import java.io.IOException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.server.database.Database;

/**
 *
 * @author rashta
 */
public class DbConnection {
    //private static final String dbpath = "/home/rashta/neo4j/busonairserver/data/graph.db";  
//    private static final String dbpath = "./db/data/graph.db";  
    private static DbConnection instance = null;
//    private GraphDatabaseService db;
    private AbstractGraphDatabase db;
    
    public static synchronized DbConnection createDbConnection(Database _db) {
        if (instance == null){ 
            instance = new DbConnection(_db);            
        }
        return instance;
    }    
    
    public static synchronized DbConnection getDbConnection() {

        return instance;
    }    
       
    private DbConnection(Database _db){
        db = _db.graph;
    }

    
    public static AbstractGraphDatabase getDb(){
        return getDbConnection().getDatabase();
    }
        
    public static void turnoff(){
        getDbConnection().shutdown();
    }
    
    public AbstractGraphDatabase getDatabase(){
        return db;
    }
    
    public void shutdown(){
        db.shutdown();
    }
    
}
