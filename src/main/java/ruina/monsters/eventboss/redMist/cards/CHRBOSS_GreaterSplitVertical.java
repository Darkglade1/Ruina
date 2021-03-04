package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_GreaterSplitVertical extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_GreaterSplitVertical.class.getSimpleName());

    public static final int DAMAGE = 35;

    public CHRBOSS_GreaterSplitVertical(RedMist parent) {
        super(ID, 4, CardType.ATTACK, CardTarget.SELF);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = parent.GSVBleed;
    }

    @Override
    public float getTitleFontSize()
    {
        return 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}