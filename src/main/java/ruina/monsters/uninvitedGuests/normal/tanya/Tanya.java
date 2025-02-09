package ruina.monsters.uninvitedGuests.normal.tanya;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.tanya.tanyaCards.*;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.RuinaMetallicize;
import ruina.powers.RuinaPlatedArmor;
import ruina.powers.act4.Guts;
import ruina.util.TexLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Tanya extends AbstractCardMonster
{
    public static final String ID = makeID(Tanya.class.getSimpleName());

    private static final byte OVERSPEED = 0;
    private static final byte BEATDOWN = 1;
    private static final byte INTIMIDATE = 2;
    private static final byte LUPINE_ASSAULT = 3;
    private static final byte KICKS_AND_STOMPS = 4;
    private static final byte FISTICUFFS = 5;

    public final int overspeedHits = 2;
    public final int kicksAndStompsHits = 2;


    public final int BLOCK = calcAscensionTankiness(16);
    public final int INITIAL_PLATED_ARMOR = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(20));
    public final int PLATED_ARMOR_GAIN = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(15));
    public final int METALLICIZE_GAIN = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionSpecial(5));
    public final int STRENGTH = calcAscensionSpecial(2);
    public final int WEAK = calcAscensionSpecial(1);
    public final int GUTS_METALLICIZE_GAIN = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionSpecial(8));
    public final int GUTS_STRENGTH = calcAscensionSpecial(3);
    private boolean usingMassAttack = false;
    public static final int POST_GUTS_PHASE = 2;

    public Tanya() {
        this(0.0f, 0.0f);
    }

    public Tanya(final float x, final float y) {
        super(ID, ID, 450, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Tanya/Spriter/Tanya.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(450));

        addMove(OVERSPEED, Intent.ATTACK_BUFF, calcAscensionDamage(30), overspeedHits);
        addMove(BEATDOWN, IntentEnums.MASS_ATTACK, calcAscensionDamage(28));
        addMove(INTIMIDATE, Intent.DEFEND_BUFF);
        addMove(LUPINE_ASSAULT, Intent.ATTACK_DEFEND, calcAscensionDamage(22));
        addMove(KICKS_AND_STOMPS, Intent.ATTACK_BUFF, calcAscensionDamage(12), kicksAndStompsHits);
        addMove(FISTICUFFS, Intent.ATTACK_DEBUFF, calcAscensionDamage(26));

        cardList.add(new Overspeed(this));
        cardList.add(new Beatdown(this));
        cardList.add(new Intimidate(this));
        cardList.add(new LupineAssault(this));
        cardList.add(new KicksAndStomps(this));
        cardList.add(new Fisticuffs(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        if (phase == POST_GUTS_PHASE) {
            AbstractDungeon.getCurrRoom().cannotLose = true;
        } else {
            AbstractDungeon.getCurrRoom().cannotLose = true;
        }
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Gebura) {
                target = (Gebura)mo;
            }
        }
        if (target != null && target.phase == Gebura.EGO_PHASE) {
            CustomDungeon.playTempMusicInstantly("RedMistBGM");
        } else {
            CustomDungeon.playTempMusicInstantly("Ensemble2");
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new Guts(this, GUTS_STRENGTH, GUTS_METALLICIZE_GAIN));
        applyToTarget(this, this, new RuinaPlatedArmor(this, INITIAL_PLATED_ARMOR));
        block(this, INITIAL_PLATED_ARMOR);
        if (AbstractDungeon.ascensionLevel >= 19) {
            applyToTarget(this, this, new BarricadePower(this));
        } else {
            applyToTarget(this, this, new InvisibleBarricadePower(this));
        }
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case OVERSPEED: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        bluntAnimation(target);
                    } else {
                        pierceAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(this, this, new RuinaMetallicize(this, METALLICIZE_GAIN));
                break;
            }
            case BEATDOWN: {
                massAttackStartAnimation();
                waitAnimation(0.25f);
                massAttackStart();
                massAttackFinish(info);
                resetIdle();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        usingMassAttack = false;
                        this.isDone = true;
                    }
                });
                waitAnimation(0.5f);
                applyToTarget(this, this, new StrengthPower(this, GUTS_STRENGTH));
                break;
            }
            case INTIMIDATE: {
                specialAnimation();
                AbstractPower platedArmor = getPower(PlatedArmorPower.POWER_ID);
                if (platedArmor != null) {
                    int amount = platedArmor.amount;
                    atb(new RemoveSpecificPowerAction(this, this, platedArmor));
                    applyToTarget(this, this, new RuinaMetallicize(this, amount));
                }
                applyToTarget(this, this, new RuinaPlatedArmor(this, PLATED_ARMOR_GAIN));
                resetIdle();
                break;
            }
            case LUPINE_ASSAULT: {
                slashAnimation(target);
                block(this, BLOCK);
                dmg(target, info);
                resetIdle();
                break;
            }
            case KICKS_AND_STOMPS: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        bluntAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                break;
            }
            case FISTICUFFS: {
                pierceAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, WEAK, true));
                resetIdle();
                break;
            }
        }
    }

    private void doMassAttack(DamageInfo info) {
        att(new DamageAllOtherCharactersAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
    }

    private void massAttackStart() {
        String start = RuinaMod.makeMonsterPath("Tanya/Spriter/Special 1.png");
        Texture texture = TexLoader.getTexture(start);
        float duration = 0.8f;
        float y = (float) Settings.HEIGHT + (375.0F * Settings.scale);
        AbstractGameEffect effect = new VfxBuilder(texture, this.hb.cX, this.hb.cY, duration)
                .arc(this.hb.cX, this.hb.cY, (float) Settings.WIDTH / 2, y, y)
                .build();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                usingMassAttack = true;
                this.isDone = true;
            }
        });
        atb(new VFXAction(effect, duration));
    }

    private void massAttackFinish(DamageInfo info) {
        String drop = RuinaMod.makeMonsterPath("Tanya/Spriter/Special 2.png");
        Texture texture = TexLoader.getTexture(drop);
        String finish = RuinaMod.makeMonsterPath("Tanya/Spriter/Special 3.png");
        Texture finishTexture = TexLoader.getTexture(finish);
        float duration = 0.8f;
        float x = (adp().hb.cX + target.hb.cX) / 2;
        float y = (float) Settings.HEIGHT + (375.0F * Settings.scale);
        AbstractGameEffect effect = new VfxBuilder(texture, (float) Settings.WIDTH / 2, y, duration)
                .arc((float) Settings.WIDTH / 2, y, x, adp().hb.cY, y)
                .triggerVfxAt(duration, 1, new BiFunction<Float, Float, AbstractGameEffect>() {
                    @Override
                    public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                        Consumer<VfxBuilder> vfxBuilderConsumer = new Consumer<VfxBuilder>() {
                            @Override
                            public void accept(VfxBuilder vfxBuilder) {
                                doMassAttack(info);
                            }
                        };
                        return new VfxBuilder(finishTexture, x, adp().hb.cY, 1.0f)
                                .playSoundAt(0.0f, makeID("GreedSlam"))
                                .whenStarted(vfxBuilderConsumer).build();
                    }
                })
                .build();
        atb(new VFXAction(effect, duration));
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BluntHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "BluntBlow", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "BluntVert", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", null, this);
    }

    private void massAttackStartAnimation() {
        animationAction("MassStart", null, this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (canUseOverspeed()) {
            setMoveShortcut(OVERSPEED);
        } else if (moveHistory.size() >= 2 && !this.lastMoveIgnoringMove(BEATDOWN, OVERSPEED) && !this.lastMoveBeforeIgnoringMove(BEATDOWN, OVERSPEED)) {
            setMoveShortcut(BEATDOWN);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(LUPINE_ASSAULT)) {
                possibilities.add(LUPINE_ASSAULT);
            }
            if (!this.lastMove(FISTICUFFS)) {
                possibilities.add(FISTICUFFS);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (canUseOverspeed()) {
            setAdditionalMoveShortcut(OVERSPEED, moveHistory);
        } else if (this.lastMove(BEATDOWN)) {
            setAdditionalMoveShortcut(INTIMIDATE, moveHistory);
        } else {
            if (!this.lastMove(FISTICUFFS, moveHistory)) {
                possibilities.add(FISTICUFFS);
            }
            if (!this.lastMove(KICKS_AND_STOMPS, moveHistory)) {
                possibilities.add(KICKS_AND_STOMPS);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory);
        }
    }

    private boolean canUseOverspeed() {
        Gebura gebura = null;
        if (target instanceof Gebura) {
            gebura = (Gebura) target;
        }
        return gebura != null && !gebura.isDead && !gebura.isDying && (gebura.moveHistory.get(gebura.moveHistory.size() - 1) == Gebura.GSH || gebura.moveHistory.get(gebura.moveHistory.size() - 1) == Gebura.GSV);
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            Iterator var2 = this.powers.iterator();

            while (var2.hasNext()) {
                AbstractPower p = (AbstractPower) var2.next();
                p.onDeath();
            }

            var2 = AbstractDungeon.player.relics.iterator();

            while (var2.hasNext()) {
                AbstractRelic r = (AbstractRelic) var2.next();
                r.onMonsterDeath(this);
            }

            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power instanceof PlatedArmorPower) && !(power instanceof MetallicizePower) && !(power instanceof StrengthPower) && !(power instanceof GainStrengthPower) && !(power instanceof InvisibleBarricadePower) && !(power instanceof BarricadePower) && !(power instanceof StunMonsterPower)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }

            att(new AbstractGameAction() {
                @Override
                public void update() {
                    halfDead = false;
                    AbstractDungeon.getCurrRoom().cannotLose = false;
                    setPhase(POST_GUTS_PHASE);
                    this.isDone = true;
                }
            });
            att(new HealAction(this, this, maxHealth));

            applyToTarget(this, this, new StrengthPower(this, GUTS_STRENGTH));
            applyToTarget(this, this, new RuinaMetallicize(this, METALLICIZE_GAIN));
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die(triggerRelics);
            if (target instanceof Gebura) {
                ((Gebura) target).onBossDeath();
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!usingMassAttack) {
            super.render(sb);
        }
    }

}