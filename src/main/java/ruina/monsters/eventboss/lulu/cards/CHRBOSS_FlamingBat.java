package ruina.monsters.eventboss.lulu.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.lulu.monster.Lulu;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_FlamingBat extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_FlamingBat.class.getSimpleName());
    private Lulu parent;

    public CHRBOSS_FlamingBat(Lulu parent) {
        super(ID, 1, CardType.ATTACK, CardRarity.RARE, CardTarget.SELF, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.FLAMING_BAT_DAMAGE;
        magicNumber = baseMagicNumber = parent.FLAMING_BAT_VULNERABLE;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_FlamingBat(parent); }
}