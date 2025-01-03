package ruina.monsters.act2.Oz;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.cards.Emerald;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ScowlingFace extends AbstractRuinaMonster
{
    public static final String ID = makeID(ScowlingFace.class.getSimpleName());

    private static final byte ATTACK = 0;
    private static final byte BUFF = 1;
    public final int STR = calcAscensionSpecial(3);
    public final int BLOCK = calcAscensionTankiness(12);
    private final boolean atkFirst;

    AbstractCard emerald = new Emerald();

    public ScowlingFace(final float x, final float y, boolean atkFirst) {
        super(ID, ID, 100, -5.0F, 0, 135.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScowlingFace/Spriter/ScowlingFace.scml"));
        setHp(calcAscensionTankiness(34), calcAscensionTankiness(38));
        addMove(ATTACK, Intent.ATTACK, calcAscensionDamage(6));
        addMove(BUFF, Intent.DEFEND_BUFF);
        this.atkFirst = atkFirst;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        addPower(new MinionPower(this));
        applyToTarget(this, this, new ruina.powers.act2.Emerald(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case ATTACK: {
                bluntAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case BUFF: {
                specialAnimation(adp());
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STR));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            if (atkFirst) {
                setMoveShortcut(ATTACK);
            } else {
                setMoveShortcut(BUFF);
            }
        } else {
            if (this.lastMove(ATTACK)) {
                setMoveShortcut(BUFF);
            } else {
                setMoveShortcut(ATTACK);
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new CardPowerTip(emerald.makeStatEquivalentCopy()));
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case BUFF: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail1 = new DetailedIntent(this, STR, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail1);
                break;
            }
        }
        return detailsList;
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "SmokeAtk", 0.7f, enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Blunt", null, enemy, this);
    }

}