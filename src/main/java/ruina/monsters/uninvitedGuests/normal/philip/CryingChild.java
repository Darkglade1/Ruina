package ruina.monsters.uninvitedGuests.normal.philip;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractAllyAttackingMinion;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act4.TorchedHeart;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class CryingChild extends AbstractAllyAttackingMinion
{
    public static final String ID = makeID(CryingChild.class.getSimpleName());

    private static final byte WING_STROKE = 0;
    private static final byte MURMUR = 1;

    private final int WEAK = calcAscensionSpecial(1);
    private final int DAMAGE_REDUCTION = 50;
    private final Philip philip;

    public CryingChild(final float x, final float y, Philip philip) {
        super(ID, ID, 40, -5.0F, 0, 100.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("CryingChild/Spriter/CryingChild.scml"));
        setHp(calcAscensionTankiness(72), calcAscensionTankiness(78));
        addMove(WING_STROKE, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
        addMove(MURMUR, Intent.ATTACK, calcAscensionDamage(10));
        this.philip = philip;
        attackingAlly = generateMultiplayerRandom().randomBoolean();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Malkuth) {
                target = (Malkuth)mo;
            }
        }
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new TorchedHeart(this, DAMAGE_REDUCTION));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        AbstractCreature target;
        if (this.target != null && !this.target.isDead && !this.target.isDying && attackingAlly) {
            target = this.target;
        } else {
            target = adp();
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case WING_STROKE: {
                slashAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, WEAK, true));
                resetIdle();
                break;
            }
            case MURMUR: {
                pierceAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                attackingAlly = generateMultiplayerRandom().randomBoolean();
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(WING_STROKE)) {
            possibilities.add(WING_STROKE);
        }
        if (!this.lastTwoMoves(MURMUR)) {
            possibilities.add(MURMUR);
        }
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setMoveShortcut(move);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "CryStab", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "CryHori", enemy, this);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (int i = 0; i < philip.minions.length; i++) {
            if (philip.minions[i] == this) {
                philip.minions[i] = null;
                break;
            }
        }
    }

}