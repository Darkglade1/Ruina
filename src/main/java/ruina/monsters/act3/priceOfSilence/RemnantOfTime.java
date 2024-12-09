package ruina.monsters.act3.priceOfSilence;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class RemnantOfTime extends AbstractRuinaMonster
{
    public static final String ID = makeID(RemnantOfTime.class.getSimpleName());

    private static final byte BACKLASH_OF_TIME = 0;
    private static final byte TORRENT_OF_HOURS = 1;

    private final int BLOCK = calcAscensionTankiness(9);
    private final int STRENGTH = calcAscensionSpecial(2);

    public RemnantOfTime() {
        this(0.0f, 0.0f);
    }

    public RemnantOfTime(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 280.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Remnant/Spriter/Remnant.scml"));
        setHp(calcAscensionTankiness(80), calcAscensionTankiness(86));
        addMove(BACKLASH_OF_TIME, Intent.ATTACK, calcAscensionDamage(7), 2);
        addMove(TORRENT_OF_HOURS, Intent.DEFEND_BUFF);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case BACKLASH_OF_TIME: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        attackForwardAnimation(adp());
                    } else {
                        attackBackAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case TORRENT_OF_HOURS: {
                blockAnimation();
                for (AbstractMonster mo : monsterList()) {
                    block(mo, BLOCK);
                    applyToTargetNextTurn(mo, new StrengthPower(mo, STRENGTH));
                }
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(BACKLASH_OF_TIME)) {
            setMoveShortcut(TORRENT_OF_HOURS);
        } else {
            setMoveShortcut(BACKLASH_OF_TIME);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case TORRENT_OF_HOURS: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE, DetailedIntent.TargetType.ALL_ENEMIES);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    private void attackForwardAnimation(AbstractCreature enemy) {
        animationAction("AttackForward", "BluntBlow", enemy, this);
    }

    private void attackBackAnimation(AbstractCreature enemy) {
        animationAction("AttackBack", "Slash", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

}