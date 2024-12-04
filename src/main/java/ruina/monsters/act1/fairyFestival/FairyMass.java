package ruina.monsters.act1.fairyFestival;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Bleed;
import ruina.powers.Meal;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class FairyMass extends AbstractRuinaMonster {
    public static final String ID = makeID(FairyMass.class.getSimpleName());

    private static final byte WINGBEATS = 0;
    private static final byte GLUTTONY = 1;

    private final int BLEED = 1;
    private final float CONSUME_THRESHOLD = 0.25f;

    public static final String POWER_ID = makeID("Meal");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private FairyQueen queen;
    private final int consumeThreshold;

    public FairyMass(final float x, final float y) {
        super(ID, ID, 75, 0.0F, 0, 160.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("FairyMass/Spriter/FairyMass.scml"));
        setHp(calcAscensionTankiness(18), calcAscensionTankiness(20));
        int wingBeatsDamage = 3;
        if (AbstractDungeon.ascensionLevel >= 19) {
            wingBeatsDamage = 4;
        }
        addMove(WINGBEATS, Intent.ATTACK_DEBUFF, wingBeatsDamage);
        addMove(GLUTTONY, Intent.ATTACK, calcAscensionDamage(5));
        consumeThreshold = (int)(this.maxHealth * CONSUME_THRESHOLD);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof FairyQueen) {
                queen = (FairyQueen) mo;
            }
        }
        addPower(new MinionPower(this));
        applyToTarget(this, this, new Meal(this, consumeThreshold, queen));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (nextMove) {
            case GLUTTONY:
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            case WINGBEATS:
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Bleed(adp(), BLEED));
                resetIdle();
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(GLUTTONY)) {
            possibilities.add(GLUTTONY);
        }
        if (!this.lastTwoMoves(WINGBEATS)) {
            possibilities.add(WINGBEATS);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "FairyMinionAtk", enemy, this);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (int i = 0; i < queen.minions.length; i++) {
            if (queen.minions[i] == this) {
                queen.minions[i] = null;
                break;
            }
        }
    }

}