package me.sargunvohra.mcmods.autoconfig1.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Attach this to your config POJO.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {

    String name();

    class Gui {
        private Gui() {
        }

        /**
         * Sets the background in the config GUI
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.TYPE)
        public @interface Background {
            String value();
        }
    }
}
