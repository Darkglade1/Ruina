package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.defect.DoubleEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class ContractOfLight extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(ContractOfLight.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied = true;
    private static final int DAMAGE_AMOUNT = 6;

    public ContractOfLight(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, DAMAGE_AMOUNT);
        this.priority = 0; //make it apply before vulnerable
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? damage + (float)this.amount : damage;
    }

    public void atStartOfTurnPostDraw(){ atb(new DoubleEnergyAction()); }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for(int i = 0; i < EnergyPanel.totalCount; i += 1){ att(new DamageAction(AbstractDungeon.player, new DamageInfo(AbstractDungeon.player, DAMAGE_AMOUNT, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE)); }
                isDone = true;
            }
        });
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], amount);
    }
}