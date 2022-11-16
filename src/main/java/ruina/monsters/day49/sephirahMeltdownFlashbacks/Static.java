package ruina.monsters.day49.sephirahMeltdownFlashbacks;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;

public class Static extends AbstractRuinaMonster
{
    public static final String ID = makeID(Static.class.getSimpleName());
    public static final String NAME = "";
    private static final byte NONE = 0;
    public Static() {
        this(0.0f, 275f);
    }
    public Static(final float x, final float y) {
        super(NAME, ID, 20, -5.0F, 0, 130.0f, 125.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Day49/AbnormalityContainer/Static/Spriter/Static.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(this.maxHealth));
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
        setMove(NONE, Intent.NONE);
    }

}