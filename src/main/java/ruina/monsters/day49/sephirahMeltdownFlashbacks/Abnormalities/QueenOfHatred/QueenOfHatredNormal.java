package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.adp;

public class QueenOfHatredNormal extends AbstractRuinaMonster {
    public static final String ID = makeID(QueenOfHatredNormal.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte NONE = 0;
    private float animationTimer = 10f;
    private float startingAnimationTimer = 10f;
    private boolean breaching = false;

    private boolean usedSpecial = false;

    public QueenOfHatredNormal(){ this(0, 295f); }
    public QueenOfHatredNormal(float x, float y) {
        super(NAME, ID, 999, 0.0F, 0.0F, 500.0F, 600.0F, null, x, y);
        loadAnimation(makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/Magic_circle.atlas"), makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/Magic_circle.json"), 2.75F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "0_Default_escape_too", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.stateData.setMix("0_Default_escape_too", "0_Dead", 1f);
        this.stateData.setMix("0_Default_escape_too", "0_Default_2", 2f);
        this.stateData.setMix("0_Default_escape_too", "0_Default_3", 3.5f);
        this.stateData.setMix("0_Default_escape_too", "0_Default_4_special", 2f);

        this.stateData.setMix("0_Default_escape_too", "1_Attack", 1f);
        this.stateData.setMix("0_Default_escape_too", "1_Casting", 1f);
        this.stateData.setMix("0_Default_escape_too", "1_Default_to_Panic_Default", 1f);
        this.stateData.setMix("1_Default_to_Panic_Default", "2_Panic_Default", 1f);

        this.stateData.setMix("2_Panic_Default", "4_transform", 1f);

        halfDead = true;
        this.flipHorizontal = true;
        hideHealthBar();
        this.type = EnemyType.BOSS;
    }

    public void usePreBattleAction() {
        drawX = adp().drawX + 480.0F * Settings.scale;;
    }

    protected void getMove(int i) {
        setMove(NONE, Intent.NONE);
    }

    public void takeTurn() {

    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (!breaching) {
            animationTimer -= Gdx.graphics.getDeltaTime();
            if (animationTimer <= 0) {
                switch (MathUtils.random(3)) {
                    case 0:
                        state.setAnimation(0, "0_Default_2", false);
                        state.setTimeScale(1f);
                        state.addAnimation(0, "0_Default_escape_too", true, 0f);
                        break;
                    case 1:
                        state.setAnimation(0, "0_Default_3", false);
                        state.setTimeScale(1f);
                        state.addAnimation(0, "0_Default_escape_too", true, 0f);
                        break;
                    case 2: {
                        state.setAnimation(0, "0_Default_4_special", false);
                        state.setTimeScale(1f);
                        state.addAnimation(0, "0_Default_escape_too", true, 0f);
                        break;
                    }
                }
                animationTimer = startingAnimationTimer;
            }
        }
    }

    public void configureAsBreachingAbno(){
        loadAnimation(makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/Magic_circle.atlas"), makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/Magic_circle.json"), 2.5F);
        breaching = true;
    }
}