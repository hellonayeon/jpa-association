package persistence.sql.metadata;

public record ColumnAlias(String value) {

    public ColumnAlias(Class<?> clazz) {
        this(makeAlias(clazz));
    }

    private static String makeAlias(Class<?> clazz) {
        String className = clazz.getSimpleName();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < className.length(); i++) {
            char c = className.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

}
