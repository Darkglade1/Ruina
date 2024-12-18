package ruina.monsters.act3.blueStar;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Paralysis;
import ruina.powers.act3.Martyr;
import ruina.powers.act3.MeetAgain;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Worshipper extends AbstractRuinaMonster
{
    public static final String ID = makeID(Worshipper.class.getSimpleName());

    public static final String YEET = RuinaMod.makeMonsterPath("Worshipper/Spriter/Suicide.png");
    private static final Texture YEET_TEXTURE = TexLoader.getTexture(YEET);

    private static final byte FOR_THE_STAR = 0;
    private static final byte EVERLASTING_FAITH = 1;
    private static final byte HEAR_STAR = 2;
    private static final byte MEET_AGAIN = 3;

    private static final float STRONG_ATK_HP_THRESHOLD = 0.66f;
    private static final float MEET_AGAIN_HP_THRESHOLD = 0.33f;

    private final int MARTYR_DAMAGE = calcAscensionSpecial(15);
    private final int PARALYSIS = calcAscensionSpecial(1);

    private BlueStar star;
    public boolean triggerMartyr = true;
    private final int meetAgainThreshold;
    private boolean yeeting = false;

    public Worshipper(final float x, final float y) {
        super(ID, ID, 40, -5.0F, 0, 220.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Worshipper/Spriter/Worshipper.scml"));
        setHp(calcAscensionTankiness(38), calcAscensionTankiness(44));
        addMove(FOR_THE_STAR, Intent.ATTACK, calcAscensionDamage(13));
        addMove(EVERLASTING_FAITH, Intent.ATTACK_DEBUFF, calcAscensionDamage(9));
        addMove(HEAR_STAR, Intent.ATTACK, calcAscensionDamage(18));
        addMove(MEET_AGAIN, Intent.UNKNOWN);
        meetAgainThreshold = Math.round(this.maxHealth * MEET_AGAIN_HP_THRESHOLD);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof BlueStar) {
                this.star = (BlueStar) mo;
            }
        }
        addPower(new MinionPower(this));
        applyToTarget(this, this, new Martyr(this, MARTYR_DAMAGE));
        applyToTarget(this, this, new MeetAgain(this, meetAgainThreshold));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case FOR_THE_STAR: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case EVERLASTING_FAITH: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                resetIdle();
                break;
            }
            case HEAR_STAR: {
                bigAttackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case MEET_AGAIN: {
                triggerMartyr = false;
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        yeeting = true;
                        this.isDone = true;
                    }
                });
                YeetEffect();
                atb(new SuicideAction(this));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (int i = 0; i < star.minions.length; i++) {
            if (star.minions[i] == this) {
                star.minions[i] = null;
                break;
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= meetAgainThreshold && this.nextMove != MEET_AGAIN) {
            rollMove();
            createIntent();
        }
    }

    @Override
    protected void getMove(final int num) {
        if (this.currentHealth <= meetAgainThreshold) {
            setMoveShortcut(MEET_AGAIN);
        } else if (this.currentHealth <= (int)(this.maxHealth * STRONG_ATK_HP_THRESHOLD)) {
            setMoveShortcut(HEAR_STAR);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(FOR_THE_STAR)) {
                possibilities.add(FOR_THE_STAR);
            }
            if (!this.lastTwoMoves(EVERLASTING_FAITH)) {
                possibilities.add(EVERLASTING_FAITH);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case EVERLASTING_FAITH: {
                DetailedIntent detail = new DetailedIntent(this, PARALYSIS, DetailedIntent.PARALYSIS_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!yeeting) {
            super.render(sb);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "WorshipperAttack", enemy, this);
    }

    private void bigAttackAnimation(AbstractCreature enemy) {
        animationAction("BigAttack", "WorshipperAttack", enemy, this);
    }

    private void YeetEffect() {
        float duration = 2.0f;
        AbstractGameEffect effect = new VfxBuilder(YEET_TEXTURE, this.hb.cX, this.hb.cY, duration)
                .arc(this.hb.cX, this.hb.cY, (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2 + (375.0F * Settings.scale), (float) Settings.HEIGHT / 2 + (375.0F * Settings.scale))
                .playSoundAt(1.5f, makeID("WorshipperSuicide"))
                .scale(1.0f, 0.0f, VfxBuilder.Interpolations.SWING)
                .rotate(-400f)
                .build();
        atb(new VFXAction(effect, duration));
    }

}