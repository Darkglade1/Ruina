package ruina.monsters.act3.seraphim;

import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.*;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class Seraphim extends AbstractMultiIntentMonster {
    public static final String ID = makeID(Seraphim.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

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
    private int wingsOfGrace = calcAscensionSpecial(1);
    private int strBuff = calcAscensionSpecial(1);
    private int baptismHeal = calcAscensionSpecial(15);
    // Phase 2
    private int riseAndServe = calcAscensionDamage(3);
    private int doNotDeny = calcAscensionDamage(10);
    private int salvationHeal = calcAscensionSpecial(75);
    private int salvationBossHeal = calcAscensionTankiness(15);
    private int prayerHeal = calcAscensionSpecial(50);
    private int prayerBlock = calcAscensionTankiness(7);
    private int beholdMyPower = calcAscensionDamage(2);
    private int revelationStr = calcAscensionSpecial(2);
    // Prevent non needed intent changes
    private boolean lockedIntent = false;

    private AbstractAnimation whiteNight;

    public Seraphim() {
        this(150.0f, 0.0f);
    }

    public Seraphim(final float x, final float y) {
        super(NAME, ID, 666, -5.0F, 0, 280.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Seraphim/Spriter/Seraphim.scml"));
        this.whiteNight = new BetterSpriterAnimation(makeMonsterPath("Seraphim/WhiteNight/WhiteNight.scml"));
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
        addMove(RISE_AND_SERVE, Intent.ATTACK, riseAndServe, 2, true);
        addMove(SALVATION, Intent.UNKNOWN);
        addMove(PRAYER, Intent.DEFEND_BUFF);
        addMove(DO_NOT_DENY, Intent.ATTACK, doNotDeny);
        addMove(FEAR_NOT, Intent.BUFF);
        addMove(BEHOLD_MY_POWER, Intent.ATTACK, beholdMyPower, 4, true);
        addMove(REVELATION, Intent.BUFF);
    }


    @Override
    public void usePreBattleAction() {
        //Summon();
        AbstractDungeon.getCurrRoom().cannotLose = true;
        //atb(new ApplyPowerAction(this, this, new Apostles(this, ApostleKillCounter)));
        atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, calcAscensionSpecial(2))));
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
                atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, wingsOfGrace)));
                break;
            case SUMMON_APOSTLES:
                Summon();
                break;
            case BAPTISM:
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
                break;
            case BEHOLD_MY_POWER:
            case RISE_AND_SERVE:
                if (move.nextMove == RISE_AND_SERVE) {
                    Summon();
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            firstMove = false;
                            this.isDone = true;
                        }
                    });
                }
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info);
                }
                break;
            case SALVATION:
                atb(new HealAction(this, this, salvationBossHeal));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (AbstractMonster m : monsterList()) {
                            if (!(m instanceof Seraphim && m.currentHealth == 1)) {
                                att(new HealAction(m, m, salvationHeal));
                                m.halfDead = false;
                            }
                        }
                        this.isDone = true;
                    }
                });
                break;
            case PRAYER:
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (AbstractMonster m : monsterList()) {
                            if (!(m instanceof Seraphim) && m.currentHealth == 1) {
                                att(new HealAction(m, m, prayerHeal));
                                att(new GainBlockAction(m, prayerBlock));
                                m.halfDead = false;
                                break;
                            }
                        }
                        this.isDone = true;
                    }
                });
                break;
            case DO_NOT_DENY:
                dmg(adp(), info);
                break;
            case FEAR_NOT:
                for (AbstractMonster m : monsterList()) {
                    if (!m.equals(this)) {
                        att(new HealAction(m, m, baptismHeal));
                        atb(new ApplyPowerAction(m, m, new StrengthPower(m, strBuff)));
                    }
                }
                break;
            case REVELATION:
                atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, wingsOfGrace)));
                atb(new ApplyPowerAction(this, this, new StrengthPower(this, revelationStr)));
                break;
        }
    }


    @Override
    public void takeTurn() {
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new IntentFlashAction(this));
            takeCustomTurn(additionalMove, adp());
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
                    if (currentHealth <= maxHealth / 2F && phase != 3) {
                        setAdditionalMoveShortcut(PHASE_TRANSITION, moveHistory);
                    }
                }
                int apostlesToRevive = 0;
                for (AbstractMonster m : monsterList()) {
                    if (!m.equals(this) && m.currentHealth == 1) {
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
            if (mo instanceof GuardianApostle && !mo.isDeadOrEscaped()) {
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
        super.render(sb);
        whiteNight.renderSprite(sb, (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2 + (75.0f * Settings.scale));
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

}