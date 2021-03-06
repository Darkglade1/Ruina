package ruina.monsters.theHead;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.NeutralStance;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.HeadDialogueAction;
import ruina.actions.SerumWAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.actions.YeetPlayerAction;
import ruina.cardmods.BlackSilenceRenderMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.theHead.baralCards.Extirpation;
import ruina.monsters.theHead.baralCards.SerumK;
import ruina.monsters.theHead.baralCards.SerumR;
import ruina.monsters.theHead.baralCards.SerumW;
import ruina.monsters.theHead.baralCards.TriSerum;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_FURIOSO;
import ruina.patches.RenderHandPatch;
import ruina.powers.AClaw;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.PlayerBlackSilence;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Baral extends AbstractCardMonster
{
    public static final String ID = makeID(Baral.class.getSimpleName());
    public static final String METRICS_ID = makeID("TheHead");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte SERUM_W = 0;
    private static final byte SERUM_R = 1;
    private static final byte EXTIRPATION = 2;
    private static final byte TRI_SERUM_COCKTAIL = 3;
    private static final byte SERUM_K = 4;

    public final int SERUM_W_DAMAGE = calcAscensionDamage(45);

    public final int serumR_Damage = calcAscensionDamage(12);
    public final int serumR_Hits = 2;
    public final int serumR_Strength = calcAscensionSpecial(5);

    public final int extirpationDamage = calcAscensionDamage(26);
    public final int extirpationBlock = calcAscensionTankiness(50);

    public final int triSerumDamage = calcAscensionDamage(9);
    public final int triSerumHits = 3;

    private static final int SERUM_COOLDOWN = 2;
    private int serumCooldown = 1;

    public final int SERUM_K_BLOCK = calcAscensionTankiness(60);
    public final int SERUM_K_HEAL = calcAscensionTankiness(150);
    public final int POWER_STRENGTH = calcAscensionSpecial(8);
    public final int POWER_DAMAGE_REDUCTION = calcAscensionSpecial(30);
    public final int POWER_THRESHOLD = 30;
    public final int KILL_THRESHOLD = 25;
    public final int INVINCIBLE;

    public RolandHead roland;
    public Zena zena;

    public int playerMaxHp;
    public int playerCurrentHp;
    public ArrayList<AbstractRelic> playerRelics = new ArrayList<>();
    public ArrayList<AbstractPotion> playerPotions = new ArrayList<>();
    public ArrayList<AbstractPower> playerPowers = new ArrayList<>(); //gotta catch stuff like guardian's MODE SHIFT from DOWNFALL
    public int playerEnergy;
    public int playerCardDraw;
    public boolean deathTriggered = false;
    private boolean usedPreBattleAction = false;

    public enum PHASE{
        PHASE1,
        PHASE2
    }

    public PHASE currentPhase;

    public static final Texture targetTexture = TexLoader.getTexture(makeUIPath("BaralIcon.png"));
    private static final ArrayList<TextureRegion> bgTextures = new ArrayList<>();
    private static final ArrayList<Texture> serumWFinish = new ArrayList<>();

    public Baral() { this(0.0f, 0.0f, PHASE.PHASE1); }
    public Baral(final float x, final float y) { this(x, y, PHASE.PHASE1); }
    public Baral(final float x, final float y, PHASE phase) {
        super(NAME, ID, 999999, -5.0F, 0, 160.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Baral/Spriter/Baral.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 0;
        maxAdditionalMoves = 1;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        currentPhase = phase;
        this.setHp(calcAscensionTankiness(3000));

        addMove(SERUM_W, Intent.ATTACK, SERUM_W_DAMAGE);
        addMove(SERUM_R, Intent.ATTACK_BUFF, serumR_Damage, serumR_Hits, true);
        addMove(EXTIRPATION, Intent.ATTACK_DEFEND, extirpationDamage);
        addMove(TRI_SERUM_COCKTAIL, Intent.ATTACK, triSerumDamage, triSerumHits, true);
        addMove(SERUM_K, Intent.DEFEND_BUFF);
        cardList.add(new SerumW(this));
        cardList.add(new SerumR(this));
        cardList.add(new Extirpation(this));
        cardList.add(new TriSerum(this));
        cardList.add(new SerumK(this));

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

        if (AbstractDungeon.ascensionLevel >= 19) {
            INVINCIBLE = 450;
        } else {
            INVINCIBLE = 600;
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
        applyToTarget(this, this, new AClaw(this, POWER_THRESHOLD, POWER_DAMAGE_REDUCTION, POWER_STRENGTH));
        applyToTarget(this, this, new InvinciblePower(this, INVINCIBLE));
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

            roland = new RolandHead(-1100.0F, 0.0f);
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
            adp().maxHealth = roland.maxHealth;
            adp().currentHealth = (int) (roland.maxHealth * 0.70f);
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
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if (move.baseDamage >= 0 && currentPhase == PHASE.PHASE2 && target == adp()) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    animation.setFlip(true, false);
                    this.isDone = true;
                }
            });
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case SERUM_W: {
                moveAnimation();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        RenderHandPatch.plsDontRenderHand = true;
                        AbstractDungeon.overlayMenu.hideCombatPanels();
                        this.isDone = true;
                    }
                });
                float duration = 3.5f;
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
                AbstractPower strength = getPower(StrengthPower.POWER_ID);
                if (strength != null && strength.amount >= KILL_THRESHOLD) {
                    if (target == adp()) {
                        atb(new YeetPlayerAction());
                    } else {
                        AbstractCreature enemy = target;
                        atb(new AbstractGameAction() {
                            @Override
                            public void update() {
                                enemy.currentHealth = 0;
                                enemy.healthBarUpdatedEvent();
                                enemy.useStaggerAnimation();
                                AbstractDungeon.effectList.add(new StrikeEffect(enemy, enemy.hb.cX, enemy.hb.cY, 999));
                                enemy.damage(new DamageInfo(null, 0, DamageInfo.DamageType.HP_LOSS));
                                this.isDone = true;
                            }
                        });
                    }
                }
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        serumCooldown = SERUM_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case SERUM_R: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        bluntAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(this, this, new StrengthPower(this, serumR_Strength));
                break;
            }
            case EXTIRPATION: {
                block(this, extirpationBlock);
                bluntAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case TRI_SERUM_COCKTAIL: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
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
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "ClawUp", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "ClawStab", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "ClawDown", enemy, this);
    }

    private void buffAnimation() {
        animationAction("Heal", "ClawInjection", this);
    }

    private void moveAnimation() {
        animationAction("Move", "ClawUltiMove", this);
    }

    private void serumWAnimation(AbstractCreature enemy) {
        animationAction("Slash", "ClawFin", enemy, this);
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new RemoveAllBlockAction(this, this));
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, roland);
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                serumCooldown--;
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
        if (serumCooldown <= 0) {
            setMoveShortcut(SERUM_W, MOVES[SERUM_W], cardList.get(SERUM_W).makeStatEquivalentCopy());
        } else {
            if (currentPhase == PHASE.PHASE1) {
                if (!this.lastMove(SERUM_R) && !this.lastMoveBefore(SERUM_R)) {
                    setMoveShortcut(SERUM_R, MOVES[SERUM_R], cardList.get(SERUM_R).makeStatEquivalentCopy());
                } else {
                    setMoveShortcut(EXTIRPATION, MOVES[EXTIRPATION], cardList.get(EXTIRPATION).makeStatEquivalentCopy());
                }
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(SERUM_K)) {
                    possibilities.add(SERUM_K);
                }
                if (!this.lastMove(EXTIRPATION)) {
                    possibilities.add(EXTIRPATION);
                }
                if (!this.lastMove(TRI_SERUM_COCKTAIL)) {
                    possibilities.add(TRI_SERUM_COCKTAIL);
                }
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
            }
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resetss cooldowns
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(SERUM_R, moveHistory) && !this.lastMoveBefore(SERUM_R, moveHistory)) {
            possibilities.add(SERUM_R);
        }
        if (!this.lastMove(SERUM_W, moveHistory) && !this.lastMoveBefore(SERUM_W, moveHistory)) {
            possibilities.add(SERUM_W);
        }
        if (!this.lastMove(EXTIRPATION, moveHistory) && !this.lastMoveBefore(EXTIRPATION, moveHistory)) {
            possibilities.add(EXTIRPATION);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, roland, roland.allyIcon);
            }
        }
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
        roland.enemyBoss = zena;
        zena.binah.targetEnemy = zena;
        AbstractDungeon.onModifyPower();
        if (zena.isDeadOrEscaped() && !zena.deathTriggered) {
            deathTriggered = true;
            zena.binah.onBossDeath();
            roland.onBossDeath();
            zena.gebura.onBossDeath();
            headClear = true;
            saveConfig();
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
}