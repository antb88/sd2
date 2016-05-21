package cs.technion.ac.il.sd.app;

/**
 * Created by ant on 5/21/16.
 */
public class Task {

    private final int cpu;
    private final int memory;
    private final int disks;
    private final int priority;
    private final String name;

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", cpu=" + cpu +
                ", memory=" + memory +
                ", disks=" + disks +
                ", priority=" + priority +
                '}';
    }

    public Task(String name, int cpu, int memory, int disks, int priority) {
        this.name = name;
        this.cpu = cpu;
        this.memory = memory;
        this.disks = disks;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }
}