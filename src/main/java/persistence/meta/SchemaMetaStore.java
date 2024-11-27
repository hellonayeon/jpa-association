package persistence.meta;

import java.util.HashMap;
import java.util.Map;

public class SchemaMetaStore {

    private static final Map<Class<?>, SchemaMeta> schemaMetaMap = new HashMap<>();

    private SchemaMetaStore() {

    }

    public static SchemaMeta get(Class<?> clazz) {
        if (notContainsKey(clazz)) {
            schemaMetaMap.put(clazz, new SchemaMeta(clazz));
        }
        return schemaMetaMap.get(clazz);
    }

    private static boolean notContainsKey(Class<?> clazz) {
        return !schemaMetaMap.containsKey(clazz);
    }

}
