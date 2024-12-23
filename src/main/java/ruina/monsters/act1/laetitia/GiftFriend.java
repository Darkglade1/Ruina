package ruina.monsters.act1.laetitia;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.SurprisePresent;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class GiftFriend extends AbstractRuinaMonster {
    public static final String ID = makeID(GiftFriend.class.getSimpleName());

    public float storedX;
    private static final byte ATK = 0;
    private static final byte ATK_DEBUFF = 1;
    private final int DEBUFF = 1;
    private final boolean debuffAtkFirst;

    public GiftFriend(final float x, final float y, boolean debuffAtkFirst) {
        super(ID, ID, 20, 0.0F, 0, 200.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Friend/Spriter/Friend.scml"));
        setHp(calcAscensionTankiness(14), calcAscensionTankiness(16));
        addMove(ATK, Intent.ATTACK, calcAscensionDamage(5));
        addMove(ATK_DEBUFF, Intent.ATTACK_DEBUFF, calcAscensionSpecial(2));
        storedX = x;
        this.debuffAtkFirst = debuffAtkFirst;
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
            case ATK: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case ATK_DEBUFF: {
                attackAnimation(adp());
                dmg(adp(), info);
                if (debuffAtkFirst) {
                    applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                } else {
                    applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                }
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (debuffAtkFirst && firstMove) {
            setMoveShortcut(ATK_DEBUFF);
        } else if (lastMove(ATK)) {
            setMoveShortcut(ATK_DEBUFF);
        } else {
            setMoveShortcut(ATK);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            AbstractMonster giftFriend1 = new WitchFriend(storedX, 0.0f);
            atb(new SpawnMonsterAction(giftFriend1, true));
            atb(new UsePreBattleActionAction(giftFriend1));
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case ATK_DEBUFF: {
                if (debuffAtkFirst) {
                    DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.WEAK_TEXTURE);
                    detailsList.add(detail);
                } else {
                    DetailedIntent detail2 = new DetailedIntent(this, DEBUFF, DetailedIntent.FRAIL_TEXTURE);
                    detailsList.add(detail2);
                }
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntVert", enemy, this);
    }
}