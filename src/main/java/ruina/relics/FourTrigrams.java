package ruina.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.ArrayList;
import java.util.Collections;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class FourTrigrams extends AbstractEasyRelic {
    public static final String ID = makeID(FourTrigrams.class.getSimpleName());

    private static final int STRENGTH = 1;
    private static final int DEXTERITY = 1;
    private static final int ENERGY = 1;
    private static final int DRAW = 1;

    private ArrayList<AbstractGameAction> trigrams = new ArrayList<>();

    public FourTrigrams() {
        super(ID, RelicTier.SPECIAL, LandingSound.SOLID);
    }

    @Override
    public void atPreBattle() {
        trigrams.clear();
        counter = 0;
        trigrams.add(new ApplyPowerAction(adp(), adp(), new StrengthPower(adp(), STRENGTH), STRENGTH));
        trigrams.add(new ApplyPowerAction(adp(), adp(), new DexterityPower(adp(), DEXTERITY), DEXTERITY));
        trigrams.add(new GainEnergyAction(ENERGY));
        trigrams.add(new DrawCardAction(DRAW));
        Collections.shuffle(trigrams, AbstractDungeon.relicRng.random);
    }

    @Override
    public void atTurnStartPostDraw() {
        if (!trigrams.isEmpty()) {
            flash();
            atb(new RelicAboveCreatureAction(adp(), this));
            atb(trigrams.remove(0));
            counter++;
        }
    }

    @Override
    public void onVictory() {
        counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + STRENGTH + DESCRIPTIONS[1] + DEXTERITY + DESCRIPTIONS[2] + DRAW + DESCRIPTIONS[3];
    }
}
