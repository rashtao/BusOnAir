package ie.transportdublin.dbinsertershortestpath;

import ie.transportdublin.xml.DBInserter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class DBInserterTest
{
    private static GraphDatabaseService db;
    private static DBInserter dbInserter;

    @BeforeClass
    public static void setup()
    {
//        db = new EmbeddedGraphDatabase( "target/db" );
//        dbInserter = new DBInserter( db );
    }

    @Test
    public void addData()
    {
//        dbInserter.addData();
    }

    @AfterClass
    public static void shutdown()
    {
//        db.shutdown();
    }

}
