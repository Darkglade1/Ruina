package ruina.monsters.day49.Aspiration;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.day49.AngelaD49;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanProtect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Aspiration extends AbstractRuinaCard {
    public final static String ID = makeID(Aspiration.class.getSimpleName());

    private static final int COST = 2;
    private AngelaD49 angela;
    public Aspiration(AngelaD49 parent) {
        super(ID, COST, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.SELF, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_yanProtect.class.getSimpleName() + ".png"));
        angela = parent;
        damage = baseDamage = angela.aspirationDamage;
        magicNumber = baseMagicNumber = angela.aspirationHits;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    public void upp() {
        upgradeBlock(3);
    }
}