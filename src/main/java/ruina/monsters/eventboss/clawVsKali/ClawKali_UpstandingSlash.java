package ruina.monsters.eventboss.clawVsKali;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_UpstandingSlash;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class ClawKali_UpstandingSlash extends AbstractRuinaCard {
    public final static String ID = makeID(ClawKali_UpstandingSlash.class.getSimpleName());

    private ClawKali parent;

    public ClawKali_UpstandingSlash(ClawKali parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_UpstandingSlash.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.upstanding_damage;
        magicNumber = baseMagicNumber = parent.UPSTANDING_SLASH_DEBUFF;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() {  }

    @Override
    public AbstractCard makeCopy() {
        return new ClawKali_UpstandingSlash(parent);
    }
}