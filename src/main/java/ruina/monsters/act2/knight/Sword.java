package ruina.monsters.act2.knight;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.RuinaMetallicize;
import ruina.powers.act2.Worthless;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Sword extends AbstractRuinaMonster
{
    public static final String ID = makeID(Sword.class.getSimpleName());

    private static final byte TEAR_HEART = 0;
    private final int BLOCK = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionSpecial(8));
    private KnightOfDespair knight;
    public boolean gainInitialBlock;
    public boolean wasFullBlocked = false;

    public Sword(final float x, final float y, boolean gainInitialBlock) {
        super(ID, ID, 40, -5.0F, 0, 150.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Sword/Spriter/Sword.scml"));
        this.gainInitialBlock = gainInitialBlock;
        setHp(calcAscensionTankiness(40));
        addMove(TEAR_HEART, Intent.ATTACK, calcAscensionDamage(18));
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof KnightOfDespair) {
                knight = (KnightOfDespair) mo;
            }
        }
        addPower(new MinionPower(this));
        applyToTarget(this, this, new Worthless(this));
        applyToTarget(this, this, new RuinaMetallicize(this, BLOCK));
        if (gainInitialBlock) {
            block(this, BLOCK);
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case TEAR_HEART: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        knight.onSwordDeath(wasFullBlocked);
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(TEAR_HEART);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Vertical", "KnightVertGaho", enemy, this);
    }

}