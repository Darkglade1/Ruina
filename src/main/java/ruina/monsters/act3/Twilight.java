package ruina.monsters.act3;

import actlikeit.dungeons.CustomDungeon;
import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.cards.Dazzled;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.LongEgg;
import ruina.powers.Paralysis;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Twilight extends AbstractRuinaMonster
{
    public static final String ID = makeID(Twilight.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte PEACE_FOR_ALL = 0;
    private static final byte SURVEILLANCE = 1;
    private static final byte TORN_MOUTH = 2;
    private static final byte TILTED_SCALE = 3;
    private static final byte TALONS = 4;
    private static final byte BRILLIANT_EYES = 5;

    private final int BLOCK = calcAscensionTankiness(24);
    private final int VULNERABLE = calcAscensionSpecial(1);
    private final int FRAIL = calcAscensionSpecial(2);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int WEAK = calcAscensionSpecial(2);
    private final int STATUS = 1;
    private final int NUM_DAZZLE = 1;
    private final int SMALL_EGG_NUM_CARDS = 2;
    private final int SMALL_EGG_COST_INCREASE = calcAscensionSpecial(1);
    private final int EGG_CYCLE_TURN_NUM = 2;

    private enum BirdEgg {
        BIG_EGG, SMALL_EGG, LONG_EGG;
    }

    public static final String BIG_EGG_POWER_ID = makeID("BigEgg");
    public static final PowerStrings bigEggPowerStrings = CardCrawlGame.languagePack.getPowerStrings(BIG_EGG_POWER_ID);
    public static final String BIG_EGG_POWER_NAME = bigEggPowerStrings.NAME;
    public static final String[] BIG_EGG_POWER_DESCRIPTIONS = bigEggPowerStrings.DESCRIPTIONS;

    public static final String SMALL_EGG_POWER_ID = makeID("SmallEgg");
    public static final PowerStrings SMALLEggPowerStrings = CardCrawlGame.languagePack.getPowerStrings(SMALL_EGG_POWER_ID);
    public static final String SMALL_EGG_POWER_NAME = SMALLEggPowerStrings.NAME;
    public static final String[] SMALL_EGG_POWER_DESCRIPTIONS = SMALLEggPowerStrings.DESCRIPTIONS;

    public static final String FADING_TWILIGHT_POWER_ID = makeID("FadingTwilight");
    public static final PowerStrings FadingTwilightPowerStrings = CardCrawlGame.languagePack.getPowerStrings(FADING_TWILIGHT_POWER_ID);
    public static final String FADING_TWILIGHT_POWER_NAME = FadingTwilightPowerStrings.NAME;
    public static final String[] FADING_TWILIGHT_POWER_DESCRIPTIONS = FadingTwilightPowerStrings.DESCRIPTIONS;

    private final AbstractAnimation bird;
    private BirdEgg currentEgg = BirdEgg.BIG_EGG;
    AbstractPower currentEggPower;

    private static final float HP_THRESHOLD_PERCENT = 0.25f;
    private final int dmgThreshold;
    private int dmgTaken = 0;
    private boolean bigEggBroken = false;
    private boolean smallEggBroken = false;
    private boolean longEggBroken = false;
    private boolean eggBrokenRecently = false;

    public Twilight() {
        this(0.0f, 0.0f);
    }

    public Twilight(final float x, final float y) {
        super(NAME, ID, 500, -5.0F, 0, 330.0f, 305.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Twilight/Spriter/Twilight.scml"));
        this.bird = new BetterSpriterAnimation(makeMonsterPath("Twilight/Bird/Bird.scml"));
        this.type = EnemyType.BOSS;

        this.setHp(calcAscensionTankiness(this.maxHealth));
        dmgThreshold = (int)(this.maxHealth * HP_THRESHOLD_PERCENT);

        addMove(PEACE_FOR_ALL, Intent.ATTACK, calcAscensionDamage(36));
        addMove(SURVEILLANCE, Intent.ATTACK_DEBUFF, calcAscensionDamage(16));
        addMove(TORN_MOUTH, Intent.ATTACK_DEBUFF, calcAscensionDamage(20));
        addMove(TILTED_SCALE, Intent.DEFEND_DEBUFF);
        addMove(TALONS, Intent.ATTACK, calcAscensionDamage(13), 2, true);
        addMove(BRILLIANT_EYES, Intent.DEBUFF);
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland3");
        switchEgg(currentEgg);
        applyToTarget(this, this, new AbstractLambdaPower(FADING_TWILIGHT_POWER_NAME, FADING_TWILIGHT_POWER_ID, AbstractPower.PowerType.BUFF, false, this, EGG_CYCLE_TURN_NUM) {
            @Override
            public void atEndOfRound() {
                if (getNumEggsLeft() > 1) {
                    amount--;
                    if (amount <= 0) {
                        flash();
                        if (owner instanceof Twilight) {
                            ((Twilight) owner).cycleEgg();
                            ((Twilight) owner).rollMove();
                            ((Twilight) owner).createIntent();
                        }
                        amount = EGG_CYCLE_TURN_NUM;
                    } else {
                        flashWithoutSound();
                    }
                }
            }

            @Override
            public void updateDescription() {
                if (amount2 <= 0) {
                    amount2 = dmgThreshold;
                }
                description = FADING_TWILIGHT_POWER_DESCRIPTIONS[0] + EGG_CYCLE_TURN_NUM + FADING_TWILIGHT_POWER_DESCRIPTIONS[1] + amount2 + FADING_TWILIGHT_POWER_DESCRIPTIONS[2];
            }
        });
    }

    private int getNumEggsLeft() {
        int count = 0;
        if (!bigEggBroken) {
            count++;
        }
        if (!smallEggBroken) {
            count++;
        }
        if (!longEggBroken) {
            count++;
        }
        return count;
    }

    private void cycleEgg() {
        if (currentEgg == BirdEgg.BIG_EGG) {
            if (!smallEggBroken) {
                switchEgg(BirdEgg.SMALL_EGG);
            } else if (!longEggBroken) {
                switchEgg(BirdEgg.LONG_EGG);
            }
        } else if (currentEgg == BirdEgg.SMALL_EGG) {
            if (!longEggBroken) {
                switchEgg(BirdEgg.LONG_EGG);
            } else if (!bigEggBroken) {
                switchEgg(BirdEgg.BIG_EGG);
            }
        } else if (currentEgg == BirdEgg.LONG_EGG) {
            if (!bigEggBroken) {
                switchEgg(BirdEgg.BIG_EGG);
            } else if (!smallEggBroken) {
                switchEgg(BirdEgg.SMALL_EGG);
            }
        }
    }
    
    private void switchEgg(BirdEgg egg) {
        atb(new RemoveSpecificPowerAction(this, this, currentEggPower));
        AbstractPower eggPower = null;
        switch(egg) {
            case BIG_EGG:
                eggPower = new AbstractLambdaPower(BIG_EGG_POWER_NAME, BIG_EGG_POWER_ID, AbstractPower.PowerType.BUFF, false, this, NUM_DAZZLE) {
                    @Override
                    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
                        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                            Dazzled dazzle = new Dazzled();
                            if (AbstractDungeon.ascensionLevel >= 19) {
                                dazzle.upgrade();
                            }
                            intoDrawMo(dazzle, amount, Twilight.this);
                        }
                    }
                    @Override
                    public void updateDescription() {
                        description = BIG_EGG_POWER_DESCRIPTIONS[0] + amount + BIG_EGG_POWER_DESCRIPTIONS[1];
                    }
                };
                applyToTarget(this, this, eggPower);
                break;
            case SMALL_EGG:
                eggPower = new AbstractLambdaPower(SMALL_EGG_POWER_NAME, SMALL_EGG_POWER_ID, AbstractPower.PowerType.BUFF, false, this, SMALL_EGG_NUM_CARDS) {
                    int counter = 0;
                    @Override
                    public void onCardDraw(AbstractCard card) {
                        if (counter < amount) {
                            card.setCostForTurn(card.costForTurn + SMALL_EGG_COST_INCREASE);
                            card.flash();
                            counter++;
                        }
                    }

                    @Override
                    public void atEndOfRound() {
                        counter = 0;
                    }

                    @Override
                    public void updateDescription() {
                        description = SMALL_EGG_POWER_DESCRIPTIONS[0] + amount + SMALL_EGG_POWER_DESCRIPTIONS[1] + SMALL_EGG_COST_INCREASE + SMALL_EGG_POWER_DESCRIPTIONS[2];
                    }
                };
                applyToTarget(this, this, eggPower);
                break;
            case LONG_EGG:
                if (AbstractDungeon.ascensionLevel >= 19) {
                    atb(new RemoveDebuffsAction(this));
                }
                eggPower = new LongEgg(this);
                applyToTarget(this, this, eggPower);
                break;
        }
        currentEgg = egg;
        currentEggPower = eggPower;
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (nextMove) {
            case PEACE_FOR_ALL: {
                commandAnimation();
                dmg(adp(), info);
                resetIdle();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        eggBrokenRecently = false;
                        this.isDone = true;
                    }
                });
                break;
            }
            case SURVEILLANCE: {
                lampAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), FRAIL, true));
                resetIdle();
                break;
            }
            case TORN_MOUTH: {
                punishAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                resetIdle();
                break;
            }
            case TILTED_SCALE: {
                specialAnimation();
                block(this, BLOCK);
                applyToTarget(adp(), this, new VulnerablePower(adp(), VULNERABLE, true));
                resetIdle();
                break;
            }
            case TALONS: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        crushAnimation(adp());
                    } else {
                        slamAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case BRILLIANT_EYES: {
                specialAnimation();
                applyToTarget(adp(), this, new WeakPower(adp(), WEAK, true));
                intoDrawMo(new VoidCard(), STATUS, this);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (eggBrokenRecently) {
            setMoveShortcut(PEACE_FOR_ALL, MOVES[PEACE_FOR_ALL]);
        } else {
            if (currentEgg == BirdEgg.BIG_EGG) {
                if (!this.lastMove(SURVEILLANCE)) {
                    setMoveShortcut(SURVEILLANCE, MOVES[SURVEILLANCE]);
                } else {
                    setMoveShortcut(TALONS, MOVES[TALONS]);
                }
            } else if (currentEgg == BirdEgg.SMALL_EGG) {
                if (!this.lastMove(TORN_MOUTH)) {
                    setMoveShortcut(TORN_MOUTH, MOVES[TORN_MOUTH]);
                } else {
                    setMoveShortcut(TALONS, MOVES[TALONS]);
                }
            } else if (currentEgg == BirdEgg.LONG_EGG) {
                if (!this.lastMove(TILTED_SCALE)) {
                    setMoveShortcut(TILTED_SCALE, MOVES[TILTED_SCALE]);
                } else {
                    setMoveShortcut(TALONS, MOVES[TALONS]);
                }
            } else {
                if (!this.lastMove(BRILLIANT_EYES) && !this.lastMoveBefore(BRILLIANT_EYES)) {
                    setMoveShortcut(BRILLIANT_EYES, MOVES[BRILLIANT_EYES]);
                } else {
                    setMoveShortcut(TALONS, MOVES[TALONS]);
                }
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        int tmpHealth = this.currentHealth;
        super.damage(info);
        if (!this.isDeadOrEscaped()) {
            this.dmgTaken += tmpHealth - this.currentHealth;
            if (this.dmgTaken >= this.dmgThreshold) {
                this.dmgTaken = 0;
                if (currentEgg == BirdEgg.BIG_EGG) {
                    bigEggBroken = true;
                }
                if (currentEgg == BirdEgg.SMALL_EGG) {
                    smallEggBroken = true;
                }
                if (currentEgg == BirdEgg.LONG_EGG) {
                    longEggBroken = true;
                }
                if (bigEggBroken && smallEggBroken && longEggBroken) {
                    currentEgg = null;
                    atb(new RemoveSpecificPowerAction(this, this, currentEggPower));
                    atb(new RemoveSpecificPowerAction(this, this, FADING_TWILIGHT_POWER_ID));
                } else {
                    cycleEgg();
                }
                this.eggBrokenRecently = true;
            }
            AbstractPower power = this.getPower(FADING_TWILIGHT_POWER_ID);
            if (power != null) {
                if (power instanceof TwoAmountPower) {
                    ((TwoAmountPower) power).amount2 = dmgThreshold - dmgTaken;
                    power.updateDescription();
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!isDead) {
            sb.setColor(Color.WHITE);
            bird.renderSprite(sb, (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2);
        }
        super.render(sb);
    }

    private void crushAnimation(AbstractCreature enemy) {
        animationAction("Crush", "BossBirdCrush", enemy, this);
    }

    private void slamAnimation(AbstractCreature enemy) {
        animationAction("Slam", "BossBirdSlam", enemy, this);
    }

    private void lampAnimation(AbstractCreature enemy) {
        animationAction("Lamp", "BossBirdLamp", enemy, this);
    }

    private void punishAnimation(AbstractCreature enemy) {
        animationAction("Punish", "BossBirdPunish", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "BossBirdSpecial", this);
    }

    private void commandAnimation() {
        animationAction("Command", "BossBirdStrong", this);
    }

}