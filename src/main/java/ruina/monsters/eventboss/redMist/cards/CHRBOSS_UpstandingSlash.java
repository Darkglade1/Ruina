package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_UpstandingSlash extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_UpstandingSlash.class.getSimpleName());
    RedMist parent;

    public CHRBOSS_UpstandingSlash(RedMist parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
        damage = baseDamage = parent.upstanding_damage;
        magicNumber = baseMagicNumber = parent.UPSTANDING_SLASH_DEBUFF;
        secondMagicNumber = baseSecondMagicNumber = parent.upstanding_threshold;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new CHRBOSS_UpstandingSlash(parent);
    }
}