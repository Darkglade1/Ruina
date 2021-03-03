package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_UpstandingSlash extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_UpstandingSlash.class.getSimpleName());

    public static final int DAMAGE = 7;
    public static final int HITS = 2;
    public static final int THRESHOLD = 7;

    public CHRBOSS_UpstandingSlash() {
        super(ID, 2, CardType.ATTACK, CardTarget.SELF);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = HITS;
        secondMagicNumber = baseSecondMagicNumber = THRESHOLD;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}