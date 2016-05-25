package cs.technion.ac.il.sd.app;

import com.google.inject.AbstractModule;

public class ManagerModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ManagerApp.class).to(ManagerAppImpl.class);
  }
}
