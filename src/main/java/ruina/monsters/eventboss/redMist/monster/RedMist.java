package ruina.monsters.eventboss.redMist.monster;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.eventboss.redMist.cards.*;
import ruina.powers.Bleed;
import ruina.powers.act3.RedMistPower;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class RedMist extends AbstractCardMonster {
    public static final String ID = makeID(RedMist.class.getSimpleName());

    private static final byte FOCUS_SPIRIT = 0;
    private static final byte UPSTANDING_SLASH = 1;
    private static final byte LEVEL_SLASH = 2;
    private static final byte SPEAR = 3;
    private static final byte GSV = 4;
    private static final byte GSH = 5;

    public final int focusSpiritBlock = calcAscensionTankiness(12);
    public final int focusSpiritStr = calcAscensionSpecial(2);
    public final int GSVBleed = calcAscensionSpecial(3);
    public final int GSHBleed = calcAscensionSpecial(5);

    public final int upstanding_damage = calcAscensionDamage(11);
    public final int upstanding_threshold = upstanding_damage;
    public final int UPSTANDING_SLASH_DEBUFF = calcAscensionSpecial(2);

    public final int level_damage = calcAscensionDamage(10);
    public final int level_threshold = level_damage;
    public final int levelStr = calcAscensionSpecial(2);

    public final int spearHits = 3;

    public final int greaterSplitVerticalDamage = calcAscensionDamage(35);
    public final int greaterSplitHorizontalDamage = calcAscensionDamage(40);

    private static final int KALI_PHASE = 1;
    private static final int EGO_PHASE = 2;
    private int phase = KALI_PHASE;

    private boolean EGO = false;
    private final int egoExtraActions = 1;
    public static final float HP_THRESHOLD = 0.5f;

    private static final int GREATER_SPLIT_COOLDOWN = 3;
    private int greaterSplitCooldownCounter = GREATER_SPLIT_COOLDOWN;

    public RedMist() {
        this(0.0f, 0.0f);
    }

    public RedMist(final float x, final float y) {
        super(ID, ID, 300, -5.0F, 0, 250.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RedMist/Spriter/RedMist.scml"));
        this.setHp(calcAscensionTankiness(300));

        addMove(FOCUS_SPIRIT, Intent.DEFEND_BUFF);
        addMove(UPSTANDING_SLASH, Intent.ATTACK_DEBUFF, upstanding_damage, 2);
        addMove(LEVEL_SLASH, Intent.ATTACK_BUFF, level_damage, 2);
        addMove(SPEAR, Intent.ATTACK, calcAscensionDamage(8), spearHits);
        addMove(GSV, Intent.ATTACK_DEBUFF, greaterSplitVerticalDamage);
        addMove(GSH, Intent.ATTACK_DEBUFF, greaterSplitHorizontalDamage);

        cardList.add(new CHRBOSS_FocusSpirit(this));
        cardList.add(new CHRBOSS_UpstandingSlash(this));
        cardList.add(new CHRBOSS_LevelSlash(this));
        cardList.add(new CHRBOSS_Spear(this));
        cardList.add(new CHRBOSS_GreaterSplitVertical(this));
        cardList.add(new CHRBOSS_GreaterSplitHorizontal(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Gebura2");
        applyToTarget(this, this, new RedMistPower(this, (int)(HP_THRESHOLD * 100)));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        final int[] threshold = {0};
        switch (move.nextMove) {
            case FOCUS_SPIRIT: {
                blockAnimation();
                block(this, focusSpiritBlock);
                applyToTarget(this, this, new StrengthPower(this, focusSpiritStr));
                resetIdle();
                break;
            }
            case UPSTANDING_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        upstandingAnimation(adp());
                    } else {
                        levelAnimation(adp());
                    }
                    dmg(adp(), info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += adp().lastDamageTaken;
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (threshold[0] >= upstanding_threshold) {
                            applyToTargetTop(adp(), RedMist.this, new FrailPower(adp(), UPSTANDING_SLASH_DEBUFF, true));
                        }
                        isDone = true;
                    }
                });

                break;
            }
            case LEVEL_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        levelAnimation(adp());
                    } else {
                        upstandingAnimation(adp());
                    }
                    dmg(adp(), info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += adp().lastDamageTaken;
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (threshold[0] >= level_threshold) {
                            applyToTargetTop(RedMist.this, RedMist.this, new StrengthPower(RedMist.this, levelStr));
                        }
                        isDone = true;
                    }
                });
                break;
            }
            case SPEAR: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        spearAnimation(adp());
                    } else {
                        upstandingAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case GSV: {
                verticalUpAnimation(adp());
                atb(new VFXAction(new WaitEffect(), 0.25f));
                verticalSplitVfx();
                verticalDownAnimation(adp());
                dmg(adp(), info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (adp().lastDamageTaken > 0) {
                            applyToTargetTop(adp(), RedMist.this, new Bleed(adp(), GSVBleed));
                        }
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        greaterSplitCooldownCounter = GREATER_SPLIT_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case GSH: {
                horizontalSplitVfx();
                horizontalAnimation(adp());
                dmg(adp(), info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (adp().lastDamageTaken > 0) {
                            applyToTargetTop(adp(), RedMist.this, new Bleed(adp(), GSHBleed));
                        }
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        greaterSplitCooldownCounter = GREATER_SPLIT_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
        }
    }

    private void upstandingAnimation(AbstractCreature enemy) {
        animationAction("Upstanding" + phase, "RedMistVert" + phase, enemy, this);
    }

    private void spearAnimation(AbstractCreature enemy) {
        animationAction("Spear" + phase, "RedMistStab" + phase, enemy, this);
    }

    private void levelAnimation(AbstractCreature enemy) {
        animationAction("Level" + phase, "RedMistHori" + phase, enemy, this);
    }

    private void verticalUpAnimation(AbstractCreature enemy) {
        animationAction("VerticalUp" + phase, "RedMistVertHit", enemy, this);
    }

    private void verticalDownAnimation(AbstractCreature enemy) {
        animationAction("VerticalDown" + phase, "RedMistVertFin", enemy, this);
    }

    private void horizontalAnimation(AbstractCreature enemy) {
        animationAction("Horizontal", "RedMistHoriFin", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block" + phase, null, this);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + phase);
                this.isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                greaterSplitCooldownCounter -= 1;
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (!EGO) {
                    CheckEGOTrigger();
                }
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (greaterSplitCooldownCounter <= 0) {
            if (EGO) {
                setMoveShortcut(GSH, MOVES[GSH], new CHRBOSS_GreaterSplitHorizontal(this));
            } else {
                setMoveShortcut(GSV, MOVES[GSV], new CHRBOSS_GreaterSplitVertical(this));
            }
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (EGO) {
                if (!this.lastMove(UPSTANDING_SLASH)) {
                    possibilities.add(UPSTANDING_SLASH);
                }
                if (!this.lastMove(SPEAR)) {
                    possibilities.add(SPEAR);
                }
                if (!this.lastMove(LEVEL_SLASH)) {
                    possibilities.add(LEVEL_SLASH);
                }
            } else {
                if (!this.lastMove(FOCUS_SPIRIT) && !this.lastMoveBefore(FOCUS_SPIRIT)) {
                    possibilities.add(FOCUS_SPIRIT);
                }
                if (!this.lastMove(UPSTANDING_SLASH) && !this.lastMoveBefore(UPSTANDING_SLASH)) {
                    possibilities.add(UPSTANDING_SLASH);
                }
                if (!this.lastMove(LEVEL_SLASH) && !this.lastMoveBefore(LEVEL_SLASH)) {
                    possibilities.add(LEVEL_SLASH);
                }
                if (!this.lastMove(SPEAR) && !this.lastMoveBefore(SPEAR)) {
                    possibilities.add(SPEAR);
                }
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());

        }
    }

    public void activateEGO() {
        playSound("RedMistChange");
        phase = EGO_PHASE;
        runAnim("Idle" + phase);
        CustomDungeon.playTempMusicInstantly("RedMistBGM");
        EGO = true;
        greaterSplitCooldownCounter = 0;
        numAdditionalMoves += egoExtraActions;
        atb(new RemoveDebuffsAction(this));
        applyToTarget(this, this, new StrengthPower(this, focusSpiritStr));
    }

    public static void verticalSplitVfx() {
        ArrayList<Texture> frames = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            frames.add(TexLoader.getTexture(makeMonsterPath("RedMist/Vertical/frame" + i + ".png")));
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("RedMistVertCut");
                this.isDone = true;
            }
        });
        fullScreenAnimation(frames, 0.1f, 0.9f);
    }

    public static void horizontalSplitVfx() {
        ArrayList<Texture> frames = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            frames.add(TexLoader.getTexture(makeMonsterPath("RedMist/Horizontal/frame" + i + ".png")));
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("RedMistHoriEye");
                playSound("RedMistHoriStart");
                this.isDone = true;
            }
        });
        fullScreenAnimation(frames, 0.1f, 1.2f);
    }

    public void CheckEGOTrigger() {
        if (this.currentHealth < (int)(this.maxHealth * HP_THRESHOLD) && phase == KALI_PHASE) {
            activateEGO();
            makePowerRemovable(this, RedMistPower.POWER_ID);
            att(new RemoveSpecificPowerAction(this, this, RedMistPower.POWER_ID));
        }
    }
}