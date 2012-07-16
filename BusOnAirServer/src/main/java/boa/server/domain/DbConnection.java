package boa.server.domain;

import java.io.File;

import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.database.Database;

public class DbConnection {
	private static final String dbpath = "/tmp/neo4j/busonairserver/data/graph.db";  
//	private static final String dbpath = "/home/rashta/neo4j/neo4j/busonairserver/data/graph.db";  
    private static DbConnection instance = null;
    private AbstractGraphDatabase db = null;
    private SpatialDatabaseService spatialDb = null;
    
    // DB per applicazione server
    public static synchronized DbConnection createDbConnection(Database _db) {
    	if (instance == null){ 
            instance = new DbConnection(_db);            
        }
        return instance;
    }    
    
    // DB per applicazione standalone
    public static synchronized DbConnection createEmbeddedDbConnection() {
        if (instance == null){ 
            instance = new DbConnection();            
        }
        return instance;
    }    
    
    public static synchronized DbConnection getDbConnection() {

        return instance;
    }    
       
    private DbConnection(Database _db){
        db = (AbstractGraphDatabase) _db.graph;
        spatialDb = new SpatialDatabaseService(db);
    }
    
    private DbConnection(){
//        db = new EmbeddedReadOnlyGraphDatabase( dbpath );
        db = new EmbeddedGraphDatabase( dbpath );
        spatialDb = new SpatialDatabaseService(db);
    }

    
    public static AbstractGraphDatabase getDb(){
        return getDbConnection().getDatabase();
    }
        
    public static SpatialDatabaseService getSpatialDb(){
        return getDbConnection().getSpatialDatabase();
    }
        
    public static void turnoff(){
        getDbConnection().shutdown();
    }
    
    public AbstractGraphDatabase getDatabase(){
        return db;
    }
    
    public SpatialDatabaseService getSpatialDatabase(){
    	return spatialDb;
    }
    
    public void shutdown(){
        db.shutdown();
    }    
    
    public static void clear(){
        deleteRecursively( new File( dbpath ) );
        //inner = new EmbeddedGraphDatabase( storeDir, params );
    }
        
    private static void deleteRecursively( File file )
         {
             if ( !file.exists() )
             {
                 return;
             }
     
             if ( file.isDirectory() )
             {
                 for ( File child : file.listFiles() )
                 {
                     deleteRecursively( child );
                 }
             }
             if ( !file.delete() )
            {
                throw new RuntimeException( "Couldn't empty database. Offending file:" + file );
            }
        }
    
//    private void deleteFileOrDirectory( final File file ) {
//        if ( file.exists() ) {
//            if ( file.isDirectory() ) {
//                for ( File child : file.listFiles() ) {
//                    deleteFileOrDirectory( child );
//                }
//            }
//            file.delete();
//        }
//    }
    

}
