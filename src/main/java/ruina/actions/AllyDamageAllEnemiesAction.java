package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import ruina.RuinaMod;
import spireTogether.patches.combatsync.ActionPatches;

public class AllyDamageAllEnemiesAction extends AbstractGameAction {
    public int[] damage;
    private int baseDamage;
    private boolean firstFrame;
    private boolean utilizeBaseDamage;

    public AllyDamageAllEnemiesAction(AbstractCreature source, int[] amount, DamageType type, AttackEffect effect, boolean isFast) {
        this.firstFrame = true;
        this.utilizeBaseDamage = false;
        this.source = source;
        this.damage = amount;
        this.actionType = ActionType.DAMAGE;
        this.damageType = type;
        this.attackEffect = effect;
        if (isFast) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FAST;
        }
        if (RuinaMod.isMultiplayerConnected()) {
            //ActionPatches.markActionForNoDataSync(this);
            //ActionPatches.markActionForNoDamageSync(this);
        }
    }

    public AllyDamageAllEnemiesAction(AbstractCreature source, int[] amount, DamageType type, AttackEffect effect) {
        this(source, amount, type, effect, false);
    }

    public void update() {
        int i;
        if (this.firstFrame) {
            boolean playedMusic = false;
            i = AbstractDungeon.getCurrRoom().monsters.monsters.size();
            if (this.utilizeBaseDamage) {
                this.damage = DamageInfo.createDamageMatrix(this.baseDamage);
            }

            for(i = 0; i < i; ++i) {
                if (!((AbstractMonster)AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).isDying && ((AbstractMonster)AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).currentHealth > 0 && !((AbstractMonster)AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).isEscaping) {
                    if (playedMusic) {
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(((AbstractMonster)AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).hb.cX, ((AbstractMonster)AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).hb.cY, this.attackEffect, true));
                    } else {
                        playedMusic = true;
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(((AbstractMonster)AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).hb.cX, ((AbstractMonster)AbstractDungeon.getCurrRoom().monsters.monsters.get(i)).hb.cY, this.attackEffect));
                    }
                }
            }

            this.firstFrame = false;
        }

        this.tickDuration();
        if (this.isDone && !source.isDeadOrEscaped()) {

            int temp = AbstractDungeon.getMonsters().monsters.size();

            for(i = 0; i < temp; ++i) {
                AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                if (!mo.isDeadOrEscaped() && mo != source) {
                    mo.damage(new DamageInfo(this.source, this.damage[i], this.damageType));
                }
            }

            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }

            if (!Settings.FAST_MODE) {
                this.addToTop(new WaitAction(0.1F));
            }
        }

    }
}
