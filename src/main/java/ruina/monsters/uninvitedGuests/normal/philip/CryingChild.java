package ruina.monsters.uninvitedGuests.normal.philip;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractAllyAttackingMinion;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;

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

    public static final String POWER_ID = makeID("TorchedHeart");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CryingChild(final float x, final float y, Philip philip) {
        super(ID, ID, 40, -5.0F, 0, 100.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("CryingChild/Spriter/CryingChild.scml"));
        setHp(calcAscensionTankiness(72), calcAscensionTankiness(78));
        addMove(WING_STROKE, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
        addMove(MURMUR, Intent.ATTACK, calcAscensionDamage(10));
        this.philip = philip;
        this.target = philip.target;
        attackingAlly = AbstractDungeon.monsterRng.randomBoolean();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, DAMAGE_REDUCTION) {
            @Override
            public float atDamageReceive(float damage, DamageInfo.DamageType type) {
                //handles attack damage
                if (type == DamageInfo.DamageType.NORMAL) {
                    return calculateDamageTakenAmount(damage, type);
                } else {
                    return damage;
                }
            }

            private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
                if (owner.hasPower(VulnerablePower.POWER_ID)) {
                    return damage;
                } else {
                    return damage * (1 - ((float)DAMAGE_REDUCTION / 100));
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        AbstractCreature target;
        if (!this.target.isDead && !this.target.isDying && attackingAlly) {
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
                attackingAlly = AbstractDungeon.monsterRng.randomBoolean();
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
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
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