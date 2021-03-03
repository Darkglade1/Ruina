package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_GreaterSplitHorizontal extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_GreaterSplitHorizontal.class.getSimpleName());

    public static final int DAMAGE = 40;
    public static final int BLEED = 5;

    public CHRBOSS_GreaterSplitHorizontal() {
        super(ID, 5, CardType.ATTACK, CardTarget.SELF);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = BLEED;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}