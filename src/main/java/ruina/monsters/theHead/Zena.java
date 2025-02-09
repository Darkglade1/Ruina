package ruina.monsters.theHead;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.savefields.CustomScore;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.ScoreBonusStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.SlaversCollar;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.actions.HeadDialogueAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.theHead.zenaCards.*;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act5.*;
import ruina.util.TexLoader;
import ruina.vfx.CustomCeilingDust;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Zena extends AbstractCardMonster {
    public static final String ID = makeID(Zena.class.getSimpleName());

    protected static final byte LINE = 0;
    protected static final byte THIN_LINE = 1;
    protected static final byte THICK_LINE = 2;
    protected static final byte SHOCKWAVE = 3;
    protected static final byte BIRDCAGE = 4;
    protected static final byte NONE = 5;


    public final int BLOCK = calcAscensionTankiness(45);
    public final int DEBUFF = 2;
    public final int POWER_DEBUFF = 2;
    public final int THICK_LINE_DEBUFF = 2;
    public final int BIRDCAGE_HITS = 2;
    public final int BIRDCAGE_FAIRY = 3;
    public final int BACK_ATTACK_AMT = 50;
    public final float playerX = 1650.0f * Settings.scale;

    public GeburaHead gebura;
    public BinahHead binah;
    public Baral baral;
    public boolean deathTriggered = false;
    public boolean usedShockwave = false;

    public enum PHASE {
        PHASE1,
        PHASE2
    }

    public PHASE currentPhase;
    private boolean usedPreBattleAction = false;
    private static final ArrayList<Texture> shockwave = new ArrayList<>();

    public Zena(final float x, final float y) {
        super(ID, ID, 999999, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Zena/Spriter/Zena.scml"));
        setNumAdditionalMoves(1);
        numAdditionalMoves = 0;
        currentPhase = PHASE.PHASE1;
        this.setHp(calcAscensionTankiness(5000));

        addMove(LINE, Intent.ATTACK_DEFEND, calcAscensionDamage(22));
        addMove(THIN_LINE, Intent.ATTACK_DEBUFF, calcAscensionDamage(20));
        addMove(THICK_LINE, Intent.ATTACK_DEBUFF, calcAscensionDamage(18));
        addMove(SHOCKWAVE, IntentEnums.MASS_ATTACK, calcAscensionDamage(35));
        addMove(BIRDCAGE, Intent.ATTACK, calcAscensionDamage(20), BIRDCAGE_HITS);
        addMove(NONE, Intent.NONE);
        halfDead = currentPhase.equals(PHASE.PHASE1);

        cardList.add(new Line(this));
        cardList.add(new ThinLine(this));
        cardList.add(new ThickLine(this));
        cardList.add(new ZenaShockwave(this));
        cardList.add(new Birdcage(this));

        this.icon = TexLoader.getTexture(makeUIPath("ZenaIcon.png"));

        if (shockwave.isEmpty()) {
            for (int i = 1; i <= 2; i++) {
                shockwave.add(TexLoader.getTexture(makeMonsterPath("Zena/Shockwave/frame" + i + ".png")));
            }
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    public void transitionToPhase2() {
        currentPhase = PHASE.PHASE2;
        numAdditionalMoves++;
        applyToTarget(this, this, new AnArbiter(this, POWER_DEBUFF));
        if (AbstractDungeon.ascensionLevel >= 19) {
            applyToTarget(this, this, new SingularityJ(this, baral));
            applyToTarget(baral, baral, new SingularityJInvisible(baral, this));
        }
    }

    @Override
    public void usePreBattleAction() {
        if (!usedPreBattleAction) {
            usedPreBattleAction = true;
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (mo instanceof Baral) {
                    baral = (Baral) mo;
                }
            }
            this.powers.add(new InvisibleBarricadePower(this));
        }
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);

        if (currentPhase == PHASE.PHASE2 && target == adp() && move.nextMove != SHOCKWAVE) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    animation.setFlip(true, false);
                    this.isDone = true;
                }
            });
        }

        switch (move.nextMove) {
            case LINE: {
                block(this, BLOCK);
                slashAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case THIN_LINE: {
                pierceAnimation(target);
                dmg(target, info);
                AbstractCreature enemy = target;
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        int debuff = DEBUFF;
                        AbstractPower str = enemy.getPower(StrengthPower.POWER_ID);
                        if (str != null) {
                            if (str.amount > 0) {
                                if (debuff > str.amount) {
                                    debuff = str.amount;
                                }
                                applyToTargetTop(enemy, Zena.this, new StrengthPower(enemy, -debuff));
                            }
                        }
                        this.isDone = true;
                    }
                });
                applyToTargetNextTurn(this, new StrengthPower(this, DEBUFF));
                resetIdle();
                break;
            }
            case THICK_LINE: {
                bluntAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, THICK_LINE_DEBUFF, true));
                applyToTarget(target, this, new VulnerablePower(target, THICK_LINE_DEBUFF, true));
                resetIdle();
                break;
            }
            case SHOCKWAVE: {
                massAttackStartAnimation();
                waitAnimation();
                shockwaveCutscene();
                massAttackFinishAnimation();
                atb(new DamageAllOtherCharactersAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        usedShockwave = true;
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.LONG, false);
                        for (int i = 0; i < 10; i++) {
                            AbstractDungeon.effectsQueue.add(new CustomCeilingDust());
                        }
                        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom());
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                break;
            }
            case BIRDCAGE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        bluntAnimation(target);
                    } else {
                        pierceAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (mo instanceof AbstractAllyMonster && !mo.isDeadOrEscaped()) {
                        applyToTarget(mo, this, new Fairy(mo, BIRDCAGE_FAIRY));
                    }
                }
                applyToTarget(adp(), this, new Fairy(adp(), BIRDCAGE_FAIRY));
                break;
            }
        }

        if (currentPhase == PHASE.PHASE2 && target == adp() && move.nextMove != SHOCKWAVE) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    animation.setFlip(false, false);
                    this.isDone = true;
                }
            });
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "ZenaNormalLine", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "ZenaThinLine", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "ZenaBoldLine", enemy, this);
    }

    private void massAttackStartAnimation() {
        animationAction("Shockwave1", "ZenaStart", this);
    }

    private void massAttackFinishAnimation() {
        animationAction("Shockwave2", "ZenaBoom", this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        handlePreEvent();
        atb(new RollMoveAction(this));
    }

    protected void handlePreEvent() {
        if (currentPhase.equals(Zena.PHASE.PHASE1)) {
            switch (GameActionManager.turn) {
                case 4:
                    waitAnimation();
                    atb(new HeadDialogueAction(12, 13));
                    binah = new BinahHead(-1250.0f, 0.0f);
                    atb(new SpawnMonsterAction(binah, false));
                    atb(new UsePreBattleActionAction(binah));
                    binah.onEntry();
                    atb(new HeadDialogueAction(14, 24));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            binah.resetIdle(0.0f);
                            isDone = true;
                        }
                    });
                    break;
                case 6:
                    waitAnimation();
                    atb(new HeadDialogueAction(25, 30));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, false);
                            playSound("Shaking");
                            this.isDone = true;
                        }
                    });
                    waitAnimation(1.5f);
                    int rolandCorrectHealth = adp().currentHealth;
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            adp().drawX = playerX;
                            adp().dialogX = (adp().drawX + 20.0F) * Settings.scale;
                            adp().flipHorizontal = true;
                            AbstractPower strength = adp().getPower(StrengthPower.POWER_ID);
                            if (strength != null) {
                                baral.roland.powers.add(new StrengthPower(baral.roland, strength.amount));
                            }
                            baral.roland.currentHealth = rolandCorrectHealth;
                            baral.roland.healthBarUpdatedEvent();
                            int furiosoCounter = 9;
                            AbstractPower playerBlackSilence = adp().getPower(PlayerBlackSilence.POWER_ID);
                            if (playerBlackSilence != null) {
                                furiosoCounter = 9 - playerBlackSilence.amount;
                            }
                            baral.roland.setFuriosoCounter(furiosoCounter);
                            adp().maxHealth = baral.playerMaxHp;
                            adp().currentHealth = baral.playerCurrentHp;
                            adp().heal(adp().maxHealth, false);
                            adp().healthBarUpdatedEvent();
                            adp().loseBlock();
                            fixOrbPositioning();
                            adp().powers.clear();
                            this.isDone = true;
                        }
                    });
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            baral.useStaggerAnimation();
                            Zena.this.useStaggerAnimation();
                            AbstractDungeon.effectList.add(new StrikeEffect(baral, baral.hb.cX, baral.hb.cY, 999));
                            AbstractDungeon.effectList.add(new StrikeEffect(Zena.this, Zena.this.hb.cX, Zena.this.hb.cY, 999));
                            this.isDone = true;
                        }
                    });

                    atb(new SpawnMonsterAction(baral.roland, false));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            //set the health again here in case corrupt the spire increases it when roland spawns
                            baral.roland.currentHealth = rolandCorrectHealth;
                            baral.roland.healthBarUpdatedEvent();
                            this.isDone = true;
                        }
                    });
                    atb(new UsePreBattleActionAction(baral.roland));
                    atb(new VFXAction(new SmokeBombEffect(playerX, adp().hb.cY), 1.5f));
                    atb(new HeadDialogueAction(31, 37));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            transitionToPhase2();
                            baral.transitionToPhase2();
                            adp().drawPile.initializeDeck(adp().masterDeck);
                            adp().hand.clear();
                            adp().discardPile.clear();
                            adp().exhaustPile.clear();
                            adp().relics.addAll(baral.playerRelics);
                            adp().potions.addAll(baral.playerPotions);
                            adp().powers.addAll(baral.playerPowers);
                            adp().energy.energy = baral.playerEnergy;
                            adp().gameHandSize = baral.playerCardDraw;
                            if (adp().hasRelic(SlaversCollar.ID)) {
                                ((SlaversCollar) adp().getRelic(SlaversCollar.ID)).beforeEnergyPrep();
                            }
                            baral.roland.rollMove();
                            GameActionManager.turn = 0;
                            adp().applyPreCombatLogic();
                            adp().applyStartOfCombatLogic();
                            adp().applyStartOfCombatPreDrawLogic();
                            CustomDungeon.playTempMusicInstantly("TheHead");
                            this.isDone = true;
                        }
                    });
                    applyToTarget(adp(), adp(), new PlayerBackAttack(adp(), BACK_ATTACK_AMT));
                    break;
            }
        }
    }

    @Override
    public void applyPowers() {
        if (currentPhase == PHASE.PHASE1 && gebura != null && !gebura.isDead && !gebura.isDying && nextMove != SHOCKWAVE) {
            attackingMonsterWithPrimaryIntent = true;
        } else {
            attackingMonsterWithPrimaryIntent = false;
        }
        super.applyPowers();
    }

    private void fixOrbPositioning() {
        for (int i = 0; i < AbstractDungeon.player.orbs.size(); i++) {
            (AbstractDungeon.player.orbs.get(i)).setSlot(i, AbstractDungeon.player.maxOrbs);
        }
    }

    @Override
    protected void getMove(final int num) {
        if (currentPhase == PHASE.PHASE1) {
            if (halfDead) {
                setMoveShortcut(NONE);
            } else if (threeTurnCooldownHasPassedForMove(SHOCKWAVE)) {
                setMoveShortcut(SHOCKWAVE);
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(THIN_LINE)) {
                    possibilities.add(THIN_LINE);
                }
                if (!this.lastMove(LINE)) {
                    possibilities.add(LINE);
                }
                byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
                setMoveShortcut(move);
            }
        } else {
            if (threeTurnCooldownHasPassedForMove(SHOCKWAVE)) {
                setMoveShortcut(SHOCKWAVE);
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(THIN_LINE)) {
                    possibilities.add(THIN_LINE);
                }
                if (!this.lastMove(LINE)) {
                    possibilities.add(LINE);
                }
                byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
                setMoveShortcut(move);
            }
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resets cooldowns
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(THIN_LINE, moveHistory) && !this.lastMoveBefore(THIN_LINE, moveHistory)) {
            possibilities.add(THIN_LINE);
        }
        if (!this.lastMove(THICK_LINE, moveHistory) && !this.lastMoveBefore(THICK_LINE, moveHistory)) {
            possibilities.add(THICK_LINE);
        }
        if (!this.lastMove(LINE, moveHistory) && !this.lastMoveBefore(LINE, moveHistory)) {
            possibilities.add(LINE);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        gebura.target = baral;
        binah.target = baral;
        baral.onZenaDeath();
        AbstractDungeon.onModifyPower();
        if (baral.isDeadOrEscaped() && !baral.deathTriggered) {
            deathTriggered = true;
            binah.onBossDeath();
            baral.roland.onBossDeath();
            gebura.onBossDeath();
            headClear = true;
            saveConfig();
            ScoreBonusStrings sbs = CardCrawlGame.languagePack.getScoreString(RuinaMod.makeID("HeadHunter"));
            CustomScore.add(RuinaMod.makeID("HeadHunter"), sbs.NAME, sbs.DESCRIPTIONS[0], 400, false);
            this.onBossVictoryLogic();
            this.onFinalBossVictoryLogic();
        }
    }

    public void shockwaveCutscene() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("ZenaCutscene");
                this.isDone = true;
            }
        });
        fullScreenAnimation(shockwave, 0.5f, 1.0f);
    }

    public void onBaralDeath() {
        if (!this.isDeadOrEscaped()) {
            atb(new TalkAction(this, DIALOG[0]));
        }
    }

}