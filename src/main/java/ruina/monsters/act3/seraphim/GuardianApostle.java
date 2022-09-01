package ruina.monsters.act3.seraphim;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Unkillable;
import ruina.powers.WingsOfGrace;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class GuardianApostle extends AbstractRuinaMonster {
    public static final String ID = makeID(GuardianApostle.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte RISE_AND_SERVE = 0;
    private static final byte COMMAND_FIRE = 1;
    private static final byte THE_KING = 2;
    private static final byte GIVE_US_REST = 3;
    private static final byte THY_WORDS_COME_UNTO_ME = 4;
    private static final byte LORD_SHEW_US = 5;
    private static final byte EMPTY = 6;

    private final int riseAndServe = calcAscensionTankiness(30);
    private final int heavenlyAura = calcAscensionSpecial(1);
    private final int giveUsRestGuardian = calcAscensionTankiness(15);
    private final int giveUsRestBoss = calcAscensionTankiness(30);
    private final int thyWordsBlock = calcAscensionTankiness(12);
    private final Seraphim seraphim;

    public GuardianApostle(final float x, final float y, Seraphim parent) {
        super(NAME, ID, 80, -5.0F, 0, 280.0f, 215.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("GuardianApostle/Spriter/GuardianApostle.scml"));
        this.setHp(calcAscensionTankiness(80));
        this.type = EnemyType.BOSS;
        addMove(RISE_AND_SERVE, Intent.DEFEND_BUFF);
        addMove(COMMAND_FIRE, Intent.ATTACK, calcAscensionDamage(15));
        addMove(THE_KING, Intent.ATTACK, calcAscensionDamage(3), 3, true);
        addMove(GIVE_US_REST, Intent.DEFEND);
        addMove(THY_WORDS_COME_UNTO_ME, Intent.ATTACK_DEFEND, calcAscensionDamage(8));
        addMove(LORD_SHEW_US, Intent.ATTACK, calcAscensionDamage(6), 2, true);
        addMove(EMPTY, Intent.NONE);
        seraphim = parent;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (nextMove) {
            case RISE_AND_SERVE:
                specialAnimation();
                block(this, riseAndServe);
                applyToTarget(this, this, new RitualPower(this, heavenlyAura, false));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        firstMove = false;
                        this.isDone = true;
                    }
                });
                resetIdle();
                break;
            case COMMAND_FIRE:
                slashUpAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            case THE_KING:
            case LORD_SHEW_US:
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slashUpAnimation(adp());
                    } else {
                        slashDownAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            case GIVE_US_REST:
                specialAnimation();
                for (AbstractMonster m : monsterList()) {
                    if (m instanceof GuardianApostle && !m.halfDead) {
                        block(m, giveUsRestGuardian);
                    } else {
                        block(m, giveUsRestBoss);
                    }
                }
                resetIdle();
                break;
            case THY_WORDS_COME_UNTO_ME:
                slashDownAnimation(adp());
                dmg(adp(), info);
                block(this, thyWordsBlock);
                resetIdle();
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (halfDead) {
            setMoveShortcut(EMPTY, MOVES[EMPTY]);
        } else if (firstMove) {
            setMoveShortcut(RISE_AND_SERVE, MOVES[RISE_AND_SERVE]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(COMMAND_FIRE)) {
                possibilities.add(COMMAND_FIRE);
            }
            if (!this.lastMove(THE_KING)) {
                possibilities.add(THE_KING);
            }
            if (!this.lastMove(GIVE_US_REST)) {
                possibilities.add(GIVE_US_REST);
            }
            if (!this.lastMove(THY_WORDS_COME_UNTO_ME)) {
                possibilities.add(THY_WORDS_COME_UNTO_ME);
            }
            if (!this.lastMove(LORD_SHEW_US)) {
                possibilities.add(LORD_SHEW_US);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new Unkillable(this));
        applyToTarget(this, this, new WingsOfGrace(this, calcAscensionSpecial(2)));
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            this.loseBlock();
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            if (this.nextMove != EMPTY) {
                setMoveShortcut(EMPTY);
                this.createIntent();
                atb(new SetMoveAction(this, EMPTY, Intent.NONE));
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power.ID.equals(Unkillable.POWER_ID)) && !(power.ID.equals(StrengthPower.POWER_ID)) && !(power.ID.equals(GainStrengthPower.POWER_ID)) && !(power.ID.equals(MinionPower.POWER_ID))) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
        }
    }

    @Override
    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die();
        }
    }

    private void slashUpAnimation(AbstractCreature enemy) {
        animationAction("SlashUp", "ApostleScytheUp", enemy, this);
    }

    private void slashDownAnimation(AbstractCreature enemy) {
        animationAction("SlashDown", "ApostleScytheDown", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Block", null, this);
    }


}