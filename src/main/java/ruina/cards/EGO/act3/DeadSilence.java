package ruina.cards.EGO.act3;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.EnergizedPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.WhirlwindEffect;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class DeadSilence extends AbstractEgoCard {
    public final static String ID = makeID(DeadSilence.class.getSimpleName());

    public static final int DAMAGE = 20;
    public static final int COST = 4;
    public static final int UP_COST = 3;
    public static final int ENERGY = 2;

    public DeadSilence() {
        super(ID, COST, CardType.ATTACK, CardTarget.ENEMY);
        baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = ENERGY;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        DamageInfo info = new DamageInfo(AbstractDungeon.player, damage, damageTypeForTurn);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                target = m;
                if (this.target != null) {
                    AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SLASH_HEAVY));
                    this.target.damage(info);
                    if ((this.target.isDying || this.target.currentHealth <= 0) && !this.target.halfDead) {
                        att(new ApplyPowerAction(adp(), adp(), new EnergizedPower(adp(), magicNumber), magicNumber));
                    }

                    if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                        AbstractDungeon.actionManager.clearPostCombatActions();
                    }
                }
                this.isDone = true;
            }
        });
        atb(new VFXAction(new WhirlwindEffect(new Color(1.0F, 0.9F, 0.4F, 1.0F), true)));
        atb(new SkipEnemiesTurnAction());
    }

    @Override
    public void upp() {
        upgradeBaseCost(UP_COST);
    }
}