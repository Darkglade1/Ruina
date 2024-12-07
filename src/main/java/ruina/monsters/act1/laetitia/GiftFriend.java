package ruina.monsters.act1.laetitia;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.SurprisePresent;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class GiftFriend extends AbstractRuinaMonster {
    public static final String ID = makeID(GiftFriend.class.getSimpleName());

    public float storedX;
    private static final byte TAKE_IT = 0;

    public GiftFriend(final float x, final float y) {
        super(ID, ID, 20, 0.0F, 0, 200.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Friend/Spriter/Friend.scml"));
        setHp(calcAscensionTankiness(17), calcAscensionTankiness(19));
        addMove(TAKE_IT, Intent.ATTACK, calcAscensionDamage(5));
        storedX = x;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        addPower(new MinionPower(this));
        atb(new ApplyPowerAction(this, this, new SurprisePresent(this)));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case TAKE_IT: {
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
        setMoveShortcut(TAKE_IT);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        AbstractMonster giftFriend1 = new WitchFriend(storedX, 0.0f);
        atb(new SpawnMonsterAction(giftFriend1, true));
        atb(new UsePreBattleActionAction(giftFriend1));
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntVert", enemy, this);
    }
}