package ruina.monsters.uninvitedGuests.bremen;

import basemod.ReflectionHacks;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
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
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BobEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.uninvitedGuests.elena.Elena;
import ruina.monsters.uninvitedGuests.elena.VermilionCross;
import ruina.monsters.uninvitedGuests.elena.binahCards.Chain;
import ruina.monsters.uninvitedGuests.elena.binahCards.Fairy;
import ruina.monsters.uninvitedGuests.elena.binahCards.Pillar;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Erosion;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Netzach extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Netzach.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte WILL = 0;
    private static final byte BALEFUL = 1;
    private static final byte BLIND_FAITH = 2;

    public final int EROSION = 3;
    public final int BLOCK = 10;
    public final int DRAW = 2;
    public final int faithHits = 2;

    public Bremen bremen;

    public static final String POWER_ID = makeID("Messenger");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Netzach() {
        this(0.0f, 0.0f);
    }

    public Netzach(final float x, final float y) {
        super(NAME, ID, 180, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Netzach/Spriter/Netzach.scml"));
        this.animation.setFlip(true, false);

        this.setHp(maxHealth);
        this.type = EnemyType.BOSS;

        addMove(WILL, Intent.DEFEND_BUFF);
        addMove(BALEFUL, Intent.ATTACK_DEBUFF, 17);
        addMove(BLIND_FAITH, Intent.ATTACK, 13, faithHits, true);

//        cardList.add(new Pillar(this));
//        cardList.add(new Chain(this));
//        cardList.add(new Fairy(this));

        this.allyIcon = makeUIPath("NetzachIcon.png");
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Bremen) {
                bremen = (Bremen)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void atStartOfTurn() {

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

        AbstractCreature target = bremen;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case WILL: {
                specialAnimation(target);
                block(adp(), BLOCK);
                atb(new AllyGainBlockAction(this, this, BLOCK));
                applyToTarget(adp(), this, new DrawCardNextTurnPower(adp(), DRAW));
                resetIdle();
                break;
            }
            case BALEFUL: {
                bluntAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new Erosion(target, EROSION));
                resetIdle();
                break;
            }
            case BLIND_FAITH: {
                for (int i = 0; i < multiplier; i++) {
                    slashAnimation(target);
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
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resets the cooldowns after all moves have been used once
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(WILL) && !this.lastMoveBefore(WILL)) {
            possibilities.add(WILL);
        }
        if (!this.lastMove(BALEFUL) && !this.lastMoveBefore(BALEFUL)) {
            possibilities.add(BALEFUL);
        }
        if (!this.lastMove(BLIND_FAITH) && !this.lastMoveBefore(BLIND_FAITH)) {
            possibilities.add(BLIND_FAITH);
        }
        if (possibilities.isEmpty()) {
            possibilities.add(BLIND_FAITH);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move], cardList.get(move));
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        applyPowers(bremen);
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

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "BinahStoneReady", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "BinahFairy", enemy, this);
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BinahChain", enemy, this);
    }

}