package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred;

import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.act3.Bloodbath;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.AbnormalityContainer;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

public class AngelaQueenOfHatred extends AbnormalityContainer
{
    public static final String ABNOID = "QueenOfHatred";
    public static final String WARNING = "WAW";
    public static final String ID = makeID(AngelaQueenOfHatred.class.getSimpleName());
    public static final String NAME = "";
    private static final byte NONE = 0;
    public AngelaQueenOfHatred(){
        this(0, 275);
    }
    public AngelaQueenOfHatred(final float x, final float y) {
        super(NAME, ID, 80, -5.0F, 0, 230.0f, 225.0f, null, x, y, ABNOID, WARNING);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 9) {
            setHp(1);
        } else {
            setHp(1);
        }
    }

    @Override
    protected void getAbnormality(int timesBreached) {
        prepareBreach();
        Bloodbath bloodbath = new Bloodbath();
        atb(new SpawnMonsterAction(bloodbath, true));
        atb(new UsePreBattleActionAction(bloodbath));
        bloodbath.rollMove();
        bloodbath.createIntent();
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(int i) {
        setMove(NONE, Intent.NONE);
    }
}