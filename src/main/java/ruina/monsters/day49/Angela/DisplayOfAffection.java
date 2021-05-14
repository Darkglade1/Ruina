package ruina.monsters.day49.Angela;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.watcher.WallopAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanProtect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.block;

@AutoAdd.Ignore
public class DisplayOfAffection extends AbstractRuinaCard {
    public final static String ID = makeID(DisplayOfAffection.class.getSimpleName());

    private static final int COST = 3;
    private static final int DAMAGE = 12;
    private static final int HITS = 3;

    public DisplayOfAffection() {
        super(ID, COST, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.SELF, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_yanProtect.class.getSimpleName() + ".png"));
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = HITS;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        for(int i = 0; i < magicNumber; i += 1){ atb(new WallopAction(m, new DamageInfo(p, damage))); }
    }

    public void upp() {
        upgradeBlock(3);
    }
}