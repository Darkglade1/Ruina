package ruina.monsters.eventboss.clawVsKali;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_FocusSpirit;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class ClawKali_FocusSpirit extends AbstractRuinaCard {
    public final static String ID = makeID(ClawKali_FocusSpirit.class.getSimpleName());

    private ClawKali parent;

    public ClawKali_FocusSpirit(ClawKali parent) {
        super(ID, 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_FocusSpirit.class.getSimpleName() + ".png"));
        block = baseBlock = parent.focusSpiritBlock;
        magicNumber = baseMagicNumber = parent.focusSpiritStr;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new ClawKali_FocusSpirit(parent);
    }
}