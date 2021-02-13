package ruina.cards.EGO;

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
import ruina.util.TexLoader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import static ruina.util.Wiz.adp;

public abstract class AbstractEgoCard extends AbstractRuinaCard {
    public static TextureAtlas.AtlasRegion[] frames;

    public AbstractEgoCard(final String cardID, final int cost, final CardType type, final CardTarget target) {
        super(cardID, cost, type, CardRarity.RARE, target, RuinaMod.Enums.EGO);
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

    public static ArrayList<AbstractCard> getRandomEgoCards(int amount) {
        ArrayList<String> list = new ArrayList<>();
        list.add(BlindRage.ID);
        list.add(FadedMemories.ID);
        list.add(GoldRush.ID);
        if (adp().energy.energyMaster >= LoveAndHate.COST) {
            list.add(LoveAndHate.ID); //only add it if the player can cast it
        }
        list.add(Mimicry.ID);
        list.add(Nihil.ID);
        list.add(Smile.ID);
        list.add(SwordSharpened.ID);
        list.add(Lumber.ID);
        list.add(Harvest.ID);
        list.add(HomingInstinct.ID);
        list.add(CobaltScar.ID);
        list.add(Thirst.ID);

        Collections.shuffle(list, AbstractDungeon.cardRandomRng.random);
        ArrayList<String> finalList = new ArrayList<>(list.subList(0, amount));
        ArrayList<AbstractCard> egoCards = new ArrayList<>();
        for (String egoID : finalList) {
            AbstractCard egoCard = CardLibrary.getCard(egoID).makeCopy();
            egoCards.add(egoCard);
        }
        return egoCards;
    }
}
