package ie.transportdublin.graphalgo;

import ie.transportdublin.graphalgo.impl.BestFirstSelectorFactory;
import ie.transportdublin.graphalgo.impl.WaitingTimeCostEvaluator;

import java.util.Iterator;

import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphalgo.impl.util.StopAfterWeightIterator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalBranch;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

/**
 * Modified version of org.neo4j.graphalgo.impl.path.Dijkstra
 */
public class Dijkstra implements PathFinder<WeightedPath>
{
    private static final TraversalDescription TRAVERSAL = Traversal.description().uniqueness(
                    Uniqueness.NONE );

    private final RelationshipExpander expander;
    private final WaitingTimeCostEvaluator costEvaluator;
    private final Double time;

    public Dijkstra( RelationshipExpander expander,  WaitingTimeCostEvaluator costEvaluator, Double time)
    {
        this.expander = expander;
        this.costEvaluator = costEvaluator;
        this.time = time;
    }

    public Iterable<WeightedPath> findAllPaths( Node start, final Node end )
    {
        Evaluator evaluator = new Evaluator()
        {
            public Evaluation evaluate( Path path )
            {
                if ( path.length() == 8 ) return Evaluation.EXCLUDE_AND_PRUNE;
                return ( path.endNode().equals( end ) ? Evaluation.INCLUDE_AND_PRUNE
                        : Evaluation.EXCLUDE_AND_CONTINUE );
            }
        };

        final Traverser traverser = TRAVERSAL.expand( expander ).order(
                new SelectorFactory( costEvaluator, time ) ).evaluator(
                evaluator ).traverse( start );

        return new Iterable<WeightedPath>()
        {
            public Iterator<WeightedPath> iterator()
            {
                return new StopAfterWeightIterator( traverser.iterator(), costEvaluator );
            }
        };
    }
    
    public WeightedPath findSinglePath( Node start, Node end )
    {
        Iterator<WeightedPath> result = findAllPaths( start, end ).iterator();
        return result.hasNext() ? result.next() : null;
    }

    private static class SelectorFactory extends BestFirstSelectorFactory<Double, Double>
    {
        private final WaitingTimeCostEvaluator evaluator;
        private Double time;

        SelectorFactory( WaitingTimeCostEvaluator evaluator , Double time)
        {
            this.evaluator = evaluator;
            this.time = time;
        }

        @Override
        protected Double calculateValue( TraversalBranch next , Double currentAggregatedValue)
        {
            return next.depth() == 0 ? 0d : evaluator.getCost(next.relationship(), Direction.OUTGOING , currentAggregatedValue);
        }

        @Override
        protected Double addPriority( TraversalBranch source, Double currentAggregatedValue, Double value )
        {
            return withDefault( currentAggregatedValue, 0d ) + withDefault( value, 0d );
        }

        private <T> T withDefault( T valueOrNull, T valueIfNull )
        {
            return valueOrNull != null ? valueOrNull : valueIfNull;
        }

        @Override
        protected Double getStartData()
        {
            return time;
        }
    }
}

