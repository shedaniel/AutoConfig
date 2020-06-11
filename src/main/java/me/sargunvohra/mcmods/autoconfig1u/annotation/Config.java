package me.sargunvohra.mcmods.autoconfig1u.annotation;

import java.lang.annotation.*;

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
            String TRANSPARENT = "cloth-config2:transparent";

            String value();
        }

        /**
         * Sets the background of a specific category in the config GUI
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.TYPE)
        @Repeatable(CategoryBackgrounds.class)
        public @interface CategoryBackground {
            String category();
            String background();
        }

        /**
         * Do not use.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.TYPE)
        public @interface CategoryBackgrounds {
            @SuppressWarnings("unused") CategoryBackground[] value();
        }
    }
}
