package cs.technion.ac.il.sd.library.dependency;

import cs.technion.ac.il.sd.library.graph.GraphUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ant on 5/28/16.
 */

public class DependencyManagerImpl<T> implements DependencyManager<T> {

    private final DirectedGraph<T, DefaultEdge> graph;
    private final HashSet<T> resolved;
    private final Map<T, Set<T>> dependantsMap;
    private final Map<T, Set<T>> dependencyMap;

    DependencyManagerImpl(DirectedGraph<T, DefaultEdge> graph, Map<T, Set<T>> dependencyMap, Map<T, Set<T>> dependantsMap) {
        this.graph = graph;
        this.resolved = new HashSet<>();
        this.dependantsMap = dependantsMap;
        this.dependencyMap = dependencyMap;
    }

    @Override
    public Set<T> resolve(T entity) {
        if (!isResolvable(entity)) {
            throw new IllegalArgumentException("entity " + entity + "  can not be resolved, has unresolved dependencies");
        }
        Set<T> newResolved = graph.outgoingEdgesOf(entity).stream()
                .map(graph::getEdgeTarget)
                .collect(Collectors.toSet());
       //must copy edges to avoid exception
        graph.outgoingEdgesOf(entity).stream()
                .collect(Collectors.toSet())
                .forEach(graph::removeEdge);
        resolved.add(entity);
        return newResolved;
    }

    @Override
    public boolean isResolvable(T entity) {
        return GraphUtils.getSourcesVertices(graph).contains(entity);
    }

    @Override
    public boolean isResolved(T entity) {
        return resolved.contains(entity);
    }

    @Override
    public boolean isResolvable() {
        return !GraphUtils.hasCycle(graph);
    }

    @Override
    public Set<T> getAllResolvable() {
        return GraphUtils.getSourcesVertices(graph);
    }

    @Override
    public Set<T> getDependantsOn(T entity) {
        Set<T> deps = dependantsMap.get(entity);
        if (deps == null) {
            throw new IllegalArgumentException("entity is not managed by this DependencyManagerImpl");
        }
        return deps.stream()
                .collect(Collectors.toSet());
    }

    @Override
    public Set<T> getDependenciesOf(T entity) {
        Set<T> deps = dependencyMap.get(entity);
        if (deps == null) {
            throw new IllegalArgumentException("entity is not managed by this DependencyManagerImpl");
        }
        return deps.stream()
                .collect(Collectors.toSet());
    }

    @Override
    public Set<T> getAllResolved() {
        return resolved.stream()
                .collect(Collectors.toSet());
    }


}
