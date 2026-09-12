package com.starion.createjeicompat.mixin;

import com.starion.createjeicompat.JeiPresence;
import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Applies Create JEI-category mixins when a {@code jei} id is present (real JEI or TMRV).
 * {@code RecipesGuiMixin} needs JEI's GUI class and is skipped for TMRV.
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
        // RecipesGui is JEI-only. TMRV satisfies the jei id but does not ship that class.
        if (mixinClassName.endsWith(".RecipesGuiMixin")) {
            return JeiPresence.hasRecipesGui();
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
