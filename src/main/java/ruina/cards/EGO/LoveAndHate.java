package ruina.cards.EGO;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.MindblastEffect;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class LoveAndHate extends AbstractEgoCard {
    public final static String ID = makeID(LoveAndHate.class.getSimpleName());

    public static final int DAMAGE = 42;
    public static final int UP_DAMAGE = 12;
    public static final int COST = 4;

    public LoveAndHate() {
        super(ID, COST, CardType.ATTACK, CardTarget.ALL_ENEMY);
        baseDamage = DAMAGE;
        isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new SFXAction("ATTACK_HEAVY"));
        atb(new VFXAction(p, new MindblastEffect(p.dialogX, p.dialogY, p.flipHorizontal), 0.1F));
        allDmg(AbstractGameAction.AttackEffect.NONE);
    }

    @Override
    public float getTitleFontSize()
    {
        return 11;
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
    }
}