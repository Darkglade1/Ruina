package ruina.powers.act1;

import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;

public class Pattern extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Pattern.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Pattern(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void onInitialApplication() {
        amount2 = 0;
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner == owner && damageAmount > 0) {
            amount2 += damageAmount;
            if (amount2 >= amount) {
                onSpecificTrigger();
//                if (RuinaMod.isMultiplayerConnected()) {
//                    P2PManager.SendData(MessengerListener.request_helperClearedDebuffs, SpireHelp.Gameplay.CreatureToUID(owner), SpireHelp.Gameplay.GetMapLocation());
//                }
            }
        }
    }

    @Override
    public void onSpecificTrigger() {
        flash();
        amount2 = 0;
        atb(new RemoveDebuffsAction(owner));
    }

    @Override
    public void atEndOfRound() {
        amount2 = 0;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
