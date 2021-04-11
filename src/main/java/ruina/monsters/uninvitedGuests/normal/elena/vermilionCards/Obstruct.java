package ruina.monsters.uninvitedGuests.normal.elena.vermilionCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.elena.VermilionCross;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Obstruct extends AbstractRuinaCard {
    public final static String ID = makeID(Obstruct.class.getSimpleName());
    VermilionCross parent;

    public Obstruct(VermilionCross parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        baseBlock = parent.OBSTRUCT_BLOCK;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Obstruct(parent);
    }
}