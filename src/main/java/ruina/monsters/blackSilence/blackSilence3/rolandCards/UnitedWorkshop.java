package ruina.monsters.blackSilence.blackSilence3.rolandCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.blackSilence.blackSilence3.BlackSilence3;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_MOOK;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class UnitedWorkshop extends AbstractRuinaCard {
    public final static String ID = makeID(UnitedWorkshop.class.getSimpleName());
    private BlackSilence3 parent;

    public UnitedWorkshop(BlackSilence3 parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.unitedDamage;
        magicNumber = baseMagicNumber = parent.unitedHits;
        this.parent = parent;

    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new UnitedWorkshop(parent); }
}