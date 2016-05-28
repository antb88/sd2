package cs.technion.ac.il.sd.library.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.Iterator;
import java.util.Optional;

/**
 * Created by Nati on 5/28/2016.
 */
public class GraphTraverse {
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
}
