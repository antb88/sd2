package cs.technion.ac.il.sd.library;

import org.jgrapht.DirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.Iterator;

/**
 *  Extension of {@link GraphUtils} graph library, focusing on different iterators\traversals on {@link DirectedGraph}.
 *  Traversals can be both cross-component (in case the graph is not connected) as well as single-component,
 *  in which case the result of the traversed set of vertices will depend on the start vertex.
 *
 */
public class GraphTraverse {
    /**
     * Returns a Depth-first iterator on the specified graph.
     * Iteration will start at the specified start vertex.
     *
     * @param graph  graph to search
     * @param startVertex vertex from which to start DFS iteration
     * @param isCrossComponent is the traverse cross-component
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return Iterator object whose order of enumeration is some dfs traversal of the graph, starting from startVertex
     *
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     */
    private static <V, E> Iterator<V> dfs(DirectedGraph<V, E> graph, V startVertex, boolean isCrossComponent) {
        DepthFirstIterator<V, E> iterator = new DepthFirstIterator<>(graph, startVertex);
        iterator.setCrossComponentTraversal(isCrossComponent);
        return iterator;
    }

    /**
     * Returns a Depth-first iterator on the specified graph.
     * The search will not be limited to the connected component that includes the specified start vertex,
     * that is, will be able to traverse all the graph.
     *
     * @param graph  graph to search
     * @param startVertex  vertex to start DFS traversal from
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return Iterator object whose order of enumeration is some dfs traversal of the graph, starting from startVertex
     *
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     * @see #dfs(DirectedGraph, Object, boolean)
     */
    public static <V, E> Iterator<V> dfsCrossComponent(DirectedGraph<V, E> graph, V startVertex) {
        return dfs(graph, startVertex, true);
    }

    /**
     * Returns a Depth-first iterator on the specified graph.
     * The search will be limited to the connected component that includes the specified start vertex
     *
     * @param graph  graph to search
     * @param startVertex  vertex to start DFS traversal from
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return Iterator object whose order of enumeration is some dfs traversal of the graph, starting from startVertex
     *
     * @see #dfs(DirectedGraph, Object, boolean)
     */
    public static <V, E> Iterator<V> dfsSingleComponent(DirectedGraph<V, E> graph, V startVertex) {
        return dfs(graph, startVertex, false);
    }

    /**
     * Returns a Breadth-first iterator on the specified graph.
     * Iteration will start at the specified start vertex.
     *
     * @param graph  graph to search
     * @param startVertex vertex to start DFS iteration
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return Iterator object whose order of enumeration is some bfs traversal of the graph, starting from startVertex
     *
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     */
    private static <V, E> Iterator<V> bfs(DirectedGraph<V, E> graph, V startVertex, boolean isCrossComponent) {
        BreadthFirstIterator<V, E> iterator = new BreadthFirstIterator<>(graph, startVertex);
        iterator.setCrossComponentTraversal(isCrossComponent);
        return iterator;
    }

    /**
     * Returns a Breadth-first iterator on the specified graph.
     * Iteration will start at the specified start vertex.
     * The search will not be limited to the connected component that includes the specified start vertex,
     * that is, will be able to traverse all the graph.
     *
     * @param graph  graph to search
     * @param startVertex vertex to start BFS iteration
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return Iterator object whose order of enumeration is some bfs traversal of the graph, starting from startVertex
     *
     * @throws IllegalArgumentException if the graph does not contain the specified start vertex
     * @see #bfs(DirectedGraph, Object, boolean)
     */
    public static <V, E> Iterator<V> bfsCrossComponent(DirectedGraph<V, E> graph, V startVertex) {
        return bfs(graph, startVertex, true);
    }

    /**
     * Returns a Breadth-first iterator on the specified graph.
     * The search will be limited to the connected component that includes the specified start vertex.
     *
     * @param graph  graph to search
     * @param startVertex vertex to start BFS iteration
     * @param <V> type of vertex object in the graph
     * @param <E> type of edge object in the graph
     * @return Iterator object whose order of enumeration is some bfs traversal of the graph, starting from startVertex
     *
     * @see #bfs(DirectedGraph, Object, boolean)
     */
    public static <V, E> Iterator<V> bfsSingleComponent(DirectedGraph<V, E> graph, V startVertex) {
        return bfs(graph, startVertex, false);
    }
}
