package com.bcw.mod.client.model;

import com.bcw.mod.BallisticWarfareMod;
import com.bcw.mod.entity.AbstractMissileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeckoLib model wrapper. Each missile family (Zircon, Tomahawk, Sarmat, ...) ships its
 * own .geo.json / .png / .animation.json triplet under assets/bcw/geo|textures|animations,
 * named after the missile's registry id, e.g. geo/zircon.geo.json, textures/zircon.png.
 */
public class MissileModel extends GeoModel<AbstractMissileEntity> {

    private final String missileId;

    public MissileModel(String missileId) {
        this.missileId = missileId;
    }

    @Override
    public ResourceLocation getModelResource(AbstractMissileEntity entity) {
        return new ResourceLocation(BallisticWarfareMod.MOD_ID, "geo/" + missileId + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AbstractMissileEntity entity) {
        return new ResourceLocation(BallisticWarfareMod.MOD_ID, "textures/entity/" + missileId + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractMissileEntity entity) {
        return new ResourceLocation(BallisticWarfareMod.MOD_ID, "animations/" + missileId + ".animation.json");
    }
}
