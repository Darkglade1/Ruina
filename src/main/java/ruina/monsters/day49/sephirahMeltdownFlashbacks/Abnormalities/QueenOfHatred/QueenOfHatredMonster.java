package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred;


import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.monster.QueenOfHatredAttackToStunAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.monster.QueenOfHatredCastingToAttackAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.monster.QueenOfHatredDefaultToCastingAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.monster.QueenOfHatredStunToNormalStateAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.normal.QueenOfHatredDefaultToPanicAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.normal.QueenOfHatredPanicToBreachAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.normal.QueenOfHatredPassiveAnimationAction;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.sephirah.SephirahHokma;
import ruina.vfx.QueenOfHatredLaserEffect;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class QueenOfHatredMonster extends AbstractRuinaMonster {
    public static final String ID = makeID(QueenOfHatredMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte NONE = 0;
    private int turnCounter = 0;

    public QueenOfHatredMonster(){ this(75, 0); }
    public QueenOfHatredMonster(float x, float y) {
        super(NAME, ID, 999, 0.0F, 0.0F, 500.0F, 600.0F, null, x, y);
        loadAnimation(makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/skeleton.atlas"), makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/skeleton.json"), 1.5F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "0_Default", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("0_Default", "0_Dead", 1F);
        this.stateData.setMix("0_Default", "1_Default_to_Casting", 1F);
        this.stateData.setMix("1_Default_to_Casting", "2_Casting_loop", 1F);
        this.stateData.setMix("2_Casting_loop", "3_Casting_to_Casting_attack", 1F);
        this.stateData.setMix("3_Casting_to_Casting_attack", "4_Casting_attack_loop", 1F);
        this.stateData.setMix("4_Casting_attack_loop", "5_Casting_attack_to_Delay", 1F);
        this.stateData.setMix("5_Casting_attack_to_Delay", "6_Delay_loop", 1F);
        this.stateData.setMix("6_Delay_loop", "7_Delay_to_Default", 1F);
        this.stateData.setMix("7_Delay_to_Default", "0_Default", 1F);

        halfDead = true;
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
            case 0:
                atb(new QueenOfHatredDefaultToCastingAnimationAction(this));
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
            case 1:
                atb(new QueenOfHatredCastingToAttackAnimationAction(this));
                atb(new VFXAction(new QueenOfHatredLaserEffect(skeleton)));
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
                atb(new QueenOfHatredAttackToStunAnimationAction(this));
                break;
            case 2:
                atb(new QueenOfHatredStunToNormalStateAnimationAction(this));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for(AbstractMonster m: monsterList()){
                            if(m instanceof SephirahHokma){
                                applyToTargetTop(m, m, new VulnerablePower(m, 3, true));
                            }
                        }
                        turnCounter = 0;
                        isDone = true;
                    }
                });
                break;
        }
    }

    // test
    public void resetAnimationData(){
        loadAnimation(makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/skeleton.atlas"), makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/skeleton.json"), 1.5F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "0_Default", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

}