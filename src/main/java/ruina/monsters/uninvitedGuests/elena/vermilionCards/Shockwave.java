package ruina.monsters.uninvitedGuests.elena.vermilionCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.elena.VermilionCross;
import ruina.monsters.uninvitedGuests.puppeteer.Puppeteer;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Shockwave extends AbstractRuinaCard {
    public final static String ID = makeID(Shockwave.class.getSimpleName());
    VermilionCross parent;

    public Shockwave(VermilionCross parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.allyIntangible;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Shockwave(parent);
    }
}