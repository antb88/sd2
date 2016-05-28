import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import cs.technion.ac.il.sd.ExternalManager;
import cs.technion.ac.il.sd.ManagerFactory;
import cs.technion.ac.il.sd.app.ManagerApp;
import cs.technion.ac.il.sd.app.ManagerAppImpl;
import cs.technion.ac.il.sd.app.ManagerModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ManagerAppImpl}
 */
public class ManagerAppTest {

    private final ExternalManager manager = new ExternalManager() {
        @Override
        public void run(String name, int cpus, int memory, int disk, Runnable callback) {
            try {
                Thread.sleep(100L);
                callback.run();
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }
        }

        @Override
        public void fail() {
        }
    };

    private final ExternalManager mock = spy(manager);

    private final Injector injector = Guice.createInjector(new ManagerModule(), new AbstractModule() {
        @Override
        protected void configure() {
            bind(ManagerFactory.class).toInstance((a, b, c) -> mock);
        }
    });

    private final ManagerApp $ = injector.getInstance(ManagerApp.class);

    private void processFile(String name) {
        $.processFile(new File(getClass().getResource(name + ".txt").getFile()));
    }

    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

    @Test
    public void emptyFile() throws InterruptedException {
        processFile("empty");
        Thread.sleep(200);
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void oneByOneByPriority() throws InterruptedException {
        processFile("onebyone");
        Thread.sleep(300);
        InOrder order = inOrder(mock);
        order.verify(mock).run(eq("a"), eq(6), eq(5), eq(5), anyObject());
        order.verify(mock).run(eq("b"), eq(5), eq(6), eq(5), anyObject());
        order.verify(mock).run(eq("c"), eq(5), eq(5), eq(6), anyObject());
        order.verify(mock).run(eq("d"), eq(6), eq(4), eq(4), anyObject());
        order.verify(mock).run(eq("e"), eq(5), eq(5), eq(5), anyObject());
        order.verifyNoMoreInteractions();
    }

    @Test
    public void priorityWithDependencyOrderCorrect() throws InterruptedException {

        processFile("priodep");
        Thread.sleep(300);
        InOrder order = inOrder(mock);
        order.verify(mock).run(eq("a"), eq(0), eq(0), eq(0), anyObject());
        order.verify(mock).run(eq("b"), eq(1), eq(0), eq(0), anyObject());
        order.verify(mock).run(eq("c"), eq(1), eq(0), eq(0), anyObject());
        order.verify(mock).run(eq("main"), eq(1), eq(1), eq(1), anyObject());
        order.verifyNoMoreInteractions();
    }

    @Test
    public void priorityWithDependencyDepCorrect() throws InterruptedException {

        class Context {
            private boolean aDone = false;
            private boolean bDone = false;
        }
        Context context = new Context();
        Mockito.doAnswer(e -> {
            context.aDone = true;
            e.callRealMethod();
            return null;
        }).when(mock).run(eq("a"), eq(0), eq(0), eq(0), any(Runnable.class));
        Mockito.doAnswer(e -> {
            if (!context.aDone)
                throw new IllegalStateException("a hasn't finished yet");
            context.bDone = true;
            e.callRealMethod();
            return null;
        }).when(mock).run(eq("b"), eq(1), eq(0), eq(0), any(Runnable.class));

        Mockito.doAnswer(e -> {
            if (!context.aDone || !context.bDone)
                throw new IllegalStateException("a hasn't finished yet");
            e.callRealMethod();
            return null;
        }).when(mock).run(eq("main"), eq(1), eq(0), eq(0), any(Runnable.class));
        processFile("priodep");
        Thread.sleep(300);
        verify(mock).run(eq("main"), eq(1), eq(1), eq(1), anyObject());
    }

    @Test
    public void circularFails() throws InterruptedException {
        processFile("circular");
        Thread.sleep(300);
        verify(mock).fail();
    }
}