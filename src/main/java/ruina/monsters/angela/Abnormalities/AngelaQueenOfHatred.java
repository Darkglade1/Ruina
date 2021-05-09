package ruina.monsters.angela.Abnormalities;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.act3.Bloodbath;
import ruina.monsters.angela.*;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class AngelaQueenOfHatred extends AbnormalityContainer
{
    public static final String ABNOID = "QueenOfHatred";
    public static final String WARNING = "WAW";

    public static final String ID = makeID(AngelaQueenOfHatred.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;

    private static final byte NONE = 0;
    private final int HEAL = calcAscensionSpecial(100);
    private float x;
    private float y;
    public AngelaQueenOfHatred(){
        this(0, 275);
    }
    public AngelaQueenOfHatred(final float x, final float y) {
        super(NAME, ID, 80, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 9) {
            setHp(1);
        } else {
            setHp(1);
        }
        this.x = x;
        this.y = y;
    }

    @Override
    protected void setupAbnormality() {
        abnormality = new AbnormalityUnit(x, y, ABNOID);
        abnormality.drawX = adp().drawX;
        abnormalityBG = new AbnormalityBackground(x, y, ABNOID);
        abnormalityBG.drawX = adp().drawX;
        abnormalityEncyclopedia = new AbnormalityEncyclopedia(x, y, ABNOID);
        abnormalityEncyclopedia.drawX = adp().drawX;
        abnormalityWarning = new AbnormalityWarning(x, y, WARNING);
        abnormalityWarning.drawX = adp().drawX;
        staticDischarge = new Static(x, y);
        staticDischarge.drawX = adp().drawX;
    }

    public void usePreBattleAction(){
        this.drawX = adp().drawX;
        setupAbnormality();
    }

    @Override
    protected void getAbnormality(int timesBreached) {
        prepareBreach();
        CustomDungeon.playTempMusicInstantly("Trumpet1");
        Bloodbath bloodbath = new Bloodbath(x, 0);
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