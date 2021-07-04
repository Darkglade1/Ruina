package ruina.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.powers.NextTurnPowerPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.adp;

public class Overexertion extends AbstractEasyRelic {
    public static final String ID = makeID(Overexertion.class.getSimpleName());

    private static final int ENERGY_AMT = 2;
    private static final int WEAK_AND_FRAIL = 1;
    private boolean firstTurn = true;

    public Overexertion() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }
    public void atPreBattle() { this.firstTurn = true; }

    public void atTurnStart() {
        if (this.firstTurn) {
            flash();
            att(new GainEnergyAction(ENERGY_AMT));
            att(new RelicAboveCreatureAction(adp(), this));
            att(new ApplyPowerAction(adp(), adp(), new NextTurnPowerPower(adp(), new FrailPower(adp(), WEAK_AND_FRAIL, false))));
            att(new ApplyPowerAction(adp(), adp(), new NextTurnPowerPower(adp(), new WeakPower(adp(), WEAK_AND_FRAIL, false))));
            this.firstTurn = false;
        }
    }
    @Override
    public String getUpdatedDescription() { return String.format(DESCRIPTIONS[0], WEAK_AND_FRAIL); }
}
