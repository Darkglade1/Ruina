package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.AnimationActions.plaguedoctor.PlagueDoctorBlessAnimation;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.AnimationActions.plaguedoctor.WhitenightTextClockAction;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class PlagueDoctor extends AbstractRuinaMonster
{
    public static final String ID = makeID(PlagueDoctor.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private int blessings = 11;

    private static final byte PALE_HANDS = 0;
    private static final byte DEPRESSION = 1;

    private final int BLOCK = calcAscensionTankiness(7);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int BLEED = calcAscensionSpecial(3);

    private Color invisibleRender = Color.WHITE.cpy();
    private Color visibleRender = Color.WHITE.cpy();
    private Color visibleRenderBlack = Color.DARK_GRAY.cpy();
    private Color visibleRenderBlue = Color.BLACK.cpy();

    public PlagueDoctor() {
        this(0.0f, 295f);
    }

    public PlagueDoctor(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 320.0f, null, x, y);
        loadAnimation(makeMonsterPath("Day49/SephirahMeltdownFlashbacks/TestProphet/skeleton.atlas"), makeMonsterPath("Day49/SephirahMeltdownFlashbacks/TestProphet/skeleton.json"), 2.5F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "0_Default_", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("0_Default_", "1_default_to_Kiss", 1F);
        this.stateData.setMix("1_default_to_Kiss", "2_Kiss_loop", 1f);
        this.stateData.setMix("2_Kiss_loop", "3_Kiss_to_Default", 1F);
        this.stateData.setMix("3_Kiss_to_Default", "0_Default_", 1F);

        skeleton.setColor(Color.BLACK.cpy());
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(88), calcAscensionTankiness(94));
        addMove(PALE_HANDS, Intent.ATTACK_DEBUFF, calcAscensionDamage(15));
        addMove(DEPRESSION, Intent.DEFEND_DEBUFF);
        invisibleRender.a = 0;
    }

    public int getBlessings(){
        return blessings;
    }
    @Override
    public void takeTurn() {
        atb(new PlagueDoctorBlessAnimation(this));
        atb(new WhitenightTextClockAction(blessings));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                blessings += 1;
                isDone = true;
            }
        });
    }

    @Override
    public void usePreBattleAction() {
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(PALE_HANDS)) {
            setMoveShortcut(DEPRESSION);
        } else {
            setMoveShortcut(PALE_HANDS);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BloodAttack", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "BloodSpecial", this);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        // Alter Appearance based on blessings - change values from the skeleton (Which defaults to 8-9 Blessings)
        switch (blessings){
            case 1:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRenderBlue);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRenderBlack);
                skeleton.findSlot("beak").getColor().set(visibleRenderBlack);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRenderBlue);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRenderBlack);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRenderBlack);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRenderBlack);

                // Invisible everything un-needed
                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_back_2").getColor().set(invisibleRender);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(invisibleRender);

                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(invisibleRender);

                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);

                break;
            case 2:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRenderBlue);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRenderBlack);
                skeleton.findSlot("beak").getColor().set(visibleRenderBlack);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRenderBlue);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRenderBlack);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRenderBlack);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRenderBlack);

                // Invisible everything un-needed
                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(invisibleRender);

                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(invisibleRender);

                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);
                break;
            case 3:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRenderBlue);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRenderBlack);
                skeleton.findSlot("beak").getColor().set(visibleRenderBlack);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRenderBlue);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRenderBlack);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRenderBlack);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRenderBlack);

                // Invisible everything un-needed
                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(invisibleRender);

                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);
                break;
            case 4:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRenderBlue);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRenderBlack);
                skeleton.findSlot("beak").getColor().set(visibleRenderBlack);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRenderBlue);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRenderBlack);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRenderBlack);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRenderBlack);


                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRenderBlack);

                // Invisible everything un-needed
                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);
                break;
            case 5:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRenderBlue);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRenderBlack);
                skeleton.findSlot("beak").getColor().set(visibleRenderBlack);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRenderBlue);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRenderBlack);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRenderBlack);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRenderBlack);


                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRenderBlack);

                // Identical, but he has purple sparkles here, render that later.

                // Invisible everything un-needed

                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);
                break;
            case 6:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRender);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRenderBlack);
                skeleton.findSlot("beak").getColor().set(visibleRenderBlack);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRender);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRenderBlack);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRenderBlack);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRenderBlack);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRenderBlack);


                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRenderBlack);

                // Invisible everything un-needed
                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);
                break;
            case 7:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRender);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRenderBlack);
                skeleton.findSlot("beak").getColor().set(visibleRenderBlack);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRender);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRender);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRender);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRender);


                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRender);

                // Invisible everything un-needed
                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);
                break;
            case 8:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRender);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRender);
                skeleton.findSlot("beak").getColor().set(visibleRender);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRender);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRender);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRender);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRender);


                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRender);

                // Invisible everything un-needed
                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);
                break;
            case 9:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRender);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRender);
                skeleton.findSlot("beak").getColor().set(visibleRender);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRender);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRender);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRender);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRender);


                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRender);

                // Identical, to the last, but  sparkles.

                // Invisible everything un-needed
                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);
                break;
            case 10:
                // make hat invisible
                skeleton.findSlot("Hat").getColor().set(invisibleRender);
                skeleton.findSlot("Hat_shadow").getColor().set(invisibleRender);
                skeleton.findSlot("Hat_back").getColor().set(invisibleRender);

                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRender);
                skeleton.findSlot("beak").getColor().set(visibleRender);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRender);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRender);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRender);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRender);


                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRender);

                skeleton.findSlot("Baby_ring").getColor().set(visibleRender);

                // Invisible everything un-needed

                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                break;
            case 11:
                // INVISIBLE
                skeleton.findSlot("Hat").getColor().set(invisibleRender);
                skeleton.findSlot("Hat_shadow").getColor().set(invisibleRender);
                skeleton.findSlot("Hat_back").getColor().set(invisibleRender);
                skeleton.findSlot("head").getColor().set(invisibleRender);
                skeleton.findSlot("beak").getColor().set(invisibleRender);
                skeleton.findSlot("Coat").getColor().set(invisibleRender);
                skeleton.findSlot("Coat_back").getColor().set(invisibleRender);
                skeleton.findSlot("Fur").getColor().set(invisibleRender);

                skeleton.findSlot("eye1").getColor().set(invisibleRender);
                skeleton.findSlot("eye2").getColor().set(invisibleRender);

                skeleton.findSlot("Baby").getColor().set(visibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(visibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(visibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(visibleRender);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRender);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRender);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRender);


                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRender);

                // Identical, to the last, but  sparkles.
                break;
            case 12:
                // INVISIBLE
                skeleton.findSlot("Hat").getColor().set(invisibleRender);
                skeleton.findSlot("Hat_shadow").getColor().set(invisibleRender);
                skeleton.findSlot("Hat_back").getColor().set(invisibleRender);
                skeleton.findSlot("head").getColor().set(invisibleRender);
                skeleton.findSlot("beak").getColor().set(invisibleRender);
                skeleton.findSlot("Coat").getColor().set(invisibleRender);
                skeleton.findSlot("Coat_back").getColor().set(invisibleRender);
                skeleton.findSlot("Fur").getColor().set(invisibleRender);

                skeleton.findSlot("eye1").getColor().set(invisibleRender);
                skeleton.findSlot("eye2").getColor().set(invisibleRender);

                skeleton.findSlot("Baby").getColor().set(visibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(visibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(visibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(visibleRender);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRender);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRender);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRender);

                // Identical, to the last, but  sparkles.
                break;
            case 13:
                // INVISIBLE
                skeleton.findSlot("Hat").getColor().set(invisibleRender);
                skeleton.findSlot("Hat_shadow").getColor().set(invisibleRender);
                skeleton.findSlot("Hat_back").getColor().set(invisibleRender);
                skeleton.findSlot("head").getColor().set(invisibleRender);
                skeleton.findSlot("beak").getColor().set(invisibleRender);
                skeleton.findSlot("Coat").getColor().set(invisibleRender);
                skeleton.findSlot("Coat_back").getColor().set(invisibleRender);
                skeleton.findSlot("Fur").getColor().set(invisibleRender);

                skeleton.findSlot("eye1").getColor().set(invisibleRender);
                skeleton.findSlot("eye2").getColor().set(invisibleRender);

                skeleton.findSlot("Baby").getColor().set(visibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(visibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(visibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(visibleRender);

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRender);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRender);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(visibleRender);
                skeleton.findSlot("Wing_front_1").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_2").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(visibleRender);

                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(visibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(visibleRender);

                // Identical, to the last, but  sparkles.
                break;
            default:
                // change hat colour
                skeleton.findSlot("Hat").getColor().set(visibleRenderBlue);
                // change head colour
                skeleton.findSlot("head").getColor().set(visibleRenderBlack);
                skeleton.findSlot("beak").getColor().set(visibleRenderBlack);
                // change coat colour
                skeleton.findSlot("Coat").getColor().set(visibleRenderBlue);
                // Only has four wings active at this point

                // Front Wing (Default)
                skeleton.findSlot("Wing_default_front_0_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_0_4").getColor().set(visibleRenderBlack);

                skeleton.findSlot("Wing_default_front_1_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_front_1_3").getColor().set(visibleRenderBlack);

                // Back Wing (Default)
                skeleton.findSlot("Wing_default_back_f_1").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_2").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_3").getColor().set(visibleRenderBlack);
                skeleton.findSlot("Wing_default_back_4").getColor().set(visibleRenderBlack);

                // Invisible everything un-needed
                // Front + Back Wing (Blessing 1)
                skeleton.findSlot("Wing_back_1").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_front_1").getColor().set(invisibleRender);

                // Front + Back Wing (Blessing 2)
                skeleton.findSlot("Wing_front_2").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_back_2").getColor().set(invisibleRender);

                // Front + Back Wing (Blessing 3)
                skeleton.findSlot("Wing_front_3").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_back_3").getColor().set(invisibleRender);

                // Front + Back Wing (Blessing 4)
                skeleton.findSlot("Wing_front_4").getColor().set(invisibleRender);
                skeleton.findSlot("Wing_back_4").getColor().set(invisibleRender);

                // WhiteNight
                skeleton.findSlot("Baby").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_necklas").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_open_2").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_eye_closed").getColor().set(invisibleRender);
                skeleton.findSlot("Baby_ring").getColor().set(invisibleRender);
                break;
        }
    }
}