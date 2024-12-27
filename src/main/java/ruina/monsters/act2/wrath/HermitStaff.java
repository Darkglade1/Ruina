package ruina.monsters.act2.wrath;

import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractAllyAttackingMinion;
import ruina.powers.InvisibleBarricadePower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class HermitStaff extends AbstractAllyAttackingMinion
{
    public static final String ID = makeID(HermitStaff.class.getSimpleName());

    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MultiIntentStrings"));
    protected static final String[] TEXT = uiStrings.TEXT;

    private static final byte ATTACK = 0;

    public HermitStaff(final float x, final float y) {
        super(ID, ID, 40, -5.0F, 0, 150.0f, 195.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Staff/Spriter/Staff.scml"));
        setHp(calcAscensionTankiness(44), calcAscensionTankiness(48));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(8));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof ServantOfWrath) {
                target = (ServantOfWrath) mo;
            }
        }
        addPower(new MinionPower(this));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RemoveAllBlockAction(this, this));

        AbstractCreature target = this.target;
        if (target.isDead || target.isDying) {
            target = adp();
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case ATTACK: {
                attackAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        AbstractDungeon.onModifyPower();
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(ATTACK);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntBlow", enemy, this);
    }

}