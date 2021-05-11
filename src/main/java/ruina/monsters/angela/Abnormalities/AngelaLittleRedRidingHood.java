package ruina.monsters.angela.Abnormalities;

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

public class AngelaLittleRedRidingHood extends AbnormalityContainer
{
    public static final String ABNOID = "LittleRedRidingHood";
    public static final String WARNING = "WAW";
    public static final String ID = makeID(AngelaLittleRedRidingHood.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    private static final byte NONE = 0;
    public AngelaLittleRedRidingHood(){
        this(0, 275);
    }
    public AngelaLittleRedRidingHood(final float x, final float y) {
        super(NAME, ID, 80, -5.0F, 0, 230.0f, 225.0f, null, x, y, ABNOID, WARNING);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 9) {
            setHp(1);
        } else {
            setHp(1);
        }
    }

    protected void setupAbnormality() {
        abnormalityBG = new AbnormalityBackground(abnoID);
        abnormalityBG.drawX = adp().drawX;
        abnormalityEncyclopedia = new AbnormalityEncyclopedia(abnoID);
        abnormalityEncyclopedia.drawX = adp().drawX;
        abnormalityWarning = new AbnormalityWarning(warningTier);
        abnormalityWarning.drawX = adp().drawX;
        staticDischarge = new Static();
        staticDischarge.drawX = adp().drawX;
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