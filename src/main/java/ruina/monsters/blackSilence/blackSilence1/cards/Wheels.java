package ruina.monsters.blackSilence.blackSilence1.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_Wheels;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Wheels extends AbstractRuinaCard {
    public final static String ID = makeID(Wheels.class.getSimpleName());

    public Wheels(BlackSilence1 parent) {
        super(ID, 4, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRALLY_Wheels.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.wheelsDamage;
        magicNumber = baseMagicNumber = parent.wheelsStrDown;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}