package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.DontLeave;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class GalaxyFriend extends AbstractRuinaMonster
{
    public static final String ID = makeID(GalaxyFriend.class.getSimpleName());

    private static final byte WAITING = 0;
    private static final byte STAR_SHOWER = 1;
    private static final byte GLIMMER = 2;
    private static final byte REVIVE = 3;

    private final int REGEN = 2;
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int BLOCK = calcAscensionTankiness(7);

    public GalaxyFriend() {
        this(0.0f, 0.0f);
    }

    public GalaxyFriend(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 250.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("GalaxyFriend/Spriter/GalaxyFriend.scml"));
        setHp(calcAscensionTankiness(28), calcAscensionTankiness(31));
        addMove(WAITING, Intent.DEFEND);
        addMove(STAR_SHOWER, Intent.ATTACK, calcAscensionDamage(9));
        addMove(GLIMMER, Intent.ATTACK_DEBUFF, calcAscensionDamage(5));
        addMove(REVIVE, Intent.BUFF);
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.getCurrRoom().cannotLose = true;
        applyToTarget(this, this, new RegenerateMonsterPower(this, REGEN));
        applyToTarget(this, this, new DontLeave(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case WAITING: {
                blockAnimation();
                block(this, BLOCK);
                resetIdle(1.0f);
                break;
            }
            case STAR_SHOWER: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case GLIMMER: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                resetIdle();
                break;
            }
            case REVIVE: {
                int healAmount = 1;
                for (AbstractMonster mo : monsterList()) {
                    if (mo instanceof GalaxyFriend && !mo.isDeadOrEscaped()) {
                        healAmount = mo.currentHealth;
                    }
                }
                atb(new HealAction(this, this, healAmount));
                halfDead = false;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(WAITING)) {
            possibilities.add(WAITING);
        }
        if (!this.lastMove(STAR_SHOWER)) {
            possibilities.add(STAR_SHOWER);
        }
        if (!this.lastMove(GLIMMER)) {
            possibilities.add(GLIMMER);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move);
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case WAITING: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case GLIMMER: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case REVIVE: {
                DetailedIntent detail = new DetailedIntent(this, 1, DetailedIntent.HEAL_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power instanceof RegenerateMonsterPower) && !(power.ID.equals(DontLeave.POWER_ID))) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }

            boolean allDead = true;
            for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                if (m instanceof GalaxyFriend && !m.halfDead) {
                    allDead = false;
                    break;
                }
            }

            if (!allDead) {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        setMove(REVIVE, Intent.BUFF);
                        createIntent();
                        setDetailedIntents();
                        isDone = true;
                    }
                });
            } else {
                (AbstractDungeon.getCurrRoom()).cannotLose = false;
                this.halfDead = false;
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (m instanceof GalaxyFriend) {
                        m.die();
                    }
                }
            }
        }
    }

    public void die() {
        if (!(AbstractDungeon.getCurrRoom()).cannotLose) {
            super.die();
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "GalaxyAtk", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", "GalaxyDef", this);
    }

}