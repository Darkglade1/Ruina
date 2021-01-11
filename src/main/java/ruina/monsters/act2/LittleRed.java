package ruina.monsters.act2;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.util.Wiz;

import static ruina.RuinaMod.makeMonsterPath;

public class LittleRed extends AbstractRuinaMonster
{
    public static final String ID = RuinaMod.makeID(LittleRed.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte BEAST_HUNT = 0;
    private static final byte CATCH_BREATH = 1;
    private static final byte HOLLOW_POINT_SHELL = 2;
    private static final byte BULLET_SHOWER = 3;

    public static final String POWER_ID = RuinaMod.makeID("StrikeWithoutHesitation");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public LittleRed() {
        this(0.0f, 0.0f);
    }

    public LittleRed(final float x, final float y) {
        super(NAME, ID, 150, -5.0F, 0, 230.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("LittleRed/Spriter/LittleRed.scml"));
        this.type = EnemyType.BOSS;

        this.setHp(calcAscensionTankiness(this.maxHealth));

        addMove(BEAST_HUNT, Intent.ATTACK, calcAscensionDamage(8));
        addMove(CATCH_BREATH, Intent.BUFF, calcAscensionTankiness(10), 3);
        addMove(HOLLOW_POINT_SHELL, Intent.ATTACK, calcAscensionDamage(7), 2, true);
        addMove(BULLET_SHOWER, Intent.ATTACK, calcAscensionDamage(8), 3, true);
    }

    @Override
    public void usePreBattleAction() {
        //CustomDungeon.playTempMusicInstantly("MasterSpark");
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }
        DamageInfo info;
        int secondMagicNum = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            secondMagicNum = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }
        if(info.base > -1) {
            info.applyPowers(this, AbstractDungeon.player);
        }
        switch (this.nextMove) {
            case BEAST_HUNT: {
                //runAnim("Spark");
                Wiz.dmg(Wiz.adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            }
            case CATCH_BREATH: {
                //runAnim("Spark");
                Wiz.atb(new AddTemporaryHPAction(this, this, getIntentBaseDmg()));
                Wiz.applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, AbstractPower.PowerType.BUFF, false, this, secondMagicNum) {

                    boolean justApplied = true;

                    @Override
                    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
                        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL && target == AbstractDungeon.player) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount), amount));
                        }
                    }

                    @Override
                    public void atEndOfRound() {
                        if (justApplied) {
                            justApplied = false;
                        } else {
                            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
                        }
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
                    }
                });
                break;
            }
            case HOLLOW_POINT_SHELL: {
                //runAnim("Smack");
                for (int i = 0; i < secondMagicNum; i++) {
                    Wiz.dmg(Wiz.adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                break;
            }
            case BULLET_SHOWER: {
                //runAnim("Special");
                for (int i = 0; i < secondMagicNum; i++) {
                    Wiz.dmg(Wiz.adp(), info, AbstractGameAction.AttackEffect.FIRE);
                }
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(HOLLOW_POINT_SHELL)) {
            setMoveShortcut(CATCH_BREATH, MOVES[CATCH_BREATH]);
        } else if (this.lastMove(CATCH_BREATH)) {
            setMoveShortcut(BEAST_HUNT, MOVES[BEAST_HUNT]);
        } else {
            setMoveShortcut(HOLLOW_POINT_SHELL, MOVES[HOLLOW_POINT_SHELL]);
        }
    }

}