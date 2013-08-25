package boa.server.domain;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.database.Database;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.neo4j.server.configuration.Configurator.DATABASE_LOCATION_PROPERTY_KEY;


public class DbConnection {
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
    
    public static void destroy() {
    	instance = null;
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
        db = new EmbeddedGraphDatabase( Config.DBPATH );
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
    
    public static void deleteDbFiles() throws IOException{
		Runtime.getRuntime().exec( "sudo chmod 777 " + Config.DBPATH + " -R" );

        deleteRecursively( new File( Config.DBPATH ) );
    }
        
    // delete all db files, for standalone application
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
    
    // delete all db files, for server application
    public static Map<String, Object> cleanDbDirectory(Database database, Configuration config) throws Throwable {
        AbstractGraphDatabase graph = (AbstractGraphDatabase) database.graph;
        String storeDir = graph.getStoreDir();
        if (storeDir == null) {
            storeDir = config.getString(DATABASE_LOCATION_PROPERTY_KEY);
        }
        graph.shutdown();
        Map<String, Object> result = removeDirectory(storeDir);
    	
        database.graph = new EmbeddedGraphDatabase(storeDir, graph.getKernelData().getConfigParams());
        
        return result;
    }

    private static Map<String, Object> removeDirectory(String storeDir) throws IOException {
        File dir = new File(storeDir);
        Map<String,Object> result=new HashMap<String, Object>();
        result.put("store-dir",dir);
        result.put("size", FileUtils.sizeOfDirectory(dir));
        FileUtils.deleteDirectory(dir);
        return result;
    }    
    
    
}
