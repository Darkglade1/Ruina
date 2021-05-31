package ruina.cards.EGO.act1.orchestra;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static com.evacipated.cardcrawl.mod.stslib.StSLib.getMasterDeckEquivalent;
import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class Finale extends AbstractEgoCard {
    public final static String ID = makeID(Finale.class.getSimpleName());
    public static final int DAMAGE = 20;
    public static final int UP_DAMAGE = 3;

    public Finale() {
        super(ID, 1, CardType.ATTACK, CardTarget.ALL_ENEMY);
        damage = baseDamage = DAMAGE;
        isMultiDamage = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        allDmg(AbstractGameAction.AttackEffect.BLUNT_HEAVY);
    }

    @Override
    public void upp() { upgradeDamage(UP_DAMAGE); }
}