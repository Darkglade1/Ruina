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
public class SerumK extends AbstractRuinaCard {
    public final static String ID = makeID(SerumK.class.getSimpleName());
    private Baral parent;

    public SerumK(Baral parent) {
        super(ID, -2, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        block = baseBlock = parent.SERUM_K_BLOCK;
        magicNumber = baseMagicNumber = parent.SERUM_K_HEAL;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new SerumK(parent); }
}