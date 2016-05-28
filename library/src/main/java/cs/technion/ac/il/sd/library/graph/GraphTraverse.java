package cs.technion.ac.il.sd.library.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.Iterator;
import java.util.Optional;

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
    private static <V, E> Iterator<V> DFSTraverse(DirectedGraph<V, E> graph, Optional<V> startVertex, boolean isCrossComponent) {
        DepthFirstIterator<V, E> iterator = new DepthFirstIterator<>(graph, startVertex.isPresent() ? startVertex.get() : null);
        iterator.setCrossComponentTraversal(isCrossComponent);
        return iterator;
    }

    /**
     * {@link #DFSTraverse(DirectedGraph, Optional, boolean) DFSTraverse}.
     * The search will not be limited to the connected component that includes the specified start vertex, that is, will be able to traverse all the graph.
     */
    public static <V, E> Iterator<V> DFSTraverseCrossComponent(DirectedGraph<V, E> graph, Optional<V> startVertex) {
        return DFSTraverse(graph, startVertex, true);
    }

    /**
     * {@link #DFSTraverse(DirectedGraph, Optional, boolean) DFSTraverse}.
     * The search will be limited to the connected component that includes the specified start vertex (or an arbitrary vertex if not specified).
     */
    public static <V, E> Iterator<V> DFSTraverseSingleComponent(DirectedGraph<V, E> graph, Optional<V> startVertex) {
        return DFSTraverse(graph, startVertex, false);
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
    private static <V, E> Iterator<V> BFSTraverse(DirectedGraph<V, E> graph, Optional<V> startVertex, boolean isCrossComponent) {
        BreadthFirstIterator<V, E> iterator = new BreadthFirstIterator<>(graph, startVertex.isPresent() ? startVertex.get() : null);
        iterator.setCrossComponentTraversal(isCrossComponent);
        return iterator;
    }

    /**
     * {@link #BFSTraverse(DirectedGraph, Optional, boolean) BFSTraverse}.
     * The search will not be limited to the connected component that includes the specified start vertex, that is, will be able to traverse all the graph.
     */
    public static <V, E> Iterator<V> BFSTraverseCrossComponent(DirectedGraph<V, E> graph, Optional<V> startVertex) {
        return BFSTraverse(graph, startVertex, true);
    }

    /**
     * {@link #BFSTraverse(DirectedGraph, Optional, boolean) BFSTraverse}.
     * The search will be limited to the connected component that includes the specified start vertex (or an arbitrary vertex if not specified).
     */
    public static <V, E> Iterator<V> BFSTraverseSingleComponent(DirectedGraph<V, E> graph, Optional<V> startVertex) {
        return BFSTraverse(graph, startVertex, false);
    }

}
