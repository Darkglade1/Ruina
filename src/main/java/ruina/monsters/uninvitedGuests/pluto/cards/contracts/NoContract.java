package ruina.monsters.uninvitedGuests.pluto.cards.contracts;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.AbstractAllyMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

@AutoAdd.Ignore
public class NoContract extends AbstractRuinaCard {
    public final static String ID = makeID(NoContract.class.getSimpleName());
    private static final int STRENGTH = 3;

    public NoContract() {
        super(ID, -2, CardType.POWER, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = STRENGTH;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upp() {
    }

    public void onChoseThisOption() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractMonster m : monsterList()) {
                    if (!(m instanceof AbstractAllyMonster)) {
                        att(new ApplyPowerAction(m, m, new StrengthPower(m, magicNumber)));
                    }
                }
                isDone = true;
            }
        });
    }
}