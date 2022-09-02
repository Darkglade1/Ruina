package ruina.monsters.eventboss.kim;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Acupuncture extends AbstractRuinaCard {
    public final static String ID = makeID(Acupuncture.class.getSimpleName());
    private Kim parent;

    public Acupuncture(Kim parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.ACUPUNCTURE_HITS;
        secondMagicNumber = baseSecondMagicNumber = parent.PARALYSIS;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new Acupuncture(parent); }
}