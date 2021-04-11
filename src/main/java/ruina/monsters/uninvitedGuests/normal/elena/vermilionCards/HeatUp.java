package ruina.monsters.uninvitedGuests.normal.elena.vermilionCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.elena.VermilionCross;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class HeatUp extends AbstractRuinaCard {
    public final static String ID = makeID(HeatUp.class.getSimpleName());
    VermilionCross parent;

    public HeatUp(VermilionCross parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO, makeImagePath("cards/" + Obstruct.class.getSimpleName() + ".png"));
        baseBlock = parent.HEAT_UP_BLOCK;
        magicNumber = baseMagicNumber = parent.STRENGTH;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new HeatUp(parent);
    }
}