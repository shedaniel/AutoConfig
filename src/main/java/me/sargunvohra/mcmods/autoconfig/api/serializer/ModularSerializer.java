package me.sargunvohra.mcmods.autoconfig.api.serializer;

import me.sargunvohra.mcmods.autoconfig.api.ConfigData;
import me.sargunvohra.mcmods.autoconfig.api.ConfigSerializer;
import me.sargunvohra.mcmods.autoconfig.impl.Utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class ModularSerializer
    <T extends ModularSerializer.ModularConfigData, M extends ModularSerializer.Module>
    implements ConfigSerializer<T> {

    private Class<T> configClass;
    private Map<Field, ConfigSerializer<M>> serializers;

    public ModularSerializer(String name, Class<T> configClass, ConfigSerializer.Factory<M> factory) {
        this.configClass = configClass;

        //noinspection unchecked
        serializers = Arrays.stream(configClass.getDeclaredFields())
            .filter(field -> Module.class.isAssignableFrom(field.getType()))
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

    public interface ModularConfigData extends ConfigData {
        @Override
        default void validatePostLoad() throws ValidationException {
            for (Field field : getClass().getDeclaredFields()) {
                if (Module.class.isAssignableFrom(field.getType())) {
                    ((Module) Utils.getUnsafely(field, this)).validatePostLoad();
                }
            }
        }
    }

    public interface Module extends ConfigData {
    }
}
