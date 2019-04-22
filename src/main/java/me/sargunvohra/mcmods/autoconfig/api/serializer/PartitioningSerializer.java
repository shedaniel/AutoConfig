package me.sargunvohra.mcmods.autoconfig.api.serializer;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigSerializer;
import me.sargunvohra.mcmods.autoconfig.impl.Utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PartitioningSerializer<T extends PartitioningSerializer.GlobalData, M extends ConfigData> implements ConfigSerializer<T> {

    private Class<T> configClass;
    private Map<Field, ConfigSerializer<M>> serializers;

    public PartitioningSerializer(String name, Class<T> configClass, ConfigSerializer.Factory<M> factory) {
        this.configClass = configClass;

        //noinspection unchecked
        serializers = getModuleFields(configClass).stream()
            .collect(
                Utils.toLinkedMap(
                    Function.identity(),
                    field -> factory.create(
                        String.format("%s/%s", name, field.getName()),
                        (Class<M>) field.getType()
                    )
                )
            );
    }

    private static boolean isConfigData(Field field) {
        return ConfigData.class.isAssignableFrom(field.getType());
    }

    private static List<Field> getModuleFields(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredFields())
            .filter(PartitioningSerializer::isConfigData)
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
                .filter(field -> !isConfigData(field))
                .forEach(field -> {
                    throw new RuntimeException(String.format("Field %s is not ConfigData!", field));
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
