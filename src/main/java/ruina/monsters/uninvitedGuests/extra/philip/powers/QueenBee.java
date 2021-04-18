package ruina.monsters.uninvitedGuests.extra.philip.powers;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;

import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class QueenBee extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(QueenBee.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final int TURN_NORMAL = 3;

    public QueenBee(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, TURN_NORMAL);
        priority = 99;
    }

    @Override
    public void updateDescription() {
        if(amount == -1){ description = DESCRIPTIONS[2]; }
        else { description = String.format(amount == 1 ? DESCRIPTIONS[1] : DESCRIPTIONS[0], amount); }
    }

    public void atStartOfTurnPostDraw(){
        flash();
        if(amount == -1){ amount = TURN_NORMAL; }
        else if (amount - 1 == 0){ amount = -1; }
        else { amount -= 1; }
        updateDescription();
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if(amount == -1) {
            if (type == DamageInfo.DamageType.NORMAL) { return damage * 2; }
        }
        return damage;
    }
}
