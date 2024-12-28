package ruina.monsters.uninvitedGuests.normal.pluto.cards.contracts;

import basemod.AutoAdd;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ConfusionPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.att;

@AutoAdd.Ignore
public class ConfusingContract extends AbstractRuinaCard {
    public final static String ID = makeID(ConfusingContract.class.getSimpleName());
    public ConfusingContract() {
        super(ID, -2, CardType.POWER, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        AbstractPower confusion = new ConfusionPower(adp());
        confusion.type = NeutralPowertypePatch.NEUTRAL;
        att(new ApplyPowerAction(adp(), adp(), confusion));
    }
}