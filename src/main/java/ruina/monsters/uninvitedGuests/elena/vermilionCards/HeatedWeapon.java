package ruina.monsters.uninvitedGuests.elena.vermilionCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.elena.VermilionCross;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class HeatedWeapon extends AbstractRuinaCard {
    public final static String ID = makeID(HeatedWeapon.class.getSimpleName());
    VermilionCross parent;

    public HeatedWeapon(VermilionCross parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + Shockwave.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = parent.heatedWeaponHits;
        secondMagicNumber = baseSecondMagicNumber = parent.BURNS;
        this.parent = parent;
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new HeatedWeapon(parent);
    }
}