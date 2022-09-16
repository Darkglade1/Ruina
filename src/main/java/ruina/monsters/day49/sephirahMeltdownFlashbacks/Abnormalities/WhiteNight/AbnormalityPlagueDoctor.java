package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.*;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class AbnormalityPlagueDoctor extends AbnormalityContainer
{
    private static final String ABNOID = "WhiteNight";
    private static final String WARNING = "ALEPH";
    public static final String ID = makeID(AbnormalityPlagueDoctor.class.getSimpleName());
    public static final String NAME = "";
    private static final byte NONE = 0;
    private final int HEAL = calcAscensionSpecial(100);
    public AbnormalityPlagueDoctor(){
        this(0, 275);
    }
    public AbnormalityPlagueDoctor(final float x, final float y) {
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

        abnormality = new PlagueDoctor();
        abnormality.drawX = this.drawX;
    }

    @Override
    protected void getAbnormality(int timesBreached) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.RED));
                this.isDone = true;
            }
        });
        CustomDungeon.playTempMusicInstantly("Trumpet3");
        WhiteNight whiteNight = new WhiteNight();
        atb(new SpawnMonsterAction(whiteNight, true));
        whiteNight.rollMove();
        whiteNight.createIntent();
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(int i) {
        setMove(NONE, Intent.NONE);
    }
}