package ruina.monsters.theHead.zenaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.theHead.Zena;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Line extends AbstractRuinaCard {
    public final static String ID = makeID(Line.class.getSimpleName());
    private Zena parent;

    public Line(Zena parent) {
        super(ID, -2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        block = baseBlock = parent.BLOCK;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new Line(parent); }
}