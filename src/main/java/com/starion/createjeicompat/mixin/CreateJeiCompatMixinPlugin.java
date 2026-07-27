package com.starion.createjeicompat.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Applies JEI-targeting mixins only when JEI is present.
 * Uses {@link LoadingModList} because {@code ModList} is not initialized yet
 * during mixin prepare.
 */
public class CreateJeiCompatMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (LoadingModList.get().getModFileById("jei") == null) {
            return false;
        }
        // Scrap two-row trim only when Cyber Goggles expanded the background.
        if (mixinClassName.endsWith("CreateJeiScrapPaddingMixin")) {
            return LoadingModList.get().getModFileById("create_cyber_goggles") != null;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
