package cs.technion.ac.il.sd.app;

import com.google.inject.Inject;
import cs.technion.ac.il.sd.ExternalManager;
import cs.technion.ac.il.sd.ManagerFactory;

import java.io.File;

public class FakeManager implements ManagerApp {
  private static final Runnable PASS = () -> {
  };
  private final ExternalManager external;

  @Inject
  public FakeManager(ManagerFactory factory) {
    this.external = factory.create(0, 0, 0);
  }

  @Override
  public void processFile(File file) {
    switch (file.getName()) {
      case "simple.txt":
        external.run("main", 1, 1024, 2048, PASS);
        break;
      case "callback.txt":
        external.run("a", 0, 0, 0, () -> external.run("b", 1, 0, 0, PASS));
        break;
      case "noResources.txt":
        external.fail();
        break;
      case "parallel.txt":
        for (int i = 0; i < 10; i++)
          external.run(String.valueOf('a' + i), 1, 1, 1, PASS);
        break;
      case "priority.txt":
        external.run("b", 1, 0, 0, PASS);
        external.run("a", 1, 0, 0, PASS);
        break;
      default:
        throw new UnsupportedOperationException("http://i.imgflip.com/112boa.jpg");
    }
  }
}
