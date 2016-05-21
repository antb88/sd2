package cs.technion.ac.il.sd.app;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration - representing a Configuration file of Tasks
 * with their dependencies and resources
 */
public class Configuration {

    private int cpus;
    private int memory;
    private int disks;
    private HashMap<String, Task> nameToTask;
    private HashMap<String, List<String>> nameToDepNames;

    private Configuration() {
        this.nameToTask = new HashMap<>();
        this.nameToDepNames = new HashMap<>();

    }

    public static Configuration fromFile(File file) {

        Configuration c = new Configuration();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String[] resources = br.readLine().split(",\\s");
            c.setCpus(Integer.parseInt(resources[0]))
                    .setMemory(Integer.parseInt(resources[1]))
                    .setDisks(Integer.parseInt(resources[2]));

            br.lines().forEach(l -> {
                String trm = l.trim();
                if (!trm.equals(""))
                    c.parseLine(trm);
            });
        } catch (IOException e) {
            throw new AssertionError();
        }
        return c;
    }

    private void parseLine(String line) {
        String[] args = line.replaceAll(",|\\(|\\)|:", " ").split("\\s+");
        String task = args[0];
        List<String> deps = args.length >= 5 ?
                Lists.newArrayList(Arrays.copyOfRange(args, 1, args.length - 4)) : Lists.newArrayList();
        List<Integer> resources = Lists.newArrayList(Arrays.copyOfRange(args, args.length - 4, args.length))
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        nameToTask.put(task, new Task(task, resources.get(0), resources.get(1), resources.get(2), resources.get(3)));

        for (String d : deps) {
            nameToTask.putIfAbsent(d, new Task(d, 0, 0, 0, 0));
            nameToDepNames.putIfAbsent(d, new ArrayList<>());
        }
        nameToDepNames.put(task, deps);
    }

    public Optional<Task> getTask(String name) {
        return Optional.ofNullable(nameToTask.get(name));
    }

    public Set<Task> getTasks() {
        return nameToTask.values()
                .stream()
                .collect(Collectors.toSet());
    }

    public Set<Task> getDepenciesOf(Task task) {
        return nameToDepNames.get(task.getName())
                .stream()
                .map(nameToTask::get)
                .collect(Collectors.toSet());
    }

    private Configuration setCpus(int cpus) {
        this.cpus = cpus;
        return this;
    }

    private Configuration setMemory(int memory) {
        this.memory = memory;
        return this;
    }

    public Configuration setDisks(int disks) {
        this.disks = disks;
        return this;
    }

}
