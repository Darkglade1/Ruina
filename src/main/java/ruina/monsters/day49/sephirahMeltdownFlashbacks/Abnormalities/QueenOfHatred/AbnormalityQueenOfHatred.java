package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.*;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.PlagueDoctor;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.WhiteNight;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

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
        this.drawX = adp().drawX;
        setupAbnormality();
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
        QueenOfHatredMonster queen = new QueenOfHatredMonster();
        atb(new SpawnMonsterAction(queen, true));
        queen.rollMove();
        queen.createIntent();
    }

    @Override
    public void takeTurn() {
        if(!currentlyBreaching){
            abnormality.takeTurn();
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    if(abnormality instanceof QueenOfHatredNormal){
                        if (((QueenOfHatredNormal) abnormality).getTurnCounter() == 5){
                            breachSetup();
                            ((QueenOfHatredNormal) abnormality).reset();
                        }
                    }
                    isDone = true;
                }
            });
        }
    }

    @Override
    protected void getMove(int i) {
        setMove(NONE, Intent.NONE);
    }
}