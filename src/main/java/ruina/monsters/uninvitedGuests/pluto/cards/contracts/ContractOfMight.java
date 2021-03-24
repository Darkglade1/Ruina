package ruina.monsters.uninvitedGuests.pluto.cards.contracts;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConfusionPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

@AutoAdd.Ignore
public class ContractOfMight extends AbstractRuinaCard {
    public final static String ID = makeID(ContractOfMight.class.getSimpleName());
    private static final int STRENGTH = 5;
    public static final int SELF_DAMAGE = 1;

    public ContractOfMight() {
        super(ID, -2, CardType.POWER, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = STRENGTH;
        secondMagicNumber = baseSecondMagicNumber = SELF_DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChooseThisOption(){
        addToBot(new ApplyPowerAction(adp(), adp(), new StrengthPower(adp(), magicNumber)));
        addToBot(new ApplyPowerAction(adp(), adp(), new ruina.powers.ContractOfMight(adp(), secondMagicNumber)));
    }
}