package importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XMLReader{
	public static BusonairSql readStops(){
		BusonairSql readData = new BusonairSql();
        try
        {
            XStream xs = new XStream( new DomDriver() );
            xs.alias( "busonair", BusonairSql.class );
            xs.alias( "stop_schedule", StopSql.class );
            xs.fromXML( new FileInputStream( "resources/stop_schedule.xml" ), readData );
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        return readData;     
    }    

	public static BusonairSql readRoutes(){
		BusonairSql readData = new BusonairSql();
        try
        {
            XStream xs = new XStream( new DomDriver() );
            xs.alias( "busonair", BusonairSql.class );
            xs.alias( "route", RouteSql.class );
            xs.fromXML( new FileInputStream( "resources/route.xml" ), readData );
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        return readData;     
    }    

	public static BusonairSql readStations(){
		BusonairSql readData = new BusonairSql();
        try
        {
            XStream xs = new XStream( new DomDriver() );
            xs.alias( "busonair", BusonairSql.class );
            xs.alias( "station", StationSql.class );
            xs.fromXML( new FileInputStream( "resources/station.xml" ), readData );
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        return readData;     
    }    
}