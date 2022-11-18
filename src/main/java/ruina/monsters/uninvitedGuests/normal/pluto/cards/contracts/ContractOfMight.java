package ruina.monsters.uninvitedGuests.normal.pluto.cards.contracts;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.att;

@AutoAdd.Ignore
public class ContractOfMight extends AbstractRuinaCard {
    public final static String ID = makeID(ContractOfMight.class.getSimpleName());
    private static final int STRENGTH = 6;
    public static final int CARD_LIMIT = 6;

    public ContractOfMight() {
        super(ID, -2, CardType.POWER, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = STRENGTH;
        secondMagicNumber = baseSecondMagicNumber = CARD_LIMIT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        att(new ApplyPowerAction(adp(), adp(), new StrengthPower(adp(), magicNumber)));
        att(new ApplyPowerAction(adp(), adp(), new ruina.powers.ContractOfMight(adp(), secondMagicNumber)));
    }
}