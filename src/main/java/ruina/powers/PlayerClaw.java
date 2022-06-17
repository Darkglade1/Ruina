package ruina.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;
import ruina.RuinaMod;
import ruina.util.TexLoader;

import java.util.Iterator;

import static ruina.RuinaMod.makePowerPath;

public class PlayerClaw extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(PlayerClaw.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TexLoader.getTexture(makePowerPath("AClaw84.png"));
    private static final Texture tex32 = TexLoader.getTexture(makePowerPath("AClaw32.png"));

    public static final int DRAW = 1;
    public static final int ENERGY = 2;

    public PlayerClaw(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);
    }

    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        addToBot(new GainEnergyAction(ENERGY));
        addToBot(new DrawCardAction(DRAW));
    }


    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
        for (int i = 0; i < ENERGY; i++) {
            description += " [E]";
        }
        description += DESCRIPTIONS[1] + DRAW + DESCRIPTIONS[2];
    }
}