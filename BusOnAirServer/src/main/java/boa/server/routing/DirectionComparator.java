
package boa.server.routing;

// di default l'ordine dei criteri di ottimizzazione è:
// - EARLIESTARRIVAL 
// - MINCHANGES
// - DURATION
// - MINWALK
// - MINTRAVELTIME


import java.util.Comparator;


public class DirectionComparator implements Comparator {

    Criteria criterion;

    public DirectionComparator(Criteria crit) {
        criterion = crit;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return 0;

        if (o1 == null)
            return 1;

        if (o2 == null)
            return -1;

        Direction d1 = (Direction) o1;
        Direction d2 = (Direction) o2;

        int ea = compareEarliestArrival(d1, d2);
        int minc = compareMinChanges(d1, d2);
        int duration = compareMinDuration(d1, d2);
        int minw = compareMinWalk(d1, d2);
        int mintrav = compareMinTravelTime(d1, d2);

        if (ea != 0) {
            return ea;
        } else if (criterion == Criteria.MINCHANGES) {
            if (minc != 0)
                return minc;
            else if (duration != 0)
                return duration;
            else if (minw != 0)
                return minw;
            else if (mintrav != 0)
                return mintrav;
            else
                return 0;
        } else if (criterion == Criteria.DURATION) {
            if (duration != 0)
                return duration;
            else if (minc != 0)
                return minc;
            else if (minw != 0)
                return minw;
            else if (mintrav != 0)
                return mintrav;
            else
                return 0;
        } else if (criterion == Criteria.MINWALK) {
            if (minw != 0)
                return minw;
            else if (minc != 0)
                return minc;
            else if (duration != 0)
                return duration;
            else if (mintrav != 0)
                return mintrav;
            else
                return 0;
        }

        return 0;
    }


    private int compareEarliestArrival(Direction o1, Direction o2) {
        Integer time1 = o1.getArrivalTime();
        Integer time2 = o2.getArrivalTime();

        if (time1 < time2)
            return -1;

        if (time1 > time2)
            return 1;

        return 0;

    }

    private int compareMinChanges(Direction o1, Direction o2) {
        Integer changes1 = o1.getNumChanges();
        Integer changes2 = o2.getNumChanges();

        if (changes1 < changes2)
            return -1;

        if (changes1 > changes2)
            return 1;

        return 0;
    }

    private int compareMinWalk(Direction o1, Direction o2) {
        Integer walk1 = o1.getWalkDistance();
        Integer walk2 = o2.getWalkDistance();

        if (walk1 < walk2)
            return -1;

        if (walk1 > walk2)
            return 1;

        return 0;
    }

    private int compareMinTravelTime(Direction o1, Direction o2) {
        Integer trav1 = o1.getTravelTime();
        Integer trav2 = o2.getTravelTime();

        if (trav1 < trav2)
            return -1;

        if (trav1 > trav2)
            return 1;

        return 0;
    }

    private int compareMinDuration(Direction o1, Direction o2) {
        Integer dur1 = o1.getDuration();
        Integer dur2 = o2.getDuration();

        if (dur1 < dur2)
            return -1;

        if (dur1 > dur2)
            return 1;

        return 0;
    }

    public int secondCriterionCompare(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return 0;

        if (o1 == null)
            return 1;

        if (o2 == null)
            return -1;

        Direction d1 = (Direction) o1;
        Direction d2 = (Direction) o2;

        int minc = compareMinChanges(d1, d2);
        int duration = compareMinDuration(d1, d2);
        int minw = compareMinWalk(d1, d2);
        int mintrav = compareMinTravelTime(d1, d2);

//            System.out.print("\nmic: " + minc + " duration: " + duration + " minw: " + minw + " mintrav: " + mintrav);

        if (criterion == Criteria.MINCHANGES) {
            if (minc != 0)
                return minc;
            else if (duration != 0)
                return duration;
            else if (minw != 0)
                return minw;
            else if (mintrav != 0)
                return mintrav;
            else
                return 0;
        } else if (criterion == Criteria.DURATION) {
            if (duration != 0)
                return duration;
            else if (minc != 0)
                return minc;
            else if (minw != 0)
                return minw;
            else if (mintrav != 0)
                return mintrav;
            else
                return 0;
        } else if (criterion == Criteria.MINWALK) {
            if (minw != 0)
                return minw;
            else if (minc != 0)
                return minc;
            else if (duration != 0)
                return duration;
            else if (mintrav != 0)
                return mintrav;
            else
                return 0;
        }

        return 0;
    }


}