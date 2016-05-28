import com.google.common.collect.Sets;
import cs.technion.ac.il.sd.library.dependency.DependencyManager;
import cs.technion.ac.il.sd.library.dependency.DependencyManagerBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by ant on 5/28/16.
 */

@SuppressWarnings("unchecked")
public class DependencyManagerTest {

    private DependencyManager<Integer> $;

    private DependencyManager createSimple() {
        return DependencyManagerBuilder.newBuilder()
                .addDependency(1,2)
                .addDependency(2,3)
                .build();
    }


    @Test
    public void isResolvable() {
        $ = createSimple();
        Assert.assertTrue($.isResolvable());
        Assert.assertTrue($.isResolvable(3));
        Assert.assertFalse($.isResolvable(2));
        Assert.assertFalse($.isResolvable(1));
    }

    @Test
    public void resolving() {
        $ = createSimple();
        Set<Integer> resolved = Sets.newHashSet();
        Assert.assertEquals($.getAllResolved(), resolved);
        $.resolve(3);
        resolved.add(3);
        Assert.assertEquals($.getAllResolved(), resolved);
        $.resolve(2);
        resolved.add(2);
        Assert.assertEquals($.getAllResolved(), resolved);
        Assert.assertTrue($.isResolvable(1));
        $.resolve(1);
        resolved.add(1);
        Assert.assertEquals($.getAllResolved(), resolved);
    }




}