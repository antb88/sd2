package cs.technion.ac.il.sd.library.dependency;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ant on 5/28/16.
 */
public class DependencyManagerBuilder<T> {

    public static <T> DependencyManagerBuilder<T> newBuilder() {
        return new DependencyManagerBuilder<>();
    }

    private Map<T, Set<T>> dependencyMap;
    private Map<T, Set<T>> dependantsMap;

    private DependencyManagerBuilder() {

        this.dependantsMap = new HashMap<>();
        this.dependencyMap = new HashMap<>();
    }

    public DependencyManagerBuilder add(T entity) {
        dependantsMap.putIfAbsent(entity, new HashSet<>());
        dependencyMap.putIfAbsent(entity, new HashSet<>());
        return this;
    }

    public DependencyManagerBuilder addDependency(T entity, T dependency) {
        add(entity);
        add(dependency);
        dependencyMap.get(entity).add(dependency);
        dependantsMap.get(dependency).add(entity);
        return this;
    }

    public DependencyManagerBuilder addDependencies(T entity, Set<T> dependencies) {
        add(entity);
        dependencies.forEach(d -> addDependency(entity, d));
        return this;
    }

    public DependencyManager<T> build() {
        DirectedGraph<T, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        dependencyMap.keySet().forEach(g::addVertex);
        dependantsMap.keySet().forEach( e -> dependantsMap.get(e).forEach( t -> g.addEdge(e, t)));
        return new DependencyManagerImpl<>(g, dependencyMap, dependantsMap);
    }
}

