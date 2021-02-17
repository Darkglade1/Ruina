package ruina.monsters;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.HashMap;
import java.util.Map;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public abstract class AbstractRuinaMonster extends CustomMonster {

    protected Map<Byte, EnemyMoveInfo> moves;
    protected boolean firstMove = true;
    private static final float ASCENSION_DAMAGE_BUFF_PERCENT = 1.10f;
    private static final float ASCENSION_TANK_BUFF_PERCENT = 1.10f;
    private static final float ASCENSION_SPECIAL_BUFF_PERCENT = 1.5f;

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

    private void setUpMisc() {
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
                if(AbstractDungeon.ascensionLevel >= 4) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if(AbstractDungeon.ascensionLevel >= 3) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if(AbstractDungeon.ascensionLevel >= 2) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    protected int calcAscensionTankiness(float base) {
        switch (this.type) {
            case BOSS:
                if(AbstractDungeon.ascensionLevel >= 9) {
                    base *= ASCENSION_TANK_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if(AbstractDungeon.ascensionLevel >= 8) {
                    base *= ASCENSION_TANK_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if(AbstractDungeon.ascensionLevel >= 7) {
                    base *= ASCENSION_TANK_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    protected int calcAscensionSpecial(float base) {
        switch (this.type) {
            case BOSS:
                if(AbstractDungeon.ascensionLevel >= 19) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if(AbstractDungeon.ascensionLevel >= 18) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if(AbstractDungeon.ascensionLevel >= 17) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    @Override
    public void die(boolean triggerRelics) {
        this.useShakeAnimation(5.0F);
        ((BetterSpriterAnimation)this.animation).startDying();
        super.die(triggerRelics);
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
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

    public void playSound(String sound, float volume) {
        if (sound != null) {
            CardCrawlGame.sound.playV(makeID(sound), volume);
        }
    }

    public void playSound(String sound) {
        playSound(sound, 1.0f);
    }

    protected void resetIdle() {
        resetIdle(0.5f);
    }

    protected void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle");
                this.isDone = true;
            }
        });
    }

}