package ruina.monsters.day49.Angela;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_yanProtect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.block;
import static ruina.util.actionShortcuts.doDmg;

@AutoAdd.Ignore
public class TokenOfFriendship extends AbstractRuinaCard {
    public final static String ID = makeID(TokenOfFriendship.class.getSimpleName());

    private static final int COST = 1;
    public static final int BLOCK = 12;
    public static final int DAMAGE = 8;
    public static final int HEAL = 6;

    public TokenOfFriendship() {
        super(ID, COST, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.SELF, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_yanProtect.class.getSimpleName() + ".png"));
        damage = baseDamage = DAMAGE;
        block = baseBlock = BLOCK;
        magicNumber = baseMagicNumber = HEAL;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        doDmg(m, damage);
        block(p, block);
        atb(new HealAction(p, p, magicNumber));
    }

    public void upp() {
        upgradeBlock(3);
    }
}