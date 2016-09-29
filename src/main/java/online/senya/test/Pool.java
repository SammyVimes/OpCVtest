package online.senya.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dsv on 9/28/16.
 */
public class Pool<T extends Pool.Poolable<T>> {

    public void release(final T pair) {
        this.poolables.add(pair);
    }

    public interface PoolFactory<T extends Poolable<T>> {

        T newObject(final Pool<T> pool);

    }

    public interface Poolable<T extends Poolable<T>> {

        void setPool(final Pool<T> pool);

        void release();

    }

    private List<T> poolables = new ArrayList<T>();

    private PoolFactory<T> factory;

    public Pool(PoolFactory<T> factory) {
        this.factory = factory;
    }

    public T get() {
        if (poolables.size() > 0) {
            return poolables.remove(0);
        } else {
            return factory.newObject(this);
        }
    }

}
