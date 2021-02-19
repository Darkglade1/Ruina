package ruina.monsters.act3.seraphim;

import basemod.animations.AbstractAnimation;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.WingsOfGrace;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Seraphim extends AbstractMultiIntentMonster {
    public static final String ID = makeID(Seraphim.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final Texture CIRCLE = new Texture(makeMonsterPath("Seraphim/Circle.png"));
    private final TextureRegion WHITENIGHT_CIRCLE_REGION;
    private final TextureRegion SELF_CIRCLE_REGION;

    private static final float whiteNightY = (float) Settings.HEIGHT / 2 + (75.0f * Settings.scale);

    public static final String GLOWING_CIRCLE = RuinaMod.makeMonsterPath("Seraphim/GlowingCircle.png");
    private static final Texture GLOWING_CIRCLE_TEXTURE = new Texture(GLOWING_CIRCLE);

    private static final byte EMPTY = 0;
    private static final byte PHASE_TRANSITION = 1;
    // Phase 1
    private static final byte WINGS_OF_GRACE = 2;
    private static final byte SUMMON_APOSTLES = 3;
    private static final byte BAPTISM = 4;
    // Phase 2

    // 3 x 2 - Summon two Guardians, First move only.
    private static final byte RISE_AND_SERVE = 5;
    // 30 Block to Self. Buff - Heal 75 HP ALL "DEAD" Apostles. 15 HP Heal to self
    private static final byte SALVATION = 6;
    // 50 HP heal to Target Apostle. // 20 Block to Target..
    private static final byte PRAYER = 7;

    // First move will always be RISE_AND_SERVE, then Salvation.

    // Then:
    // 1 Damage Move:
    // 25 Damage or 2 x 4.

    // 2nd Move Action
    // Salvation: If both apostles are "dead", Salvation. If one is dead, PRAYER, otherwise Fear Not or Baptism (25% chance).

    // 25
    private static final byte DO_NOT_DENY = 8;

    // 1 Wings of Grace to All, 2 Str to ALL nonboss.
    private static final byte FEAR_NOT = 9;
    // 2 x 4
    private static final byte BEHOLD_MY_POWER = 10;

    // Phase 3: (after 35 turns or half health)
    // Gains another moveaction: Exclusively Casts Revelation with it
    // 1 Wings Of Grace, 2 Strength to self
    private static final byte REVELATION = 11;


    private int phase = 2;
    // Phase 1
    private final int wingsOfGrace = calcAscensionSpecial(1);
    private final int strBuff = calcAscensionSpecial(1);
    private final int baptismHeal = calcAscensionTankiness(15);
    // Phase 2
    private final int salvationBossHeal = calcAscensionTankiness(15);
    private final float prayerHeal = 0.66f;
    private final int prayerBlock = calcAscensionTankiness(8);
    private final int revelationStr = calcAscensionSpecial(2);
    // Prevent non needed intent changes
    private boolean lockedIntent = false;

    private final AbstractAnimation whiteNight;

    public Seraphim() {
        this(150.0f, 0.0f);
    }

    public Seraphim(final float x, final float y) {
        super(NAME, ID, 666, -5.0F, 0, 280.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Seraphim/Spriter/Seraphim.scml"));
        this.whiteNight = new BetterSpriterAnimation(makeMonsterPath("Seraphim/WhiteNight/WhiteNight.scml"));
        WHITENIGHT_CIRCLE_REGION = new TextureRegion(CIRCLE);
        SELF_CIRCLE_REGION = new TextureRegion(CIRCLE);
        runAnim("Idle");
        this.type = EnemyType.BOSS;
        // Phase 3: Enrage and gain 1 more.
        numAdditionalMoves = 2;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }

        firstMove = true;
        addMove(EMPTY, Intent.NONE);
        addMove(PHASE_TRANSITION, Intent.UNKNOWN);
        addMove(SUMMON_APOSTLES, Intent.UNKNOWN);
        addMove(BAPTISM, Intent.BUFF);
        addMove(WINGS_OF_GRACE, Intent.BUFF);
        addMove(RISE_AND_SERVE, Intent.ATTACK, calcAscensionDamage(40));
        addMove(SALVATION, Intent.UNKNOWN);
        addMove(PRAYER, Intent.DEFEND_BUFF);
        addMove(DO_NOT_DENY, Intent.ATTACK, calcAscensionDamage(17));
        addMove(FEAR_NOT, Intent.BUFF);
        addMove(BEHOLD_MY_POWER, Intent.ATTACK, calcAscensionDamage(4), 3, true);
        addMove(REVELATION, Intent.BUFF);
    }


    @Override
    public void usePreBattleAction() {
        playSound("WhiteNightAppear");
        AbstractDungeon.getCurrRoom().cannotLose = true;
        applyToTarget(this, this, new WingsOfGrace(this, calcAscensionSpecial(2)));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if (info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case EMPTY:
                break;
            case PHASE_TRANSITION:
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        phase += 1;
                        if (phase > 3) {
                            phase = 3;
                        }
                        this.isDone = true;
                    }
                });
                // Apply Powers for Phases Here.
                break;
            case WINGS_OF_GRACE:
                specialAnimation();
                atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, wingsOfGrace)));
                resetIdle();
                break;
            case SUMMON_APOSTLES:
                Summon();
                break;
            case BAPTISM:
                specialAnimation();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (AbstractMonster m : monsterList()) {
                            if (!(m instanceof Seraphim)) {
                                att(new ApplyPowerAction(m, m, new StrengthPower(m, strBuff)));
                            }
                        }
                        this.isDone = true;
                    }
                });
                resetIdle();
                break;
            case BEHOLD_MY_POWER:
                attackAnimation(adp());
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info);
                }
                resetIdle();
                break;
            case RISE_AND_SERVE:
                shockwaveAnimation(adp());
                shockwaveEffect();
                dmg(adp(), info);
                Summon();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        firstMove = false;
                        this.isDone = true;
                    }
                });
                resetIdle();
                break;
            case SALVATION:
                specialAnimation();
                atb(new HealAction(this, this, salvationBossHeal));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (AbstractMonster m : monsterList()) {
                            if (m instanceof GuardianApostle && m.halfDead) {
                                att(new RollMoveAction(m));
                                att(new HealAction(m, m, m.maxHealth));
                                m.halfDead = false;
                            }
                        }
                        this.isDone = true;
                    }
                });
                resetIdle();
                break;
            case PRAYER:
                specialAnimation();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (AbstractMonster m : monsterList()) {
                            if (m instanceof GuardianApostle && m.halfDead) {
                                att(new RollMoveAction(m));
                                att(new GainBlockAction(m, prayerBlock));
                                att(new HealAction(m, m, (int)(m.maxHealth * prayerHeal)));
                                m.halfDead = false;
                                break;
                            }
                        }
                        this.isDone = true;
                    }
                });
                resetIdle();
                break;
            case DO_NOT_DENY:
                shockwaveAnimation(adp());
                shockwaveEffect();
                dmg(adp(), info);
                resetIdle();
                break;
            case FEAR_NOT:
                specialAnimation();
                for (AbstractMonster m : monsterList()) {
                    if (!m.equals(this) && !m.isDeadOrEscaped()) {
                        atb(new HealAction(m, m, baptismHeal));
                        atb(new ApplyPowerAction(m, m, new StrengthPower(m, strBuff)));
                    }
                }
                resetIdle();
                break;
            case REVELATION:
                specialAnimation();
                atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, wingsOfGrace)));
                atb(new ApplyPowerAction(this, this, new StrengthPower(this, revelationStr)));
                resetIdle();
                break;
        }
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            takeCustomTurn(additionalMove, adp());
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                lockedIntent = false;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        switch (phase) {
            case 1:
                setMoveShortcut(WINGS_OF_GRACE, MOVES[WINGS_OF_GRACE]);
                break;
            case 2:
            case 3:
                if (firstMove && !lockedIntent) {
                    setMoveShortcut(RISE_AND_SERVE, MOVES[RISE_AND_SERVE]);
                } else {
                    if (!lockedIntent) {
                        setMoveShortcut(num <= 45 ? BEHOLD_MY_POWER : DO_NOT_DENY, num <= 45 ? MOVES[BEHOLD_MY_POWER] : MOVES[DO_NOT_DENY]);
                    }
                }
                break;
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        switch (phase) {
            case 1:
                int count = 0;
                for (AbstractMonster m : monsterList()) {
                    if (!m.equals(this)) {
                        count += 1;
                    }
                }
                if (count == 0 && whichMove == 0) {
                    setAdditionalMoveShortcut(PHASE_TRANSITION, moveHistory);
                } else if (whichMove == 0) {
                    setAdditionalMoveShortcut(BAPTISM, moveHistory);
                }
                break;
            case 2:
            case 3:
                if (phase == 3 && whichMove == 1) {
                    setAdditionalMoveShortcut(REVELATION, moveHistory);
                } else if (whichMove == 1) {
                    if (currentHealth <= maxHealth / 2F) {
                        setAdditionalMoveShortcut(PHASE_TRANSITION, moveHistory);
                    }
                }
                int apostlesToRevive = 0;
                for (AbstractMonster m : monsterList()) {
                    if (m instanceof GuardianApostle && m.halfDead) {
                        apostlesToRevive += 1;
                    }
                }
                if (whichMove == 0) {
                    switch (apostlesToRevive) {
                        case 1:
                            setAdditionalMoveShortcut(PRAYER, moveHistory);
                            break;
                        case 2:
                            setAdditionalMoveShortcut(SALVATION, moveHistory);
                            break;
                        default:
                            if (!lockedIntent) {
                                setAdditionalMoveShortcut(num <= 45 ? FEAR_NOT : BEHOLD_MY_POWER, moveHistory);
                            }
                            break;
                    }
                }
                break;
        }
        lockedIntent = true;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
            }
        }
    }


    @Override
    public void die(boolean triggerRelics) {
        AbstractDungeon.getCurrRoom().cannotLose = false;
        super.die(triggerRelics);
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            if (mo instanceof GuardianApostle) {
                atb(new SuicideAction(mo));
            }
        }
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                onBossVictoryLogic();
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!isDead) {
            sb.setColor(Color.WHITE);
            sb.draw(SELF_CIRCLE_REGION, this.hb.cX - (float)this.SELF_CIRCLE_REGION.getRegionWidth() / 2, this.hb.cY - (float)this.SELF_CIRCLE_REGION.getRegionHeight() / 2, 0.0F, 0.0F, this.SELF_CIRCLE_REGION.getRegionWidth(), this.SELF_CIRCLE_REGION.getRegionHeight(), Settings.scale, Settings.scale, 0.0F);
        }
        super.render(sb);
        if (!isDead) {
            sb.setColor(Color.WHITE);
            sb.draw(WHITENIGHT_CIRCLE_REGION, (float)Settings.WIDTH / 2 - (float)this.WHITENIGHT_CIRCLE_REGION.getRegionWidth() / 2 - 15.0f * Settings.scale, (float) Settings.HEIGHT / 2, 0.0F, 0.0F, this.WHITENIGHT_CIRCLE_REGION.getRegionWidth(), this.WHITENIGHT_CIRCLE_REGION.getRegionHeight(), Settings.scale, Settings.scale, 0.0F);
            whiteNight.renderSprite(sb, (float) Settings.WIDTH / 2, whiteNightY);
        }
    }

    public void Summon() {
        float xPos_Farthest_L = -750.0F;
        float xPos_Middle_L = -450F;
        float xPos_Short_L = -150F;
        float xPos_Shortest_L = 0F;
        AbstractMonster apostle1 = new GuardianApostle(xPos_Middle_L, 0.0f, this);
        atb(new SpawnMonsterAction(apostle1, true));
        atb(new UsePreBattleActionAction(apostle1));
        apostle1.rollMove();
        apostle1.createIntent();
        AbstractMonster apostle2 = new GuardianApostle(xPos_Short_L, 0.0f, this);
        atb(new SpawnMonsterAction(apostle2, true));
        atb(new UsePreBattleActionAction(apostle2));
        apostle2.rollMove();
        apostle2.createIntent();
    }

    private void shockwaveAnimation(AbstractCreature enemy) {
        animationAction("Special3", null, enemy, this);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Special3", "WhiteNightCall", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special1", "ProphetBless", this);
    }

    private void shockwaveEffect() {
        float y = whiteNightY + 100.0f * Settings.scale;
        float chargeDuration = 1.2f;
        AbstractGameEffect shockwaveCharge = new VfxBuilder(GLOWING_CIRCLE_TEXTURE, (float)Settings.WIDTH / 2, y, chargeDuration)
                .scale(4.0f, 0.0f, VfxBuilder.Interpolations.LINEAR)
                .playSoundAt(0.0f, makeID("WhiteNightCharge"))
                .rotate(-600f)
                .build();
        float burstDuration = 0.7f;
        AbstractGameEffect shockwaveBurst = new VfxBuilder(GLOWING_CIRCLE_TEXTURE, (float)Settings.WIDTH / 2, y, burstDuration)
                .scale(0.0f, 8.0f, VfxBuilder.Interpolations.SWING)
                .playSoundAt(0.0f, makeID("WhiteNightFire"))
                .build();
        atb(new VFXAction(shockwaveCharge, chargeDuration));
        atb(new VFXAction(shockwaveBurst, burstDuration - 0.3f));
    }

}