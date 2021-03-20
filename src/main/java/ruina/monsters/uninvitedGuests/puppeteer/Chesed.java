package ruina.monsters.uninvitedGuests.puppeteer;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
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
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Chesed extends AbstractAllyMonster
{
    public static final String ID = RuinaMod.makeID(Chesed.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte BATTLEFIELD_COMMAND = 0;
    private static final byte ENERGY_SHIELD = 1;
    private static final byte CONCENTRATE = 2;
    private static final byte DISPOSAL = 3;

    private static final int STRENGTH = 2;
    private static final int ENERGY_SHIELD_BLOCK = 15;
    private static final int CONCENTRATE_BLOCK = 11;
    
    private static final float MARK_BOOST = 0.50f;
    private static final int MARK_DURATION = 1;

    private static final float DISPOSAL_HP_THRESHOLD = 0.25f;

    public Puppeteer puppeteer;

    public static final String FINISHING_POWER_ID = RuinaMod.makeID("FinishingTouch");
    public static final PowerStrings FINISHINGPowerStrings = CardCrawlGame.languagePack.getPowerStrings(FINISHING_POWER_ID);
    public static final String FINISHING_POWER_NAME = FINISHINGPowerStrings.NAME;
    public static final String[] FINISHING_POWER_DESCRIPTIONS = FINISHINGPowerStrings.DESCRIPTIONS;

    public static final String MARK_POWER_ID = RuinaMod.makeID("Mark");
    public static final PowerStrings MARKPowerStrings = CardCrawlGame.languagePack.getPowerStrings(MARK_POWER_ID);
    public static final String MARK_POWER_NAME = MARKPowerStrings.NAME;
    public static final String[] MARK_POWER_DESCRIPTIONS = MARKPowerStrings.DESCRIPTIONS;

    public static final Texture targetTexture = TexLoader.getTexture(makeUIPath("ChesedIcon.png"));

    public Chesed() {
        this(0.0f, 0.0f);
    }

    public Chesed(final float x, final float y) {
        super(NAME, ID, 160, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Chesed/Spriter/Chesed.scml"));
        this.animation.setFlip(true, false);

        this.setHp(maxHealth);
        this.type = EnemyType.BOSS;

        addMove(BATTLEFIELD_COMMAND, Intent.ATTACK_BUFF, 16);
        addMove(ENERGY_SHIELD, Intent.ATTACK_DEFEND, 6);
        addMove(CONCENTRATE, Intent.ATTACK_DEFEND, 10, 2, true);
        addMove(DISPOSAL, Intent.ATTACK, 16, 2, true);

        this.allyIcon = makeUIPath("ChesedIcon.png");
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Puppeteer) {
                puppeteer = (Puppeteer)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(FINISHING_POWER_NAME, FINISHING_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (info.type == DamageInfo.DamageType.NORMAL && target != owner && target != adp()) {
                    applyToTarget(target, owner, new AbstractLambdaPower(MARK_POWER_NAME, MARK_POWER_ID, PowerType.DEBUFF, false, target, MARK_DURATION) {

                        boolean justApplied = true;

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
                            return damage * (1 + MARK_BOOST);
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
                        public void atEndOfRound() {
                            if (justApplied) {
                                justApplied = false;
                            } else {
                                if (amount == 1) {
                                    makePowerRemovable(this);
                                }
                                atb(new ReducePowerAction(owner, owner, this, 1));
                            }
                        }

                        @Override
                        public void updateDescription() {
                            this.description = MARK_POWER_DESCRIPTIONS[0] + (int) (MARK_BOOST * 100) + MARK_POWER_DESCRIPTIONS[1] + MARK_DURATION + MARK_POWER_DESCRIPTIONS[2];
                        }
                    });
                }
            }

            @Override
            public void atEndOfRound() {
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        checkDisposalCanUse();
                        this.isDone = true;
                    }
                });
            }

            @Override
            public void updateDescription() {
                description = FINISHING_POWER_DESCRIPTIONS[0] + MARK_DURATION + FINISHING_POWER_DESCRIPTIONS[1] + (int) (MARK_BOOST * 100) + FINISHING_POWER_DESCRIPTIONS[2];
            }
        });
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        DamageInfo info;
        int multiplier = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }

        AbstractCreature target;
        if (puppeteer.puppet.isDeadOrEscaped()) {
            target = puppeteer;
        } else {
            target = puppeteer.puppet;
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case BATTLEFIELD_COMMAND: {
                slashAnimation(target);
                dmg(target, info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(adp(), this, new StrengthPower(adp(), STRENGTH));
                resetIdle();
                break;
            }
            case ENERGY_SHIELD: {
                blockAnimation();
                atb(new AllyGainBlockAction(this, this, ENERGY_SHIELD_BLOCK));
                block(adp(), ENERGY_SHIELD_BLOCK);
                waitAnimation();
                pierceAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case CONCENTRATE: {
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
                        resetIdle();
                        disposalFinish(target);
                    }
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if (puppeteer.hasPower(MARK_POWER_ID)) {
                                info.applyPowers(Chesed.this, puppeteer);
                                info.output *= 2;
                            }
                            if (puppeteer.currentHealth <= (int)(puppeteer.maxHealth * DISPOSAL_HP_THRESHOLD)) {
                                info.applyPowers(Chesed.this, puppeteer);
                                info.output *= 2;
                            }
                            this.isDone = true;
                        }
                    });
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(BATTLEFIELD_COMMAND) && !this.lastMoveBefore(BATTLEFIELD_COMMAND)) {
            possibilities.add(BATTLEFIELD_COMMAND);
        }
        if (!this.lastMove(ENERGY_SHIELD) && !this.lastMoveBefore(ENERGY_SHIELD)) {
            possibilities.add(ENERGY_SHIELD);
        }
        if (!this.lastMove(CONCENTRATE) && !this.lastMoveBefore(CONCENTRATE)) {
            possibilities.add(CONCENTRATE);
        }
        if (possibilities.isEmpty()) {
            possibilities.add(CONCENTRATE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    public void checkDisposalCanUse() {
        if (puppeteer.currentHealth <= (int)(puppeteer.maxHealth * DISPOSAL_HP_THRESHOLD)) {
            if (puppeteer.puppet.isDeadOrEscaped()) {
                if (puppeteer.hasPower(MARK_POWER_ID)) {
                    atb(new TalkAction(this, DIALOG[2]));
                    setMoveShortcut(DISPOSAL, MOVES[DISPOSAL]);
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
        applyPowers(target);
    }

    public void onBossDeath() {
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

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "SwordHori", enemy, this);
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
    }

}