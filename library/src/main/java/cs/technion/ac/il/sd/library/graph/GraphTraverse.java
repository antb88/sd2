package cs.technion.ac.il.sd.library.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.Iterator;

/**
 * {@link GraphUtils}
 */
public class GraphTraverse {
    /**
     * Returns a Depth-first iterator on the specified graph.
     * Iteration will start at the specified start vertex.
     * If the specified start vertex is empty, iteration will start at an arbitrary vertex.
     *
     * @param graph       graph to search
     * @param startVertex vertex to start DFS iteration
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     */
    private static <V, E> Iterator<V> dfs(DirectedGraph<V, E> graph, V startVertex, boolean isCrossComponent) {
        DepthFirstIterator<V, E> iterator = new DepthFirstIterator<>(graph, startVertex);
        iterator.setCrossComponentTraversal(isCrossComponent);
        return iterator;
    }

    /**
     * The search will not be limited to the connected component that includes the specified start vertex,
     * that is, will be able to traverse all the graph.
     *
     * @param graph
     * @param startVertex
     * @param <V>
     * @param <E>
     * @return
     *
     * @see #dfs(DirectedGraph, Object, boolean)
     */
    public static <V, E> Iterator<V> dfsCrossComponent(DirectedGraph<V, E> graph, V startVertex) {
        return dfs(graph, startVertex, true);
    }

    /**
     * The search will be limited to the connected component that includes the specified start vertex
     * (or an arbitrary vertex if not specified).
     *
     * @param graph
     * @param startVertex
     * @param <V>
     * @param <E>
     * @return
     *
     * @see #dfs(DirectedGraph, Object, boolean)
     */
    public static <V, E> Iterator<V> dfsSingleComponent(DirectedGraph<V, E> graph, V startVertex) {
        return dfs(graph, startVertex, false);
    }

    /**
     * Returns a Breadth-first iterator on the specified graph.
     * Iteration will start at the specified start vertex.
     * If the specified start vertex is empty, iteration will start at an arbitrary vertex.
     *
     * @param graph       graph to search
     * @param startVertex vertex to start DFS iteration
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     */
    private static <V, E> Iterator<V> bfs(DirectedGraph<V, E> graph, V startVertex, boolean isCrossComponent) {
        BreadthFirstIterator<V, E> iterator = new BreadthFirstIterator<>(graph, startVertex);
        iterator.setCrossComponentTraversal(isCrossComponent);
        return iterator;
    }

    /**
     * he search will not be limited to the connected component that includes the specified start vertex,
     * that is, will be able to traverse all the graph.
     *
     * @param graph
     * @param startVertex
     * @param <V>
     * @param <E>
     * @return
     *
     * @see #bfs(DirectedGraph, Object, boolean)
     */
    public static <V, E> Iterator<V> bfsCrossComponent(DirectedGraph<V, E> graph, V startVertex) {
        return bfs(graph, startVertex, true);
    }

    /**
     *
     * The search will be limited to the connected component that includes the specified start vertex
     *  (or an arbitrary vertex if not specified).
     *
     * @param graph
     * @param startVertex
     * @param <V>
     * @param <E>
     * @return
     *
     * @see #bfs(DirectedGraph, Object, boolean)
     */
    public static <V, E> Iterator<V> bfsSingleComponent(DirectedGraph<V, E> graph, V startVertex) {
        return bfs(graph, startVertex, false);
    }

}
