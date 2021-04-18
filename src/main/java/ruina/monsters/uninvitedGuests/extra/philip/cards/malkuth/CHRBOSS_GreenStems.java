package ruina.monsters.uninvitedGuests.extra.philip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.att;

@AutoAdd.Ignore
public class CHRBOSS_GreenStems extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_GreenStems.class.getSimpleName());
    public static final int ENERGY = 2;
    public static final int DRAW = 1;

    public CHRBOSS_GreenStems() {
        super(ID, -2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = ENERGY;
        secondMagicNumber = baseSecondMagicNumber = DRAW;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }
    @Override
    public void upp() {
    }
    @Override
    public void triggerWhenDrawn() {
            att(new GainEnergyAction(magicNumber));
            att(new DrawCardAction(secondMagicNumber));
            att(new ExhaustSpecificCardAction(this, adp().hand));
    }
    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) { return false; }
    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_GreenStems(); }
}