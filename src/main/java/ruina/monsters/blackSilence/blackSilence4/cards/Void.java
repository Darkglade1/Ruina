package ruina.monsters.blackSilence.blackSilence4.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Void extends AbstractRuinaCard {
    public final static String ID = makeID(Void.class.getSimpleName());
    BlackSilence4 parent;

    public Void(BlackSilence4 parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
        baseBlock = parent.BLOCK;
        magicNumber = baseMagicNumber = parent.NUM_VOIDS;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Void(parent);
    }
}