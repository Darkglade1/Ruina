package ruina.monsters.uninvitedGuests.extra.phillip.cards.malkuth;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FlameBarrierPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class CHRBOSS_EncroachingMalice extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_EncroachingMalice.class.getSimpleName());
    public static final int BLOCK = 12;
    public static final int UPG_BLOCK = 4;
    public static final int TIMES = 3;
    public static final int TEMP_THORNS = 6;

    public CHRBOSS_EncroachingMalice() {
        super(ID, 3, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        block = baseBlock = BLOCK;
        magicNumber = baseMagicNumber = TIMES;
        secondMagicNumber = baseSecondMagicNumber = TEMP_THORNS;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for(int i = 0; i < magicNumber; i += 1){ block(p, block); }
        applyToSelf(new FlameBarrierPower(p, secondMagicNumber));
    }

    @Override
    public void upp() {
        upgradeBlock(UPG_BLOCK);
    }


    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_EncroachingMalice(); }
}