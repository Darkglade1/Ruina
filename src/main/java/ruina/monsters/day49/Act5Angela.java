package ruina.monsters.day49;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.green.DaggerSpray;
import com.megacrit.cardcrawl.cards.green.Terror;
import com.megacrit.cardcrawl.cards.red.Bludgeon;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.Day49PhaseTransition5Action;
import ruina.actions.SilentGirlEffectAction;
import ruina.cards.Guilt;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.act3.silentGirl.DummyHammer;
import ruina.monsters.act3.silentGirl.DummyNail;
import ruina.monsters.day49.angelaCards.silentGirlPhase1.*;
import ruina.powers.*;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.io.IOException;
import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Act5Angela extends AbstractCardMonster
{
    public static final String ID = makeID(Act5Angela.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte DIGGING_NAIL = 0;
    private static final byte SLAM = 1;
    private static final byte A_CRACKED_HEART = 2;
    private static final byte COLLAPSING_HEART = 3;
    private static final byte BROKEN = 4;
    private static final byte LEER = 5;
    private static final byte SUPPRESS = 6;
    private static final byte PHASE_TRANSITION = 7;

    public final int diggingNailDamage = 22;
    public final int diggingNailFrail = 3;

    public final int slamDamage = 24;
    public final int slamBlock = 24;

    public final int crackedHeartDamage = 26;
    public final int crackedHeartVulnerable = 3;

    public final int collapsingHeartDamage = 28;

    public final int brokenDamage = 20;
    public final int brokenHits = 3;

    public final int leerStrength = 5;
    public final int leerParalysis = 3;

    public final int suppressBlock = 72;
    public final int suppressStrength = 5;

    private final int CURSE_AMT = 1;
    private final float HP_THRESHOLD = 0.75f;
    private int TRANSITION_HP_THRESHOLD;
    private int enraged = 1; //1 is false, 2 is true

    private DummyHammer hammer = new DummyHammer(100.0f, 0.0f);
    private DummyNail nail = new DummyNail(-300.0f, 0.0f);
    AbstractCard curse = new Guilt();

    public static final String POWER_ID = makeID("Remorse");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final int HP = 5000;

    public Act5Angela() {
        this(-100.0f, 0.0f);
    }

    public Act5Angela(final float x, final float y) {
        super(NAME, ID, 480, 0.0F, 0, 250.0f, 290.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Day49/Remorse/Spriter/SilentGirl.scml"));
        this.type = EnemyType.BOSS;
        setHp(HP);
        addMove(DIGGING_NAIL, Intent.ATTACK_DEBUFF, diggingNailDamage);
        addMove(SLAM, Intent.ATTACK_DEFEND, slamDamage);
        addMove(A_CRACKED_HEART, Intent.ATTACK_DEBUFF, crackedHeartDamage);
        addMove(COLLAPSING_HEART, Intent.ATTACK, collapsingHeartDamage);
        addMove(BROKEN, Intent.ATTACK, brokenDamage, brokenHits, true);
        addMove(LEER, Intent.DEBUFF);
        addMove(SUPPRESS, Intent.DEFEND_BUFF);
        addMove(PHASE_TRANSITION, Intent.UNKNOWN);

        if (AbstractDungeon.ascensionLevel >= 19) {
            curse.upgrade();
        }
        numAdditionalMoves = 1;
        maxAdditionalMoves = 1;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        TRANSITION_HP_THRESHOLD = maxHealth / 2;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom());
        CustomDungeon.playTempMusicInstantly("Story2");
        PlayerAngela playerAngela = new PlayerAngela(adp());
        AbstractDungeon.player.powers.add(playerAngela);
        AbstractDungeon.player.powers.add(new Memoir(adp()));
        AbstractDungeon.player.powers.add(new InvisibleBarricadePower(adp()));
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        atb(new ApplyPowerAction(this, this, new Refracting(this, -1)));
        atb(new ApplyPowerAction(this, this, new DamageReductionInvincible(this, HP / 4)));
        applyToTarget(this, this, new SilentGirlPower(this));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, CURSE_AMT) {
            @Override
            public void atEndOfRound() {
                atb(new MakeTempCardInDrawPileAction(curse.makeStatEquivalentCopy(), amount, false, true));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + CURSE_AMT + POWER_DESCRIPTIONS[1] + FontHelper.colorString(curse.name, "y") + POWER_DESCRIPTIONS[2];
            }
        });
        playerAngela.atStartOfTurnPostDraw();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) { firstMove = false; }
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
                PhaseCheck();
                isDone = true;
            }
        });
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) { info.applyPowers(this, adp()); }

        switch (move.nextMove) {
            case DIGGING_NAIL: {
                nail.attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new NextTurnPowerPower(adp(), new FrailPower(adp(), diggingNailFrail, false)));
                nail.resetIdle();
                break;
            }
            case SLAM: {
                block(this, slamBlock);
                hammer.attackAnimation(adp());
                dmg(adp(), info);
                hammer.resetIdle();
                break;
            }
            case A_CRACKED_HEART: {
                nail.attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new NextTurnPowerPower(adp(), new VulnerablePower(adp(), crackedHeartVulnerable, false)));
                nail.resetIdle();
                break;
            }
            case COLLAPSING_HEART: {
                hammer.attackAnimation(adp());
                dmg(adp(), info);
                hammer.resetIdle();
                break;
            }
            case BROKEN: {
                for (int i = 0; i < multiplier; i++) {
                    specialUpAnimation(adp());
                    waitAnimation(1.5f);
                    specialDownAnimation(adp());
                    dmg(adp(), info);
                }
                resetIdle();
                break;
            }
            case LEER: {
                rangedAnimation(adp());
                applyToTarget(adp(), this, new Paralysis(adp(), leerParalysis));
                applyToTarget(this, this, new StrengthPower(this, leerStrength));
                resetIdle(1.0f);
                break;
            }
            case SUPPRESS: {
                phaseChangeAnimation();
                hammer.deadAnimation();
                nail.deadAnimation();
                atb(new RemoveDebuffsAction(this));
                block(this, suppressBlock);
                atb(new HealAction(this, this, (int)(maxHealth * 0.25f)));
                applyToTarget(this, this, new StrengthPower(this, suppressStrength));
                enraged = 2;
                resetIdle(1.0f);
                break;
            }
            case PHASE_TRANSITION:
                specialUpAnimation(adp());
                waitAnimation(1.5f);
                specialDownAnimation(adp());
                atb(new SilentGirlEffectAction(true));
                resetIdle();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(!RuinaMod.ruinaConfig.getBool("seenD49Message")){
                            RuinaMod.ruinaConfig.setBool("seenD49Message", true);
                            try { RuinaMod.ruinaConfig.save(); } catch (IOException e) { e.printStackTrace(); }
                            att(new Day49PhaseTransition5Action(0, 5, Act5Angela.this));
                        }
                        else { att(new Day49PhaseTransition5Action(0, 2, Act5Angela.this)); }
                        isDone = true;
                    }
                });
                break;
        }
    }

    @Override
    protected void getMove(final int num) {
        if(halfDead){ setMoveShortcut(PHASE_TRANSITION); }
        else if (enraged == 1) {
            if (currentHealth <= maxHealth * HP_THRESHOLD) {
                setMoveShortcut(SUPPRESS, MOVES[SUPPRESS], getMoveCardFromByte(SUPPRESS));
            } else if (lastMove(DIGGING_NAIL)) {
                setMoveShortcut(SLAM, MOVES[SLAM], getMoveCardFromByte(SLAM));
            } else if (lastMove(SLAM)) {
                setMoveShortcut(A_CRACKED_HEART, MOVES[A_CRACKED_HEART], getMoveCardFromByte(A_CRACKED_HEART));
            } else if (lastMove(A_CRACKED_HEART)) {
                setMoveShortcut(COLLAPSING_HEART, MOVES[COLLAPSING_HEART], getMoveCardFromByte(COLLAPSING_HEART));
            } else {
                setMoveShortcut(DIGGING_NAIL, MOVES[DIGGING_NAIL], getMoveCardFromByte(DIGGING_NAIL));
            }
        } else {
            if (lastMove(BROKEN)) {
                setMoveShortcut(LEER, MOVES[LEER], getMoveCardFromByte(LEER));
            } else {
                setMoveShortcut(BROKEN, MOVES[BROKEN], getMoveCardFromByte(BROKEN));
            }
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if(halfDead || ((currentHealth <= maxHealth * HP_THRESHOLD) && enraged == 1) || enraged == 2){}
        else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!lastMove(SLAM, moveHistory)) {
                possibilities.add(SLAM);
            }
            if (!lastMove(COLLAPSING_HEART, moveHistory)) {
                possibilities.add(COLLAPSING_HEART);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory, getMoveCardFromByte(move));
        }
    }

    protected AbstractCard getMoveCardFromByte(Byte move) {
        ArrayList<AbstractCard> list = new ArrayList<>();
        list.add(new DiggingNail(this));
        list.add(new Slam(this));
        list.add(new CrackedHeart(this));
        list.add(new CollapsingHeart(this));
        list.add(new BrokenToPieces(this));
        list.add(new Leer(this));
        list.add(new Suppress(this));
        return list.get(move);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + enraged);
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (!this.isDeadOrEscaped()) {
            hammer.render(sb);
            nail.render(sb);
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new CardPowerTip(curse.makeStatEquivalentCopy()));
    }

    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            super.die();
        }
    }

    public void dieBypass() {
        super.die(false);
    }

    private void rangedAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "SilentEye", enemy, this);
    }

    private void phaseChangeAnimation() {
        animationAction("Ranged", "SilentPhaseChange", this);
    }

    private void specialUpAnimation(AbstractCreature enemy) {
        animationAction("SpecialUp", "SilentHammer", enemy, this);
    }

    private void specialDownAnimation(AbstractCreature enemy) {
        animationAction("SpecialDown", "SilentHammer", enemy, this);
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

    private void PhaseCheck() {
        if(currentHealth <= TRANSITION_HP_THRESHOLD){
            specialUpAnimation(adp());
            waitAnimation(1.5f);
            specialDownAnimation(adp());
            atb(new SilentGirlEffectAction(true));
            resetIdle();
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    if(!RuinaMod.ruinaConfig.getBool("seenD49Message")){
                        RuinaMod.ruinaConfig.setBool("seenD49Message", true);
                        try { RuinaMod.ruinaConfig.save(); } catch (IOException e) { e.printStackTrace(); }
                        att(new Day49PhaseTransition5Action(0, 5, Act5Angela.this));
                    }
                    else { att(new Day49PhaseTransition5Action(0, 2, Act5Angela.this)); }
                    isDone = true;
                }
            });
        }
        else {
            att(new RollMoveAction(this));
        }
    }
}