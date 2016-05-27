package cs.technion.ac.il.sd.library;

import com.google.common.collect.Sets;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * GraphUtils library
 */
public class GraphUtils {

    /**
     * Performs topological sort on DirectedGraph
     *
     * @param graph graph on which topological sort will be applied
     * @return Optional Iterator whose order of iteration is graph's topological sort
     * OR Optional.empty if there is no valid sort
     */

    public static <V, E> Optional<Iterator<V>> toposort(DirectedGraph<V, E> graph) {
        return hasCycle(graph) ? Optional.empty() : Optional.of(new TopologicalOrderIterator<>(graph));
    }

    /**
     * Checks whether a given {@link DirectedGraph} has a cycle
     *
     * @param graph graph
     * @return true iff graph has cycle
     */
    public static <V, E> boolean hasCycle(DirectedGraph<V, E> graph) {
        return new CycleDetector<>(graph).detectCycles();
    }

    /**
     * Get all sources of a {@link DirectedGraph} - all vertices with no incoming edges
     *
     * @param graph - graph to search
     * @return - set of sources in the received graph
     */
    public static <V, E> Set<V> getSourcesVertices(DirectedGraph<V, E> graph) {
        return graph.vertexSet().stream()
                .filter(v -> graph.inDegreeOf(v) == 0)
                .collect(Collectors.toSet());
    }

    /**
     * Get all leaves of a {@link DirectedGraph} - all vertices with no outgoing edges
     *
     * @param graph - graph to search
     * @return - set of leaves in the received graph
     */
    public static <V, E> Set<V> getLeafVertices(DirectedGraph<V, E> graph) {
        return graph.vertexSet().stream()
                .filter(v -> graph.outDegreeOf(v) == 0)
                .collect(Collectors.toSet());
    }

    /**
     * Returns all vertices that satisfy a given predicate, for instance :
     * <br>
     * {@code getVerticesThat(graph, v -> graph.outDegreeOf(v) == 0) is equivalent to getLeafVertices(graph)}
     * @param graph the graph to operate on
     * @param predicate predicate on vertices
     * @return -
     */
    public static <V, E> Set<V> getLVerticesSuchThat(DirectedGraph<V, E> graph, Predicate<V> predicate) {
        return graph.vertexSet().stream()
                .filter(predicate)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a Depth-first iterator on the specified graph.
     * Iteration will start at the specified start vertex.
     * If the specified start vertex is empty, iteration will start at an arbitrary vertex.
     *
     * @param graph       graph to search
     * @param startVertex vertex to start DFS iteration
     * @param listener    {@link TraversalListener}, implements methods to be applied on visited vertices
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     */
    private static <V, E> Iterator<V> DFSTraverse(DirectedGraph<V, E> graph, Optional<V> startVertex, Optional<TraversalListener<V, E>> listener, boolean isCrossComponent) {
        DepthFirstIterator<V, E> iterator = new DepthFirstIterator<>(graph, startVertex.isPresent() ? startVertex.get() : null);
        iterator.setCrossComponentTraversal(isCrossComponent);
        listener.ifPresent(iterator::addTraversalListener);
        return iterator;
    }

    /**
     * {@link #DFSTraverseCrossComponent(DirectedGraph, Optional, TraversalListener) DFSTraverseCrossComponent}.
     */
    public static <V, E> Iterator<V> DFSTraverseCrossComponent(DirectedGraph<V, E> graph, Optional<V> startVertex) {
        return DFSTraverse(graph, startVertex, Optional.empty(), true);
    }

    /**
     * {@link #DFSTraverse(DirectedGraph, Optional, Optional, boolean) DFSTraverse}.
     * The search will not be limited to the connected component that includes the specified start vertex, that is, will be able to traverse all the graph.
     */
    public static <V, E> Iterator<V> DFSTraverseCrossComponent(DirectedGraph<V, E> graph, Optional<V> startVertex, TraversalListener<V, E> listener) {
        return DFSTraverse(graph, startVertex, Optional.of(listener), true);
    }


    /**
     * {@link #DFSTraverseSingleComponent(DirectedGraph, Optional, TraversalListener) DFSTraverseSingleComponent}.
     */
    public static <V, E> Iterator<V> DFSTraverseSingleComponent(DirectedGraph<V, E> graph, Optional<V> startVertex) {
        return DFSTraverse(graph, startVertex, Optional.empty(), false);
    }


    /**
     * {@link #DFSTraverse(DirectedGraph, Optional, Optional, boolean) DFSTraverse}.
     * The search will be limited to the connected component that includes the specified start vertex (or an arbitrary vertex if not specified).
     */
    public static <V, E> Iterator<V> DFSTraverseSingleComponent(DirectedGraph<V, E> graph, Optional<V> startVertex, TraversalListener<V, E> listener) {
        return DFSTraverse(graph, startVertex, Optional.of(listener), false);
    }

    /**
     * Returns a Breadth-first iterator on the specified graph.
     * Iteration will start at the specified start vertex.
     * If the specified start vertex is empty, iteration will start at an arbitrary vertex.
     *
     * @param graph       graph to search
     * @param startVertex vertex to start DFS iteration
     * @param listener    {@link TraversalListener}, implements methods to be applied on visited vertices
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     */
    private static <V, E> Iterator<V> BFSTraverse(DirectedGraph<V, E> graph, Optional<V> startVertex, Optional<TraversalListener<V, E>> listener, boolean isCrossComponent) {
        BreadthFirstIterator<V, E> iterator = new BreadthFirstIterator<>(graph, startVertex.isPresent() ? startVertex.get() : null);
        iterator.setCrossComponentTraversal(isCrossComponent);
        listener.ifPresent(iterator::addTraversalListener);
        return iterator;
    }

    /**
     * {@link #BFSTraverseCrossComponent(DirectedGraph, Optional, TraversalListener) BFSTraverseCrossComponent}.
     */
    public static <V, E> Iterator<V> BFSTraverseCrossComponent(DirectedGraph<V, E> graph, Optional<V> startVertex) {
        return BFSTraverse(graph, startVertex, Optional.empty(), true);
    }

    /**
     * {@link #BFSTraverse(DirectedGraph, Optional, Optional, boolean) BFSTraverse}.
     * The search will not be limited to the connected component that includes the specified start vertex, that is, will be able to traverse all the graph.
     */
    public static <V, E> Iterator<V> BFSTraverseCrossComponent(DirectedGraph<V, E> graph, Optional<V> startVertex, TraversalListener<V, E> listener) {
        return BFSTraverse(graph, startVertex, Optional.of(listener), true);
    }

    /**
     * {@link #BFSTraverseSingleComponent(DirectedGraph, Optional, TraversalListener) BFSTraverseSingleComponent}.
     */
    public static <V, E> Iterator<V> BFSTraverseSingleComponent(DirectedGraph<V, E> graph, Optional<V> startVertex) {
        return BFSTraverse(graph, startVertex, Optional.empty(), false);
    }

    /**
     * {@link #BFSTraverse(DirectedGraph, Optional, Optional, boolean) BFSTraverse}.
     * The search will be limited to the connected component that includes the specified start vertex (or an arbitrary vertex if not specified).
     */
    public static <V, E> Iterator<V> BFSTraverseSingleComponent(DirectedGraph<V, E> graph, Optional<V> startVertex, TraversalListener<V, E> listener) {
        return BFSTraverse(graph, startVertex, Optional.of(listener), false);
    }

    /**
     * Get all reachable vertices from a specified source vertex in a {@link DirectedGraph}.
     *
     * @param graph  the graph to search
     * @param source source vertex
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     */
    public static <V, E> Set<V> getAllReachableVerticesFromSource(DirectedGraph<V, E> graph, V source) {
        return Sets.newHashSet(DFSTraverseSingleComponent(graph, Optional.of(source)));
    }

}
