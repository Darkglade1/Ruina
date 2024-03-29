package ruina.monsters.theHead.zenaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.theHead.Zena;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Birdcage extends AbstractRuinaCard {
    public final static String ID = makeID(Birdcage.class.getSimpleName());
    private Zena parent;

    public Birdcage(Zena parent) {
        super(ID, 4, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + ThinLine.class.getSimpleName() + ".png"));
        this.magicNumber = baseMagicNumber = parent.BIRDCAGE_HITS;
        this.secondMagicNumber = baseSecondMagicNumber = parent.BIRDCAGE_FAIRY;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new Birdcage(parent); }
}