package ruina.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.*;

public class Burn extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Burn.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Burn(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.DEBUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount);
    }

    public void atEndOfTurn(boolean isPlayer) {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            flash();
            dmg(owner, new DamageInfo(owner, amount, DamageInfo.DamageType.THORNS));
            atb(new RemoveSpecificPowerAction(owner, owner, this));
        }
    }
}