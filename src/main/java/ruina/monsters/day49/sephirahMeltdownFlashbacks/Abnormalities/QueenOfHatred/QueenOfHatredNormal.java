package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred;


import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.normal.QueenOfHatredDefaultToPanicAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.normal.QueenOfHatredPanicToBreachAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.normal.QueenOfHatredPassiveAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.AnimationActions.plaguedoctor.PlagueDoctorBlessAnimation;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.AnimationActions.plaguedoctor.WhitenightTextClockAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.sephirah.SephirahHokma;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class QueenOfHatredNormal extends AbstractRuinaMonster {
    public static final String ID = makeID(QueenOfHatredNormal.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte NONE = 0;

    private int turnCounter = 3;

    public QueenOfHatredNormal(){ this(0, 295f); }
    public QueenOfHatredNormal(float x, float y) {
        super(NAME, ID, 999, 0.0F, 0.0F, 500.0F, 600.0F, null, x, y);
        loadAnimation(makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/Magic_circle.atlas"), makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/Magic_circle.json"), 2.75F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "0_Default_escape_too", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        this.stateData.setMix("0_Default_escape_too", "0_Dead", 1f);
        this.stateData.setMix("0_Default_escape_too", "0_Default_2", 1f);
        this.stateData.setMix("0_Default_escape_too", "0_Default_3", 1f);
        this.stateData.setMix("0_Default_escape_too", "0_Default_4_special", 1f);

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
        switch (turnCounter){
            default:
                atb(new QueenOfHatredPassiveAnimationAction(this));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for(AbstractMonster m: monsterList()){
                            if(m instanceof SephirahHokma){
                                applyToTargetTop(m, m, new VulnerablePower(m, 3, true));
                            }
                        }
                        turnCounter += 1;
                        isDone = true;
                    }
                });
                break;
            case 3:
                atb(new QueenOfHatredDefaultToPanicAnimationAction(this));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        turnCounter += 1;
                        isDone = true;
                    }
                });
                break;
            case 4:
                atb(new QueenOfHatredPanicToBreachAnimationAction(this));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        turnCounter += 1;
                        isDone = true;
                    }
                });
                break;
        }
    }

    public int getTurnCounter(){ return turnCounter; }

    public void reset(){
        state.addAnimation(0, "0_Default_escape_too", true, 0f);
        state.setTimeScale(1f);
        turnCounter = 0;
    }
}