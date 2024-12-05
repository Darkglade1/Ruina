package ruina.monsters.uninvitedGuests.normal.argalia.monster;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_ALLAS;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_Crystal;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_Durandal;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_FURIOSO;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_GUN;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_MACE;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_MOOK;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_OLDBOYS;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_RANGA;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_Wheels;
import ruina.powers.BlackSilence;
import ruina.powers.Bleed;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Roland extends AbstractAllyCardMonster {
    public static final String ID = RuinaMod.makeID(Roland.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final byte CRYSTAL = 0;
    public static final byte WHEELS = 1;
    public static final byte DURANDAL = 2;
    public static final byte ALLAS = 3;
    public static final byte GUN = 4;
    public static final byte MOOK = 5;
    public static final byte OLD_BOY = 6;
    public static final byte RANGA = 7;
    public static final byte MACE = 8;
    public static final byte FURIOSO = 9;

    public final int crystalDamage = 22;
    public final int crystalHits = 2;
    public final int crystalBlock = 33;

    public final int wheelsStrDown = 4;
    public final int wheelsDamage = 45;

    public final int durandalDamage = 15;
    public final int durandalHits = 2;
    public final int durandalStrength = 4;

    public final int ALLAS_DAMAGE = 32;
    public final int ALLAS_DEBUFF = 2;

    public final int GUN_DAMAGE = 16;
    public final int GUN_HITS = 3;

    public final int MOOK_DAMAGE = 24;
    public final int MOOK_DEBUFF = 2;

    public final int OLD_BOY_DAMAGE = 12;
    public final int OLD_BOY_BLOCK = 20;

    public final int RANGA_DAMAGE = 9;
    public final int RANGA_HITS = 3;
    public final int RANGA_DEBUFF = 8;

    public final int MACE_DAMAGE = 14;
    public final int MACE_HITS = 2;

    public final int furiosoDamage = 25;
    public final int furiosoHits = 16;

    public static final int furiosoCap = 9;
    public int furiosoCount = furiosoCap;

    public AbstractMonster enemyBoss;
    private ArrayList<Byte> movepool = new ArrayList<>();

    public BlackSilence power = new BlackSilence(this, 2);

    public Roland() {
        this(0.0f, 0.0f);
    }

    public Roland(final float x, final float y) {
        super(NAME, ID, 650, 10.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Roland/Spriter/Roland.scml"));

        this.setHp(calcAscensionTankiness(650));
        this.type = EnemyType.BOSS;

        addMove(CRYSTAL, Intent.ATTACK_DEFEND, crystalDamage, crystalHits, true);
        addMove(WHEELS, Intent.ATTACK_DEBUFF, wheelsDamage);
        addMove(DURANDAL, Intent.ATTACK_BUFF, durandalDamage, durandalHits, true);
        addMove(ALLAS, Intent.ATTACK_DEBUFF, ALLAS_DAMAGE);
        addMove(GUN, Intent.ATTACK, GUN_DAMAGE, GUN_HITS, true);
        addMove(MOOK, Intent.ATTACK_DEBUFF, MOOK_DAMAGE);
        addMove(OLD_BOY, Intent.ATTACK_DEFEND, OLD_BOY_DAMAGE);
        addMove(RANGA, Intent.ATTACK_DEBUFF, RANGA_DAMAGE, RANGA_HITS, true);
        addMove(MACE, Intent.ATTACK, MACE_DAMAGE, MACE_HITS, true);
        addMove(FURIOSO, Intent.ATTACK, furiosoDamage, furiosoHits, true);
        this.icon = TexLoader.getTexture(makeUIPath("RolandIcon.png"));
        populateCards();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, power);
        updatePower();
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Argalia) { enemyBoss = mo; }
        }
        super.usePreBattleAction();
    }

    public void updatePower() {
        power.amount2 = furiosoCount;
        power.updateDescription();
    }

    public void dialogue() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (firstMove) {
                    att(new TalkAction(Roland.this, DIALOG[0]));
                    firstMove = false;
                }
                isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();
        dialogue();
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else { info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL); }
        AbstractCreature target;
        target = enemyBoss;
        if (info.base > -1) { info.applyPowers(this, target); }
        switch (this.nextMove) {
            case CRYSTAL: {
                block(this, crystalBlock);
                for (int i = 0; i < multiplier; i++) {
                    slashAnimation(target);
                    dmg(target, info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case WHEELS: {
                wheelsAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new StrengthPower(target, -wheelsStrDown));
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
                applyToTarget(this, this, new StrengthPower(this, durandalStrength));
                break;
            }
            case ALLAS: {
                pierceAnimation(target);
                dmg(target, info);
                resetIdle();
                applyToTarget(target, this, new WeakPower(target, ALLAS_DEBUFF, false));
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
                applyToTarget(target, this, new VulnerablePower(target, MOOK_DEBUFF, true));
                break;
            }
            case OLD_BOY: {
                attackAnimation(target);
                block(this, OLD_BOY_BLOCK);
                dmg(target, info);
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
                applyToTarget(target, this, new Bleed(target, RANGA_DEBUFF));
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
                break;
            }
            case FURIOSO: {
                float initialX = drawX;
                float targetBehind = target.drawX + 150.0f * Settings.scale;
                float targetFront = target.drawX - 200.0f * Settings.scale;
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.getCurrRoom().cannotLose = true;
                        this.isDone = true;
                    }
                });
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
                setFlipAnimation(true, target);
                attackAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                moveAnimation(targetFront, target);
                knifeAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                setFlipAnimation(false, target);
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
                setFlipAnimation(true, target);
                claw2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                setFlipAnimation(false, target);
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
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.getCurrRoom().cannotLose = false;
                        this.isDone = true;
                    }
                });
                dmg(target, info);
                resetIdle();
                moveAnimation(initialX, null);
                setFlipAnimation(false, null);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        furiosoCount = furiosoCap + 1;
                        isDone = true;
                    }
                });
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                furiosoCount--;
                updatePower();
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    public void setFuriosoCounter(int newAmount) {
        furiosoCount = newAmount;
        updatePower();
    }

    @Override
    protected void waitAnimation(AbstractCreature enemy) {
        waitAnimation(0.25f, enemy);
    }

    public void moveAnimation(float x, AbstractCreature enemy) {
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

    public void setFlipAnimation(boolean flipHorizontal, AbstractCreature enemy) {
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
        if (furiosoCount <= 0) {
            setMoveShortcut(FURIOSO, MOVES[FURIOSO], cardList.get(FURIOSO));
        } else {
            if (movepool.isEmpty()) {
                populateMovepool();
            }
            byte move = movepool.remove(AbstractDungeon.monsterRng.random(movepool.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move));
        }
    }

    private void populateMovepool() {
        movepool.add(CRYSTAL);
        movepool.add(WHEELS);
        movepool.add(DURANDAL);
        movepool.add(ALLAS);
        movepool.add(GUN);
        movepool.add(MOOK);
        movepool.add(OLD_BOY);
        movepool.add(RANGA);
        movepool.add(MACE);
    }

    private void populateCards() {
        cardList.add(new CHRALLY_Crystal(this));
        cardList.add(new CHRALLY_Wheels(this));
        cardList.add(new CHRALLY_Durandal(this));
        cardList.add(new CHRALLY_ALLAS(this));
        cardList.add(new CHRALLY_GUN(this));
        cardList.add(new CHRALLY_MOOK(this));
        cardList.add(new CHRALLY_OLDBOYS(this));
        cardList.add(new CHRALLY_RANGA(this));
        cardList.add(new CHRALLY_MACE(this));
        cardList.add(new CHRALLY_FURIOSO(this));
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1 || enemyBoss.isDeadOrEscaped()) {
            super.applyPowers();
            return;
        }
        AbstractCreature target;
        target = enemyBoss;
        applyPowers(target);
    }

    public void onArgaliaDeath() {
        atb(new TalkAction(this, DIALOG[1]));
        atb(new VFXAction(new WaitEffect(), 1.0F));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                disappear();
                this.isDone = true;
            }
        });
    }

    public void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "RolandAxe", enemy, this);
    }

    public void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "RolandAxe", enemy, this);
    }

    public void gun1Animation(AbstractCreature enemy) {
        animationAction("Gun1", "RolandRevolver", enemy, this);
    }

    public void gun2Animation(AbstractCreature enemy) {
        animationAction("Gun2", "RolandRevolver", enemy, this);
    }

    public void gun3Animation(AbstractCreature enemy) {
        animationAction("Gun3", "RolandShotgun", enemy, this);
    }

    public void mook1Animation(AbstractCreature enemy) {
        animationAction("Mook1", "RolandLongSwordStart", enemy, this);
    }

    public void mook2Animation(AbstractCreature enemy) {
        animationAction("Mook2", "RolandLongSwordAtk", enemy, this);
        animationAction("Mook2", "RolandLongSwordFin", enemy, this);
    }

    public void claw1Animation(AbstractCreature enemy) {
        animationAction("Claw1", "SwordStab", enemy, this);
    }

    public void claw2Animation(AbstractCreature enemy) {
        animationAction("Claw2", "SwordStab", enemy, this);
    }

    public void knifeAnimation(AbstractCreature enemy) {
        animationAction("Knife", "RolandShortSword", enemy, this);
    }

    public void club1Animation(AbstractCreature enemy) {
        animationAction("Club1", "BluntVert", enemy, this);
    }

    public void club2Animation(AbstractCreature enemy) {
        animationAction("Club2", "BluntVert", enemy, this);
    }

    public void wheelsAnimation(AbstractCreature enemy) {
        animationAction("Wheels", "RolandGreatSword", enemy, this);
    }

    public void sword1Animation(AbstractCreature enemy) {
        animationAction("Sword1", "RolandDuralandalDown", enemy, this);
    }

    public void sword2Animation(AbstractCreature enemy) {
        animationAction("Sword2", "RolandDuralandalUp", enemy, this);
    }

    public void sword3Animation(AbstractCreature enemy) {
        animationAction("Sword3", "RolandDuralandalStrong", enemy, this);
    }

    public void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "RolandDualSword", enemy, this);
    }

}
