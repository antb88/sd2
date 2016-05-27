package cs.technion.ac.il.sd.app;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import cs.technion.ac.il.sd.ExternalManager;
import cs.technion.ac.il.sd.ManagerFactory;
import cs.technion.ac.il.sd.library.GraphUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * ManagerApp Implementation
 */

public class ManagerAppImpl implements ManagerApp {

    private final ManagerFactory factory;

    private ExternalManager manager;
    private int cpus;
    private int memory;
    private int disks;
    private DirectedGraph<Task, DefaultEdge> dependencyGraph;

    private Queue<Task> readyToRun;
    private LinkedBlockingQueue<Task> calledBack;
    private Set<Task> running;
    private Set<Task> complete;
    private int totalTasks;

    @Inject
    public ManagerAppImpl(ManagerFactory factory) {
        this.factory = factory;
        this.readyToRun = new PriorityQueue<>();
        this.running = new HashSet<>();
        this.complete = new HashSet<>();
        this.calledBack = new LinkedBlockingQueue<>();
        this.dependencyGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    @Override
    public void processFile(File file) {

        Configuration configuration = Configuration.fromFile(file);
        loadResources(configuration);
        buildGraph(configuration);

        if (canProcess(configuration)) {
            process();
        } else {
            fail();
        }
    }

    private boolean canProcess(Configuration configuration) {
        return isEnoughResources(configuration) && !hasCircularDependency(dependencyGraph);
    }

    private void fail() {
        factory.create(0, 0, 0).fail();
    }

    private void process() {
        manager = factory.create(cpus, memory, disks);
        readyToRun.addAll(GraphUtils.getSourcesVertices(dependencyGraph));

        while (!allTasksLaunched()) {
            runAvailable();
            try {
                onTaskDone(calledBack.take());
            } catch (InterruptedException e) {
                throw new AssertionError("interrupted while waiting for callback");
            }
        }
    }

    private boolean allTasksLaunched() {
        return Sets.union(complete, running).size() == totalTasks;
    }

    private boolean isEnoughResources(Configuration configuration) {
        return configuration.getTasks().stream()
                .allMatch(this::isAbleToRun);
    }

    private boolean hasCircularDependency(DirectedGraph<Task, DefaultEdge> dependencyGraph) {
        return GraphUtils.hasCycle(dependencyGraph);
    }

    private void runAvailable() {

        Set<Task> newRunning = readyToRun.stream()
                .map(this::runIfPossible)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        newRunning.forEach( t-> {
            readyToRun.remove(t);
            running.add(t);
        });
    }

    private Optional<Task> runIfPossible(Task task) {
        if (isAbleToRun(task)) {
            run(task);
            return Optional.of(task);
        }
        return Optional.empty();
    }

    private void run(Task task) {
        useResources(task);
        manager.run(task.getName(), task.getCpu(), task.getMemory(), task.getDisks(), () -> callback(task));
    }

    private void callback(Task task) {
        try {
            calledBack.put(task);
        } catch (InterruptedException e) {
            throw new AssertionError("interrupted while posting callback");
        }
    }

    private void onTaskDone(Task task) {
        restoreResources(task);
        running.remove(task);
        complete.add(task);
        dependencyGraph.removeVertex(task);
        Set<Task> newReadyToRun = GraphUtils.getSourcesVertices(dependencyGraph)
                .stream()
                .filter(this::taskNotProcessed)
                .collect(Collectors.toSet());
        readyToRun.addAll(newReadyToRun);
    }

    private boolean taskNotProcessed(Task t) {
        return !readyToRun.contains(t) && !running.contains(t) && !complete.contains(t);
    }

    private void useResources(Task task) {
        cpus -= task.getCpu();
        memory -= task.getMemory();
        disks -= task.getDisks();
    }

    private void restoreResources(Task task) {
        cpus += task.getCpu();
        memory += task.getMemory();
        disks += task.getDisks();
    }

    private boolean isAbleToRun(Task t) {
        return t.getCpu() <= cpus && t.getDisks() <= disks && t.getMemory() <= memory;
    }

    private void loadResources(Configuration configuration) {
        totalTasks = configuration.getTasks().size();
        cpus = configuration.getCpus();
        memory = configuration.getMemory();
        disks = configuration.getDisks();
    }

    private DirectedGraph<Task, DefaultEdge> buildGraph(Configuration configuration) {
        configuration.getTasks().forEach(dependencyGraph::addVertex);
        configuration.getTasks()
                .forEach(task -> configuration.getDependenciesOf(task)
                        .forEach(d -> dependencyGraph.addEdge(d, task)));
        return dependencyGraph;
    }
}
