package ruina.monsters.act3.snowQueen;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.stances.CalmStance;
import ruina.BetterSpriterAnimation;
import ruina.cardmods.FrozenMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.CenterOfAttention;
import ruina.util.DetailedIntent;
import ruina.vfx.FlexibleCalmParticleEffect;
import ruina.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SnowQueen extends AbstractRuinaMonster
{
    public static final String ID = makeID(SnowQueen.class.getSimpleName());

    private static final byte BLIZZARD = 0;
    private static final byte FRIGID_GAZE = 1;
    private static final byte ICE_SPLINTERS = 2;
    private static final byte FROZEN_THRONE = 3;

    private static final int THRESHOLD = 3;
    private static final int MAX_FROZEN_THRONE_USES = 2;
    private final int DEBUFF = calcAscensionSpecial(3);
    private final int BLOCK = calcAscensionTankiness(16);
    private final int STRENGTH = calcAscensionSpecial(3);
    private final int METALLICIZE = calcAscensionSpecial(5);

    public static final String POWER_ID = makeID("PromiseOfWinter");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public boolean canBlizzard = true;
    private int frozenThroneCounter = 0;

    public SnowQueen() {
        this(0.0f, 0.0f);
    }

    public SnowQueen(final float x, final float y) {
        super(ID, ID, 260, 0.0F, 0, 280.0f, 325.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SnowQueen/Spriter/SnowQueen.scml"));
        setHp(calcAscensionTankiness(260));
        addMove(BLIZZARD, Intent.STRONG_DEBUFF);
        addMove(FRIGID_GAZE, Intent.ATTACK_DEFEND, calcAscensionDamage(20));
        addMove(ICE_SPLINTERS, Intent.ATTACK, calcAscensionDamage(26));
        addMove(FROZEN_THRONE, Intent.DEFEND_BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning1");
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                this.amount++;
                if (this.amount >= THRESHOLD) {
                    this.flash();
                    FrozenMod mod = new FrozenMod();
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if (!CardModifierManager.hasModifier(card, FrozenMod.ID)) {
                                CardModifierManager.addModifier(card, mod.makeCopy());
                            }
                            this.isDone = true;
                        }
                    });
                    this.amount = 0;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
        applyToTarget(this, this, new CenterOfAttention(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case BLIZZARD: {
                specialAnimation(adp());
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        canBlizzard = false;
                        this.isDone = true;
                    }
                });
                break;
            }
            case FRIGID_GAZE: {
                block(this, BLOCK);
                attack2Animation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case ICE_SPLINTERS: {
                attack1Animation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case FROZEN_THRONE: {
                specialAnimation(adp());
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(this, this, new MetallicizePower(this, METALLICIZE));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        frozenThroneCounter++;
                        this.isDone = true;
                    }
                });
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (canBlizzard) {
            setMoveShortcut(BLIZZARD);
        } else if (frozenThroneCounter < MAX_FROZEN_THRONE_USES){
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(FRIGID_GAZE)) {
                possibilities.add(FRIGID_GAZE);
            }
            if (!this.lastMove(ICE_SPLINTERS)) {
                possibilities.add(ICE_SPLINTERS);
            }
            if (!this.lastMove(FROZEN_THRONE) && !this.lastMoveBefore(FROZEN_THRONE)) {
                possibilities.add(FROZEN_THRONE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(FRIGID_GAZE)) {
                possibilities.add(FRIGID_GAZE);
            }
            if (!this.lastTwoMoves(ICE_SPLINTERS)) {
                possibilities.add(ICE_SPLINTERS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case BLIZZARD: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.WEAK_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, DEBUFF, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case FRIGID_GAZE: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case FROZEN_THRONE: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail2);
                DetailedIntent detail3 = new DetailedIntent(this, METALLICIZE, DetailedIntent.METALLICIZE_TEXTURE);
                detailsList.add(detail3);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(POWER_ID)) {
            if (this.getPower(POWER_ID).amount >= THRESHOLD - 1) {
                this.particleTimer -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer < 0.0F) {
                    this.particleTimer = 0.04F;
                    AbstractDungeon.effectsQueue.add(new FlexibleCalmParticleEffect(this));
                }

                this.particleTimer2 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer2 < 0.0F) {
                    this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                    AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(CalmStance.STANCE_ID, this));
                }
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof PrisonOfIce) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "SnowAttack", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "SnowAttackFar", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "SnowBlizzard", enemy, this);
    }

}