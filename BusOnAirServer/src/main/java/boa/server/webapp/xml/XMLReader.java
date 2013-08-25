package boa.server.webapp.xml;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class XMLReader {
    public static Routes read() {
        Routes routes = new Routes();
        try {
            XStream xs = new XStream(new DomDriver());
            xs.alias("xml.Routes", Routes.class);
            xs.alias("xml.Route", Route.class);
            xs.alias("xml.Stop", Stop.class);
            xs.fromXML(new FileInputStream("src/main/resources/TransportData.xml"), routes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return routes;
    }
}