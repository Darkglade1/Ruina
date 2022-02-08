package ruina.monsters.act2;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.cards.FragmentOfBliss;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class BrilliantBliss extends AbstractRuinaMonster
{
    public static final String ID = makeID(BrilliantBliss.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;

    private static final byte NONE = 0;

    public static final String POWER_ID = makeID("Bliss");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    AbstractCard fragment = new FragmentOfBliss();

    public BrilliantBliss() {
        this(0.0f, 0.0f);
    }

    public BrilliantBliss(final float x, final float y) {
        super(NAME, ID, 20, -5.0F, 0, 130.0f, 125.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Bliss/Spriter/Bliss.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(20));
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + FontHelper.colorString(fragment.name, "y") + POWER_DESCRIPTIONS[1];
            }

            @Override
            public void onDeath() {
                atb(new MakeTempCardInHandAction(fragment, 1));
            }
        });
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