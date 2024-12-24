package ruina.monsters.act1.laetitia;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class WitchFriend extends AbstractRuinaMonster
{
    public static final String ID = makeID(WitchFriend.class.getSimpleName());

    private static final byte NONE = 0;
    private static final byte GLITCH = 1;

    public WitchFriend(final float x, final float y) {
        super(ID, ID, 15, 0.0F, 0, 220.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("WeeWitch/Spriter/WeeWitch.scml"));
        setHp(calcAscensionTankiness(10), calcAscensionTankiness(12));
        addMove(NONE, Intent.NONE);
        addMove(GLITCH, Intent.ATTACK, calcAscensionDamage(9));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        rollMove();
        createIntent();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case GLITCH: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(NONE);
        } else {
            setMoveShortcut(GLITCH);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "LaetitiaFriendAtk", enemy, this);
    }

}