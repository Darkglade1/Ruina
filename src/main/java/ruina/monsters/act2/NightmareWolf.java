package ruina.monsters.act2;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ClawEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class NightmareWolf extends AbstractMultiIntentMonster
{
    public static final String ID = RuinaMod.makeID(NightmareWolf.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(RuinaMod.makeID("AllyStrings"));
    private static final String[] TEXT = uiStrings.TEXT;

    private static final byte CRUEL_CLAWS = 0;
    private static final byte FEROCIOUS_FANGS = 1;
    private static final byte BLOODSTAINED_HUNT = 2;
    private static final byte HOWL = 3;

    private final int BLOCK = calcAscensionTankiness(20);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int HEAL = calcAscensionSpecial(100);
    private final int BLEED = calcAscensionSpecial(2);
    public LittleRed red;
    private boolean targetRed = false;

    public static final String POWER_ID = RuinaMod.makeID("BloodstainedClaws");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public NightmareWolf() {
        this(0.0f, 0.0f);
    }

    public NightmareWolf(final float x, final float y) {
        super(NAME, ID, 400, -5.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("NightmareWolf/Spriter/NightmareWolf.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(this.maxHealth));

        addMove(CRUEL_CLAWS, Intent.ATTACK_DEFEND, calcAscensionDamage(12));
        addMove(FEROCIOUS_FANGS, Intent.ATTACK_DEBUFF, calcAscensionDamage(8), 2, true);
        addMove(BLOODSTAINED_HUNT, Intent.ATTACK, calcAscensionDamage(7), 3, true);
        addMove(HOWL, Intent.BUFF);
    }

    @Override
    public void usePreBattleAction() {
        //CustomDungeon.playTempMusicInstantly("MasterSpark");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof LittleRed) {
                red = (LittleRed)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, AbstractPower.PowerType.BUFF, false, this, HEAL) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        AbstractCreature target;
        if (!targetRed || red.isDead) {
            target = adp();
        } else {
            target = red;
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case CRUEL_CLAWS: {
                //runAnim("Spark");
                atb(new VFXAction(new ClawEffect(target.hb.cX, target.hb.cY, Color.CYAN, Color.WHITE), 0.1F));
                block(this, BLOCK);
                dmg(target, info, AbstractGameAction.AttackEffect.NONE);
                break;
            }
            case FEROCIOUS_FANGS: {
                //runAnim("Spark");
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                }
                applyToTarget(target, this, new Bleed(target, BLEED));
                break;
            }
            case BLOODSTAINED_HUNT: {
                //runAnim("Smack");
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                }
                break;
            }
            case HOWL: {
                //runAnim("Special");
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                break;
            }
        }
    }

    @Override
    public void takeTurn() {
        targetRed = false;
        takeCustomTurn(this.moves.get(nextMove));
        for (EnemyMoveInfo additionalMove : additionalMoves) {
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new WaitEffect(), 1.0F));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    targetRed = true;
                    addToBot(new VFXAction(new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
                    addToBot(new IntentFlashAction(red.wolf));
                    takeCustomTurn(additionalMove);
                    this.isDone = true;
                }
            });
        }
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(red.wolf));
                this.isDone = true;
            }
        });

    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(CRUEL_CLAWS)) {
            setMoveShortcut(BLOODSTAINED_HUNT, MOVES[BLOODSTAINED_HUNT]);
        } else if (this.lastMove(FEROCIOUS_FANGS)) {
            setMoveShortcut(CRUEL_CLAWS, MOVES[CRUEL_CLAWS]);
        } else {
            setMoveShortcut(FEROCIOUS_FANGS, MOVES[FEROCIOUS_FANGS]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (lastMove(BLOODSTAINED_HUNT, moveHistory)) {
            setAdditionalMoveShortcut(HOWL, MOVES[HOWL], moveHistory);
        } else if (lastMove(HOWL, moveHistory)) {
            setAdditionalMoveShortcut(FEROCIOUS_FANGS, MOVES[FEROCIOUS_FANGS], moveHistory);
        } else {
            setAdditionalMoveShortcut(BLOODSTAINED_HUNT, MOVES[BLOODSTAINED_HUNT], moveHistory);
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
                if (additionalMove.nextMove == -1 || red.isDead) {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp());
                } else {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, red);
                    PowerTip intentTip = additionalIntent.intentTip;
                    if (additionalIntent.numHits > 0) {
                        intentTip.body = TEXT[2] + additionalIntent.damage + TEXT[3] + additionalIntent.numHits + TEXT[4];
                    } else {
                        intentTip.body = TEXT[0] + additionalIntent.damage + TEXT[1];
                    }
                }
            }
        }
    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    public void onRedDeath() {
        atb(new HealAction(this, this, HEAL));
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.isDead || this.currentHealth <= 0) {
            if (info.owner == red) {
                red.onKillWolf();
            } else {
                if (!red.isDead) {
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            red.enrage();
                            this.isDone = true;
                        }
                    });
                } else {
                    onBossVictoryLogic();
                }
            }
        }
    }

}