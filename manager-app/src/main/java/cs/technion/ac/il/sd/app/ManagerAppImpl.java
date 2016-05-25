package cs.technion.ac.il.sd.app;

import com.google.inject.Inject;
import cs.technion.ac.il.sd.ExternalManager;
import cs.technion.ac.il.sd.ManagerFactory;
import cs.technion.ac.il.sd.library.GraphUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by ant on 5/23/16.
 */
public class ManagerAppImpl implements ManagerApp {

    private final ManagerFactory factory;
    private ExternalManager manager;
    private int cpus;
    private int memory;
    private int disks;
    private DirectedGraph<Task, DefaultEdge> dependencyGraph;

    private Queue<Task> readyToRun;
    private List<Task> scheduled = new ArrayList<>();
    @Inject
    public ManagerAppImpl(ManagerFactory factory) {
        this.factory = factory;

    }

    @Override
    public void processFile(File file) {

        Configuration configuration = Configuration.fromFile(file);
        dependencyGraph = buildGraphFrom(configuration);
        loadResources(configuration);
        manager = factory.create(cpus, memory, disks);

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
        readyToRun = new PriorityBlockingQueue<>(10, (t1, t2) -> t1.getPriority() < t2.getPriority() ? -1 : 1);

        readyToRun.addAll(GraphUtils.getSourcesVertices(dependencyGraph));
        scheduleAllAvailiable();
    }

    private boolean isEnoughResources(Configuration configuration) {
        return configuration.getTasks().stream()
                .allMatch(this::isAbleRunning);

    }

    private boolean hasCyrcularDependency(DirectedGraph<Task, DefaultEdge> dependencyGraph) {
        return GraphUtils.hasCycle(dependencyGraph);
    }

    private synchronized void scheduleAllAvailiable() { //TODO - handle synchronization - currently just added synchronized to some methods
        readyToRun.forEach(this::runIfPossible);
        readyToRun.stream().filter(t -> scheduled.contains(t));
    }

    private synchronized void runIfPossible(Task task){if (isAbleRunning(task)) {run(task);}}

    private void run(Task task)
    {
        useResources(task);
        scheduled.add(task);
        manager.run(task.getName(), task.getCpu(), task.getMemory(), task.getDisks(), () -> taskDone(task));
    }

    private synchronized void taskDone(Task task)
    {
        restoreResources(task);
        dependencyGraph.removeVertex(task);
        readyToRun.addAll(GraphUtils.getSourcesVertices(dependencyGraph));
        scheduleAllAvailiable();
    }

    private void useResources(Task task)
    {
        cpus -= task.getCpu();
        memory -= task.getMemory();
        disks -= task.getDisks();
    }

    private void restoreResources(Task task)
    {
        cpus += task.getCpu();
        memory += task.getMemory();
        disks += task.getDisks();
    }

    private boolean isAbleRunning(Task t) {//TODO - added scheduled patch because synchronization not handled yet
        return !scheduled.contains(t) && t.getCpu() <= cpus && t.getDisks() <= disks && t.getMemory() <= memory;
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
