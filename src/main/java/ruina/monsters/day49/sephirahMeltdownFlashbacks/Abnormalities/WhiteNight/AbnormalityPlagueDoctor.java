package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.*;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
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
        CardCrawlGame.fadeIn(2.5f);
        WhiteNight whiteNight = new WhiteNight();
        atb(new SpawnMonsterAction(whiteNight, true));

        AbnormalityPlagueDoctor.this.gold = 0;
        AbnormalityPlagueDoctor.this.currentHealth = 0;
        AbnormalityPlagueDoctor.this.dieBypass();
        AbstractDungeon.getMonsters().monsters.remove(this);
        whiteNight.hideHealthBar();
        whiteNight.halfDead = true;
        whiteNight.rollMove();
        whiteNight.createIntent();
    }

    @Override
    public void takeTurn() {
        if(!currentlyBreaching){
            abnormality.takeTurn();
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    if(abnormality instanceof PlagueDoctor){
                        if (((PlagueDoctor) abnormality).getBlessings() == 13){
                            Texture apostles = TexLoader.getTexture(makeMonsterPath("Seraphim/Apostles.png"));
                            atb(new AbstractGameAction() {
                                @Override
                                public void update() {
                                    playSound("WhiteNightSummon");
                                    this.isDone = true;
                                }
                            });
                            flashImageVfx(apostles, 2.0f);
                            breachSetup();
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