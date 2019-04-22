package me.sargunvohra.mcmods.autoconfig1.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ConfigEntry {

    private ConfigEntry() {
    }

    /**
     * Sets the category name of the config entry. Categories are created in order.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Category {
        String value();
    }

    /**
     * Applies to int and long fields.
     * Sets the GUI to a slider.
     * In a future version it will enforce bounds at deserialization.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface BoundedDiscrete {
        long min() default 0;

        long max();
    }

    /**
     * Applies to float and double fields.
     * In a future version it will enforce bounds at deserialization.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface BoundedFloating {
        double min() default 0;

        double max();
    }

    public static class Gui {
        private Gui() {
        }

        /**
         * Removes the field from the config GUI.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface Excluded {
        }

        /**
         * Applies to objects.
         * Adds GUI entries for the field's inner fields at the same level as this field.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface TransitiveObject {
        }

        /**
         * Applies to objects.
         * Adds GUI entries for the field's inner fields in a collapsible section.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface CollapsibleObject {
            boolean startExpanded() default false;
        }

        /**
         * Applies to objects.
         * Sets the object's tree to be expanded by default.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface StartExpanded {
        }
    }
}
