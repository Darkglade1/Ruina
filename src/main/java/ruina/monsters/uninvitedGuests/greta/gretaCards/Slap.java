package ruina.monsters.uninvitedGuests.greta.gretaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.greta.Greta;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Slap extends AbstractRuinaCard {
    public final static String ID = makeID(Slap.class.getSimpleName());
    Greta parent;

    public Slap(Greta parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        baseBlock = parent.BLOCK;
        magicNumber = baseMagicNumber = parent.BLEED;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Slap(parent);
    }
}