package ruina.monsters.day49.sephirahMeltdownFlashbacks;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.day49.Act2Angela;
import ruina.monsters.day49.ui.TreeOfLife;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class TreeOfLifeManager extends AbstractRuinaMonster
{
    public static final String ID = RuinaMod.makeID(TreeOfLifeManager.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;

    private static final byte NONE = 0;
    private TreeOfLife treeOfLife = new TreeOfLife();

    // invisible enemy, should never appear onscreen.
    public TreeOfLifeManager() {
        this(9999f, 9999f);
    }

    public TreeOfLifeManager(final float x, final float y) {
        super(NAME, ID, 99999, -5.0F, 0, 130.0f, 125.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Bliss/Spriter/Bliss.scml"));
        this.type = EnemyType.NORMAL;
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.getCurrRoom().cannotLose = true;
        treeOfLife.init();
        halfDead = true;
        hideHealthBar();
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
        setMove(NONE, Intent.NONE);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        treeOfLife.render(sb);
    }

    @Override
    public void update() {
        super.update();
        treeOfLife.update();
    }
}