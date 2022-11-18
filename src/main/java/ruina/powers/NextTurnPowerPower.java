package ruina.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;

public class NextTurnPowerPower extends AbstractEasyPower {
    private AbstractPower powerToGain;
    public static final String POWER_ID = RuinaMod.makeID("NextTurnPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public NextTurnPowerPower(AbstractCreature owner, AbstractPower powerToGrant) {
        super(NAME + powerToGrant.name, POWER_ID + powerToGrant.name, powerToGrant.type, false, owner, powerToGrant.amount);
        this.img = powerToGrant.img;
        this.region48 = powerToGrant.region48;
        this.region128 = powerToGrant.region128;
        this.powerToGain = powerToGrant;
        updateDescription();
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        super.renderIcons(sb, x, y, Color.GREEN.cpy());
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        powerToGain.amount += stackAmount;
    }

    @Override
    public void atEndOfRound() {
        flash();
        if (!AbstractDungeon.actionManager.turnHasEnded) {
            addToTop(new RemoveSpecificPowerAction(owner, owner, this.ID));
            addToTop(new ApplyPowerAction(owner, owner, powerToGain, powerToGain.amount));
        } else {
            addToBot(new ApplyPowerAction(owner, owner, powerToGain, powerToGain.amount));
            addToBot(new RemoveSpecificPowerAction(owner, owner, this.ID));
        }
    }

    @Override
    public void updateDescription() {
        if (powerToGain == null) {
            description = "???";
        } else {
            description = DESCRIPTIONS[0] + powerToGain.amount + DESCRIPTIONS[1] + powerToGain.name + DESCRIPTIONS[2];
        }
    }
}
