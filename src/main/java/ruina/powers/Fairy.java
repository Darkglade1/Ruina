package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.att;

public class Fairy extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("Fairy");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int damageMultiplier = 2;

    public Fairy(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.DEBUFF, true, owner, amount);
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner == this.owner) {
            this.flash();
            att(new DamageAction(info.owner, new DamageInfo(this.owner, amount * damageMultiplier, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.POISON, true));
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner) {
            this.flashWithoutSound();
            att(new GainBlockAction(info.owner, this.amount, Settings.FAST_MODE));
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount * damageMultiplier + DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
    }
}
