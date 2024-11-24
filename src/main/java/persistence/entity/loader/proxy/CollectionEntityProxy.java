package persistence.entity.loader.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import persistence.entity.loader.EntityCollectionLoader;
import persistence.meta.ColumnMeta;

public class CollectionEntityProxy implements InvocationHandler {

    private final ColumnMeta columnMeta;
    private final Object id;
    private List<?> target;
    private final EntityCollectionLoader entityLoader;

    public CollectionEntityProxy(ColumnMeta columnMeta, Object id, EntityCollectionLoader entityLoader) {
        this.columnMeta = columnMeta;
        this.id = id;
        this.entityLoader = entityLoader;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (targetIsNull()) {
            target = entityLoader.load(columnMeta.relationMeta().joinColumnType(), columnMeta.field(), id);
        }
        return method.invoke(target, args);
    }

    public static List<?> createProxy(ColumnMeta columnMeta, Object id, EntityCollectionLoader entityLoader) {
        return (List<?>) Proxy.newProxyInstance(
                        columnMeta.field().getType().getClassLoader(),
                        new Class[]{List.class},
                        new CollectionEntityProxy(columnMeta, id, entityLoader));
    }

    private boolean targetIsNull() {
        return target == null;
    }

}
