package cs.technion.ac.il.sd.library.dependency;

import java.util.Set;

/**
 * Created by ant on 5/28/16.
 */
public interface DependencyManager<T> {

    Set<T> resolve(T entity);

    boolean isResolvable(T entity);

    boolean isResolved(T entity);

    boolean isResolvable();

    Set<T> getAllResolvable();

    Set<T> getDependantsOn(T entity);

    Set<T> getDependenciesOf(T entity);

    Set<T> getAllResolved();


}
