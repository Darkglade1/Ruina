package ruina.monsters.act3;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Paralysis;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class JudgementBird extends AbstractRuinaMonster
{
    public static final String ID = makeID(JudgementBird.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte STARE = 0;
    private static final byte JUDGEMENT = 1;
    private static final byte HEAVY_GUILT = 2;
    private static final byte CEASELESS_DUTY = 3;

    private static final int COOLDOWN = 2;
    private int cooldownCounter = COOLDOWN;

    private final int DEBUFF = calcAscensionSpecial(1);
    private final int STRENGTH = calcAscensionSpecial(3);
    private final int BLOCK = calcAscensionTankiness(8);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int COST_THRESHOLD = calcAscensionSpecial(1);
    private final int COST_INCREASE = 1;

    public static final String POWER_ID = makeID("Sin");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public JudgementBird() {
        this(0.0f, 0.0f);
    }

    public JudgementBird(final float x, final float y) {
        super(NAME, ID, 250, 0.0F, 0, 280.0f, 360.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("JudgementBird/Spriter/JudgementBird.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(STARE, Intent.STRONG_DEBUFF);
        addMove(JUDGEMENT, Intent.ATTACK, calcAscensionDamage(20));
        addMove(HEAVY_GUILT, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
        addMove(CEASELESS_DUTY, Intent.DEFEND_BUFF);
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, COST_THRESHOLD) {
            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                if (card.costForTurn <= amount) {
                    this.flash();
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            card.modifyCostForCombat(COST_INCREASE);
                            this.isDone = true;
                        }
                    });

                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + COST_INCREASE + POWER_DESCRIPTIONS[2];
            }
        });
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case STARE: {
                specialAnimation(adp());
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
                resetIdle();
                break;
            }
            case JUDGEMENT: {
                judgementAnimation(adp());
                atb(new VFXAction(new WaitEffect(), 0.25f));
                judgementVfx();
                dmg(adp(), info);
                resetIdle();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        cooldownCounter = COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case HEAVY_GUILT: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                resetIdle();
                break;
            }
            case CEASELESS_DUTY: {
                specialAnimation(adp());
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                cooldownCounter--;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(STARE)) {
            setMoveShortcut(JUDGEMENT, MOVES[JUDGEMENT]);
        } else if (cooldownCounter <= 0) {
            setMoveShortcut(STARE, MOVES[STARE]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(HEAVY_GUILT)) {
                possibilities.add(HEAVY_GUILT);
            }
            if (!this.lastMove(CEASELESS_DUTY)) {
                possibilities.add(CEASELESS_DUTY);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "JudgementAttack", enemy, this);
    }

    private void judgementAnimation(AbstractCreature enemy) {
        animationAction("Judgement", "JudgementDing", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "JudgementGong", enemy, this);
    }

    public static void judgementVfx() {
        ArrayList<Texture> frames = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            frames.add(TexLoader.getTexture(makeMonsterPath("JudgementBird/Hang/Hang" + i + ".png")));
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("JudgementHang");
                this.isDone = true;
            }
        });
        fullScreenAnimation(frames, 0.1f, 1.4f);
    }

}