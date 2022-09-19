package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class Fairy extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("Fairy");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Fairy(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.DEBUFF, true, owner, amount);
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner == this.owner) {
            this.flash();
            att(new DamageAction(info.owner, new DamageInfo(this.owner, info.output * amount, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.POISON, true));
        }
    }

    @Override
    public void atEndOfRound() {
        atb(new ReducePowerAction(owner, owner, this, 1));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
