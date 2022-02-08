package ruina.monsters.blackSilence.blackSilence1;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.blackSilence.blackSilence1.cards.*;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.util.AdditionalIntent;

import java.util.ArrayList;
import java.util.Collections;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BlackSilence1 extends AbstractCardMonster {
    public static final String ID = RuinaMod.makeID(BlackSilence1.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte CRYSTAL = 0;
    private static final byte WHEELS = 1;
    private static final byte DURANDAL = 2;
    private static final byte ALLAS = 3;
    private static final byte GUN = 4;
    private static final byte MOOK = 5;
    private static final byte OLD_BOY = 6;
    private static final byte RANGA = 7;
    private static final byte MACE = 8;
    private static final byte FURIOSO = 9;

    public final int crystalDamage = calcAscensionDamage(12);
    public final int crystalHits = 2;
    public final int crystalBlock = calcAscensionTankiness(30);

    public final int wheelsStrDown = calcAscensionSpecial(8);
    public final int wheelsDamage = calcAscensionDamage(22);

    public final int durandalDamage = calcAscensionDamage(9);
    public final int durandalHits = 2;
    public final int durandalStrength = calcAscensionSpecial(6);

    public final int ALLAS_DAMAGE = calcAscensionDamage(20);
    public final int ALLAS_DEBUFF = calcAscensionSpecial(10);

    public final int GUN_DAMAGE = calcAscensionDamage(8);
    public final int GUN_HITS = 3;

    public final int MOOK_DAMAGE = calcAscensionDamage(21);

    public final int OLD_BOY_DAMAGE = calcAscensionDamage(12);
    public final int OLD_BOY_BLOCK = calcAscensionTankiness(50);

    public final int RANGA_DAMAGE = calcAscensionDamage(7);
    public final int RANGA_HITS = 3;
    public final int RANGA_DEBUFF = calcAscensionSpecial(4);

    public final int MACE_DAMAGE = calcAscensionDamage(8);
    public final int MACE_HITS = 2;
    public final int MACE_STR = calcAscensionSpecial(4);

    public final int furiosoDamage = calcAscensionDamage(4);
    public final int furiosoHits = 16;
    public final int furiosoDebuff = calcAscensionSpecial(3);

    public int CARDS_PER_TURN;

    private final ArrayList<Byte> movepool = new ArrayList<>();
    private byte previewIntent = -1;

    public static final String POWER_ID = makeID("Orlando");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final String KILLING_POWER_ID = makeID("KillingIntent");
    public static final PowerStrings KillingpowerStrings = CardCrawlGame.languagePack.getPowerStrings(KILLING_POWER_ID);
    public static final String KILLING_POWER_NAME = KillingpowerStrings.NAME;
    public static final String[] KILLING_POWER_DESCRIPTIONS = KillingpowerStrings.DESCRIPTIONS;

    public BlackSilence1() {
        this(0.0f, 0.0f);
    }

    public BlackSilence1(final float x, final float y) {
        super(NAME, ID, 1000, 0.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSilence1/Spriter/BlackSilence1.scml"));
        animation.setFlip(true, false);
        this.setHp(calcAscensionTankiness(1000));
        this.type = EnemyType.BOSS;

        addMove(CRYSTAL, Intent.ATTACK_DEFEND, crystalDamage, crystalHits, true);
        addMove(WHEELS, Intent.ATTACK_DEBUFF, wheelsDamage);
        addMove(DURANDAL, Intent.ATTACK_BUFF, durandalDamage, durandalHits, true);
        addMove(ALLAS, Intent.ATTACK_DEBUFF, ALLAS_DAMAGE);
        addMove(GUN, Intent.ATTACK, GUN_DAMAGE, GUN_HITS, true);
        addMove(MOOK, Intent.ATTACK_DEBUFF, MOOK_DAMAGE);
        addMove(OLD_BOY, Intent.ATTACK_DEFEND, OLD_BOY_DAMAGE);
        addMove(RANGA, Intent.ATTACK_DEBUFF, RANGA_DAMAGE, RANGA_HITS, true);
        addMove(MACE, Intent.ATTACK_BUFF, MACE_DAMAGE, MACE_HITS, true);
        addMove(FURIOSO, Intent.ATTACK_DEBUFF, furiosoDamage, furiosoHits, true);
        populateCards();
        populateMovepool();

        if (AbstractDungeon.ascensionLevel >= 19) {
            CARDS_PER_TURN = 4;
        } else {
            CARDS_PER_TURN = 5;
        }
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland1");
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                this.amount++;
                if (this.amount >= CARDS_PER_TURN) {
                    flash();
                    this.amount = 0;
                    if (!owner.hasPower(StunMonsterPower.POWER_ID)) {
                        takeTurn();
                    }
                } else {
                    flashWithoutSound();
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + CARDS_PER_TURN + POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, new AbstractLambdaPower(KILLING_POWER_NAME, KILLING_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = KILLING_POWER_DESCRIPTIONS[0];
            }
        });
    }

    private boolean isPlayerTurn() {
        return !AbstractDungeon.actionManager.turnHasEnded;
    }

    @Override
    public void takeTurn() {
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }
        AbstractCreature target = adp();
        if (info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case CRYSTAL: {
                for (int i = 0; i < multiplier; i++) {
                    slashAnimation(target);
                    dmg(target, info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                if (!isPlayerTurn()) {
                    block(this, crystalBlock);
                }
                break;
            }
            case WHEELS: {
                wheelsAnimation(target);
                dmg(target, info);
                if (isPlayerTurn()) {
                    applyToTarget(target, this, new StrengthPower(target, -wheelsStrDown));
                    if (!target.hasPower(ArtifactPower.POWER_ID)) {
                        applyToTarget(target, this, new GainStrengthPower(target, wheelsStrDown));
                    }
                }
                resetIdle(1.0f);
                break;
            }
            case DURANDAL: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        sword2Animation(target);
                    } else {
                        sword3Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                if (isPlayerTurn()) {
                    applyToTarget(this, this, new StrengthPower(this, durandalStrength));
                    applyToTarget(this, this, new LoseStrengthPower(this, durandalStrength));
                }
                break;
            }
            case ALLAS: {
                pierceAnimation(target);
                dmg(target, info);
                resetIdle();
                if (isPlayerTurn()) {
                    applyToTarget(target, this, new StrengthPower(target, -ALLAS_DEBUFF));
                    if (!target.hasPower(ArtifactPower.POWER_ID)) {
                        applyToTarget(target, this, new GainStrengthPower(target, ALLAS_DEBUFF));
                    }
                }
                break;
            }
            case GUN: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        gun1Animation(target);
                    } else if (i == 1) {
                        gun2Animation(target);
                    } else {
                        gun3Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case MOOK: {
                mook1Animation(target);
                waitAnimation(0.25f);
                mook2Animation(target);
                dmg(target, info);
                resetIdle(1.0f);
                atb(new RemoveAllBlockAction(target, this));
                break;
            }
            case OLD_BOY: {
                attackAnimation(target);
                dmg(target, info);
                if (!isPlayerTurn()) {
                    block(this, OLD_BOY_BLOCK);
                }
                resetIdle();
                break;
            }
            case RANGA: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        claw1Animation(target);
                    } else if (i == 1) {
                        claw2Animation(target);
                    } else {
                        knifeAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                if (!isPlayerTurn()) {
                    applyToTarget(target, this, new Bleed(target, RANGA_DEBUFF));
                }
                break;
            }
            case MACE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        club1Animation(target);
                    } else {
                        club2Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                if (isPlayerTurn()) {
                    applyToTarget(this, this, new StrengthPower(this, MACE_STR));
                    applyToTarget(this, this, new LoseStrengthPower(this, MACE_STR));
                }
                break;
            }
            case FURIOSO: {
                float initialX = drawX;
                float targetBehind = target.drawX - 200.0f * Settings.scale;
                float targetFront = target.drawX + 200.0f * Settings.scale;
                gun1Animation(target);
                dmg(target, info);
                waitAnimation(target);
                gun2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                moveAnimation(targetBehind, target);
                pierceAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                setFlipAnimation(false, target);
                attackAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                moveAnimation(targetFront, target);
                knifeAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                setFlipAnimation(true, target);
                mook1Animation(target);
                waitAnimation(0.15f, target);
                mook2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                moveAnimation(targetBehind, target);
                claw1Animation(target);
                dmg(target, info);
                waitAnimation(target);
                moveAnimation(targetFront, target);
                setFlipAnimation(false, target);
                claw2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                setFlipAnimation(true, target);
                club1Animation(target);
                dmg(target, info);
                waitAnimation(target);
                club2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                wheelsAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                slashAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                gun3Animation(target);
                dmg(target, info);
                waitAnimation(target);
                sword1Animation(target);
                dmg(target, info);
                waitAnimation(target);
                sword2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                sword3Animation(target);
                dmg(target, info);
                resetIdle();
                moveAnimation(initialX, null);
                setFlipAnimation(true, null);
                applyToTarget(target, this, new WeakPower(target, furiosoDebuff, true));
                applyToTarget(target, this, new FrailPower(target, furiosoDebuff, true));
                break;
            }
        }
        atb(new RollMoveAction(this));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                createIntent();
                this.isDone = true;
            }
        });
    }

    @Override
    protected void waitAnimation(AbstractCreature enemy) {
        waitAnimation(0.25f, enemy);
    }

    private void moveAnimation(float x, AbstractCreature enemy) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (enemy == null || !enemy.isDeadOrEscaped()) {
                    drawX = x;
                }
                this.isDone = true;
            }
        });
    }

    private void setFlipAnimation(boolean flipHorizontal, AbstractCreature enemy) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (enemy == null || !enemy.isDeadOrEscaped()) {
                    animation.setFlip(flipHorizontal, false);
                }
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        boolean rollAgain = false;
        if (previewIntent >= 0) {
            setMoveShortcut(previewIntent, MOVES[previewIntent], cardList.get(previewIntent));
        } else {
            rollAgain = true;
        }
        if (movepool.isEmpty()) {
            previewIntent = FURIOSO;
            populateMovepool();
        } else {
            previewIntent = movepool.remove(0);
        }
        setAdditionalMoveShortcut(previewIntent, moveHistory, cardList.get(previewIntent));
        for (AdditionalIntent additionalIntent : additionalIntents) {
            additionalIntent.transparent = true;
            additionalIntent.usePrimaryIntentsColor = false;
        }
        //if previewIntent wasn't a valid intent, roll again (should only happen at the start of combat)
        if (rollAgain) {
            rollMove();
        }
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

    private void populateMovepool() {
        movepool.add(CRYSTAL);
        movepool.add(WHEELS);
        movepool.add(ALLAS);
        movepool.add(GUN);
        movepool.add(MOOK);
        movepool.add(OLD_BOY);
        movepool.add(RANGA);
        Collections.shuffle(movepool, AbstractDungeon.monsterRng.random);
        //make sure these below can't be used right before furioso
        movepool.add(AbstractDungeon.monsterRng.random(movepool.size() - 3), DURANDAL);
        movepool.add(AbstractDungeon.monsterRng.random(movepool.size() - 3), MACE);
    }

    private void populateCards() {
        cardList.add(new Crystal(this));
        cardList.add(new Wheels(this));
        cardList.add(new Durandal(this));
        cardList.add(new Allas(this));
        cardList.add(new Gun(this));
        cardList.add(new Mook(this));
        cardList.add(new OldBoys(this));
        cardList.add(new Ranga(this));
        cardList.add(new Mace(this));
        cardList.add(new Furioso(this));
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

    private void gun2Animation(AbstractCreature enemy) {
        animationAction("Gun2", "RolandRevolver", enemy, this);
    }

    private void gun3Animation(AbstractCreature enemy) {
        animationAction("Gun3", "RolandShotgun", enemy, this);
    }

    private void mook1Animation(AbstractCreature enemy) {
        animationAction("Mook1", "RolandLongSwordStart", enemy, this);
    }

    private void mook2Animation(AbstractCreature enemy) {
        animationAction("Mook2", "RolandLongSwordAtk", enemy, this);
        animationAction("Mook2", "RolandLongSwordFin", enemy, this);
    }

    private void claw1Animation(AbstractCreature enemy) {
        animationAction("Claw1", "SwordStab", enemy, this);
    }

    private void claw2Animation(AbstractCreature enemy) {
        animationAction("Claw2", "SwordStab", enemy, this);
    }

    private void knifeAnimation(AbstractCreature enemy) {
        animationAction("Knife", "RolandShortSword", enemy, this);
    }

    private void club1Animation(AbstractCreature enemy) {
        animationAction("Club1", "BluntVert", enemy, this);
    }

    private void club2Animation(AbstractCreature enemy) {
        animationAction("Club2", "BluntVert", enemy, this);
    }

    private void wheelsAnimation(AbstractCreature enemy) {
        animationAction("Wheels", "RolandGreatSword", enemy, this);
    }

    private void sword1Animation(AbstractCreature enemy) {
        animationAction("Sword1", "RolandDuralandalDown", enemy, this);
    }

    private void sword2Animation(AbstractCreature enemy) {
        animationAction("Sword2", "RolandDuralandalUp", enemy, this);
    }

    private void sword3Animation(AbstractCreature enemy) {
        animationAction("Sword3", "RolandDuralandalStrong", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "RolandDualSword", enemy, this);
    }

}
