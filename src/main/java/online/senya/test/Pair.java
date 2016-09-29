package online.senya.test;

/**
 * Created by dsv on 9/28/16.
 */
public class Pair<F, S> implements Pool.Poolable<Pair<F, S>> {

    private Pool<Pair<F, S>> pairPool;

    public F first;

    public S second;

    public void setPool(final Pool<Pair<F, S>> pool) {
        this.pairPool = pool;
    }

    public void release() {
        first = null;
        second = null;
        pairPool.release(this);
    }


}
