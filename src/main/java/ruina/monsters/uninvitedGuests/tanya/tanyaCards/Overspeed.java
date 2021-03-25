package ruina.monsters.uninvitedGuests.tanya.tanyaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.tanya.Tanya;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Overspeed extends AbstractRuinaCard {
    public final static String ID = makeID(Overspeed.class.getSimpleName());
    Tanya parent;

    public Overspeed(Tanya parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
        magicNumber = baseMagicNumber = parent.overspeedHits;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Overspeed(parent);
    }
}