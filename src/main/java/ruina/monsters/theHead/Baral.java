package ruina.monsters.theHead;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.savefields.CustomScore;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.ScoreBonusStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BackAttackPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.NeutralStance;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.HeadDialogueAction;
import ruina.actions.SerumWAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.actions.YeetPlayerAction;
import ruina.cardmods.BlackSilenceRenderMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.theHead.baralCards.*;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Argalia;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_FURIOSO;
import ruina.patches.RenderHandPatch;
import ruina.powers.*;
import ruina.powers.act5.AClaw;
import ruina.powers.act5.PlayerBlackSilence;
import ruina.powers.act5.SingularityT;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Baral extends AbstractCardMonster
{
    public static final String ID = makeID(Baral.class.getSimpleName());
    public static final String METRICS_ID = makeID("TheHead");

    private static final byte SERUM_W = 0;
    private static final byte SERUM_R = 1;
    private static final byte EXTIRPATION = 2;
    private static final byte TRI_SERUM_COCKTAIL = 3;
    private static final byte SERUM_K = 4;

    public final int SERUM_W_DAMAGE = calcAscensionDamage(40);

    public final int serumR_Damage = calcAscensionDamage(12);
    public final int serumR_Hits = 2;
    public final int serumR_Strength = 5;

    public final int extirpationDamage = calcAscensionDamage(24);
    public final int extirpationBlock = calcAscensionTankiness(50);

    public final int triSerumDamage = calcAscensionDamage(8);
    public final int triSerumHits = 3;

    public final int SERUM_K_BLOCK = calcAscensionTankiness(60);
    public final int SERUM_K_HEAL = calcAscensionTankiness(200);
    public final int SERUM_K_STR = calcAscensionSpecial(2);
    public final int KILL_THRESHOLD = 30;
    private final int SERUM_W_DAMAGE_MULTIPLIER = 3;

    public Zena zena;
    public RolandHead roland;

    public int playerMaxHp;
    public int playerCurrentHp;
    public ArrayList<AbstractRelic> playerRelics = new ArrayList<>();
    public ArrayList<AbstractPotion> playerPotions = new ArrayList<>();
    public ArrayList<AbstractPower> playerPowers = new ArrayList<>(); //gotta catch stuff like guardian's MODE SHIFT from DOWNFALL
    public int playerEnergy;
    public int playerCardDraw;
    public boolean deathTriggered = false;
    private boolean usedPreBattleAction = false;
    private boolean useExtirpation = true;

    public enum PHASE{
        PHASE1,
        PHASE2
    }

    public PHASE currentPhase;
    private static final ArrayList<TextureRegion> bgTextures = new ArrayList<>();
    private static final ArrayList<Texture> serumWFinish = new ArrayList<>();

    public Baral() { this(0.0f, 0.0f, PHASE.PHASE1); }
    public Baral(final float x, final float y) { this(x, y, PHASE.PHASE1); }
    public Baral(final float x, final float y, PHASE phase) {
        super(ID, ID, 999999, -5.0F, 0, 160.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Baral/Spriter/Baral.scml"));
        setNumAdditionalMoves(1);
        numAdditionalMoves = 0;
        currentPhase = phase;
        this.setHp(calcAscensionTankiness(5000));

        addMove(SERUM_W, Intent.ATTACK, SERUM_W_DAMAGE);
        addMove(SERUM_R, Intent.ATTACK_BUFF, serumR_Damage, serumR_Hits);
        addMove(EXTIRPATION, Intent.ATTACK_DEFEND, extirpationDamage);
        addMove(TRI_SERUM_COCKTAIL, Intent.ATTACK, triSerumDamage, triSerumHits);
        addMove(SERUM_K, Intent.DEFEND_BUFF);
        cardList.add(new SerumW(this));
        cardList.add(new SerumR(this));
        cardList.add(new Extirpation(this));
        cardList.add(new TriSerum(this));
        cardList.add(new SerumK(this));

        this.icon = TexLoader.getTexture(makeUIPath("BaralIcon.png"));

        if (bgTextures.isEmpty()) {
            ArrayList<Texture> bgs = new ArrayList<>();
            for (int i = 1; i <= 9; i++) {
                bgs.add(TexLoader.getTexture(makeMonsterPath("Baral/Backgrounds/BG" + i + ".png")));
            }
            for (Texture texture : bgs) {
                bgTextures.add(new TextureRegion(texture));
            }
        }

        if (serumWFinish.isEmpty()) {
            for (int i = 6; i <= 15; i++) {
                serumWFinish.add(TexLoader.getTexture(makeMonsterPath("Baral/Frames/frame" + i + ".png")));
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
        rollMove();
        createIntent();
        applyToTarget(this, this, new AClaw(this, KILL_THRESHOLD));
        if (AbstractDungeon.ascensionLevel >= 19) {
            applyToTarget(this, this, new SingularityT(this));
        }
    }

    @Override
    public void usePreBattleAction() {
        if (!usedPreBattleAction) {
            AbstractDungeon.lastCombatMetricKey = METRICS_ID;
            usedPreBattleAction = true;
            CustomDungeon.playTempMusicInstantly("TheHead");
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (mo instanceof Zena) {
                    zena = (Zena)mo;
                }
            }
            applyToTarget(this, this, new InvisibleBarricadePower(this));

            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            playerPowers.addAll(adp().powers);
                            adp().powers.clear();
                            this.isDone = true;
                        }
                    });
                    this.isDone = true;
                }
            });

            target = roland = new RolandHead(-1100.0F, 0.0f);
            roland.drawX = AbstractDungeon.player.drawX;
            AbstractPower power = new PlayerBlackSilence(adp(), roland);
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    applyToTarget(adp(), adp(), power);
                    this.isDone = true;
                }
            });

            playerMaxHp = adp().maxHealth;
            playerCurrentHp = adp().currentHealth;
            adp().maxHealth = roland.maxHealth + RuinaMod.corHealthMod(roland.maxHealth);
            adp().currentHealth = (int) (adp().maxHealth * 0.70f);
            adp().healthBarUpdatedEvent();
            playerRelics.addAll(adp().relics);
            adp().relics.clear();
            playerPotions.addAll(adp().potions);
            adp().potions.clear();
            playerEnergy = adp().energy.energy;
            playerCardDraw = adp().gameHandSize;
            adp().energy.energy = 5;
            EnergyPanel.totalCount = 5 - playerEnergy; //that way when the initial energy is added it adds up to 5
            adp().gameHandSize = 5;
            adp().loseBlock();
            TempHPField.tempHp.set(adp(), 0);
            adp().stance = new NeutralStance();

            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractDungeon.player.drawPile.group.clear();
                    AbstractDungeon.player.discardPile.group.clear();
                    AbstractDungeon.player.exhaustPile.group.clear();
                    AbstractDungeon.player.hand.group.clear();
                    this.isDone = true;
                }
            });
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractCard card : roland.cardList) {
                        if (!card.cardID.equals(CHRALLY_FURIOSO.ID)) {
                            CardModifierManager.addModifier(card, new BlackSilenceRenderMod());
                            AbstractDungeon.player.drawPile.group.add(card.makeStatEquivalentCopy());
                        }
                    }
                    AbstractDungeon.player.drawPile.shuffle();
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if(currentPhase == PHASE.PHASE1){
            if (roland != null) {
                roland.render(sb);
            }
        }
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        if (move.baseDamage >= 0 && currentPhase == PHASE.PHASE2 && target == adp()) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    animation.setFlip(true, false);
                    this.isDone = true;
                }
            });
        }
        if (roland != null && target == roland) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    roland.halfDead = false;
                    this.isDone = true;
                }
            });
        }
        switch (move.nextMove) {
            case SERUM_W: {
                if (serumWThresholdCheck()) {
                    info.output *= SERUM_W_DAMAGE_MULTIPLIER;
                }
                moveAnimation();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        RenderHandPatch.plsDontRenderHandOrTips = true;
                        AbstractDungeon.overlayMenu.hideCombatPanels();
                        this.isDone = true;
                    }
                });
                float duration = 2.5f;
                if (currentPhase == PHASE.PHASE1) {
                    atb(new VFXAction(new SerumWAnimation(roland, this, false, bgTextures), duration));
                } else {
                    if (target == adp()) {
                        atb(new VFXAction(new SerumWAnimation(target, this, true, bgTextures), duration));
                    } else {
                        atb(new VFXAction(new SerumWAnimation(target, this, false, bgTextures), duration));
                    }
                }
                serumWFinishAnimation();
                waitAnimation(0.25f);
                serumWAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case SERUM_R: {
                float targetOffset;
                boolean flip1;
                boolean flip2;
                if (target instanceof RolandHead || currentPhase == PHASE.PHASE1) {
                    targetOffset = -150.0f;
                    flip1 = true;
                    flip2 = false;
                } else {
                    targetOffset = 100.0f;
                    flip1 = false;
                    flip2 = true;
                }
                float initialX = this.drawX;
                float targetBehind = target.drawX + targetOffset * Settings.scale;
                serumR1(target);
                waitAnimation(0.5f);
                moveSpriteAnimation(targetBehind);
                serumR2(target);
                dmg(target, info);
                waitAnimation(0.75f);
                setFlipAnimation(flip1);
                serumR1(target);
                waitAnimation(0.5f);
                moveSpriteAnimation(initialX);
                serumR2(target);
                dmg(target, info);
                resetIdle(0.75f);
                setFlipAnimation(flip2);
                applyToTarget(this, this, new StrengthPower(this, serumR_Strength));
                break;
            }
            case EXTIRPATION: {
                block(this, extirpationBlock);
                pierceAnimation(target);
                waitAnimation(0.5f);
                pierceFinAnimation(target);
                dmg(target, info);
                resetIdle(0.75f);
                break;
            }
            case TRI_SERUM_COCKTAIL: {
                float targetOffset;
                boolean flip1;
                if (target instanceof RolandHead || currentPhase == PHASE.PHASE1) {
                    targetOffset = -150.0f;
                    flip1 = true;
                } else {
                    targetOffset = 100.0f;
                    flip1 = false;
                }
                float initialX = this.drawX;
                float targetBehind = target.drawX + targetOffset * Settings.scale;
                serumR1(target);
                waitAnimation(0.5f);
                moveSpriteAnimation(targetBehind);
                serumR2(target);
                dmg(target, info);
                waitAnimation(0.75f);
                setFlipAnimation(flip1);
                pierceAnimation(target);
                waitAnimation(0.5f);
                pierceFinAnimation(target);
                dmg(target, info);
                waitAnimation(0.75f);
                serumR1(target);
                waitAnimation(0.5f);
                moveSpriteAnimation(initialX);
                serumR2(target);
                dmg(target, info);
                resetIdle(0.75f);
                if (flip1) {
                    setFlipAnimation(false);
                }
                break;
            }
            case SERUM_K: {
                buffAnimation();
                AbstractCreature healTarget = this;
                if (!zena.isDeadOrEscaped() && zena.currentHealth <= this.currentHealth) {
                    healTarget = zena;
                }
                block(healTarget, SERUM_K_BLOCK);
                atb(new HealAction(healTarget, this, SERUM_K_HEAL));
                applyToTargetNextTurn(healTarget, this, new StrengthPower(healTarget, SERUM_K_STR));
                resetIdle(1.0f);
                break;
            }
        }

        if (move.baseDamage >= 0 && currentPhase == PHASE.PHASE2 && target == adp()) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    animation.setFlip(false, false);
                    this.isDone = true;
                }
            });
        }

        //Make roland half dead again if Baral took a turn during the player's turn
        if (roland != null && target == roland && !AbstractDungeon.actionManager.turnHasEnded) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    roland.halfDead = true;
                    this.isDone = true;
                }
            });
        }
    }

    public void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "ClawUp", enemy, this);
    }

    public void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "ClawStab", enemy, this);
    }

    public void pierceFinAnimation(AbstractCreature enemy) {
        animationAction("PierceFin", "ClawStabBlood", enemy, this);
    }

    public void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "ClawDown", enemy, this);
    }

    public void buffAnimation() {
        animationAction("Heal", "ClawInjection", this);
    }

    public void moveAnimation() {
        animationAction("Move", "ClawUltiMove", this);
    }

    public void serumWAnimation(AbstractCreature enemy) {
        animationAction("Slash", "ClawFin", enemy, this);
    }

    public void serumR1(AbstractCreature enemy) {
        animationAction("SerumR1", "ClawRedReady", enemy, this);
    }

    public void serumR2(AbstractCreature enemy) {
        animationAction("SerumR2", "ClawRedEnd", enemy, this);
    }

    public void moveSpriteAnimation(float x) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                drawX = x;
                this.isDone = true;
            }
        });
    }

    public void setFlipAnimation(boolean flipHorizontal) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                animation.setFlip(flipHorizontal, false);
                this.isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        // Make Serum K give the Strength at end of Baral's turn if he took a turn during the player's turn
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (!AbstractDungeon.actionManager.turnHasEnded) {
                    for (AbstractPower power : Baral.this.powers) {
                        if (power instanceof NextTurnPowerPower) {
                            power.atEndOfRound();
                        }
                    }
                }
                this.isDone = true;
            }
        });

        atb(new RollMoveAction(this));

        if(currentPhase.equals(PHASE.PHASE1)){
            switch (GameActionManager.turn){
                case 2:
                    waitAnimation();
                    atb(new HeadDialogueAction(0, 2));
                    GeburaHead gebura = new GeburaHead(-700.0f, 0.0f);
                    atb(new SpawnMonsterAction(gebura, false));
                    atb(new UsePreBattleActionAction(gebura));
                    gebura.onEntry();
                    atb(new HeadDialogueAction(3, 11));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            gebura.resetIdle(0.0f);
                            zena.halfDead = false;
                            isDone = true;
                        }
                    });
                    break;
            }
        }
    }

    @Override
    protected void getMove(final int num) {
        if (this.hasPower(StrengthPower.POWER_ID) && this.getPower(StrengthPower.POWER_ID).amount >= KILL_THRESHOLD) {
            if (!this.lastMove(SERUM_W)) {
                setMoveShortcut(SERUM_W, MOVES[SERUM_W], cardList.get(SERUM_W).makeStatEquivalentCopy());
            } else {
                setMoveShortcut(TRI_SERUM_COCKTAIL, MOVES[TRI_SERUM_COCKTAIL], cardList.get(TRI_SERUM_COCKTAIL).makeStatEquivalentCopy());
            }
        } else if (this.currentHealth <= this.maxHealth / 2) {
            if (this.lastMove(SERUM_K)) {
                setMoveShortcut(TRI_SERUM_COCKTAIL, MOVES[TRI_SERUM_COCKTAIL], cardList.get(TRI_SERUM_COCKTAIL).makeStatEquivalentCopy());
            } else {
                setMoveShortcut(SERUM_K, MOVES[SERUM_K], cardList.get(SERUM_K).makeStatEquivalentCopy());
            }
        } else {
            if (currentPhase == PHASE.PHASE1) {
                if (this.lastMove(SERUM_R)) {
                    setMoveShortcut(SERUM_W, MOVES[SERUM_W], cardList.get(SERUM_W).makeStatEquivalentCopy());
                } else if (this.lastMove(SERUM_W)) {
                    setMoveShortcut(EXTIRPATION, MOVES[EXTIRPATION], cardList.get(EXTIRPATION).makeStatEquivalentCopy());
                } else {
                    setMoveShortcut(SERUM_R, MOVES[SERUM_R], cardList.get(SERUM_R).makeStatEquivalentCopy());
                }
            } else {
                if (this.lastMove(SERUM_W) || this.lastMove(EXTIRPATION)) {
                    setMoveShortcut(TRI_SERUM_COCKTAIL, MOVES[TRI_SERUM_COCKTAIL], cardList.get(TRI_SERUM_COCKTAIL).makeStatEquivalentCopy());
                } else if (this.lastMove(SERUM_K)){
                    if (useExtirpation) {
                        setMoveShortcut(EXTIRPATION, MOVES[EXTIRPATION], cardList.get(EXTIRPATION).makeStatEquivalentCopy());
                        useExtirpation = false;
                    } else {
                        setMoveShortcut(SERUM_W, MOVES[SERUM_W], cardList.get(SERUM_W).makeStatEquivalentCopy());
                        useExtirpation = true;
                    }
                } else {
                    setMoveShortcut(SERUM_K, MOVES[SERUM_K], cardList.get(SERUM_K).makeStatEquivalentCopy());
                }
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
        if (!this.lastMove(SERUM_R, moveHistory) && !this.lastMoveBefore(SERUM_R, moveHistory)) {
            possibilities.add(SERUM_R);
        }
        if (!this.lastMove(TRI_SERUM_COCKTAIL, moveHistory) && !this.lastMoveBefore(TRI_SERUM_COCKTAIL, moveHistory)) {
            possibilities.add(TRI_SERUM_COCKTAIL);
        }
        if (serumWThresholdCheck()) {
            if (!this.lastMove(SERUM_W, moveHistory) && !this.lastMoveBefore(SERUM_W, moveHistory)) {
                possibilities.add(SERUM_W);
            }
        } else {
            if (!this.lastMove(EXTIRPATION, moveHistory) && !this.lastMoveBefore(EXTIRPATION, moveHistory)) {
                possibilities.add(EXTIRPATION);
            }
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        if (this.nextMove == SERUM_W && serumWThresholdCheck()) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            info.applyPowers(this, adp());
            info.output *= SERUM_W_DAMAGE_MULTIPLIER;
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
            Texture attackImg = getAttackIntent(info.output);
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            updateCard();
        }
    }

    @Override
    protected int applySpecialMultiplier(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, AbstractCreature target, int whichMove, int dmg) {
        if (additionalMove.nextMove == SERUM_W && serumWThresholdCheck()) {
            return dmg * SERUM_W_DAMAGE_MULTIPLIER;
        }
        return dmg;
    }

    private boolean serumWThresholdCheck() {
        return this.hasPower(StrengthPower.POWER_ID) && this.getPower(StrengthPower.POWER_ID).amount >= KILL_THRESHOLD;
    }

    @Override
    public void damage(DamageInfo info) {
        if (info.owner != zena) {
            super.damage(info);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        roland.target = zena;
        zena.binah.target = zena;
        zena.onBaralDeath();
        AbstractDungeon.onModifyPower();
        if (zena.isDeadOrEscaped() && !zena.deathTriggered) {
            deathTriggered = true;
            zena.binah.onBossDeath();
            roland.onBossDeath();
            zena.gebura.onBossDeath();
            headClear = true;
            saveConfig();
            ScoreBonusStrings sbs = CardCrawlGame.languagePack.getScoreString(RuinaMod.makeID("HeadHunter"));
            CustomScore.add(RuinaMod.makeID("HeadHunter"), sbs.NAME, sbs.DESCRIPTIONS[0], 400, false);
            this.onBossVictoryLogic();
            this.onFinalBossVictoryLogic();
        }
    }

    public void serumWFinishAnimation() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("ClawCutscene");
                this.isDone = true;
            }
        });
        waitAnimation();
        fullScreenAnimation(serumWFinish, 0.1f, 1.0f);
    }

    public void onZenaDeath() {
        if (!this.isDeadOrEscaped()) {
            atb(new TalkAction(this, DIALOG[0]));
        }
    }
}