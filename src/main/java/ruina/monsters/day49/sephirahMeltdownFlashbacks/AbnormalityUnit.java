package ruina.monsters.day49.sephirahMeltdownFlashbacks;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;

public class AbnormalityUnit extends AbstractRuinaMonster
{
    public static final String ID = makeID(AbnormalityUnit.class.getSimpleName());
    public static final String NAME = "";
    private static final byte NONE = 0;
    public AbnormalityUnit() {
        this(0.0f, 0.0f, "ts_BloodBath");
    }
    public AbnormalityUnit(String ABNOID) { this(0.0f, 0.0f, ABNOID); }
    public AbnormalityUnit(final float x, final float y, String ABNOID) {
        super(NAME, ID, 20, -5.0F, 0, 130.0f, 125.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("AbnormalityContainer/AbnormalityUnit/Spriter/AbnormalityUnit.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(this.maxHealth));
        runAnim(ABNOID);
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
        setMove(NONE, Intent.NONE);
    }

}