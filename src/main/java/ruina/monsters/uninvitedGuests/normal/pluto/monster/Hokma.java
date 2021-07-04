package ruina.monsters.uninvitedGuests.normal.pluto.monster;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
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
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.normal.pluto.hokmaCards.Silence;
import ruina.monsters.uninvitedGuests.normal.pluto.hokmaCards.Time;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Hokma extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Hokma.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte SILENCE = 0;
    private static final byte TIME = 1;

    public final int DAMAGE_INCREASE = 10;
    public final int CARDS_PER_TURN = 6;

    public Pluto pluto;

    public static final String POWER_ID = makeID("PriceOfTime");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Hokma() {
        this(0.0f, 0.0f);
    }

    public Hokma(final float x, final float y) {
        super(NAME, ID, 160, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Hokma/Spriter/Hokma.scml"));
        this.animation.setFlip(true, false);

        this.setHp(calcAscensionTankiness(this.maxHealth));
        this.type = EnemyType.BOSS;

        addMove(SILENCE, Intent.ATTACK, 10);
        addMove(TIME, Intent.BUFF);

        cardList.add(new Silence(this));
        cardList.add(new Time(this));

        this.allyIcon = makeUIPath("HokmaIcon.png");
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Pluto) {
                pluto = (Pluto)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                this.amount++;
                if (this.amount >= CARDS_PER_TURN) {
                    this.amount = 0;
                    takeTurn();
                }
            }
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + CARDS_PER_TURN + POWER_DESCRIPTIONS[1];
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

        AbstractCreature target = pluto.shade;
        if (target.isDeadOrEscaped()) {
            target = pluto;
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case SILENCE: {
                slashAnimation(target);
                dmg(target, info);
                resetIdle();
                int newDamage = info.base += DAMAGE_INCREASE;
                addMove(SILENCE, Intent.ATTACK, newDamage);
                break;
            }
            case TIME: {
                atb(new SkipEnemiesTurnAction());
                atb(new PressEndTurnButtonAction());
                playSound("SilenceStop");
                AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
                AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
                break;
            }
        }
        atb(new RollMoveAction(this));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                createIntent();
                this.isDone = true;
            }
        });
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                halfDead = true;
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(SILENCE) && this.lastMoveBefore(SILENCE)) {
            setMoveShortcut(TIME, MOVES[TIME], cardList.get(TIME));
        } else {
            setMoveShortcut(SILENCE, MOVES[SILENCE], cardList.get(SILENCE));
        }
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        AbstractCreature target;
        if (pluto.shade.isDeadOrEscaped()) {
            target = pluto;
        } else {
            target = pluto.shade;
        }
        applyPowers(target);
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

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "SilenceEffect", enemy, this);
    }

}