import cs.technion.ac.il.sd.app.Configuration;
import cs.technion.ac.il.sd.app.Task;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.util.Set;

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
        Assert.assertTrue("should contain exactly 2", tasks.size() == 4);
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


}
