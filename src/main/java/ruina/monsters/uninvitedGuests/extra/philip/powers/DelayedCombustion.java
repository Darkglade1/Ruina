package ruina.monsters.uninvitedGuests.extra.philip.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.beyond.OrbWalker;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.powers.AbstractEasyPower;
import ruina.vfx.CombustionEffect;

import static ruina.util.Wiz.*;

public class DelayedCombustion extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(DelayedCombustion.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public DelayedCombustion(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        loadRegion("combust");
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0], amount);
    }


    public void atEndOfTurn(boolean isPlayer) {
        atb(new VFXAction(new CombustionEffect(owner)));
        dmg(owner, new DamageInfo(owner, amount, DamageInfo.DamageType.THORNS));
        atb(new RemoveSpecificPowerAction(owner, owner, this));
    }
}