package ruina.monsters.uninvitedGuests.normal.puppeteer;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.puppeteer.puppeteerCards.*;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;

import java.util.ArrayList;
import java.util.function.BiFunction;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Puppeteer extends AbstractCardMonster
{
    public static final String ID = makeID(Puppeteer.class.getSimpleName());

    public static final String RED_LINE = RuinaMod.makeMonsterPath("Puppeteer/Line.png");
    private static final Texture RED_LINE_TEXTURE = new Texture(RED_LINE);

    private static final byte PULLING_STRINGS_TAUT = 0;
    private static final byte TUGGING_STRINGS = 1;
    private static final byte ASSAILING_PULLS = 2;
    private static final byte THIN_STRINGS = 3;
    private static final byte PUPPETRY = 4;

    public final int tuggingStringsHits = 2;

    private final int MASTERMIND_DAMAGE_REDUCTION = calcAscensionSpecial(50);
    private static final int MASS_ATTACK_COOLDOWN = 3;
    private int massAttackCooldown = MASS_ATTACK_COOLDOWN;

    public final int BLOCK = calcAscensionTankiness(20);
    public final int STRENGTH = calcAscensionSpecial(3);
    public final int DEBUFF = calcAscensionSpecial(1);
    public final int VULNERABLE = 1;
    public Puppet puppet;

    public static final String POWER_ID = makeID("Mastermind");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Puppeteer() {
        this(0.0f, 0.0f);
    }

    public Puppeteer(final float x, final float y) {
        super(ID, ID, 800, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Puppeteer/Spriter/Puppeteer.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(800));

        addMove(PULLING_STRINGS_TAUT, IntentEnums.MASS_ATTACK, calcAscensionDamage(38));
        addMove(TUGGING_STRINGS, Intent.ATTACK, calcAscensionDamage(12), tuggingStringsHits);
        addMove(ASSAILING_PULLS, Intent.ATTACK_DEBUFF, calcAscensionDamage(16));
        addMove(THIN_STRINGS, Intent.DEFEND_DEBUFF);
        addMove(PUPPETRY, Intent.BUFF);

        cardList.add(new PullingStrings(this));
        cardList.add(new TuggingStrings(this));
        cardList.add(new AssailingPulls(this));
        cardList.add(new ThinStrings(this));
        cardList.add(new Puppetry(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble2");
        AbstractDungeon.getCurrRoom().cannotLose = true;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Chesed) {
                target = (Chesed)mo;
            }
        }
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Puppet) {
                puppet = (Puppet)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public float atDamageReceive(float damage, DamageInfo.DamageType type) {
                //handles attack damage
                if (type == DamageInfo.DamageType.NORMAL) {
                    return calculateDamageTakenAmount(damage, type);
                } else {
                    return damage;
                }
            }

            private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
                if (owner.hasPower(Chesed.MARK_POWER_ID)) {
                    return damage;
                } else {
                    return damage * (1 - ((float)MASTERMIND_DAMAGE_REDUCTION / 100));
                }
            }

            @Override
            public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
                //handles non-attack damage
                if (info.type != DamageInfo.DamageType.NORMAL) {
                    return (int) calculateDamageTakenAmount(damageAmount, info.type);
                } else {
                    return damageAmount;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + MASTERMIND_DAMAGE_REDUCTION + POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case PULLING_STRINGS_TAUT: {
                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }

                massAttackStartAnimation();
                massAttackEffect();
                massAttackFinishAnimation();
                atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        massAttackCooldown = MASS_ATTACK_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case TUGGING_STRINGS: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        bluntAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case ASSAILING_PULLS: {
                rangedAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new VulnerablePower(target, VULNERABLE, true));
                resetIdle();
                //force puppet to attack this target next turn
                if (target == this.target) {
                    puppet.attackingAlly = true;
                } else {
                    puppet.attackingAlly = false;
                }
                AbstractDungeon.onModifyPower();
                break;
            }
            case THIN_STRINGS: {
                blockAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                        block(mo, BLOCK);
                    }
                }
                applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                applyToTarget(target, this, new FrailPower(target, DEBUFF, true));
                resetIdle(1.0f);
                break;
            }
            case PUPPETRY: {
                buffAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                        applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                    }
                }
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BluntHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "BluntBlow", enemy, this);
    }

    private void rangedAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "PuppetBreak", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    private void buffAnimation() {
        animationAction("Special", null, this);
    }

    private void massAttackStartAnimation() {
        animationAction("Special", "PuppetStart", this);
    }

    private void massAttackFinishAnimation() {
        animationAction("Special", "PuppetStrongAtk", this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                massAttackCooldown--;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (massAttackCooldown <= 0) {
            setMoveShortcut(PULLING_STRINGS_TAUT, MOVES[PULLING_STRINGS_TAUT], cardList.get(PULLING_STRINGS_TAUT).makeStatEquivalentCopy());
        } else if (lastMove(ASSAILING_PULLS)) {
            setMoveShortcut(TUGGING_STRINGS, MOVES[TUGGING_STRINGS], cardList.get(TUGGING_STRINGS).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(TUGGING_STRINGS)) {
                possibilities.add(TUGGING_STRINGS);
            }
            if (!this.lastMove(ASSAILING_PULLS) && target != null && !target.isDead && !target.isDying && massAttackCooldown != 1) {
                possibilities.add(ASSAILING_PULLS);
            }
            if (!this.lastMove(THIN_STRINGS)) {
                possibilities.add(THIN_STRINGS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (this.lastMove(ASSAILING_PULLS, moveHistory)) {
            setAdditionalMoveShortcut(TUGGING_STRINGS, moveHistory, cardList.get(TUGGING_STRINGS).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(TUGGING_STRINGS, moveHistory)) {
                possibilities.add(TUGGING_STRINGS);
            }
            if (!this.lastMove(ASSAILING_PULLS, moveHistory) && this.nextMove != ASSAILING_PULLS && massAttackCooldown != 1) {
                possibilities.add(ASSAILING_PULLS);
            }
            if (!this.lastMove(PUPPETRY, moveHistory)) {
                possibilities.add(PUPPETRY);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        AbstractDungeon.getCurrRoom().cannotLose = false;
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Puppet) {
                atb(new SuicideAction(mo));
            }
        }
        if (target instanceof Chesed) {
            ((Chesed) target).onBossDeath();
        }
    }

    private void massAttackEffect() {
        float chargeDuration = 1.5f;
        float scale = 1.5f;
        float startY = 0.0f;
        int numStrings = 20;

        VfxBuilder effect = new VfxBuilder(RED_LINE_TEXTURE, 0.0f, -9999, 0.0f)
                .scale(0.0f, scale, VfxBuilder.Interpolations.LINEAR)
                .setY(startY)
                .setAngle((float)AbstractDungeon.monsterRng.random(45, 135));
        for (int i = 1; i < numStrings; i++) {
            int finalI = i;
            effect.triggerVfxAt(0.0f, 1,new BiFunction<Float, Float, AbstractGameEffect>() {
                @Override
                public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                    return new VfxBuilder(RED_LINE_TEXTURE, (float) Settings.WIDTH / numStrings * finalI, -9999, chargeDuration)
                            .scale(0.0f, scale, VfxBuilder.Interpolations.LINEAR)
                            .setY(startY)
                            .setAngle((float)AbstractDungeon.monsterRng.random(45, 135)).build();
                }
            });
        }
        AbstractGameEffect finalEffect = effect.build();
        atb(new VFXAction(finalEffect, chargeDuration));
    }

}