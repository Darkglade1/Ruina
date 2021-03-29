package ruina.monsters.uninvitedGuests.elena;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.uninvitedGuests.puppeteer.Chesed;
import ruina.monsters.uninvitedGuests.puppeteer.Puppeteer;
import ruina.monsters.uninvitedGuests.puppeteer.chesedCards.BattleCommand;
import ruina.monsters.uninvitedGuests.puppeteer.chesedCards.Concentration;
import ruina.monsters.uninvitedGuests.puppeteer.chesedCards.Disposal;
import ruina.monsters.uninvitedGuests.puppeteer.chesedCards.EnergyShield;
import ruina.powers.AbstractLambdaPower;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;
import java.util.Iterator;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Binah extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Binah.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte DEGRADED_PILLAR = 0;
    private static final byte DEGRADED_CHAIN = 1;
    private static final byte DEGRADED_FAIRY = 2;

    public final int WEAK = 1;
    public final int BLOCK = 14;
    public final int fairyHits = 2;

    public Elena elena;
    private AbstractCreature targetEnemy;

    public static final String POWER_ID = makeID("Arbiter");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Binah() {
        this(0.0f, 0.0f);
    }

    public Binah(final float x, final float y) {
        super(NAME, ID, 180, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Chesed/Spriter/Chesed.scml"));
        this.animation.setFlip(true, false);

        this.setHp(maxHealth);
        this.type = EnemyType.BOSS;

        addMove(DEGRADED_PILLAR, Intent.ATTACK_DEFEND, 24);
        addMove(DEGRADED_CHAIN, Intent.ATTACK_DEBUFF, 21);
        addMove(DEGRADED_FAIRY, Intent.ATTACK, 14, fairyHits, true);

//        cardList.add(new BattleCommand(this));
//        cardList.add(new EnergyShield(this));
//        cardList.add(new Concentration(this));
//        cardList.add(new Disposal(this));

        this.allyIcon = makeUIPath("BinahIcon.png");
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Elena) {
                elena = (Elena)mo;
                targetEnemy = elena;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                AbstractCreature enemy = target;
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (enemy instanceof AbstractMultiIntentMonster) {
                            ((AbstractMultiIntentMonster) enemy).additionalIntents.clear();
                            ((AbstractMultiIntentMonster) enemy).additionalMoves.clear();
                        }
                        this.isDone = true;
                    }
                });
            }

            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                flashWithoutSound();
                targetEnemy = action.target;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }

        DamageInfo info;
        int multiplier = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }

        AbstractCreature target = targetEnemy;
        if (target == null || (target != elena && target != elena.vermilionCross)) {
            target = elena;
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case DEGRADED_PILLAR: {
                slashAnimation(target);
                dmg(target, info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(adp(), this, new StrengthPower(adp(), STRENGTH));
                resetIdle();
                break;
            }
            case DEGRADED_CHAIN: {
                blockAnimation();
                atb(new AllyGainBlockAction(this, this, ENERGY_SHIELD_BLOCK));
                block(adp(), ENERGY_SHIELD_BLOCK);
                waitAnimation();
                pierceAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case DEGRADED_FAIRY: {
                atb(new AllyGainBlockAction(this, this, CONCENTRATE_BLOCK));
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case DISPOSAL: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        disposalUp(target);
                        waitAnimation();
                        disposalDown(target);
                    } else {
                        moveAnimation(target);
                        waitAnimation();
                        disposalFinish(target);
                    }
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            info.applyPowers(Binah.this, puppeteer);
                            if (puppeteer.hasPower(MARK_POWER_ID)) {
                                info.output *= 2;
                            }
                            if (puppeteer.currentHealth <= (int)(puppeteer.maxHealth * DISPOSAL_HP_THRESHOLD)) {
                                info.output *= 2;
                            }
                            this.isDone = true;
                        }
                    });
                    dmg(target, info);
                    resetIdle(1.0f);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(DEGRADED_PILLAR) && !this.lastMoveBefore(DEGRADED_PILLAR)) {
            possibilities.add(DEGRADED_PILLAR);
        }
        if (!this.lastMove(DEGRADED_CHAIN) && !this.lastMoveBefore(DEGRADED_CHAIN)) {
            possibilities.add(DEGRADED_CHAIN);
        }
        if (!this.lastMove(DEGRADED_FAIRY) && !this.lastMoveBefore(DEGRADED_FAIRY)) {
            possibilities.add(DEGRADED_FAIRY);
        }
        if (possibilities.isEmpty()) {
            possibilities.add(DEGRADED_FAIRY);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move], cardList.get(move));
    }

    public void checkDisposalCanUse() {
        if (puppeteer.currentHealth <= (int)(puppeteer.maxHealth * DISPOSAL_HP_THRESHOLD)) {
            if (puppeteer.puppet.isDeadOrEscaped()) {
                if (puppeteer.hasPower(MARK_POWER_ID)) {
                    att(new TalkAction(this, DIALOG[2]));
                    setMoveShortcut(DISPOSAL, MOVES[DISPOSAL], cardList.get(DISPOSAL));
                    createIntent();
                }
            }
        }
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1 || puppeteer.isDeadOrEscaped()) {
            super.applyPowers();
            return;
        }
        AbstractCreature target;
        if (puppeteer.puppet.isDeadOrEscaped()) {
            target = puppeteer;
        } else {
            target = puppeteer.puppet;
        }
        if (nextMove == DISPOSAL) {
            float multiplier = 1;
            if (puppeteer.hasPower(MARK_POWER_ID)) {
                multiplier *= 2;
            }
            if (puppeteer.currentHealth <= (int)(puppeteer.maxHealth * DISPOSAL_HP_THRESHOLD)) {
                multiplier *= 2;
            }
            applyPowers(target, multiplier);
        } else {
            applyPowers(target);
        }
    }

    public void onBossDeath() {
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[1]));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    disappear();
                    this.isDone = true;
                }
            });
        }
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "SwordVert", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "SwordStab", enemy, this);
    }

    private void disposalUp(AbstractCreature enemy) {
        animationAction("Special3", "DisposalUp", enemy, this);
    }

    private void disposalDown(AbstractCreature enemy) {
        animationAction("Special4", "DisposalDown", enemy, this);
    }

    private void disposalFinish(AbstractCreature enemy) {
        animationAction("Special2", "DisposalFinish", enemy, this);
        animationAction("Special2", "DisposalBlood", enemy, this);
    }

    private void moveAnimation(AbstractCreature enemy) {
        animationAction("Move", null, enemy, this);
    }

}