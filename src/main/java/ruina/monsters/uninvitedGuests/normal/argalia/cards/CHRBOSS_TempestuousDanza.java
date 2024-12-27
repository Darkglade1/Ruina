package ruina.monsters.uninvitedGuests.normal.argalia.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Argalia;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_TempestuousDanza extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_TempestuousDanza.class.getSimpleName());

    Argalia parent;

    public CHRBOSS_TempestuousDanza(Argalia parent) {
        super(ID, 7, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
        damage = baseDamage = parent.tempestuousDamage;
        magicNumber = baseMagicNumber = parent.tempestuousHits;
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
        return new CHRBOSS_TempestuousDanza(parent);
    }
}