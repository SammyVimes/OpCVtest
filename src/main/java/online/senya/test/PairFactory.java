package online.senya.test;

/**
 * Created by dsv on 9/28/16.
 */
public class PairFactory implements Pool.PoolFactory<Pair<Object, Object>> {

    public Pair<Object, Object> newObject(Pool<Pair<Object, Object>> pool) {
        Pair<Object, Object> objectObjectPair = new Pair<Object, Object>();
        objectObjectPair.setPool(pool);
        return objectObjectPair;
    }

}
