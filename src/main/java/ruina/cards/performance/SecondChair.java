package ruina.cards.performance;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTargetNextTurnTop;

@AutoAdd.Ignore
public class SecondChair extends AbstractPerformanceCard {
    public final static String ID = makeID(SecondChair.class.getSimpleName());
    private static final int COST = 1;
    private static final int STRENGTH = 1;
    private static final int UP_STRENGTH = 1;

    public SecondChair() {
        super(ID, COST);
        magicNumber = baseMagicNumber = STRENGTH;
    }

    @Override
    public void EndOfTurnEffect() {
        AbstractMonster target = AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
        applyToTargetNextTurnTop(target, adp(), new StrengthPower(target, magicNumber));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_STRENGTH);
    }
}