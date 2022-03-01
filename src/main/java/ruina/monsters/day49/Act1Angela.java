package ruina.monsters.day49;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.stances.WrathStance;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.MakeTempCardInDiscardActionButItCanFizzle;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.blackSilence.blackSilence4.ImageOfBygones;
import ruina.monsters.day49.angelaCards.bloodbath.*;
import ruina.powers.*;
import ruina.powers.SorrowAngela;
import ruina.util.AdditionalIntent;
import ruina.vfx.FlexibleStanceAuraEffect;
import ruina.vfx.FlexibleWrathParticleEffect;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Act1Angela extends AbstractCardMonster {
    public static final String ID = RuinaMod.makeID(Act1Angela.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte WRIST_CUTTER = 0;
    public int wristCutterHPLoss = 9;
    public int wristCutterWounds = 2;

    private static final byte NUMBNESS = 1;
    public int numbnessHPLoss = 15;
    public int numbnessParalysis = 2;

    private static final byte SORROW = 2;
    public int sorrowDamage = 14;
    public int sorrowScaling = 1;

    private static final byte LOATHING = 3;
    public int loathingHPLoss = 10;
    public int loathingHPHeal = 15;

    private static final byte PALE_HANDS = 4;
    public int paleHandsDamage = 5;
    public int paleHandHits = 3;

    private static final byte SINKING = 5;
    public int sinkingDamage = 65;

    private static final byte STAINS_OF_BLOOD = 6;
    public int bloodDamage = 18;
    public int bloodBleed = 3;

    private float particleTimer;
    private float particleTimer2;

    private enum State {
        OPENING,
        TURN2,
        TURN3,
        SINKING,
        PALE_HANDS_5X,
        CYCLE_PATTERN,
    }
    private State currentState = State.OPENING;
    public Act1Angela() {
        this(0.0f, 0.0f);
    }

    public Act1Angela(final float x, final float y) {
        super(NAME, ID, 600, -15.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSilence4/Spriter/BlackSilence4.scml"));

        numAdditionalMoves = 1;
        maxAdditionalMoves = 4;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }

        this.setHp(2000);
        this.type = EnemyType.BOSS;

        addMove(WRIST_CUTTER, Intent.UNKNOWN);
        addMove(NUMBNESS, Intent.STRONG_DEBUFF);
        addMove(SORROW, Intent.ATTACK_BUFF, sorrowDamage);
        addMove(LOATHING, Intent.BUFF);
        addMove(PALE_HANDS, Intent.ATTACK, paleHandsDamage, paleHandHits,true);
        addMove(SINKING, Intent.ATTACK_BUFF, sinkingDamage);
        addMove(STAINS_OF_BLOOD, Intent.ATTACK_DEBUFF, bloodDamage);

        cardList.add(new WristCutterAngela(this));
        cardList.add(new Numbness(this));
        cardList.add(new ruina.monsters.day49.angelaCards.bloodbath.SorrowAngela(this));
        cardList.add(new Loathing(this));
        cardList.add(new PaleHands(this));
        cardList.add(new Sinking(this));
        cardList.add(new StainsOfBlood(this));

    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("GoneAngels");
        currentState = State.OPENING;
        atb(new ApplyPowerAction(this, this, new Scars(this, calcAscensionSpecial(50))));
        atb(new ApplyPowerAction(this, this, new Refracting(this, -1)));

    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case WRIST_CUTTER:
                claw1Animation(target);
                atb(new LoseHPAction(this, this, wristCutterHPLoss));
                atb(new MakeTempCardInDiscardActionButItCanFizzle(new Wound(), wristCutterWounds, this));
                resetIdle();
                break;
            case NUMBNESS:
                claw1Animation(target);
                atb(new LoseHPAction(this, this, numbnessHPLoss));
                atb(new ApplyPowerAction(adp(), this, new Paralysis(adp(), numbnessParalysis)));
                resetIdle();
                break;
            case SORROW:
                claw1Animation(target);
                dmg(target, info);
                atb(new ApplyPowerAction(this, this, new SorrowAngela(this, sorrowScaling)));
                resetIdle();
                break;
            case LOATHING:
                claw1Animation(target);
                atb(new LoseHPAction(this, this, loathingHPLoss));
                atb(new LoseHPAction(this, this, loathingHPHeal));
                resetIdle();
                break;
            case PALE_HANDS:
                for (int i = 0; i < multiplier; i++) {
                    claw1Animation(target);
                    dmg(target, info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if(adp() != null && adp().lastDamageTaken > 0){att(new LoseHPAction(Act1Angela.this, Act1Angela.this, adp().lastDamageTaken * 2));}
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                break;
            case SINKING:
                claw1Animation(target);
                dmg(target,info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(adp() != null && adp().lastDamageTaken > 0){att(new HealAction(Act1Angela.this, Act1Angela.this, adp().lastDamageTaken));}
                        isDone = true;
                    }
                });
                resetIdle();
                break;
            case STAINS_OF_BLOOD:
                claw1Animation(target);
                dmg(target, info);
                atb(new ApplyPowerAction(adp(), adp(), new Bleed(adp(), bloodBleed)));
                resetIdle();
                break;
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
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
                switch (currentState){
                    case OPENING:
                        currentState = State.TURN2;
                        break;
                    case TURN2:
                        currentState = State.TURN3;
                        numAdditionalMoves = maxAdditionalMoves;
                        break;
                    default:
                    case TURN3:
                        currentState = State.SINKING;
                        break;
                    case SINKING:
                        currentState = State.CYCLE_PATTERN;
                        break;
                    case CYCLE_PATTERN:
                        currentState = State.PALE_HANDS_5X;
                        break;
                }
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        switch (currentState){
            case OPENING:
                setMoveShortcut(WRIST_CUTTER, MOVES[WRIST_CUTTER], cardList.get(WRIST_CUTTER).makeStatEquivalentCopy());
                break;
            case CYCLE_PATTERN:
            case TURN2:
                setMoveShortcut(NUMBNESS, MOVES[NUMBNESS], cardList.get(NUMBNESS).makeStatEquivalentCopy());
                break;
            case TURN3:
            case PALE_HANDS_5X:
                setMoveShortcut(PALE_HANDS, MOVES[PALE_HANDS], cardList.get(PALE_HANDS).makeStatEquivalentCopy());
                break;
            case SINKING:
                setMoveShortcut(SINKING, MOVES[SINKING], cardList.get(SINKING).makeStatEquivalentCopy());
                break;
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        switch (currentState){
            case OPENING:
                setAdditionalMoveShortcut(NUMBNESS, moveHistory, cardList.get(NUMBNESS).makeStatEquivalentCopy());
                break;
            case TURN2:
                setAdditionalMoveShortcut(SORROW, moveHistory, cardList.get(SORROW).makeStatEquivalentCopy());
                break;
            case TURN3:
                switch (whichMove){
                    default:
                        setAdditionalMoveShortcut(PALE_HANDS, moveHistory, cardList.get(PALE_HANDS).makeStatEquivalentCopy());
                        break;
                    case 2:
                        setAdditionalMoveShortcut(WRIST_CUTTER, moveHistory, cardList.get(WRIST_CUTTER).makeStatEquivalentCopy());
                        break;
                    case 3:
                        setAdditionalMoveShortcut(NUMBNESS, moveHistory, cardList.get(NUMBNESS).makeStatEquivalentCopy());
                        break;
                }
                break;
            case PALE_HANDS_5X:
                setAdditionalMoveShortcut(PALE_HANDS, moveHistory, cardList.get(PALE_HANDS).makeStatEquivalentCopy());
                break;
            case SINKING:
                break;
            case CYCLE_PATTERN:
                switch (whichMove){
                    case 0:
                        setAdditionalMoveShortcut(WRIST_CUTTER, moveHistory, cardList.get(WRIST_CUTTER).makeStatEquivalentCopy());
                        break;
                    case 1:
                        setAdditionalMoveShortcut(LOATHING, moveHistory, cardList.get(LOATHING).makeStatEquivalentCopy());
                        break;
                    case 2:
                        setAdditionalMoveShortcut(STAINS_OF_BLOOD, moveHistory, cardList.get(STAINS_OF_BLOOD).makeStatEquivalentCopy());
                        break;
                    default:
                        setAdditionalMoveShortcut(SORROW, moveHistory, cardList.get(SORROW).makeStatEquivalentCopy());
                        break;
                }
        }
    }


    @Override
    public void applyPowers() {
        if (this.nextMove == SINKING) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            applyPowersOnlyIncrease(adp(), info);
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
            Texture attackImg = getAttackIntent(info.output * 1);
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            updateCard();
        } else {
            super.applyPowers();
        }
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
        halfDead = true;
        runAnim("Defeat");
        blacksilenceClear = true;
        saveConfig();
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof ImageOfBygones) {
                atb(new SuicideAction(mo));
            }
        }
        waitAnimation(1.0f);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                Act1Angela.super.die(triggerRelics);
                onBossVictoryLogic();
                onFinalBossVictoryLogic();
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(Scars.POWER_ID)) {
            if (this.getPower(Scars.POWER_ID).amount >= Scars.THRESHOLD) {
                this.particleTimer -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer < 0.0F) {
                    this.particleTimer = 0.04F;
                    AbstractDungeon.effectsQueue.add(new FlexibleWrathParticleEffect(this));
                }

                this.particleTimer2 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer2 < 0.0F) {
                    this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                    AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(WrathStance.STANCE_ID, this));
                }
            }
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "RolandAxe", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "RolandAxe", enemy, this);
    }

    private void gun1Animation(AbstractCreature enemy) {
        animationAction("Gun1", "RolandRevolver", enemy, this);
    }

    private void gun3Animation(AbstractCreature enemy) {
        animationAction("Gun3", "RolandShotgun", enemy, this);
    }

    private void claw1Animation(AbstractCreature enemy) {
        animationAction("Claw1", "SwordStab", enemy, this);
    }

    private void club1Animation(AbstractCreature enemy) {
        animationAction("Club1", "BluntVert", enemy, this);
    }

    private void wheelsAnimation(AbstractCreature enemy) {
        animationAction("Wheels", "RolandGreatSword", enemy, this);
    }

    private void sword1Animation(AbstractCreature enemy) {
        animationAction("Sword1", "RolandDuralandalDown", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "RolandDualSword", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }


}