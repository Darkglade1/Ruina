package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred;

import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.*;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.WhiteNight;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.atb;

public class AbnormalityQueenOfHatred extends AbnormalityContainer
{
    private static final String ABNOID = "QueenOfHatred";
    private static final String WARNING = "WAW";
    public static final String ID = makeID(AbnormalityQueenOfHatred.class.getSimpleName());
    public static final String NAME = "";
    private static final byte NONE = 0;
    public AbnormalityQueenOfHatred(){
        this(0, 275);
    }
    public AbnormalityQueenOfHatred(final float x, final float y) {
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
        abnormalityBG.drawX = this.drawX;
        abnormalityEncyclopedia = new AbnormalityEncyclopedia(abnoID);
        abnormalityEncyclopedia.drawX = this.drawX;
        abnormalityWarning = new AbnormalityWarning(warningTier);
        abnormalityWarning.drawX = this.drawX;
        staticDischarge = new Static();
        staticDischarge.drawX = this.drawX;

        abnormality = new QueenOfHatredNormal();
        abnormality.drawX = this.drawX;
    }

    @Override
    protected void getAbnormality(int timesBreached) {
        QueenOfHatredNormal queen = new QueenOfHatredNormal();
        queen.configureAsBreachingAbno();
        atb(new SpawnMonsterAction(queen, true));
        queen.rollMove();
        queen.createIntent();
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(int i) {
        setMove(NONE, Intent.NONE);
    }
}