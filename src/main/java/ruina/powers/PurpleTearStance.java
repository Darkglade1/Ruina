package ruina.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnReceivePowerPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.uninvitedGuests.greta.Hod;

import static ruina.util.Wiz.atb;

public class PurpleTearStance extends AbstractUnremovablePower implements OnReceivePowerPower {

    public static final String POWER_ID = RuinaMod.makeID("PurpleTearStance");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int stance;

    public PurpleTearStance(AbstractCreature owner, int stance) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, 0);
        this.stance = stance;
        this.priority = 99;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL && stance == Hod.SLASH) {
            return damage * (1 + ((float)amount / 100));
        } else {
            return damage;
        }
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner == owner && stance == Hod.PIERCE) {
            amount++;
            if (amount >= Hod.pierceTriggerHits) {
                flash();
                amount = 0;
                for (AbstractPower power : target.powers) {
                    if (power.type == PowerType.DEBUFF) {
                        atb(new ApplyPowerAction(target, owner, power, power.amount));
                    }
                }
            }
        }
    }

    public void changeStance(int stance) {
        this.stance = stance;
        if (stance == Hod.SLASH) {
            amount = Hod.slashDamageBonus;
        } else {
            amount = 0;
        }
        updateDescription();
        AbstractDungeon.onModifyPower();
        AbstractRuinaMonster.playSound("PurpleChange");
        flash();
        if (owner instanceof Hod) {
            ((Hod) owner).rollMove();
            ((Hod) owner).createIntent();
        }
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (stance == Hod.GUARD) {
            return power.type != PowerType.DEBUFF;
        } else {
            return true;
        }
    }

    @Override
    public void updateDescription() {
        if (stance == Hod.SLASH) {
            name = DESCRIPTIONS[0];
            description = DESCRIPTIONS[1] + Hod.slashDamageBonus + DESCRIPTIONS[2];
        } else if (stance == Hod.PIERCE) {
            name = DESCRIPTIONS[3];
            description = DESCRIPTIONS[4] + Hod.pierceTriggerHits + DESCRIPTIONS[5];
        } else {
            name = DESCRIPTIONS[6];
            description = DESCRIPTIONS[7];
        }
    }
}
