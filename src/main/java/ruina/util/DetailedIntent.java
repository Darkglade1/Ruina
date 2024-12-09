package ruina.util;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BobEffect;
import ruina.RuinaMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeUIPath;

public class DetailedIntent {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("IntentStrings"));
    public static final String[] TEXT = uiStrings.TEXT;

    public static final String YOU = TEXT[0];
    public static final String SELF = TEXT[1];
    public static final String ALL_ENEMIES = TEXT[2];
    public static final String RANDOM_ENEMY = TEXT[3];
    public static final String LIFESTEAL = TEXT[4];
    public static final String ALL_MINIONS = TEXT[5];
    public static final String STEALS = TEXT[6];
    public static final String CLEANSE = TEXT[7];
    public static final String PARASITE = TEXT[8];
    public static final String DIES = TEXT[9];
    public static final String HALF_HEAL = TEXT[10];
    public static final String REMOVE_NEG_STR = TEXT[11];
    public static final String SUMMON = TEXT[12];


    public static final String WEAK = makeUIPath("detailedIntents/Weak.png");
    public static Texture WEAK_TEXTURE = TexLoader.getTexture(WEAK);

    public static final String FRAIL = makeUIPath("detailedIntents/Frail.png");
    public static Texture FRAIL_TEXTURE = TexLoader.getTexture(FRAIL);

    public static final String VULNERABLE = makeUIPath("detailedIntents/Vulnerable.png");
    public static Texture VULNERABLE_TEXTURE = TexLoader.getTexture(VULNERABLE);

    public static final String STRENGTH = makeUIPath("detailedIntents/Strength.png");
    public static Texture STRENGTH_TEXTURE = TexLoader.getTexture(STRENGTH);

    public static final String DEXTERITY = makeUIPath("detailedIntents/Dexterity.png");
    public static Texture DEXTERITY_TEXTURE = TexLoader.getTexture(DEXTERITY);

    public static final String PLATED_ARMOR = makeUIPath("detailedIntents/PlatedArmor.png");
    public static Texture PLATED_ARMOR_TEXTURE = TexLoader.getTexture(PLATED_ARMOR);

    public static final String METALLICIZE = makeUIPath("detailedIntents/Metal.png");
    public static Texture METALLICIZE_TEXTURE = TexLoader.getTexture(METALLICIZE);

    public static final String DRAW_DOWN = makeUIPath("detailedIntents/DrawDown.png");
    public static Texture DRAW_DOWN_TEXTURE = TexLoader.getTexture(DRAW_DOWN);

    public static final String HEAL = makeUIPath("detailedIntents/Heal.png");
    public static Texture HEAL_TEXTURE = TexLoader.getTexture(HEAL);

    public static final String BLOCK = makeUIPath("detailedIntents/Block.png");
    public static Texture BLOCK_TEXTURE = TexLoader.getTexture(BLOCK);

    public static final String DRAW_PILE = makeUIPath("detailedIntents/DrawPile.png");
    public static Texture DRAW_PILE_TEXTURE = TexLoader.getTexture(DRAW_PILE);

    public static final String DISCARD_PILE = makeUIPath("detailedIntents/DiscardPile.png");
    public static Texture DISCARD_PILE_TEXTURE = TexLoader.getTexture(DISCARD_PILE);

    public static final String BURN = makeUIPath("detailedIntents/Burn.png");
    public static Texture BURN_TEXTURE = TexLoader.getTexture(BURN);

    public static final String DAZED = makeUIPath("detailedIntents/Dazed.png");
    public static Texture DAZED_TEXTURE = TexLoader.getTexture(DAZED);

    public static final String SLIMED = makeUIPath("detailedIntents/Slimed.png");
    public static Texture SLIMED_TEXTURE = TexLoader.getTexture(SLIMED);

    public static final String VOID = makeUIPath("detailedIntents/Void.png");
    public static Texture VOID_TEXTURE = TexLoader.getTexture(VOID);

    public static final String WOUND = makeUIPath("detailedIntents/Wound.png");
    public static Texture WOUND_TEXTURE = TexLoader.getTexture(WOUND);

    public enum TargetType {
        SIMPLE(""), YOU(DetailedIntent.YOU), SELF(DetailedIntent.SELF), ALL_ENEMIES(DetailedIntent.ALL_ENEMIES), RANDOM_ENEMY(DetailedIntent.RANDOM_ENEMY), DRAW_PILE(""), DISCARD_PILE(""), ALL_MINIONS(DetailedIntent.ALL_MINIONS);

        public String text;

        TargetType(String text) {
            this.text = text;
        }
    }

    private final AbstractMonster monster;
    private final int amount;
    private final Texture icon;
    private final TargetType target;

    private boolean overrideWithDescription;
    private String description;

    float scaleWidth = Settings.scale;
    float scaleHeight = Settings.scale;
    BobEffect bob;

    // Holds the detailed intents
    public static Map<AbstractMonster, Map<Integer, ArrayList<DetailedIntent>>> intents = new HashMap<>();

    private final float Y_OFFSET = 46.0f;
    private float X_OFFSET = 106.0f;

    public DetailedIntent(AbstractMonster monster, int amount, Texture icon) {
        this(monster, amount, icon, TargetType.SIMPLE);
    }

    public DetailedIntent(AbstractMonster monster, int amount, Texture icon, TargetType target) {
        this.monster = monster;
        this.amount = amount;
        this.icon = icon;
        this.target = target;
        bob = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "bobEffect");
    }

    public DetailedIntent(AbstractMonster monster, String description) {
        this(monster, 0, null, null);
        this.overrideWithDescription = true;
        this.description = description;
    }

    public void renderDetails(SpriteBatch sb, int yPosition, int intentNum) {
        Color color = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "intentColor");
        sb.setColor(color);
        float textY = monster.intentHb.cY + (Y_OFFSET * scaleHeight * yPosition) + bob.y;
        float iconY = monster.intentHb.cY - 16.0F + (Y_OFFSET * scaleHeight * yPosition) + bob.y;
        float xOffset = X_OFFSET * scaleWidth * intentNum;
        if (!overrideWithDescription) {
            if (target == TargetType.SIMPLE) {
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, Integer.toString(amount), monster.intentHb.cX - (22.0f * scaleWidth) + xOffset, textY, color);
                sb.draw(icon, monster.intentHb.cX - 16.0F + (8.0f * scaleWidth) + xOffset, iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
            } else if (target == TargetType.DRAW_PILE || target == TargetType.DISCARD_PILE) {
                Texture pileTexture;
                if (target == TargetType.DRAW_PILE) {
                    pileTexture = DRAW_PILE_TEXTURE;
                } else {
                    pileTexture = DISCARD_PILE_TEXTURE;
                }
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, Integer.toString(amount), monster.intentHb.cX - (32.0f * scaleWidth), textY, color);
                sb.draw(icon, monster.intentHb.cX - 16.0F - (7.0f * scaleWidth) + xOffset, iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
                sb.draw(pileTexture, monster.intentHb.cX - 16.0F + (27.0f * scaleWidth) + xOffset, iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
            } else if (target == TargetType.ALL_ENEMIES || target == TargetType.ALL_MINIONS || target == TargetType.RANDOM_ENEMY) {
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, Integer.toString(amount), monster.intentHb.cX - (42.0f * scaleWidth) + xOffset, textY, color);
                sb.draw(icon, monster.intentHb.cX - 16.0F - (12.0f * scaleWidth) + xOffset, iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, "-> " + target.text, monster.intentHb.cX - (42.0f * scaleWidth) + (145.0f * scaleWidth) + xOffset, textY, color);
            } else {
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, Integer.toString(amount), monster.intentHb.cX - (42.0f * scaleWidth) + xOffset, textY, color);
                sb.draw(icon, monster.intentHb.cX - 16.0F - (12.0f * scaleWidth) + xOffset, iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, "-> " + target.text, monster.intentHb.cX - (42.0f * scaleWidth) + (90.0f * scaleWidth) + xOffset, textY, color);
            }
        } else {
            FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, description, monster.intentHb.cX - (12.0f * scaleWidth) + xOffset, textY, color);
        }
    }
}
