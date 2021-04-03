package ruina.monsters.uninvitedGuests.tanya;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.tanya.tanyaCards.Beatdown;
import ruina.monsters.uninvitedGuests.tanya.tanyaCards.Fisticuffs;
import ruina.monsters.uninvitedGuests.tanya.tanyaCards.Intimidate;
import ruina.monsters.uninvitedGuests.tanya.tanyaCards.KicksAndStomps;
import ruina.monsters.uninvitedGuests.tanya.tanyaCards.LupineAssault;
import ruina.monsters.uninvitedGuests.tanya.tanyaCards.Overspeed;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;

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
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte OVERSPEED = 0;
    private static final byte BEATDOWN = 1;
    private static final byte INTIMIDATE = 2;
    private static final byte LUPINE_ASSAULT = 3;
    private static final byte KICKS_AND_STOMPS = 4;
    private static final byte FISTICUFFS = 5;

    public final int overspeedHits = 3;
    public final int kicksAndStompsHits = 2;

    private static final int MASS_ATTACK_COOLDOWN = 3; //cooldown resets to 3
    private int massAttackCooldown = 2; //start cooldown at 2

    public final int BLOCK = calcAscensionTankiness(16);
    public final int INITIAL_PLATED_ARMOR = calcAscensionTankiness(20);
    public final int PLATED_ARMOR_GAIN = calcAscensionTankiness(15);
    public final int METALLICIZE_GAIN = calcAscensionSpecial(5);
    public final int STRENGTH = calcAscensionSpecial(2);
    public final int WEAK = calcAscensionSpecial(1);
    public final int GUTS_METALLICIZE_GAIN = calcAscensionSpecial(5);
    public final int GUTS_STRENGTH = calcAscensionSpecial(2);
    public Gebura gebura;
    private boolean usingMassAttack = false;

    public static final String POWER_ID = makeID("Guts");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Tanya() {
        this(0.0f, 0.0f);
    }

    public Tanya(final float x, final float y) {
        super(NAME, ID, 500, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Tanya/Spriter/Tanya.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(OVERSPEED, Intent.ATTACK, calcAscensionDamage(20), overspeedHits, true);
        addMove(BEATDOWN, IntentEnums.MASS_ATTACK, calcAscensionDamage(28));
        addMove(INTIMIDATE, Intent.DEFEND_BUFF);
        addMove(LUPINE_ASSAULT, Intent.ATTACK_DEFEND, calcAscensionDamage(22));
        addMove(KICKS_AND_STOMPS, Intent.ATTACK_BUFF, calcAscensionDamage(12), kicksAndStompsHits, true);
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
        CustomDungeon.playTempMusicInstantly("Ensemble2");
        AbstractDungeon.getCurrRoom().cannotLose = true;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Gebura) {
                gebura = (Gebura)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + GUTS_STRENGTH + POWER_DESCRIPTIONS[1] + GUTS_METALLICIZE_GAIN + POWER_DESCRIPTIONS[2];
            }
        });
        applyToTarget(this, this, new PlatedArmorPower(this, INITIAL_PLATED_ARMOR));
        block(this, INITIAL_PLATED_ARMOR);
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
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
                applyToTarget(this, this, new MetallicizePower(this, METALLICIZE_GAIN));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        massAttackCooldown = MASS_ATTACK_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case INTIMIDATE: {
                specialAnimation();
                AbstractPower platedArmor = getPower(PlatedArmorPower.POWER_ID);
                if (platedArmor != null) {
                    int amount = platedArmor.amount;
                    atb(new RemoveSpecificPowerAction(this, this, platedArmor));
                    applyToTarget(this, this, new MetallicizePower(this, amount));
                }
                applyToTarget(this, this, new PlatedArmorPower(this, PLATED_ARMOR_GAIN));
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
        int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
        info.applyPowers(this, adp());
        damageArray[damageArray.length - 1] = info.output;
        for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
            AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
            info.applyPowers(this, mo);
            damageArray[i] = info.output;
        }
        att(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
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
        float x = (adp().hb.cX + gebura.hb.cX) / 2;
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
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new RemoveAllBlockAction(this, this));
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, gebura);
            }
        }
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
        if (gebura != null && !gebura.isDead && !gebura.isDying && gebura.greaterSplitCooldownCounter <= 0) {
            setMoveShortcut(OVERSPEED, MOVES[OVERSPEED], cardList.get(OVERSPEED).makeStatEquivalentCopy());
        } else if (massAttackCooldown <= 0) {
            setMoveShortcut(BEATDOWN, MOVES[BEATDOWN], cardList.get(BEATDOWN).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(LUPINE_ASSAULT)) {
                possibilities.add(LUPINE_ASSAULT);
            }
            if (!this.lastMove(FISTICUFFS)) {
                possibilities.add(FISTICUFFS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (gebura != null && !gebura.isDead && !gebura.isDying && gebura.greaterSplitCooldownCounter <= 0) {
            setAdditionalMoveShortcut(OVERSPEED, moveHistory, cardList.get(OVERSPEED).makeStatEquivalentCopy());
        } else if (massAttackCooldown <= 0) {
            setAdditionalMoveShortcut(INTIMIDATE, moveHistory, cardList.get(INTIMIDATE).makeStatEquivalentCopy());
        } else {
            if (!this.lastMove(LUPINE_ASSAULT, moveHistory)) {
                possibilities.add(LUPINE_ASSAULT);
            }
            if (!this.lastMove(FISTICUFFS, moveHistory)) {
                possibilities.add(FISTICUFFS);
            }
            if (!this.lastMove(KICKS_AND_STOMPS, moveHistory) && !this.lastMoveBefore(KICKS_AND_STOMPS)) {
                possibilities.add(KICKS_AND_STOMPS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, gebura, gebura.allyIcon);
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        int previousHealth = currentHealth;
        super.damage(info);
        int nextHealth = currentHealth;
        int difference = previousHealth - nextHealth;
        if (difference > 0) {
            AbstractPower relentless = gebura.getPower(Gebura.R_POWER_ID);
            if (relentless != null) {
                relentless.amount -= difference;
                if (relentless.amount < 0) {
                    relentless.amount = 0;
                }
                relentless.updateDescription();
            }
        }
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
                if (!(power instanceof PlatedArmorPower) && !(power instanceof MetallicizePower) && !(power instanceof StrengthPower) && !(power instanceof GainStrengthPower) && !(power instanceof InvisibleBarricadePower)) {
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
                    this.isDone = true;
                }
            });
            att(new HealAction(this, this, maxHealth));

            applyToTarget(this, this, new StrengthPower(this, GUTS_STRENGTH));
            applyToTarget(this, this, new MetallicizePower(this, METALLICIZE_GAIN));
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die(triggerRelics);
            gebura.onBossDeath();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!usingMassAttack) {
            super.render(sb);
        }
    }

}