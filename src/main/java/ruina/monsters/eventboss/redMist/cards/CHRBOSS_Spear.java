package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_Spear extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_Spear.class.getSimpleName());

    public static final int DAMAGE = 6;
    public static final int HITS = 3;

    public CHRBOSS_Spear() {
        super(ID, 2, CardType.ATTACK, CardTarget.SELF);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = HITS;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}