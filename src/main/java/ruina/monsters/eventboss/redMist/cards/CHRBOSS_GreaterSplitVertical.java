package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_GreaterSplitVertical extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_GreaterSplitVertical.class.getSimpleName());
    RedMist parent;

    public CHRBOSS_GreaterSplitVertical(RedMist parent) {
        super(ID, 5, CardType.ATTACK, CardTarget.ENEMY);
        this.parent = parent;
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

    @Override
    public AbstractCard makeCopy() {
        return new CHRBOSS_GreaterSplitVertical(parent);
    }
}