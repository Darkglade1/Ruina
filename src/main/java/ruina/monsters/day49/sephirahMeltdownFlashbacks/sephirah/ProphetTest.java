package ruina.monsters.day49.sephirahMeltdownFlashbacks.sephirah;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Bleed;
import ruina.shaders.Yesod.YesodShader;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ProphetTest extends AbstractRuinaMonster
{
    public static final String ID = makeID(ProphetTest.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PALE_HANDS = 0;
    private static final byte DEPRESSION = 1;

    private final int BLOCK = calcAscensionTankiness(7);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int BLEED = calcAscensionSpecial(3);

    private Color invisibleRender = Color.WHITE.cpy();
    private Color visible = Color.WHITE.cpy();

    public ProphetTest() {
        this(0.0f, 0.0f);
    }

    public ProphetTest(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 320.0f, null, x, y);
        loadAnimation(makeMonsterPath("Day49/SephirahMeltdownFlashbacks/TestProphet/skeleton.atlas"), makeMonsterPath("Day49/SephirahMeltdownFlashbacks/TestProphet/skeleton.json"), 1.6F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "0_Default_", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("0_Default_", "1_default_to_Kiss", 0.2F);
        this.stateData.setMix("1_default_to_Kiss", "2_Kiss_loop", 0.2F);
        this.stateData.setMix("2_Kiss_loop", "3_Kiss_to_Default", 0.2F);
        skeleton.setColor(Color.BLACK.cpy());
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(88), calcAscensionTankiness(94));
        addMove(PALE_HANDS, Intent.ATTACK_DEBUFF, calcAscensionDamage(15));
        addMove(DEPRESSION, Intent.DEFEND_DEBUFF);
        invisibleRender.a = 0;

    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case PALE_HANDS: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Bleed(adp(), BLEED));
                resetIdle();
                break;
            }
            case DEPRESSION: {
                specialAnimation();
                block(this, BLOCK);
                applyToTarget(adp(), this, new StrengthPower(adp(), -DEBUFF));
                applyToTarget(adp(), this, new DexterityPower(adp(), -DEBUFF));
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void usePreBattleAction() {
        //AbstractDungeon.effectsQueue.add(new YesodShader());
        System.out.println(skeleton.getData().getSkins());
        // going insane
        //System.out.println(skeleton.findBone("head").getData().getColor());
        //ReflectionHacks.setPrivate(skeleton.findBone("head").getData().getColor(), Color.class, "color", invisibleRender);
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
        System.out.println(state.getData().getSkeletonData().findSlot("Hat").getColor());
        skeleton.findSlot("Hat").setAttachment(null);
        skeleton.findSlot("beak").getColor().set(invisibleRender);

        //state.getData().getSkeletonData().findSlot("Hat").getColor().set(Color.BLACK);
    }
}