package ruina.monsters.eventboss.clawVsKali;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_LevelSlash;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class ClawKali_LevelSlash extends AbstractRuinaCard {
    public final static String ID = makeID(ClawKali_LevelSlash.class.getSimpleName());

    public ClawKali_LevelSlash(ClawKali parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_LevelSlash.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.level_damage;
        magicNumber = baseMagicNumber = parent.level_strength;
        secondMagicNumber = baseSecondMagicNumber = parent.level_threshold;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}