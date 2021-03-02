package ruina.monsters.eventBoss.core.power;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.monsters.eventBoss.core.actions.util.EnemyGainEnergyAction;

import static ruina.RuinaMod.makeID;

public class EnemyEnergizedPower extends AbstractPower {
    public static final String POWER_ID = makeID(EnemyEnergizedPower.class.getSimpleName());
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final PowerStrings powerStrings;

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Energized");
        NAME = EnemyEnergizedPower.powerStrings.NAME;
        DESCRIPTIONS = EnemyEnergizedPower.powerStrings.DESCRIPTIONS;
    }

    public EnemyEnergizedPower(final AbstractCreature owner, final int energyAmt) {
        this.name = EnemyEnergizedPower.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = energyAmt;
        if (this.amount >= 999) {
            this.amount = 999;
        }
        this.updateDescription();
        this.loadRegion("energized_green");
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = EnemyEnergizedPower.DESCRIPTIONS[0] + this.amount + EnemyEnergizedPower.DESCRIPTIONS[1];
        } else {
            this.description = EnemyEnergizedPower.DESCRIPTIONS[0] + this.amount + EnemyEnergizedPower.DESCRIPTIONS[2];
        }
    }

    @Override
    public void onEnergyRecharge() {
        this.flash();
        AbstractDungeon.actionManager.addToBottom(new EnemyGainEnergyAction(this.amount));
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this.ID));

    }

}