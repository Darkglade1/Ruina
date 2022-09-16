package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred;


import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.adp;

public class QueenOfHatredMonster extends AbstractRuinaMonster {
    public static final String ID = makeID(QueenOfHatredMonster.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte NONE = 0;

    public QueenOfHatredMonster(){ this(0, 125); }
    public QueenOfHatredMonster(float x, float y) {
        super(NAME, ID, 999, 0.0F, 0.0F, 500.0F, 600.0F, null, x, y);
        loadAnimation(makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/skeleton.atlas"), makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/QueenOfHatred/skeleton.json"), 1.5F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "0_Dead", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("0_Default", "0_Dead", 1F);
        this.stateData.setMix("0_Default", "1_Default_to_Casting", 1F);
        this.stateData.setMix("1_Default_to_Casting", "2_Casting_loop", 1F);
        this.stateData.setMix("2_Casting_loop", "3_Casting_to_Casting_attack", 1F);
        this.stateData.setMix("3_Casting_to_Casting_attack", "4_Casting_attack_loop", 1F);
        this.stateData.setMix("4_Casting_attack_loop", "5_Casting_attack_to_Delay", 1F);
        this.stateData.setMix("5_Casting_attack_to_Delay", "6_Delay_loop", 1F);
        this.stateData.setMix("6_Delay_loop", "7_Delay_to_Default", 1F);

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


}