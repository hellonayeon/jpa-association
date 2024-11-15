package persistence.sql.ddl.query;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import persistence.exception.NotExistException;
import persistence.util.PackageScanner;
import persistence.validator.AnnotationValidator;

public class Association {

    private static final Association INSTANCE = new Association();
    private static final String SCAN_PACKAGE_NAME = "sample";

    private static Map<Class<?>, ForeignKeyConstraint> foreignKeyConstraints;

    private Association() {
        List<Class<?>> classes = getClasses();

        foreignKeyConstraints = new HashMap<>();
        addOneToManyForeignKeyConstraint(classes);
    }

    public static void addOneToManyForeignKeyConstraint(List<Class<?>> classes) {
        Map<Class<?>, Optional<Field>> associationFields = classes.stream()
                .collect(Collectors.toMap(
                        clazz -> clazz,
                        clazz -> Arrays.stream(clazz.getDeclaredFields())
                                .filter(field -> AnnotationValidator.isPresent(field, OneToMany.class))
                                .filter(field -> AnnotationValidator.isPresent(field, JoinColumn.class))
                                .findFirst()
                ));

        for (Class<?> clazz : associationFields.keySet()) {
            Optional<Field> associationField = associationFields.get(clazz);
            if (associationField.isEmpty()) {
                continue;
            }

            Class<?> associationClass = null;
            Type genericType = associationField.get().getGenericType();
            if (genericType instanceof ParameterizedType parameterizedType) {
                Type actualType = parameterizedType.getActualTypeArguments()[0]; // 첫 번째 제네릭 타입

                if (actualType instanceof Class<?>) {
                    associationClass = (Class<?>) actualType;
                    System.out.println("The type of orderItems is: " + clazz.getName());
                }
            }
            JoinColumn joinColumnAnnotation = associationField.get().getAnnotation(JoinColumn.class);
            Field idField = Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Id.class))
                    .findFirst()
                    .orElseThrow(() -> new NotExistException("@Id field."));
            foreignKeyConstraints.put(associationClass, new SoftForeignKeyConstraint(idField, joinColumnAnnotation.name()));
        }
    }

    private List<Class<?>> getClasses() {
        try {
            return PackageScanner.scan(SCAN_PACKAGE_NAME);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
