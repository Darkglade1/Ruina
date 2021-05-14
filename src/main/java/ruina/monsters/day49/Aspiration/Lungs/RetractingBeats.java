package ruina.monsters.day49.Aspiration.Lungs;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanProtect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class RetractingBeats extends AbstractRuinaCard {
    public final static String ID = makeID(RetractingBeats.class.getSimpleName());

    private static final int COST = 2;
    private LungsOfCravingD49 lungs;
    public RetractingBeats(LungsOfCravingD49 parent) {
        super(ID, COST, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.SELF, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_yanProtect.class.getSimpleName() + ".png"));
        lungs = parent;
        damage = baseDamage = lungs.RETRACTINGDAMAGE;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    public void upp() {
        upgradeBlock(3);
    }
}