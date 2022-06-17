package ruina.monsters.eventboss.clawVsKali;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_UpstandingSlash;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class ClawKali_UpstandingSlash extends AbstractRuinaCard {
    public final static String ID = makeID(ClawKali_UpstandingSlash.class.getSimpleName());

    public ClawKali_UpstandingSlash(ClawKali parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_UpstandingSlash.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.upstanding_damage;
        magicNumber = baseMagicNumber = parent.UPSTANDING_SLASH_DEBUFF;
        secondMagicNumber = baseSecondMagicNumber = parent.upstanding_threshold;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() {  }
}