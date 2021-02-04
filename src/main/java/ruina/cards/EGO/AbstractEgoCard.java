package ruina.cards.EGO;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.cards.AbstractCard;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.util.TexLoader;

import java.lang.reflect.Method;

public abstract class AbstractEgoCard extends AbstractRuinaCard {
    public static TextureAtlas.AtlasRegion[] frames;

    public AbstractEgoCard(final String cardID, final int cost, final CardType type, final CardRarity rarity, final CardTarget target) {
        super(cardID, cost, type, rarity, target, RuinaMod.Enums.EGO);
        this.setBannerTexture(RuinaMod.makeImagePath("512/banner_blacky.png"), RuinaMod.makeImagePath("1024/banner_blacky.png"));
        if(frames == null) {
            frames = new TextureAtlas.AtlasRegion[6];
            frames[0] = regionFromTexture(RuinaMod.makeImagePath("512/attackframe_blacky.png"));
            frames[1] = regionFromTexture(RuinaMod.makeImagePath("512/skillframe_blacky.png"));
            frames[2] = regionFromTexture(RuinaMod.makeImagePath("512/powerframe_blacky.png"));
            frames[3] = regionFromTexture(RuinaMod.makeImagePath("1024/attackframe_blacky.png"));
            frames[4] = regionFromTexture(RuinaMod.makeImagePath("1024/skillframe_blacky.png"));
            frames[5] = regionFromTexture(RuinaMod.makeImagePath("1024/powerframe_blacky.png"));
        }
    }

    @SpireOverride
    protected void renderAttackPortrait(SpriteBatch sb, float x, float y) {
        try {
            Method renderHelper = AbstractCard.class.getDeclaredMethod("renderHelper", SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class);
            renderHelper.setAccessible(true);
            renderHelper.invoke(this, sb,
                    ReflectionHacks.getPrivate(this, AbstractCard.class, "renderColor"),
                    frames[0], x, y);
        }catch(Exception ex) {}
    }

    @SpireOverride
    protected void renderSkillPortrait(SpriteBatch sb, float x, float y) {
        try {
            Method renderHelper = AbstractCard.class.getDeclaredMethod("renderHelper", SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class);
            renderHelper.setAccessible(true);
            renderHelper.invoke(this, sb,
                    ReflectionHacks.getPrivate(this, AbstractCard.class, "renderColor"),
                    frames[1], x, y);
        }catch(Exception ex) {}
    }

    @SpireOverride
    protected void renderPowerPortrait(SpriteBatch sb, float x, float y) {
        try {
            Method renderHelper = AbstractCard.class.getDeclaredMethod("renderHelper", SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class);
            renderHelper.setAccessible(true);
            renderHelper.invoke(this, sb,
                    ReflectionHacks.getPrivate(this, AbstractCard.class, "renderColor"),
                    frames[2], x, y);
        }catch(Exception ex) {}
    }

    private TextureAtlas.AtlasRegion regionFromTexture(String tex) {
        Texture texture = TexLoader.getTexture(tex);
        return new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
    }
}
