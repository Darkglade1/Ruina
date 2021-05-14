package ruina.cards.EGO.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class FrostSplinter extends AbstractEgoCard {
    public final static String ID = makeID(FrostSplinter.class.getSimpleName());

    public static final int COST = 1;
    public static final int DRAW = 1;
    public static final int DISPLAY_FREEZE = 1;

    public FrostSplinter() {
        super(ID, COST, CardType.POWER, CardTarget.SELF);
        magicNumber = baseMagicNumber = DRAW;
        secondMagicNumber = baseSecondMagicNumber = DISPLAY_FREEZE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new ApplyPowerAction(p, p, new ruina.powers.FrostSplinter(p, magicNumber)));
    }

    @Override
    public void upp() { isInnate = true; }
}