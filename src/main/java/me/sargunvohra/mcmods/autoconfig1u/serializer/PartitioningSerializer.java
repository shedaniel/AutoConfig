package me.sargunvohra.mcmods.autoconfig1u.serializer;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This serializer wraps another serializer and produces a folder with each field of the config
 * corresponding to a single config file.
 * The top level config must inherit from GlobalData.
 * Each field of the top level config must be of a type inheriting from ConfigData.
 */
public final class PartitioningSerializer<T extends PartitioningSerializer.GlobalData, M extends ConfigData> implements ConfigSerializer<T> {

    private Class<T> configClass;
    private Map<Field, ConfigSerializer<M>> serializers;

    private PartitioningSerializer(Config definition, Class<T> configClass, ConfigSerializer.Factory<M> factory) {
        this.configClass = configClass;

        //noinspection unchecked
        serializers = getModuleFields(configClass).stream()
            .collect(
                Utils.toLinkedMap(
                    Function.identity(),
                    field -> factory.create(
                        createDefinition(
                            String.format(
                                "%s/%s",
                                definition.name(),
                                field.getType().getAnnotation(Config.class).name()
                            )
                        ),
                        (Class<M>) field.getType()
                    )
                )
            );
    }

    public static <T extends PartitioningSerializer.GlobalData, M extends ConfigData>
    ConfigSerializer.Factory<T> wrap(ConfigSerializer.Factory<M> inner) {
        return (definition, configClass) -> new PartitioningSerializer<>(definition, configClass, inner);
    }

    private static Config createDefinition(String name) {
        return new Config() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Config.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public int hashCode() {
                return ("name".hashCode() * 127) ^ name().hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Config && ((Config) obj).name().equals(name());
            }
        };
    }

    private static boolean isValidModule(Field field) {
        return ConfigData.class.isAssignableFrom(field.getType())
            && field.getType().isAnnotationPresent(Config.class);
    }

    private static List<Field> getModuleFields(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredFields())
            .filter(PartitioningSerializer::isValidModule)
            .collect(Collectors.toList());
    }

    @Override
    public void serialize(T config) throws SerializationException {
        for (Map.Entry<Field, ConfigSerializer<M>> entry : serializers.entrySet()) {
            entry.getValue().serialize(Utils.getUnsafely(entry.getKey(), config));
        }
    }

    @Override
    public T deserialize() throws SerializationException {
        T ret = createDefault();
        for (Map.Entry<Field, ConfigSerializer<M>> entry : serializers.entrySet()) {
            Utils.setUnsafely(entry.getKey(), ret, entry.getValue().deserialize());
        }
        return ret;
    }

    @Override
    public T createDefault() {
        return Utils.constructUnsafely(configClass);
    }

    public static abstract class GlobalData implements ConfigData {

        public GlobalData() {
            Arrays.stream(getClass().getDeclaredFields())
                .filter(field -> !isValidModule(field))
                .forEach(field -> {
                    throw new RuntimeException(String.format("Invalid module: %s", field));
                });
        }

        @Override
        final public void validatePostLoad() throws ValidationException {
            for (Field moduleField : getModuleFields(getClass())) {
                ((ConfigData) Utils.getUnsafely(moduleField, this)).validatePostLoad();
            }
        }
    }
}
