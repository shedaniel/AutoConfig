# Auto Config API

Annotation based API for mod configs, with Cloth Config GUI integration.

## Usage

*build.gradle*
```groovy
dependencies {
    // no need to jij cloth; it's already in auto-config
    modCompile "cloth-config:ClothConfig:${project.cloth_config_version}"
    modCompile "io.github.prospector.modmenu:ModMenu:${project.modmenu_version}"
    modCompile "me.sargunvohra.mcmods:auto-config:${project.auto_config_version}"
    include "me.sargunvohra.mcmods:auto-config:${project.auto_config_version}"
}
```

*mod code*
```java
class ModConfig {
    
    @ConfigGuiEntry
    private int exampleInt;

    @ConfigGuiEntry
    @ConfigGuiEntry.IntSlider(min = 0, max = 10)
    private int exampleSlider;
    
    @ConfigGuiEntry(category = "whatever")
    private boolean exampleInCategory;
    
    private int exampleHiddenFromGui = 500;
    
    // works with boolean, int, long, double, float, enum, and string
}

class MyModInit implements ModInitializer {

    public static final String MOD_ID = "mymodid";

    @Override
    public void onInitialize() {
        AutoConfig.registerJankson(MOD_ID, ModConfig.class);
        // or AutoConfig.registerGson(MOD_ID, ModConfig.class);
        // or AutoConfig.register(MOD_ID, ModConfig.class, custom serializer);
        
        // later, you can access the config with AutoConfig.<ModConfig>getConfigHolder(MOD_ID)
        // you can even register multiple configs with different names
    }
}

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    
    // don't forget to register this entrypoint in your fabric.mod.json
    // see https://github.com/Prospector/ModMenu
    
    @Override
    public String getModId() {
        return MyModInit.MOD_ID;
    }

    @Override
    public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
        return Optional.of(AutoConfig.getConfigScreen(getModId(), screen));
    }
}
```

## Building from source

```bash
git clone https://gitlab.com/sargunv-mc-mods/auto-config.git
cd auto-config
./gradlew build
# On Windows, use "gradlew.bat" instead of "gradlew"
```
