package ruina.monsters.theHead.baralCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.theHead.Baral;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class TriSerum extends AbstractRuinaCard {
    public final static String ID = makeID(TriSerum.class.getSimpleName());
    private Baral parent;

    public TriSerum(Baral parent) {
        super(ID, 4, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.triSerumDamage;
        magicNumber = baseMagicNumber = parent.triSerumHits;
        secondMagicNumber = baseSecondMagicNumber = parent.STATUS;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new TriSerum(parent); }
}