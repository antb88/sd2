package cs.technion.ac.il.sd.app;

/**
 * Represents a runnable Task
 */
public class Task  implements Comparable{

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

    public int getCpu() {
        return cpu;
    }

    public int getMemory() {
        return memory;
    }

    public int getDisks() {
        return disks;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Task)) return false;
        Task other = (Task)obj;
        return name == other.name && cpu == other.cpu && memory == other.memory && disks == other.disks && priority == other.priority;

    }

    @Override
    public int compareTo(Object o) {
        Task other = (Task) o;
        return getPriority() < other.getPriority() ? -1 : 1;
    }
}
