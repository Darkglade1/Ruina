package ruina.monsters.day49;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.ClearCardQueueAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.*;
import ruina.cardmods.FrozenMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractDeckMonster;
import ruina.monsters.day49.angelaCards.frostsplinter.*;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.powers.*;
import ruina.util.AdditionalIntent;
import ruina.vfx.*;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Act4Angela extends AbstractDeckMonster
{
    public static final String ID = makeID(Act4Angela.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final String POWER_ID = makeID("PromiseOfWinter");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final int THRESHOLD = 9;

    private static final byte FRIGID_GAZE = 0;
    private static final byte BITTER_COLD = 1;
    private static final byte FROST_SPLINTER = 2;
    private static final byte SWORD_OF_FROST = 3;
    private static final byte HAILSTORM = 4;
    private static final byte BLIZZARD = 5;
    private static final byte DEEP_FREEZE = 6;
    private static final byte ABSOLUTE_ZERO = 7;
    private static final byte PHASE_TRANSITION = 8;

    public final int frigidGazeBlock = 28;
    public final int frigidGazeStrength = 6;

    public final int bitterColdDamage = 18;
    public final int bitterColdHits = 3;
    public final int bitterColdFrail = 1;

    public final int frostSplinter_damage = 22;
    public final int frostSplinter_hits = 2;

    public final int swordOfFrost_damage = 26;
    public final int swordOfFrost_hits = 3;

    public final int hailstormDamage = 70;
    public final int hailstormChill = 2;

    public final int blizzardDamage = 70;
    public final int blizzardChill = 4;

    public final int deepFreezeDamage = 85;
    public final int deepFreezeChill = 4;
    public final int deepFreezeDebuff = 1;

    public boolean blizzardUsed = false;
    public int deepFreezesUsed = 0;
    public int absoluteZeroFreeze = 5;

    private enum State {
        PHASE1,
        PHASE2,
    }
    private State currentState = State.PHASE2;
    private final int HP = 4000;
    private boolean recentlyPhaseTransitioned = false;
    private final int baseExtraActions = 1;
    private final int phase2ExtraActions = 1;
    private int frostSplinterExtraActions = 0;

    private static final int BASE_COOLDOWN = 3;
    private int CooldownCounter = BASE_COOLDOWN;

    private float particleTimer;
    private float particleTimer2;
    private float particleTimer3;


    public Act4Angela() {
        this(0.0f, 0.0f);
    }
    public Act4Angela(final float x, final float y) {
        super(NAME, ID, 600, -15.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SnowQueen/Spriter/SnowQueen.scml"));
        this.type = EnemyType.BOSS;
        this.setHp(HP);

        maxAdditionalMoves = 4;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        numAdditionalMoves = baseExtraActions;

        addMove(FRIGID_GAZE, Intent.DEFEND_BUFF);
        addMove(BITTER_COLD, Intent.ATTACK_DEBUFF, bitterColdDamage, bitterColdHits, true);
        addMove(FROST_SPLINTER, Intent.ATTACK_BUFF, frostSplinter_damage, frostSplinter_hits, true);
        addMove(SWORD_OF_FROST, Intent.ATTACK, swordOfFrost_damage, swordOfFrost_hits, true);
        addMove(HAILSTORM, Intent.ATTACK_DEBUFF, hailstormDamage);
        addMove(BLIZZARD, Intent.ATTACK_DEBUFF, blizzardDamage);
        addMove(DEEP_FREEZE, Intent.ATTACK_DEBUFF, deepFreezeDamage);
        addMove(ABSOLUTE_ZERO, Intent.UNKNOWN);
        addMove(PHASE_TRANSITION, Intent.UNKNOWN);

        firstMove = true;
        initializeDeck();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction()
    {
        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom());
        CustomDungeon.playTempMusicInstantly("Roland1");
        (AbstractDungeon.getCurrRoom()).cannotLose = true;
        atb(new FrostSplinterIceEffectAction(this));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                Act4Angela.this.runAnim("Idle");
                recentlyPhaseTransitioned = true;
                calculateAllocatedMoves();
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
        atb(new ApplyPowerAction(this, this, new Refracting(this, -1)));
        atb(new ApplyPowerAction(this, this, new DamageReductionInvincible(this, HP / 4)));
        atb(new ApplyPowerAction(this, this, new FrostSplinterStrongAttackWarning(this, 0)));
        atb(new ApplyPowerAction(this, this, new ReticentFrigidity(this)));

        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                this.amount++;
                if (this.amount >= THRESHOLD) {
                    this.flash();
                    FrozenMod mod = new FrozenMod();
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if (!CardModifierManager.hasModifier(card, FrozenMod.ID)) {
                                CardModifierManager.addModifier(card, mod.makeCopy());
                            }
                            this.isDone = true;
                        }
                    });
                    this.amount = 0;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, target); }
        switch (move.nextMove) {
            case FRIGID_GAZE: {
                specialAnimation(adp());
                block(this, frigidGazeBlock);
                applyToTarget(this, this, new NextTurnPowerPower(this, new StrengthPower(this, frigidGazeStrength)));
                applyToTarget(this, this, new NextTurnPowerPower(this, new LoseStrengthPower(this, frigidGazeStrength)));
                resetIdle();
                break;
            }
            case BITTER_COLD: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        attack1Animation(adp());
                    } else {
                        attack2Animation(adp());
                    }
                    dmg(adp(), info);

                    resetIdle();
                }
                applyToTarget(adp(), this, new FrailPower(adp(), bitterColdFrail, true));
                break;
            }
            case FROST_SPLINTER: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        attack1Animation(adp());
                    } else {
                        attack2Animation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        frostSplinterExtraActions += 1;
                        isDone = true;
                    }
                });
                break;
            }
            case SWORD_OF_FROST: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                       attack1Animation(adp());
                    } else {
                        attack2Animation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case HAILSTORM: {
                atb(new FrostSplinterWeakIceEffectAction(this));
                atb(new VFXAction(new WaitEffect(), 0.25f));
                dmg(adp(), info);
                applyToTarget(adp(), Act4Angela.this, new Chill(adp(), hailstormChill));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        CooldownCounter = BASE_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case BLIZZARD: {
                atb(new FrostSplinterIceEffectAction(this));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(recentlyPhaseTransitioned){ recentlyPhaseTransitioned = false; }
                        isDone = true;
                    }
                });
                dmg(adp(), info);
                applyToTarget(adp(), Act4Angela.this, new Chill(adp(), blizzardChill));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        blizzardUsed = true;
                        CooldownCounter = BASE_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case DEEP_FREEZE: {
                atb(new FrostSplinterIceEffectAction(this));
                dmg(adp(), info);
                applyToTarget(adp(), Act4Angela.this, new Chill(adp(), deepFreezeChill));
                applyToTarget(adp(), Act4Angela.this, new DeepFreezePower(adp(), deepFreezeDebuff));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        deepFreezesUsed += 1;
                        CooldownCounter = BASE_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case ABSOLUTE_ZERO: {
                atb(new FrostSplinterIceEffectAction(this));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractPower p = adp().getPower(DeepFreezePower.POWER_ID);
                        if(p != null){
                            if (p.amount > absoluteZeroFreeze){ att(new YeetPlayerAction()); }
                        }
                        isDone = true;
                    }
                });
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        deepFreezesUsed = 0;
                        CooldownCounter = BASE_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                break;
            }
            case PHASE_TRANSITION:
                CardCrawlGame.fadeIn(3f);
                atb(new Day49PhaseTransition4Action(0, 1));
                Act4Angela.this.gold = 0;
                Act4Angela.this.currentHealth = 0;
                Act4Angela.this.dieBypass();
                AbstractDungeon.getMonsters().monsters.remove(this);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractMonster m = new Act5Angela();
                        att(new AbstractGameAction() {
                            @Override
                            public void update() {
                                m.usePreBattleAction();
                                isDone = true;
                            }
                        });
                        att(new SpawnMonsterAction(m, false));
                        isDone = true;
                    }
                });
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
                        for (AbstractPower power : adp().powers) {
                            if (power instanceof ColdHearted || power instanceof DeepFreezePower) {
                                if(power instanceof AbstractUnremovablePower){
                                    makePowerRemovable(power);
                                    powersToRemove.add(power);
                                }
                            }
                        }
                        for (AbstractPower power : powersToRemove) { adp().powers.remove(power); }
                        for (AbstractCard card : adp().hand.group) {
                            if (CardModifierManager.hasModifier(card, FrozenMod.ID)) {
                                CardModifierManager.removeModifiersById(card, FrozenMod.ID, false);
                            }
                        }
                        for (AbstractCard card : adp().discardPile.group) {
                            if (CardModifierManager.hasModifier(card, FrozenMod.ID)) {
                                CardModifierManager.removeModifiersById(card, FrozenMod.ID, false);
                            }
                        }
                        for (AbstractCard card : adp().drawPile.group) {
                            if (CardModifierManager.hasModifier(card, FrozenMod.ID)) {
                                CardModifierManager.removeModifiersById(card, FrozenMod.ID, false);
                            }
                        }
                        isDone = true;
                    }
                });
                break;
        }
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "SnowAttack", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "SnowAttackFar", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "SnowBlizzard", enemy, this);
    }


    @Override
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
                CooldownCounter -= 1;
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower p = Act4Angela.this.getPower(FrostSplinterStrongAttackWarning.POWER_ID);
                if(p != null){
                    p.flash();
                    p.amount = CooldownCounter;
                    p.updateDescription();
                }
                isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                calculateAllocatedMoves();
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(halfDead){ setMoveShortcut(PHASE_TRANSITION); }
        else {
            if (CooldownCounter <= 0) {
                if (currentState == State.PHASE2) {
                    if (!blizzardUsed) {
                        setMoveShortcut(BLIZZARD, MOVES[BLIZZARD], new Blizzard(this));
                    } else if (deepFreezesUsed != absoluteZeroFreeze) {
                        setMoveShortcut(DEEP_FREEZE, MOVES[DEEP_FREEZE], new DeepFreeze(this));
                    } else {
                        setMoveShortcut(ABSOLUTE_ZERO, MOVES[ABSOLUTE_ZERO], new AbsoluteZero(this));
                    }
                } else {
                    setMoveShortcut(HAILSTORM, MOVES[HAILSTORM], new Hailstorm(this));
                }
            } else {
                if (recentlyPhaseTransitioned) {
                    setMoveShortcut(BLIZZARD, MOVES[BLIZZARD], new Blizzard(this));
                } else {
                    ArrayList<Byte> possibilities = new ArrayList<>();
                    if (firstMove) {
                        possibilities.add(FRIGID_GAZE);
                    } else {
                        possibilities.add(BITTER_COLD);
                        if (currentState == State.PHASE1) {
                            possibilities.add(FROST_SPLINTER);
                        }
                        possibilities.add(SWORD_OF_FROST);
                    }
                    byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                    setMoveShortcut(move, MOVES[move], getMoveCardFromByte(move));
                }
            }
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if(halfDead){}
        else { createAdditionalMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove)); }
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
    protected void createDeck() {
        for(int i = 0; i < 3; i += 1){
            masterDeck.addToBottom(new SwordOfFrost(this));
        }
        for(int i = 0; i < 2; i += 1){
            masterDeck.addToBottom(new FrigidGaze(this));
            masterDeck.addToBottom(new FrostSplinterAngela(this));
            masterDeck.addToBottom(new BitterCold(this));
        }
    }

    protected void createAdditionalMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {
        if (c.cardID.equals(FrostSplinterAngela.ID)) {
            setAdditionalMoveShortcut(FROST_SPLINTER, moveHistory, c);
        } else if (c.cardID.equals(SwordOfFrost.ID)) {
            setAdditionalMoveShortcut(SWORD_OF_FROST, moveHistory, c);
        } else if (c.cardID.equals(BitterCold.ID)) {
            setAdditionalMoveShortcut(BITTER_COLD, moveHistory, c);
        } else {
            setAdditionalMoveShortcut(FRIGID_GAZE, moveHistory, c);
        }
    }

    public void calculateAllocatedMoves(){
        if(CooldownCounter <= 0){
            numAdditionalMoves = 0;
            numAdditionalMoves += frostSplinterExtraActions;
            frostSplinterExtraActions = 0;
            if (numAdditionalMoves > maxAdditionalMoves) {
                numAdditionalMoves = maxAdditionalMoves;
            }
        }
        else {
            if(recentlyPhaseTransitioned){
                numAdditionalMoves = 0;
                frostSplinterExtraActions = 0;
            }
            else {
                numAdditionalMoves = baseExtraActions;
                if (currentState == State.PHASE2) { numAdditionalMoves += phase2ExtraActions; }
                numAdditionalMoves += frostSplinterExtraActions;
                frostSplinterExtraActions = 0;
                if (numAdditionalMoves > maxAdditionalMoves) {
                    numAdditionalMoves = maxAdditionalMoves;
                }
            }
        }
    }


    protected AbstractCard getMoveCardFromByte(Byte move) {
        switch (move){
            case FRIGID_GAZE: return new FrigidGaze(this);
            case BITTER_COLD: return new BitterCold(this);
            case FROST_SPLINTER: return new FrostSplinterAngela(this);
            case SWORD_OF_FROST: return new SwordOfFrost(this);
            default: return new Madness();
        }
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            cardsToRender.clear();
            AbstractCardMonster.hoveredCard = null;
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            att(new ClearCardQueueAction());
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power instanceof Refracting)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }

            cardsToRender.clear();
            setMove(PHASE_TRANSITION, Intent.UNKNOWN);
            additionalIntents.clear();
            additionalMoves.clear();
            ArrayList<AbstractCard> cards = cardsToRender;
            if (cards.size() > 1) {
                cards.remove(cards.size() - 1);
            }
            createIntent();
        }
    }

    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            super.die();
        }
    }

    public void dieBypass() {
        super.die(false);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        if(currentState == State.PHASE2) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.2F;
                AbstractDungeon.effectsQueue.add(new SnowflakeEffect());
                for (int i = 0; i < 50; i++){ AbstractDungeon.effectsQueue.add(new FrostSplinterSnowEffect()); }
            }
        }

        if (this.hasPower(POWER_ID)) {
            if (this.getPower(POWER_ID).amount >= THRESHOLD - 1) {
                this.particleTimer3 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer3 < 0.0F) {
                    this.particleTimer3 = 0.04F;
                    AbstractDungeon.effectsQueue.add(new FlexibleCalmParticleEffect(this));
                }

                this.particleTimer2 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer2 < 0.0F) {
                    this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                    AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(CalmStance.STANCE_ID, this));
                }
            }
        }
    }
}