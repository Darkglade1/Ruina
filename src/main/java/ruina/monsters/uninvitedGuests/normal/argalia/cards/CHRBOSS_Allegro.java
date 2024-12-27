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
public class CHRBOSS_Allegro extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_Allegro.class.getSimpleName());

    Argalia parent;

    public CHRBOSS_Allegro(Argalia parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
        damage = baseDamage = parent.allegroDamage;
        magicNumber = baseMagicNumber = parent.allegroHits;
        secondMagicNumber = baseSecondMagicNumber = parent.allegroStrength;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRBOSS_Allegro(parent);
    }
}