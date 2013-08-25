// This class maps the db objects and grants that every db object has at most one Stop object instance

package boa.server.routing;

import boa.server.domain.Stop;
import org.neo4j.graphdb.Node;

import java.util.HashMap;

public final class StopMediator {
    private HashMap<Long, Stop> cache;

    public StopMediator() {
        clear();
    }

    public void clear() {
        cache = new HashMap<Long, Stop>();
    }

    public Stop get(Node n) {
        if (n == null)
            return null;

        Stop s = cache.get(n.getId());
        if (s == null) {
            s = new Stop(n);
            cache.put(n.getId(), s);
        }

        return s;
    }

    public Stop get(Stop s) {
        if (s == null)
            return null;

        return get(s.getUnderlyingNode());
    }

    public boolean check(Node n) {
        if (n == null)
            return false;

        return cache.get(n.getId()) != null;
    }

    public boolean check(Stop s) {
        if (s == null)
            return false;

        return check(s.getUnderlyingNode());
    }

    public HashMap<Long, Stop> getHashMap() {
        return cache;
    }
}
