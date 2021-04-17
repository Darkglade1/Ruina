package ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.block;

@AutoAdd.Ignore
public class CHRBOSS_StigmaOfGlory extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_StigmaOfGlory.class.getSimpleName());
    public static final int BLOCK = 8;
    public static final int ENERGY = 1;
    public static final int DRAW = 2;
    public static final int UPG_BLOCK = 4;

    public CHRBOSS_StigmaOfGlory() {
        super(ID, 1, CardType.SKILL, CardRarity.RARE, CardTarget.SELF, RuinaMod.Enums.EGO);
        block = baseBlock = BLOCK;
        magicNumber = baseMagicNumber = DRAW;
        secondMagicNumber = baseSecondMagicNumber = ENERGY;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        block(p, block);
        atb(new GainEnergyAction(secondMagicNumber));
        atb(new DrawCardAction(magicNumber));
    }

    @Override
    public void upp() {
        upgradeBlock(UPG_BLOCK);
    }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_StigmaOfGlory(); }
}