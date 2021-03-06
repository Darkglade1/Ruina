package ruina.monsters.uninvitedGuests.normal.argalia.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Argalia;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_TrailsOfBlue extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_TrailsOfBlue.class.getSimpleName());

    public CHRBOSS_TrailsOfBlue(Argalia parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.trailsDamage;
        magicNumber = baseMagicNumber = parent.trailsStrengthLoss;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}