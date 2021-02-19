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
import ruina.cards.EGO.act2.BlindRage;
import ruina.cards.EGO.act2.CobaltScar;
import ruina.cards.EGO.act2.CrimsonScar;
import ruina.cards.EGO.act2.FadedMemories;
import ruina.cards.EGO.act2.GoldRush;
import ruina.cards.EGO.act2.Harvest;
import ruina.cards.EGO.act2.HomingInstinct;
import ruina.cards.EGO.act2.LoveAndHate;
import ruina.cards.EGO.act2.Lumber;
import ruina.cards.EGO.act2.Mimicry;
import ruina.cards.EGO.act2.Nihil;
import ruina.cards.EGO.act3.Penitence;
import ruina.cards.EGO.act2.Smile;
import ruina.cards.EGO.act2.SwordSharpened;
import ruina.cards.EGO.act2.Thirst;
import ruina.cards.EGO.act3.DeadSilence;
import ruina.cards.EGO.act3.Heaven;
import ruina.cards.EGO.act3.ParadiseLost;
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

    public static ArrayList<AbstractCard> getRandomEgoCards(int amount, int actNum) {
        ArrayList<String> list = new ArrayList<>();
        if (actNum == 2) {
            addAct2EgoCards(list);
        } else if (actNum == 3) {
            addAct3EgoCards(list);
        } else {
            addAct2EgoCards(list);
            addAct3EgoCards(list);
        }

        Collections.shuffle(list, AbstractDungeon.cardRandomRng.random);
        ArrayList<String> finalList = new ArrayList<>(list.subList(0, amount));
        ArrayList<AbstractCard> egoCards = new ArrayList<>();
        for (String egoID : finalList) {
            AbstractCard egoCard = CardLibrary.getCard(egoID).makeCopy();
            egoCards.add(egoCard);
        }
        return egoCards;
    }

    public static ArrayList<AbstractCard> getRandomEgoCards(int amount) {
        return getRandomEgoCards(amount, AbstractDungeon.actNum);
    }

    public static void addAct2EgoCards(ArrayList<String> list) {
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
        list.add(CrimsonScar.ID);
    }

    public static void addAct3EgoCards(ArrayList<String> list) {
        list.add(ParadiseLost.ID);
        list.add(DeadSilence.ID);
        list.add(Heaven.ID);
        list.add(Penitence.ID);
    }
}
