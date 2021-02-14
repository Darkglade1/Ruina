package ruina.cards.EGO;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import ruina.actions.CallbackScryAction;

import java.util.ArrayList;
import java.util.function.Consumer;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class CrimsonScar extends AbstractEgoCard {
    public final static String ID = makeID(CrimsonScar.class.getSimpleName());
    public static final int BLOCK = 10;
    public static final int UP_BLOCK = 2;
    public static final int VIGOR = 3;
    public static final int SCRY = 2;
    public static final int UP_SCRY = 1;

    public CrimsonScar() {
        super(ID, 1, CardType.SKILL, CardTarget.SELF);
        baseBlock = BLOCK;
        magicNumber = baseMagicNumber = SCRY;
        secondMagicNumber = baseSecondMagicNumber = VIGOR;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        blck();
        Consumer<ArrayList<AbstractCard>> consumer = cards -> {
            for (AbstractCard card : cards) {
                applyToTarget(adp(), adp(), new VigorPower(adp(), secondMagicNumber));
            }
        };
        atb(new CallbackScryAction(magicNumber, consumer));
    }

    @Override
    public void upp() {
        upgradeBlock(UP_BLOCK);
        upgradeMagicNumber(UP_SCRY);
    }
}