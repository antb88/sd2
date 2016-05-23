package cs.technion.ac.il.sd.app;

import com.google.inject.Inject;
import cs.technion.ac.il.sd.ManagerFactory;
import cs.technion.ac.il.sd.library.GraphUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by ant on 5/23/16.
 */
public class ManagerAppImpl implements ManagerApp {

    private final ManagerFactory factory;
    private int cpus;
    private int memory;
    private int disks;
    private DirectedGraph<Task, DefaultEdge> dependencyGraph;

    @Inject
    public ManagerAppImpl(ManagerFactory factory) {
        this.factory = factory;

    }

    @Override
    public void processFile(File file) {

        Configuration configuration = Configuration.fromFile(file);
        dependencyGraph = buildGraphFrom(configuration);
        cpus = configuration.getCpus();
        memory = configuration.getMemory();
        disks = configuration.getDisks();

        if (!hasCyrcularDependency(dependencyGraph) && isEnoughResources(configuration) ) {
            process();
        }
        else {
            fail();
        }
    }

    private void fail() {
        factory.create(0,0,0).fail();
    }

    //TODO : work in progress
    private void process() {
        Queue<Task> scheduled = new PriorityBlockingQueue<>(10, (t1, t2) -> t1.getPriority() < t2.getPriority() ? -1 : 1);

        scheduled.addAll(GraphUtils.getSourcesVertices(dependencyGraph));
        scheduleAllAvailiable(scheduled);
    }

    private boolean isEnoughResources(Configuration configuration) {
        return configuration.getTasks().stream()
                .allMatch(this::isAbleRunning);

    }

    private boolean hasCyrcularDependency(DirectedGraph<Task, DefaultEdge> dependencyGraph) {
        return GraphUtils.hasCycle(dependencyGraph);
    }

    private void scheduleAllAvailiable(Queue<Task> scheduled) {
        for (Task t : scheduled) {
            if (isAbleRunning(t)) {

            }
        }

    }

    private boolean isAbleRunning(Task t) {
        return t.getCpu() <= cpus && t.getDisks() <= disks && t.getMemory() <= memory;
    }

    private void loadResources(Configuration configuration) {
        cpus = configuration.getCpus();
        memory = configuration.getMemory();
        disks = configuration.getDisks();
    }

    private DirectedGraph<Task, DefaultEdge> buildGraphFrom(Configuration configuration) {
        DirectedGraph<Task, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        configuration.getTasks().forEach(g::addVertex);
        configuration.getTasks().forEach(task -> configuration.getDependenciesOf(task).forEach(d -> g.addEdge(d, task)));
        return g;
    }
}
