package ruina.monsters.act2.greed;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.cards.FragmentOfBliss;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act2.Bliss;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.applyToTarget;

public class BrilliantBliss extends AbstractRuinaMonster
{
    public static final String ID = makeID(BrilliantBliss.class.getSimpleName());

    private static final byte NONE = 0;

    AbstractCard fragment = new FragmentOfBliss();

    public BrilliantBliss() {
        this(0.0f, 0.0f);
    }

    public BrilliantBliss(final float x, final float y) {
        super(ID, ID, 20, -5.0F, 0, 130.0f, 125.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Bliss/Spriter/Bliss.scml"));
        setHp(calcAscensionTankiness(20));
    }

    @Override
    public void usePreBattleAction() {
        addPower(new MinionPower(this));
        applyToTarget(this, this, new Bliss(this, fragment));
    }

    @Override
    public void takeTurn() {
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new CardPowerTip(fragment.makeStatEquivalentCopy()));
    }

    @Override
    protected void getMove(final int num) {
        setMove(NONE, Intent.NONE);
    }

}