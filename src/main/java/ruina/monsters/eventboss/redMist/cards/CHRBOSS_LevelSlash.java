package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_LevelSlash extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_LevelSlash.class.getSimpleName());

    public static final int HITS = 2;

    public CHRBOSS_LevelSlash(RedMist parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.level_damage;
        magicNumber = baseMagicNumber = HITS;
        secondMagicNumber = baseSecondMagicNumber = parent.level_threshold;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}