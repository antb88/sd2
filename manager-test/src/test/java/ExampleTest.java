import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import cs.technion.ac.il.sd.ExternalManager;
import cs.technion.ac.il.sd.ManagerFactory;
import cs.technion.ac.il.sd.app.ManagerApp;
import cs.technion.ac.il.sd.app.ManagerModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ExampleTest {
  private final ExternalManager mock = spy(new ExternalManager() {
    @Override
    public void fail() {}
  });
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

  private static class ManagerWithoutCallback {
    private final ExternalManager manager;

    public ManagerWithoutCallback(ExternalManager manager) {
      this.manager = manager;
    }

    public void run(String name, int cpu, int mem, int disk) {
      manager.run(eq(name), eq(cpu), eq(mem), eq(disk), any(Runnable.class));
    }
  }

  private static ManagerWithoutCallback ignoringCallback(ExternalManager managerVerifier) {
    return new ManagerWithoutCallback(managerVerifier);
  }

  @Rule
  public Timeout globalTimeout = Timeout.seconds(10);

  @Test
  public void testSimple() {
    processFile("simple");
    ignoringCallback(verify(mock)).run("main", 1, 1024, 2048);
    verifyNoMoreInteractions(mock);
  }

  @Test
  public void testDep() throws Exception {
    class Context {
      boolean aHasFinished = false;
    }
    Context context = new Context();
    Mockito.doAnswer(e -> {
      context.aHasFinished = true;
      e.callRealMethod();
      return null;
    }).when(mock).run(eq("a"), eq(0), eq(0), eq(0), any(Runnable.class));
    // ensure b is called only after a finishes
    Mockito.doAnswer(e -> {
      if (!context.aHasFinished)
        throw new IllegalStateException("a hasn't finished yet");
      e.callRealMethod();
      return null;
    }).when(mock).run(eq("b"), eq(1), eq(0), eq(0), any(Runnable.class));
    processFile("callback");
    Thread.sleep(2500);
    ignoringCallback(verify(mock)).run("b", 1, 0, 0);
  }

  @Test
  public void testNotEnoughResources() throws Exception {
    processFile("noResources");
    verify(mock).fail();
    verifyNoMoreInteractions(mock);
  }

  @Test
  public void parallel() throws Exception {
    processFile("parallel");
    Thread.sleep(1500);
    for (int i = 0; i < 10; i++)
      ignoringCallback(verify(mock)).run(String.valueOf((char)('a' + i)), 1, 1, 1);
  }

  @Test
  public void priority() throws Exception {
    processFile("priority");
    InOrder inOrder = inOrder(mock);
    ignoringCallback(inOrder.verify(mock)).run("b", 1, 0, 0);
    ignoringCallback(inOrder.verify(mock)).run("a", 1, 0, 0);
    verifyNoMoreInteractions(mock);
  }
}
