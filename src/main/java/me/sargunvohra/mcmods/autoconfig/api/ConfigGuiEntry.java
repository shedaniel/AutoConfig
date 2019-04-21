package me.sargunvohra.mcmods.autoconfig.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigGuiEntry {

    String category() default "general";

    @Retention(RetentionPolicy.RUNTIME)
    @interface IntSlider {
        int min();

        int max();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface LongSlider {
        long min();

        long max();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Transitive {
    }
}
