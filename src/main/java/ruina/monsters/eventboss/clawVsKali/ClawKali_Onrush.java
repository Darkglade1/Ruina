package ruina.monsters.eventboss.clawVsKali;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class ClawKali_Onrush extends AbstractRuinaCard {
    public final static String ID = makeID(ClawKali_Onrush.class.getSimpleName());

    private ClawKali parent;

    public ClawKali_Onrush(ClawKali parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.baseBlock = parent.onrushBlock;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new ClawKali_Onrush(parent);
    }
}