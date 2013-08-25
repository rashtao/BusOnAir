package boa.server.domain.utils;

import boa.server.domain.Stop;

import java.util.Comparator;

public class TimeComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {

        int time1 = ((Stop) o1).getTime();
        int time2 = ((Stop) o2).getTime();

        if (time1 > time2)
            return 1;
        else if (time1 < time2)
            return -1;
        else
            return 0;
    }

}