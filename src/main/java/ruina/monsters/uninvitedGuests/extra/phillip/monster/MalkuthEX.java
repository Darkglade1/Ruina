package ruina.monsters.uninvitedGuests.extra.phillip.monster;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;

public class MalkuthEX extends AbstractRuinaMonster
{
    public static final String ID = makeID(MalkuthEX.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;

    private static final byte NONE = 0;

    public MalkuthEX() {
        this(0.0f, 0.0f);
    }

    public MalkuthEX(final float x, final float y) {
        super(NAME, ID, 20, -5.0F, 0, 130.0f, 125.0f, null, x, y);
        // replace with proper malkuth later
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Malkuth/Spriter/Malkuth.scml"));
        this.animation.setFlip(true, false);
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