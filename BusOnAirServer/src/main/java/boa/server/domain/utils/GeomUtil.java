package boa.server.domain.utils;

import com.vividsolutions.jts.geom.Coordinate;

public class GeomUtil {

    public static Coordinate proiezione(Coordinate cp1, Coordinate cp2, Coordinate cp3) {
        // proietta cp3 sulla retta passante per cp1-cp2
        // cp1 è il pto in comune tra i 2 segmenti

        double distance = GeoUtil.getDistance2(cp1.y, cp1.x, cp3.y, cp3.x);
        double rapporto = distance / GeoUtil.getDistance2(cp1.y, cp1.x, cp2.y, cp2.x);

        Coordinate d = new Coordinate();

        d.y = cp1.y * (1 - rapporto) + cp2.y * rapporto;
        d.x = cp1.x * (1 - rapporto) + cp2.x * rapporto;

        return d;
    }

}
