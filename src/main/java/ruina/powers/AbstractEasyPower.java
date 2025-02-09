package ruina.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.RuinaMod;
import ruina.util.TexLoader;

import static ruina.RuinaMod.getModID;

public abstract class AbstractEasyPower extends TwoAmountPower {
    public AbstractEasyPower(String NAME, String ID, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        this.ID = ID;
        this.isTurnBased = isTurnBased;

        this.name = NAME;

        this.owner = owner;
        this.amount = amount;
        this.amount2 = -1;
        this.type = powerType;

        Texture normalTexture = TexLoader.getTexture(RuinaMod.makePowerPath(ID.replace(getModID() + ":", "") + "32.png"));
        Texture hiDefImage = TexLoader.getTexture(RuinaMod.makePowerPath(ID.replace(getModID() + ":", "") + "84.png"));
        if (hiDefImage != null) {
            region128 = new TextureAtlas.AtlasRegion(hiDefImage, 0, 0, hiDefImage.getWidth(), hiDefImage.getHeight());
            if (normalTexture != null)
                region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
        } else if (normalTexture != null) {
            this.img = normalTexture;
            region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
        }

        this.updateDescription();
    }

    protected void setPowerImage(String existingPowerName) {
        Texture normalTexture = TexLoader.getTexture(RuinaMod.makePowerPath(existingPowerName + "32.png"));
        Texture hiDefImage = TexLoader.getTexture(RuinaMod.makePowerPath(existingPowerName + "84.png"));
        if (hiDefImage != null) {
            region128 = new TextureAtlas.AtlasRegion(hiDefImage, 0, 0, hiDefImage.getWidth(), hiDefImage.getHeight());
            if (normalTexture != null)
                region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
        } else if (normalTexture != null) {
            this.img = normalTexture;
            region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
        }
    }
}