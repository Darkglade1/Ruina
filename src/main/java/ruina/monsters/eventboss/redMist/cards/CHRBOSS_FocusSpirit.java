package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_FocusSpirit extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_FocusSpirit.class.getSimpleName());
    RedMist parent;

    public CHRBOSS_FocusSpirit(RedMist parent) {
        super(ID, 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        this.parent = parent;
        block = baseBlock = parent.focusSpiritBlock;
        magicNumber = baseMagicNumber = parent.focusSpiritStr;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRBOSS_FocusSpirit(parent);
    }
}