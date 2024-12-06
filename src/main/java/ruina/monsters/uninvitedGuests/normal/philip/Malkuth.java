package ruina.monsters.uninvitedGuests.normal.philip;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.AllyDamageAllEnemiesAction;
import ruina.cardmods.ManifestMod;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.uninvitedGuests.normal.philip.malkuthCards.*;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.act4.Emotion;
import ruina.util.TexLoader;
import ruina.vfx.ExplosionEffect;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Malkuth extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Malkuth.class.getSimpleName());

    private static final byte COORDINATED_ASSAULT = 0;
    private static final byte EMOTIONAL_TURBULENCE = 1;
    private static final byte FERVID_EMOTIONS = 2;
    private static final byte RAGING_STORM = 3;
    private static final byte INFERNO = 4;

    public final int STARTING_STR = 2;
    public final int STRENGTH = 2;
    public final int ALLY_BLOCK = 10;
    public final int SELF_BLOCK = 14;
    public final int DRAW = 1;
    public final int fervidHits = 2;
    public final int fervidEmotions = 4;
    public final int emotionalEmotions = 4;
    public final int stormHits = 2;
    public final int infernoStrScaling = 5;
    public final int VULNERABLE = 1;
    public final int passiveVulnerable = 1;
    public static final int firstEmotionThreshold = 2;
    public static final int secondEmotionThreshold = 4;
    public static final int EMOTION_CAP = 4;

    public final int massAttackCooldown = 2;
    public int massAttackCooldownCounter = massAttackCooldown;

    public final int EMOTION_THRESHOLD = 10;
    public static final int EXHAUST_GAIN = 2;

    private boolean distorted = false;
    private boolean manifestedEGO = false;
    private static final int NORMAL = 1;
    private static final int DISTORTED = 2;
    private static final int EGO = 3;
    private int phase = NORMAL;

    public static final String POWER_ID = makeID("Dragon");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final String R_POWER_ID = makeID("Wildfire");
    public static final PowerStrings R_powerStrings = CardCrawlGame.languagePack.getPowerStrings(R_POWER_ID);
    public static final String R_POWER_NAME = R_powerStrings.NAME;
    public static final String[] R_POWER_DESCRIPTIONS = R_powerStrings.DESCRIPTIONS;

    public static final Texture targetTexture = TexLoader.getTexture(makeUIPath("MalkuthIcon.png"));

    public Malkuth() {
        this(0.0f, 0.0f);
    }

    public Malkuth(final float x, final float y) {
        super(ID, ID, 150, -5.0F, 0, 200.0f, 240.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Malkuth/Spriter/Malkuth.scml"));
        this.animation.setFlip(true, false);
        this.setHp(calcAscensionTankiness(150));

        addMove(COORDINATED_ASSAULT, Intent.DEFEND_BUFF);
        addMove(EMOTIONAL_TURBULENCE, Intent.ATTACK_DEFEND, 16);
        addMove(FERVID_EMOTIONS, Intent.ATTACK_BUFF, 12, fervidHits);
        addMove(RAGING_STORM, IntentEnums.MASS_ATTACK, 20, stormHits);
        addMove(INFERNO, IntentEnums.MASS_ATTACK, 50);

        cardList.add(new Coordinated(this));
        cardList.add(new Emotional(this));
        cardList.add(new FervidEmotions(this));
        cardList.add(new RagingStorm(this));
        cardList.add(new Inferno(this));

        this.icon = TexLoader.getTexture(makeUIPath("MalkuthIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    public void distort() {
        if (!distorted) {
            phase = DISTORTED;
            distorted = true;
            massAttackCooldownCounter = 0;
            IdlePose();
        }
    }

    public void manifest() {
        if (!manifestedEGO) {
            phase = EGO;
            manifestedEGO = true;
            distorted = true;
            massAttackCooldownCounter = 0;
            playSound("XiaoRoar");
            applyToTargetTop(this, this, new AbstractLambdaPower(R_POWER_NAME, R_POWER_ID, AbstractPower.PowerType.BUFF, false, this, passiveVulnerable) {
                @Override
                public void atEndOfRound() {
                    for (AbstractMonster mo : monsterList()) {
                        if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                            applyToTarget(mo, owner, new VulnerablePower(mo, amount, true));
                        }
                    }
                }

                @Override
                public void updateDescription() {
                    description = R_POWER_DESCRIPTIONS[0] + amount + R_POWER_DESCRIPTIONS[1];
                }
            });
            IdlePose();
            waitAnimation();
        }
    }

    private void IdlePose() {
        runAnim("Idle" + phase);
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Philip) {
                target = (Philip)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, EXHAUST_GAIN) {
            @Override
            public void onInitialApplication() {
                for (AbstractCard card : adp().drawPile.group) {
                    CardModifierManager.addModifier(card, new ManifestMod());
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, new Emotion(this, 0, EMOTION_THRESHOLD));
        applyToTarget(this, this, new StrengthPower(this, STARTING_STR));
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();

        switch (this.nextMove) {
            case COORDINATED_ASSAULT: {
                blockAnimation();
                applyToTarget(adp(), this, new StrengthPower(adp(), STRENGTH));
                block(adp(), ALLY_BLOCK);
                applyToTarget(adp(), this, new DrawCardNextTurnPower(adp(), DRAW));
                resetIdle();
                break;
            }
            case EMOTIONAL_TURBULENCE: {
                slashAnimation(target);
                block(this, SELF_BLOCK);
                dmg(target, info);
                applyToTarget(this, this, new Emotion(this, emotionalEmotions, EMOTION_THRESHOLD));
                resetIdle();
                break;
            }
            case FERVID_EMOTIONS: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(this, this, new Emotion(this, fervidEmotions, EMOTION_THRESHOLD));
                break;
            }
            case RAGING_STORM: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        ragingStormStart(target);
                    } else {
                        ragingStormFin(target);
                    }
                    atb(new AllyDamageAllEnemiesAction(this, calcMassAttackNoHitPlayer(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                    for (AbstractMonster mo : monsterList()) {
                        if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                            applyToTarget(mo, this, new VulnerablePower(mo, VULNERABLE, true));
                        }
                    }
                    resetIdle(1.0f);
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        massAttackCooldownCounter = massAttackCooldown + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case INFERNO: {
                infernoStart(target);
                waitAnimation(1.0f);
                infernoFin(target);
                atb(new VFXAction(new ExplosionEffect(target.hb.cX, target.hb.cY), 0.1F));
                atb(new AllyDamageAllEnemiesAction(this, calcMassAttackNoHitPlayer(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        massAttackCooldownCounter = massAttackCooldown + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                massAttackCooldownCounter--;
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                Emotion power = (Emotion) getPower(Emotion.POWER_ID);
                if (power != null) {
                    if (power.amount2 >= Malkuth.secondEmotionThreshold) {
                        manifest();
                    }
                    if (power.amount2 >= Malkuth.firstEmotionThreshold) {
                        distort();
                    }
                }
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash" + phase, "XiaoVert", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce" + phase, "XiaoStab", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Guard" + phase, "FireGuard", this);
    }

    private void ragingStormStart(AbstractCreature enemy) {
        animationAction("Slash" + phase, "XiaoStrongStart", enemy, this);
    }

    private void ragingStormFin(AbstractCreature enemy) {
        animationAction("Special2", "XiaoStrongFin", enemy, this);
    }

    private void infernoStart(AbstractCreature enemy) {
        animationAction("Special3", "XiaoStart", enemy, this);
    }

    private void infernoFin(AbstractCreature enemy) {
        animationAction("Special4", "XiaoFin", enemy, this);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                IdlePose();
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        if (moveHistory.size() >= 3) {
            moveHistory.clear();
        }
        if (canUseMassAttack()) {
            if (phase == EGO) {
                setMoveShortcut(INFERNO, cardList.get(INFERNO));
            } else {
                setMoveShortcut(RAGING_STORM, cardList.get(RAGING_STORM));
            }
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (phase == NORMAL) {
                if (!this.lastMove(COORDINATED_ASSAULT) && !this.lastMoveBefore(COORDINATED_ASSAULT)) {
                    possibilities.add(COORDINATED_ASSAULT);
                }
            }
            if (!this.lastMove(FERVID_EMOTIONS) && !this.lastMoveBefore(FERVID_EMOTIONS)) {
                possibilities.add(FERVID_EMOTIONS);
            }
            if (!this.lastMove(EMOTIONAL_TURBULENCE) && !this.lastMoveBefore(EMOTIONAL_TURBULENCE)) {
                possibilities.add(EMOTIONAL_TURBULENCE);
            }
            if (possibilities.isEmpty()) {
                possibilities.add(FERVID_EMOTIONS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, cardList.get(move));
        }
    }

    public boolean canUseMassAttack() {
        if (massAttackCooldownCounter <= 0 && phase > NORMAL) {
            for (AbstractMonster mo : monsterList()) {
                if (mo instanceof CryingChild) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onBossDeath() {
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[1]));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    disappear();
                    this.isDone = true;
                }
            });
        }
    }

}