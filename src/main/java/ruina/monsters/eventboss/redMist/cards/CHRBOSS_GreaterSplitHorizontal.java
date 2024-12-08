package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_GreaterSplitHorizontal extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_GreaterSplitHorizontal.class.getSimpleName());
    RedMist parent;

    public CHRBOSS_GreaterSplitHorizontal(RedMist parent) {
        super(ID, 6, CardType.ATTACK, CardTarget.ENEMY);
        this.parent = parent;
        magicNumber = baseMagicNumber = parent.GSHBleed;
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
        return new CHRBOSS_GreaterSplitHorizontal(parent);
    }
}