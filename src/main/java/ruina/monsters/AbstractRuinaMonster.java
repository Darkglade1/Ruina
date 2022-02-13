package ruina.monsters;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public abstract class AbstractRuinaMonster extends CustomMonster {

    protected Map<Byte, EnemyMoveInfo> moves;
    protected boolean firstMove = true;
    private static final float ASCENSION_DAMAGE_BUFF_PERCENT = 1.10f;
    private static final float ASCENSION_TANK_BUFF_PERCENT = 1.10f;
    private static final float ASCENSION_SPECIAL_BUFF_PERCENT = 1.5f;
    private static final float ASCENSION_TANK_NERF_PERCENT = 0.85f;

    public AbstractRuinaMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        setUpMisc();
    }

    public AbstractRuinaMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        setUpMisc();
    }

    public AbstractRuinaMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
        setUpMisc();
    }


    protected void setUpMisc() {
        moves = new HashMap<>();
        this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;
    }

    protected void addMove(byte moveCode, Intent intent) {
        this.addMove(moveCode, intent, -1);
    }

    protected void addMove(byte moveCode, Intent intent, int baseDamage) {
        this.addMove(moveCode, intent, baseDamage, 0);
    }

    protected void addMove(byte moveCode, Intent intent, int baseDamage, int multiplier) {
        this.addMove(moveCode, intent, baseDamage, multiplier, false);
    }

    protected void addMove(byte moveCode, Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {
        this.moves.put(moveCode, new EnemyMoveInfo(moveCode, intent, baseDamage, multiplier, isMultiDamage));
    }

    public void setMoveShortcut(byte next, String text) {
        EnemyMoveInfo info = this.moves.get(next);
        this.setMove(text, next, info.intent, info.baseDamage, info.multiplier, info.isMultiDamage);
    }

    public void setMoveShortcut(byte next) {
        this.setMoveShortcut(next, null);
    }

    protected int calcAscensionDamage(float base) {
        switch (this.type) {
            case BOSS:
                if (AbstractDungeon.ascensionLevel >= 4) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if (AbstractDungeon.ascensionLevel >= 3) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if (AbstractDungeon.ascensionLevel >= 2) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    protected int calcAscensionTankiness(float base) {
        if (this instanceof AbstractAllyMonster) {
            switch (this.type) {
                case BOSS:
                    if (AbstractDungeon.ascensionLevel >= 9) {
                        base *= ASCENSION_TANK_NERF_PERCENT;
                    }
                    break;
                case ELITE:
                    if (AbstractDungeon.ascensionLevel >= 8) {
                        base *= ASCENSION_TANK_NERF_PERCENT;
                    }
                    break;
                case NORMAL:
                    if (AbstractDungeon.ascensionLevel >= 7) {
                        base *= ASCENSION_TANK_NERF_PERCENT;
                    }
                    break;
            }
        } else {
            switch (this.type) {
                case BOSS:
                    if (AbstractDungeon.ascensionLevel >= 9) {
                        base *= ASCENSION_TANK_BUFF_PERCENT;
                    }
                    break;
                case ELITE:
                    if (AbstractDungeon.ascensionLevel >= 8) {
                        base *= ASCENSION_TANK_BUFF_PERCENT;
                    }
                    break;
                case NORMAL:
                    if (AbstractDungeon.ascensionLevel >= 7) {
                        base *= ASCENSION_TANK_BUFF_PERCENT;
                    }
                    break;
            }
        }
        return Math.round(base);
    }

    protected int calcAscensionSpecial(float base) {
        switch (this.type) {
            case BOSS:
                if (AbstractDungeon.ascensionLevel >= 19) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if (AbstractDungeon.ascensionLevel >= 18) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if (AbstractDungeon.ascensionLevel >= 17) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    @Override
    public void die(boolean triggerRelics) {
        this.useShakeAnimation(5.0F);
        if (this.animation instanceof BetterSpriterAnimation) {
            ((BetterSpriterAnimation) this.animation).startDying();
        }
        super.die(triggerRelics);
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation) this.animation).myPlayer.setAnimation(animation);
    }

    protected void animationAction(String animation, String sound, AbstractCreature enemy, AbstractCreature owner) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner.isDeadOrEscaped()) {
                    isDone = true;
                    return;
                }
                if (enemy == null) {
                    runAnim(animation);
                    playSound(sound);
                } else if (!enemy.isDeadOrEscaped()) {
                    runAnim(animation);
                    playSound(sound);
                }
                this.isDone = true;
            }
        });
    }

    protected void animationAction(String animation, String sound, float volume, AbstractCreature enemy, AbstractCreature owner) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner.isDeadOrEscaped()) {
                    isDone = true;
                    return;
                }
                if (enemy == null) {
                    runAnim(animation);
                    playSound(sound, volume);
                } else if (!enemy.isDeadOrEscaped()) {
                    runAnim(animation);
                    playSound(sound, volume);
                }
                this.isDone = true;
            }
        });
    }

    protected void animationAction(String animation, String sound, AbstractCreature owner) {
        animationAction(animation, sound, null, owner);
    }

    protected void animationAction(String animation, String sound, float volume, AbstractCreature owner) {
        animationAction(animation, sound, volume, null, owner);
    }

    public static void playSound(String sound, float volume) {
        if (sound != null) {
            CardCrawlGame.sound.playV(makeID(sound), volume);
        }
    }

    public static void playSound(String sound) {
        playSound(sound, 1.0f);
    }

    public void resetIdle() {
        resetIdle(0.5f);
    }

    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle");
                this.isDone = true;
            }
        });
    }

    public void waitAnimation() {
        waitAnimation(0.5f, null);
    }

    public void waitAnimation(float duration) {
        waitAnimation(duration, null);
    }

    protected void waitAnimation(AbstractCreature enemy) {
        waitAnimation(0.5f, enemy);
    }

    public void waitAnimation(float time, AbstractCreature enemy) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractRuinaMonster.this.isDeadOrEscaped()) {
                    isDone = true;
                    return;
                }
                if (enemy == null) {
                    att(new VFXActionButItCanFizzle(AbstractRuinaMonster.this, new WaitEffect(), time));
                } else if (!enemy.isDeadOrEscaped()) {
                    att(new VFXActionButItCanFizzle(AbstractRuinaMonster.this, new WaitEffect(), time));
                }
                this.isDone = true;
            }
        });
    }

    public void applyPowersOnlyIncrease(AbstractCreature target, DamageInfo info) {
        info.output = info.base;// 30
        info.isModified = false;// 31
        float tmp = (float) info.output;// 32
        float highest = (float) info.output;
        AbstractPower p;
        Iterator var6;

        if (Settings.isEndless && AbstractDungeon.player.hasBlight("DeadlyEnemies")) {// 37
            float mod = AbstractDungeon.player.getBlight("DeadlyEnemies").effectFloat();// 38
            tmp *= mod;// 39
            if (info.base != (int) tmp) {// 41
                info.isModified = true;// 42
            }
        }

        var6 = this.powers.iterator();// 47

        while (var6.hasNext()) {
            p = (AbstractPower) var6.next();
            tmp = p.atDamageGive(tmp, info.type);// 48
            if (tmp > highest) {
                highest = tmp;
            } else {
                tmp = highest;
            }
            if (info.base != (int) highest) {// 50
                info.isModified = true;// 51
            }
        }

        var6 = target.powers.iterator();// 56

        while (var6.hasNext()) {
            p = (AbstractPower) var6.next();
            tmp = p.atDamageReceive(tmp, info.type);// 57
            if (tmp > highest) {
                highest = tmp;
            } else {
                tmp = highest;
            }
            if (info.base != (int) highest) {// 58
                info.isModified = true;// 59
            }
        }

        tmp = AbstractDungeon.player.stance.atDamageReceive(tmp, info.type);// 64
        if (tmp > highest) {
            highest = tmp;
        } else {
            tmp = highest;
        }
        if (info.base != (int) highest) {// 65
            info.isModified = true;// 66
        }

        var6 = this.powers.iterator();// 70

        while (var6.hasNext()) {
            p = (AbstractPower) var6.next();
            tmp = p.atDamageFinalGive(tmp, info.type);// 71
            if (tmp > highest) {
                highest = tmp;
            } else {
                tmp = highest;
            }
            if (info.base != (int) highest) {// 72
                info.isModified = true;// 73
            }
        }

        var6 = target.powers.iterator();// 78

        while (var6.hasNext()) {
            p = (AbstractPower) var6.next();
            tmp = p.atDamageFinalReceive(tmp, info.type);// 79
            if (tmp > highest) {
                highest = tmp;
            } else {
                tmp = highest;
            }
            if (info.base != (int) highest) {// 72
                info.isModified = true;// 73
            }
        }

        info.output = MathUtils.floor(highest);// 86
        if (info.output < 0) {// 87
            info.output = 0;// 88
        }
    }

}