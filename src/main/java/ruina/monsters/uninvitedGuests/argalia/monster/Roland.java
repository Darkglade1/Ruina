package ruina.monsters.uninvitedGuests.argalia.monster;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.BlackSilence;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Roland extends AbstractAllyMonster {
    public static final String ID = RuinaMod.makeID(Roland.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte CRYSTAL = 0;
    private static final byte WHEELS = 1;
    private static final byte DURANDAL = 2;
    private static final byte FURIOSO = 3;

    public final int crystalDamage = calcAscensionDamage(10);
    public final int crystalHits = 3;
    public final int crystalBlock = calcAscensionTankiness(20);

    public final int wheelsBlock = calcAscensionTankiness(30);
    public final int wheelsDamage = calcAscensionDamage(30);

    public final int durandalDamage = calcAscensionDamage(15);
    public final int durandalHits = 2;
    public final int durandalStrength = calcAscensionSpecial(4);

    public final int furiosoDamage = calcAscensionDamage(50);
    public final int furiosoBlock = calcAscensionDamage(60);
    public final int furiosoHits = 5;

    public final int furiosoCap = 3;
    public int furiosoCount = 0;

    public Argalia argalia;

    public Roland() {
        this(0.0f, 0.0f);
    }

    public Roland(final float x, final float y) {
        super(NAME, ID, 750, 10.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Roland/Spriter/Roland.scml"));
        this.animation.setFlip(false, false);

        this.setHp(calcAscensionTankiness(this.maxHealth));
        this.type = EnemyType.BOSS;

        addMove(CRYSTAL, Intent.ATTACK_DEFEND, crystalDamage, crystalHits, true);
        addMove(WHEELS, Intent.ATTACK_DEBUFF, wheelsDamage);
        addMove(DURANDAL, Intent.ATTACK_BUFF, durandalDamage, durandalHits, true);
        addMove(FURIOSO, Intent.ATTACK_DEFEND, furiosoDamage, furiosoHits, true);
        this.allyIcon = makeUIPath("RolandIcon.png");
        firstMove = true;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new BlackSilence(this, calcAscensionSpecial(2)));
        CustomDungeon.playTempMusicInstantly("Roland1");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Argalia) { argalia = (Argalia) mo; }
        }
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();
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
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else { info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL); }
        AbstractCreature target;
        target = argalia;
        if (info.base > -1) { info.applyPowers(this, target); }
        switch (this.nextMove) {
            case CRYSTAL: {
                block(this, crystalBlock);
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        slashAnimation(target);
                    } else if (i == 1) {
                        sword2Animation(target);
                    } else {
                        sword1Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case WHEELS: {
                wheelsAnimation(target);
                block(this, wheelsBlock);
                dmg(target, info);
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
                applyToSelf(new StrengthPower(adp(), durandalStrength));
                applyToTarget(this, this, new StrengthPower(this, durandalStrength));
                break;
            }
            case FURIOSO: {
                block(this, furiosoBlock);
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        sword2Animation(target);
                    } else {
                        sword3Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        furiosoCount = 0;
                        isDone = true;
                    }
                });
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                furiosoCount += 1;
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if(firstMove){ setMoveShortcut(CRYSTAL, MOVES[CRYSTAL]); }
        else if (furiosoCap == furiosoCount){ setMoveShortcut(FURIOSO, MOVES[FURIOSO]); }
        else{
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(CRYSTAL)) { possibilities.add(CRYSTAL); }
            if (!this.lastMove(WHEELS)) { possibilities.add(WHEELS); }
            if (!this.lastMove(DURANDAL)) { possibilities.add(DURANDAL); }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1 || argalia.isDeadOrEscaped()) {
            super.applyPowers();
            return;
        }
        AbstractCreature target;
        target = argalia;
        applyPowers(target);
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
