package cs.technion.ac.il.sd.library.graph;

import com.google.common.collect.Sets;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * GraphUtils and GraphTraverse library based on underlying JGraphT graph library (http://jgrapht.org, http://jgrapht.org/javadoc).
 * <br> Library contains graph iterators such as : topological sort, bfs, dfs along with utility functions such as getting
 * all vertices that satisfy a condition, all leafs, all sources etc'
 * <br> <br> We chose to support mainly DirectedGraph as a graph because of the nature of the assignments,
 * however underlying JGraphT supports other graphs as well so library can be easily extended.
 *
 * <br> <br> As an example, creating a directed graph to be used with the library is as follows:
 * <br> {@code DirectedGraph<Integer, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class)}
 * <br> where DefaultEdge means edges hold no other information other than connecting 2 nodes, off course other
 * implementations are possible
 *
 */
public class GraphUtils {

    /**
     * Performs topological sort on {@link DirectedGraph}
     *
     * @param graph graph on which topological sort will be applied
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return Optional Iterator whose order of iteration is graph's topological sort OR Optional.empty
     * if there is no valid sort
     */

    public static <V, E> Optional<Iterator<V>> toposort(DirectedGraph<V, E> graph) {
        return hasCycle(graph) ? Optional.empty() : Optional.of(new TopologicalOrderIterator<>(graph));
    }

    /**
     * Checks whether a given {@link DirectedGraph} has a cycle
     *
     * @param graph graph
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return true iff graph has cycle
     */
    public static <V, E> boolean hasCycle(DirectedGraph<V, E> graph) {
        return new CycleDetector<>(graph).detectCycles();
    }

    /**
     * Get all sources of a {@link DirectedGraph} - that is, all vertices with no incoming edges.
     *
     * @param graph - graph to search
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return  set of sources in the received graph
     */
    public static <V, E> Set<V> getSourcesVertices(DirectedGraph<V, E> graph) {
        return graph.vertexSet().stream()
                .filter(v -> graph.inDegreeOf(v) == 0)
                .collect(Collectors.toSet());
    }

    /**
     * Get all leaves of a {@link DirectedGraph} - that is, all vertices with no outgoing edges
     *
     * @param graph - graph to search
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return  set of leaves in the received graph
     */
    public static <V, E> Set<V> getLeafVertices(DirectedGraph<V, E> graph) {
        return graph.vertexSet().stream()
                .filter(v -> graph.outDegreeOf(v) == 0)
                .collect(Collectors.toSet());
    }

    /**
     * Returns all vertices that satisfy a given predicate, for instance :
     * <br>
     * <code> getVerticesThat(graph, v -> graph.outDegreeOf(v) == 0) </code>
     * <br>
     * is equivalent to:
     * <br> <code>getLeafVertices(graph)</code>
     *
     * @param graph the graph to operate on
     * @param predicate predicate condition on vertices
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return set of vertices satisfying the given predicate in the  graph
     */
    public static <V, E> Set<V> getVerticesSuchThat(DirectedGraph<V, E> graph, Predicate<V> predicate) {
        return graph.vertexSet().stream()
                .filter(predicate)
                .collect(Collectors.toSet());
    }

    /**
     * Get all reachable vertices from a specified source vertex in a {@link DirectedGraph}.
     *
     * @param graph  the graph to search
     * @param source source vertex
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return Set ov vertices reachable from source in the graph
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     */
    public static <V, E> Set<V> getAllReachableVerticesFromSource(DirectedGraph<V, E> graph, V source) {
        return Sets.newHashSet(GraphTraverse.dfsSingleComponent(graph, source));
    }

}
