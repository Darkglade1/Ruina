package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.WallopEffect;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.util.Wiz;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class Forgotten extends AbstractEgoCard {
    public final static String ID = makeID(Forgotten.class.getSimpleName());
    public static final int DAMAGE = 8;
    private static final int UP_DAMAGE = 3;
    public static final int BLOCK = 8;
    private static final int UP_BLOCK = 3;
    private static final int DEBUFF = 2;

    public Forgotten() {
        super(ID, 2, CardType.ATTACK, CardTarget.ENEMY);
        baseDamage = DAMAGE;
        baseBlock = BLOCK;
        magicNumber = baseMagicNumber = DEBUFF;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Wiz.block(adp(), block);
        atb(new VFXAction(new WallopEffect(damage, m.hb.cX, m.hb.cY)));
        dmg(m, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        if (m.getIntentBaseDmg() >= 0) {
            applyToTarget(m, p, new WeakPower(m, magicNumber, false));
            applyToTarget(m, p, new VulnerablePower(m, magicNumber, false));
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!m.isDeadOrEscaped() && m.getIntentBaseDmg() >= 0) {
                this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
                break;
            }
        }
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
        upgradeBlock(UP_BLOCK);
    }
}