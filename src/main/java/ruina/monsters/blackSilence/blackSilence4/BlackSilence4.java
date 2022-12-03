package ruina.monsters.blackSilence.blackSilence4;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.blights.Shield;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.WrathStance;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.ChooseOneActionButItCanFizzle;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.blackSilence.blackSilence4.cards.Agony;
import ruina.monsters.blackSilence.blackSilence4.cards.Scream;
import ruina.monsters.blackSilence.blackSilence4.cards.Void;
import ruina.monsters.blackSilence.blackSilence4.memories.Shi.Shi;
import ruina.monsters.blackSilence.blackSilence4.memories.Shi.Shi1;
import ruina.monsters.blackSilence.blackSilence4.memories.Shi.Shi2;
import ruina.monsters.blackSilence.blackSilence4.memories.blue.Blue;
import ruina.monsters.blackSilence.blackSilence4.memories.blue.Blue1;
import ruina.monsters.blackSilence.blackSilence4.memories.blue.Blue2;
import ruina.monsters.blackSilence.blackSilence4.memories.dawn.Dawn;
import ruina.monsters.blackSilence.blackSilence4.memories.dawn.Dawn1;
import ruina.monsters.blackSilence.blackSilence4.memories.dawn.Dawn2;
import ruina.monsters.blackSilence.blackSilence4.memories.hana.Hana;
import ruina.monsters.blackSilence.blackSilence4.memories.hana.Hana1;
import ruina.monsters.blackSilence.blackSilence4.memories.hana.Hana2;
import ruina.monsters.blackSilence.blackSilence4.memories.liu.Liu;
import ruina.monsters.blackSilence.blackSilence4.memories.liu.Liu1;
import ruina.monsters.blackSilence.blackSilence4.memories.liu.Liu2;
import ruina.monsters.blackSilence.blackSilence4.memories.love.Love;
import ruina.monsters.blackSilence.blackSilence4.memories.love.Love1;
import ruina.monsters.blackSilence.blackSilence4.memories.love.Love2;
import ruina.monsters.blackSilence.blackSilence4.memories.purple.Purple;
import ruina.monsters.blackSilence.blackSilence4.memories.purple.Purple1;
import ruina.monsters.blackSilence.blackSilence4.memories.purple.Purple2;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun1;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun2;
import ruina.monsters.blackSilence.blackSilence4.memories.zwei.Zwei;
import ruina.monsters.blackSilence.blackSilence4.memories.zwei.Zwei1;
import ruina.monsters.blackSilence.blackSilence4.memories.zwei.Zwei2;
import ruina.powers.Paralysis;
import ruina.powers.Scars;
import ruina.util.AdditionalIntent;
import ruina.vfx.FlexibleStanceAuraEffect;
import ruina.vfx.FlexibleWrathParticleEffect;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class BlackSilence4 extends AbstractCardMonster {
    public static final String ID = RuinaMod.makeID(BlackSilence4.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte AGONY = 0;
    private static final byte SCREAM = 1;
    private static final byte VOID = 2;
    private static final byte YUN = 3;
    private static final byte ZWEI = 4;
    private static final byte DAWN = 5;
    private static final byte SHI = 6;
    private static final byte LOVE = 7;
    private static final byte LIU = 8;
    private static final byte PURPLE_TEAR = 9;
    private static final byte HANA = 10;
    private static final byte BLUE_REVERB = 11;

    public final int screamHits = 2;
    public final int screamDebuff = 3;
    public final int DEBUFF = calcAscensionSpecial(1);
    public final int NUM_VOIDS = calcAscensionSpecial(2);
    public final int scarHeal = calcAscensionSpecial(50);
    public int INVINCIBLE;

    public final int yunDazes = calcAscensionSpecial(3);
    public final int yunWounds = calcAscensionSpecial(2);

    public final int zweiMetallicize = calcAscensionTankiness(15);
    public final int zweiArmor = calcAscensionTankiness(25);

    public final int dawnBurns = calcAscensionSpecial(2);
    public final int dawnProtection = calcAscensionSpecial(5);

    public final int shiWeak = calcAscensionSpecial(3);
    public final int shiFrail = calcAscensionSpecial(3);

    public final int loveRegen = calcAscensionSpecial(15);
    public final int loveSlimed = calcAscensionSpecial(4);

    public final int liuStrength = calcAscensionSpecial(4);
    public final int liuVulnerable = calcAscensionSpecial(2);

    public final int purpleIntangible = calcAscensionSpecial(1);
    public final int purpleMinions = 1;

    public final int hanaRegret = calcAscensionSpecial(2);
    public final int hanaHpLoss = calcAscensionSpecial(20);

    private final ArrayList<Byte> memories = new ArrayList<>();

    private float particleTimer;
    private float particleTimer2;

    AbstractMonster minion;

    public BlackSilence4() {
        this(0.0f, 0.0f);
    }

    public BlackSilence4(final float x, final float y) {
        super(NAME, ID, 1300, -15.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSilence4/Spriter/BlackSilence4.scml"));

        numAdditionalMoves = 1;
        maxAdditionalMoves = 4;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }

        this.setHp(calcAscensionTankiness(1300));
        this.type = EnemyType.BOSS;

        addMove(AGONY, Intent.ATTACK, calcAscensionDamage(40));
        addMove(SCREAM, Intent.ATTACK_DEBUFF, calcAscensionDamage(16), screamHits, true);
        addMove(VOID, Intent.STRONG_DEBUFF);
        addMove(YUN, Intent.UNKNOWN);
        addMove(ZWEI, Intent.UNKNOWN);
        addMove(DAWN, Intent.UNKNOWN);
        addMove(SHI, Intent.UNKNOWN);
        addMove(LOVE, Intent.UNKNOWN);
        addMove(LIU, Intent.UNKNOWN);
        addMove(PURPLE_TEAR, Intent.UNKNOWN);
        addMove(HANA, Intent.UNKNOWN);
        addMove(BLUE_REVERB, Intent.UNKNOWN);
        populateMemories();

        cardList.add(new Agony(this));
        cardList.add(new Scream(this));
        cardList.add(new Void(this));
        cardList.add(new Yun(this));
        cardList.add(new Zwei(this));
        cardList.add(new Dawn(this));
        cardList.add(new Shi(this));
        cardList.add(new Love(this));
        cardList.add(new Liu(this));
        cardList.add(new Purple(this));
        cardList.add(new Hana(this));
        cardList.add(new Blue(this));

        if (AbstractDungeon.ascensionLevel >= 19) {
            INVINCIBLE = 200;
        } else {
            INVINCIBLE = 300;
        }
        if (Settings.isEndless && AbstractDungeon.player.hasBlight(Shield.ID)) {
            float mod = AbstractDungeon.player.getBlight(Shield.ID).effectFloat();
            INVINCIBLE = (int)((float)INVINCIBLE * mod); //scale the invincible with the endless blight
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    public void increaseNumIntents() {
        numAdditionalMoves++;
        if (numAdditionalMoves > maxAdditionalMoves) {
            numAdditionalMoves = maxAdditionalMoves;
        }
    }

    @Override
    public void usePreBattleAction() {
        if (altMusic) {
            CustomDungeon.playTempMusicInstantly("GoneAngelsMusicBox");
        } else {
            CustomDungeon.playTempMusicInstantly("GoneAngels");
        }
        applyToTarget(this, this, new Scars(this, scarHeal));
        applyToTarget(this, this, new InvinciblePower(this, INVINCIBLE));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case AGONY: {
                int animation = AbstractDungeon.monsterRng.random(2);
                switch (animation) {
                    case 0:
                        wheelsAnimation(target);
                        break;
                    case 1:
                        claw1Animation(target);
                        break;
                    case 2:
                        club1Animation(target);
                        break;
                }
                dmg(target, info);
                resetIdle();
                break;
            }
            case SCREAM: {
                int animation = AbstractDungeon.monsterRng.random(2);
                for (int i = 0; i < multiplier; i++) {
                    switch (animation) {
                        case 0:
                            if (i % 2 == 0) {
                                pierceAnimation(target);
                            } else {
                                attackAnimation(target);
                            }
                            break;
                        case 1:
                            if (i % 2 == 0) {
                                gun1Animation(target);
                            } else {
                                gun3Animation(target);
                            }
                            break;
                        case 2:
                            if (i % 2 == 0) {
                                slashAnimation(target);
                            } else {
                                sword1Animation(target);
                            }
                            break;
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(target, this, new Paralysis(target, screamDebuff));
                break;
            }
            case VOID: {
                blockAnimation();
                applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                applyToTarget(target, this, new FrailPower(target, DEBUFF, true));
                intoDiscardMo(new VoidCard(), NUM_VOIDS, this);
                resetIdle(1.0f);
                break;
            }
            case YUN: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Yun1(this));
                options.add(new Yun2(this));
                atb(new ChooseOneActionButItCanFizzle(options, this));
                break;
            }
            case ZWEI: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Zwei1(this));
                options.add(new Zwei2(this));
                atb(new ChooseOneActionButItCanFizzle(options, this));
                break;
            }
            case DAWN: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Dawn1(this));
                options.add(new Dawn2(this));
                atb(new ChooseOneActionButItCanFizzle(options, this));
                break;
            }
            case SHI: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Shi1(this));
                options.add(new Shi2(this));
                atb(new ChooseOneActionButItCanFizzle(options, this));
                break;
            }
            case LOVE: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Love1(this));
                options.add(new Love2(this));
                atb(new ChooseOneActionButItCanFizzle(options, this));
                break;
            }
            case LIU: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Liu1(this));
                options.add(new Liu2(this));
                atb(new ChooseOneActionButItCanFizzle(options, this));
                break;
            }
            case PURPLE_TEAR: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Purple1(this));
                options.add(new Purple2(this));
                atb(new ChooseOneActionButItCanFizzle(options, this));
                break;
            }
            case HANA: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Hana1(this));
                options.add(new Hana2(this));
                atb(new ChooseOneActionButItCanFizzle(options, this));
                break;
            }
            case BLUE_REVERB: {
                ArrayList<AbstractCard> options = new ArrayList<>();
                options.add(new Blue1(this));
                options.add(new Blue2(this));
                atb(new ChooseOneActionButItCanFizzle(options, this));
                break;
            }
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
        if (!memories.isEmpty()) {
            memories.remove(0);
        }
        if (memories.isEmpty()) {
            populateMemories();
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(AGONY)) {
            possibilities.add(AGONY);
        }
        if (!this.lastMove(SCREAM)) {
            possibilities.add(SCREAM);
        }
        if (!this.lastMove(VOID) && !this.lastMoveBefore(VOID)) {
            possibilities.add(VOID);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (whichMove == numAdditionalMoves - 1) { //the last intent handles memories
            byte move = memories.get(0);
            setAdditionalMoveShortcut(move, moveHistory, cardList.get(move));
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(AGONY, moveHistory)) {
                possibilities.add(AGONY);
            }
            if (!this.lastMove(SCREAM, moveHistory)) {
                possibilities.add(SCREAM);
            }
            if (!this.lastMove(VOID, moveHistory) && !this.lastMoveBefore(VOID, moveHistory)) {
                possibilities.add(VOID);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
        }
    }

    private void populateMemories() {
        memories.add(YUN);
        memories.add(ZWEI);
        memories.add(DAWN);
        memories.add(SHI);
        memories.add(LOVE);
        memories.add(LIU);
        memories.add(PURPLE_TEAR);
        memories.add(HANA);
        memories.add(BLUE_REVERB);
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
                BlackSilence4.super.die(triggerRelics);
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

    public void Summon() {
        if (minion != null) {
            atb(new HealAction(minion, minion, minion.maxHealth));
        } else {
            float xPosition = -350.0F;
            minion = new ImageOfBygones(xPosition, 0.0f, this);
            atb(new SpawnMonsterAction(minion, true));
            atb(new UsePreBattleActionAction(minion));
        }
    }

}
