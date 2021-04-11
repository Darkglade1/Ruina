package ruina.monsters.uninvitedGuests.normal.pluto.cards.contracts;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

@AutoAdd.Ignore
public class ContractOfLight extends AbstractRuinaCard {
    public final static String ID = makeID(ContractOfLight.class.getSimpleName());

    public ContractOfLight() {
        super(ID, -2, CardType.POWER, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        addToBot(new ApplyPowerAction(adp(), adp(), new ruina.powers.ContractOfLight(adp())));
    }
}