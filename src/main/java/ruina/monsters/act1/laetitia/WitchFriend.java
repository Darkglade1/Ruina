package ruina.monsters.act1.laetitia;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.Gimme;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class WitchFriend extends AbstractRuinaMonster
{
    public static final String ID = makeID(WitchFriend.class.getSimpleName());
    private static final byte GLITCH = 0;

    public WitchFriend(final float x, final float y) {
        super(ID, ID, 15, 0.0F, 0, 220.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("WeeWitch/Spriter/WeeWitch.scml"));
        setHp(calcAscensionTankiness(12), calcAscensionTankiness(14));
        addMove(GLITCH, Intent.ATTACK, calcAscensionDamage(11));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new Gimme(this));
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
        setMoveShortcut(GLITCH);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "LaetitiaFriendAtk", enemy, this);
    }

}