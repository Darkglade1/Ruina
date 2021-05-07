package ruina.monsters.angela;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;

public class AbnormalityEncyclopedia extends AbstractRuinaMonster
{
    public static final String ID = makeID(ruina.monsters.act2.BrilliantBliss.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    private static final byte NONE = 0;
    public AbnormalityEncyclopedia() {
        this(0.0f, 0.0f, "BloodBath");
    }

    public AbnormalityEncyclopedia(final float x, final float y, String ABNOID) {
        super(NAME, ID, 20, -5.0F, 0, 130.0f, 125.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("AbnormalityContainer/AbnormalityEncyclopedia/Spriter/AbnormalityEncyclopedia.scml"));
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