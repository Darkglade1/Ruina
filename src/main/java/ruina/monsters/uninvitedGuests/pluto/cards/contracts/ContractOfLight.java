package ruina.monsters.uninvitedGuests.pluto.cards.contracts;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ConfusionPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

@AutoAdd.Ignore
public class ContractOfLight extends AbstractRuinaCard {
    public final static String ID = makeID(ContractOfLight.class.getSimpleName());
    private static final int DAMAGE_AMOUNT = 8;

    public ContractOfLight() {
        super(ID, -2, CardType.POWER, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = DAMAGE_AMOUNT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChooseThisOption(){
        addToBot(new ApplyPowerAction(adp(), adp(), new ruina.powers.ContractOfLight(adp())));
    }
}