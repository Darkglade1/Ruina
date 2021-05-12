package ruina.cards.ABNO;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.act2.*;
import ruina.cards.EGO.act3.*;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_GUN;
import ruina.util.TexLoader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import static ruina.RuinaMod.makeImagePath;
import static ruina.util.Wiz.adp;

public abstract class AbstractAbnormalityCard extends AbstractRuinaCard {
    public static TextureAtlas.AtlasRegion[] frames;

    public AbstractAbnormalityCard(final String cardID, final int cost, final CardType type, final CardTarget target) {
        super(cardID, cost, type, CardRarity.RARE, target, RuinaMod.AbnoEnums.ANGELA_LOR_ABNO , makeImagePath("cards/" + CHRALLY_GUN.class.getSimpleName() + ".png"));
        this.setBannerTexture(RuinaMod.makeImagePath("512/void.png"), RuinaMod.makeImagePath("1024/void.png"));
        if(frames == null) {
            frames = new TextureAtlas.AtlasRegion[6];
            frames[0] = regionFromTexture(RuinaMod.makeImagePath("512/void.png"));
            frames[1] = regionFromTexture(RuinaMod.makeImagePath("512/void.png"));
            frames[2] = regionFromTexture(RuinaMod.makeImagePath("512/void.png"));
            frames[3] = regionFromTexture(RuinaMod.makeImagePath("1024/void.png"));
            frames[4] = regionFromTexture(RuinaMod.makeImagePath("1024/void.png"));
            frames[5] = regionFromTexture(RuinaMod.makeImagePath("1024/void.png"));
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