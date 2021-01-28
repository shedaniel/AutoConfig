package me.sargunvohra.mcmods.autoconfig1u.forge;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.example.ExampleInits;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(AutoConfig.MOD_ID)
public class AutoConfigForge {
    public AutoConfigForge() {
        // check this class for an example on how to register and initialise a config
        ExampleInits.exampleCommonInit();
        
        // check this class for an example on how to register custom gui handlers
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ExampleInits::exampleClientInit);
        
        // this is the forge way of adding a custom config gui for your mod's config
        // beware you need to dereference this in order to be side-safe! 
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> AutoConfigForgeGui::registerConfigScreen);
    }
}
