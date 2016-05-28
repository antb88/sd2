import cs.technion.ac.il.sd.app.Configuration;
import cs.technion.ac.il.sd.app.Task;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tests fpr {@link Configuration}
 */
public class ConfigurationTest {


    private Configuration $;


    private Configuration parseFile(String name) {
        $ = Configuration.fromFile(new File(getClass().getResource(name + ".txt").getFile()));
        return $;
    }

    private Task find(String name) {
        return $.getTask(name).get();
    }

    private boolean depends(String who, String onWhom) {
        return $.getDependenciesOf(find(who)).contains(find(onWhom));
    }

    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    @Test
    public void containsAllTasks() {
        parseFile("small");
        Set<Task> tasks = $.getTasks();
        Assert.assertTrue("should contain main", tasks.contains(find("main")));
        Assert.assertTrue("should contain f1", tasks.contains(find("f1")));
        Assert.assertTrue("should contain f2", tasks.contains(find("f2")));
        Assert.assertTrue("should contain f3", tasks.contains(find("f3")));
        Assert.assertTrue("should contain exactly 4", tasks.size() == 4);
    }

    @Test
    public void totalResourcesAreCorrect() {
        parseFile("small");
        Assert.assertEquals(10, $.getCpus());
        Assert.assertEquals(11, $.getMemory());
        Assert.assertEquals(12, $.getDisks());
    }

    @Test
    public void definedTaskResourcesAreCorrect() {
        parseFile("small");
        Assert.assertEquals(1, find("main").getCpu());
        Assert.assertEquals(2, find("main").getMemory());
        Assert.assertEquals(3, find("main").getDisks());
        Assert.assertEquals(4, find("main").getPriority());
    }

    @Test
    public void defaultTaskResourcesAreCorrect() {
        parseFile("small");
        Assert.assertEquals(0, find("f1").getCpu());
        Assert.assertEquals(0, find("f1").getMemory());
        Assert.assertEquals(0, find("f1").getDisks());
        Assert.assertEquals(0, find("f1").getPriority());

        Assert.assertEquals(0, find("f2").getCpu());
        Assert.assertEquals(0, find("f2").getMemory());
        Assert.assertEquals(0, find("f2").getDisks());
        Assert.assertEquals(0, find("f2").getPriority());

        Assert.assertEquals(0, find("f3").getCpu());
        Assert.assertEquals(0, find("f3").getMemory());
        Assert.assertEquals(0, find("f3").getDisks());
        Assert.assertEquals(0, find("f3").getPriority());
    }

    @Test
    public void definedTaskDependenciesAreCorrect() {
        parseFile("small");
        Assert.assertEquals(3, $.getDependenciesOf("main").size());
        Assert.assertTrue($.getDependenciesOf("main").contains(find("f1")));
        Assert.assertTrue($.getDependenciesOf("main").contains(find("f2")));
        Assert.assertTrue($.getDependenciesOf("main").contains(find("f3")));
    }

    @Test
    public void defaultTaskDependenciesAreCorrect() {
        parseFile("small");
        Assert.assertEquals(0, $.getDependenciesOf("f1").size());
        Assert.assertEquals(0, $.getDependenciesOf("f2").size());
        Assert.assertEquals(0, $.getDependenciesOf("f3").size());
    }


    @Test
    public void complexContainsAllTasks() {
        parseFile("complex");
        Set<Task> tasks = $.getTasks();
        Assert.assertTrue("should contain f1", tasks.contains(find("f1")));
        Assert.assertTrue("should contain a", tasks.contains(find("a")));
        Assert.assertTrue("should contain b", tasks.contains(find("b")));
        Assert.assertTrue("should contain c", tasks.contains(find("c")));
        Assert.assertTrue("should contain d", tasks.contains(find("d")));
        Assert.assertTrue("should contain e", tasks.contains(find("e")));
        Assert.assertTrue("should contain f", tasks.contains(find("f")));
        Assert.assertTrue("should contain g", tasks.contains(find("g")));
        Assert.assertTrue("should contain h", tasks.contains(find("h")));
        Assert.assertTrue("should contain i", tasks.contains(find("i")));
        Assert.assertTrue("should contain j", tasks.contains(find("j")));
        Assert.assertEquals("should contain exactly 11", tasks.size(),11);
    }

    @Test
    public void complexTotalResourcesAreCorrect() {
        parseFile("complex");
        Assert.assertEquals(7, $.getCpus());
        Assert.assertEquals(70, $.getMemory());
        Assert.assertEquals(15, $.getDisks());
    }

    @Test
    public void complexTaskResourcesAreCorrect() {
        parseFile("complex");
        Assert.assertEquals(0, find("f1").getCpu());
        Assert.assertEquals(0, find("f1").getMemory());
        Assert.assertEquals(0, find("f1").getDisks());
        Assert.assertEquals(0, find("f1").getPriority());

        Assert.assertEquals(1, find("a").getCpu());
        Assert.assertEquals(1, find("a").getMemory());
        Assert.assertEquals(1, find("a").getDisks());
        Assert.assertEquals(1, find("a").getPriority());

        Assert.assertEquals(2, find("b").getCpu());
        Assert.assertEquals(2, find("b").getMemory());
        Assert.assertEquals(2, find("b").getDisks());
        Assert.assertEquals(2, find("b").getPriority());

        Assert.assertEquals(3, find("c").getCpu());
        Assert.assertEquals(3, find("c").getMemory());
        Assert.assertEquals(3, find("c").getDisks());
        Assert.assertEquals(3, find("c").getPriority());

        Assert.assertEquals(4, find("d").getCpu());
        Assert.assertEquals(4, find("d").getMemory());
        Assert.assertEquals(4, find("d").getDisks());
        Assert.assertEquals(4, find("d").getPriority());

        Assert.assertEquals(5, find("e").getCpu());
        Assert.assertEquals(5, find("e").getMemory());
        Assert.assertEquals(5, find("e").getDisks());
        Assert.assertEquals(5, find("e").getPriority());

        Assert.assertEquals(6, find("f").getCpu());
        Assert.assertEquals(6, find("f").getMemory());
        Assert.assertEquals(6, find("f").getDisks());
        Assert.assertEquals(6, find("f").getPriority());


        Assert.assertEquals(7, find("g").getCpu());
        Assert.assertEquals(7, find("g").getMemory());
        Assert.assertEquals(7, find("g").getDisks());
        Assert.assertEquals(7, find("g").getPriority());


        Assert.assertEquals(8, find("h").getCpu());
        Assert.assertEquals(8, find("h").getMemory());
        Assert.assertEquals(8, find("h").getDisks());
        Assert.assertEquals(8, find("h").getPriority());


        Assert.assertEquals(9, find("i").getCpu());
        Assert.assertEquals(9, find("i").getMemory());
        Assert.assertEquals(9, find("i").getDisks());
        Assert.assertEquals(9, find("i").getPriority());

        Assert.assertEquals(10, find("j").getCpu());
        Assert.assertEquals(10, find("j").getMemory());
        Assert.assertEquals(10, find("j").getDisks());
        Assert.assertEquals(10, find("j").getPriority());

    }

    @Test
    public void complexTaskDependenciesAreCorrect() {
        parseFile("complex");
        Assert.assertEquals(Collections.EMPTY_SET
                , $.getDependenciesOf("f1").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Collections.EMPTY_SET
                , $.getDependenciesOf("a").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Arrays.asList("f1").stream().collect(Collectors.toSet())
                , $.getDependenciesOf("b").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Collections.EMPTY_SET
                , $.getDependenciesOf("c").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Collections.EMPTY_SET
                , $.getDependenciesOf("d").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Arrays.asList("j","a","f1").stream().collect(Collectors.toSet())
                , $.getDependenciesOf("e").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Arrays.asList("a").stream().collect(Collectors.toSet())
                , $.getDependenciesOf("f").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Arrays.asList("h","j","i").stream().collect(Collectors.toSet())
                , $.getDependenciesOf("g").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Collections.EMPTY_SET
                , $.getDependenciesOf("h").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Collections.EMPTY_SET
                , $.getDependenciesOf("i").stream().map(Task::getName).collect(Collectors.toSet()));
        Assert.assertEquals(Collections.EMPTY_SET
                , $.getDependenciesOf("j").stream().map(Task::getName).collect(Collectors.toSet()));
    }

    @Test
    public void emptyContainsNoTasks() {
        parseFile("empty");
        Assert.assertEquals($.getTasks(),Collections.EMPTY_SET);
    }

    @Test
    public void emptyTotalResourcesAreCorrect() {
        parseFile("empty");
        Assert.assertEquals(1, $.getCpus());
        Assert.assertEquals(1024, $.getMemory());
        Assert.assertEquals(2048, $.getDisks());
    }


}
