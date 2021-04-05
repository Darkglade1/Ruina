package ruina.monsters.uninvitedGuests.bremen;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
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
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.cards.AttackPrescript;
import ruina.cards.PowerPrescript;
import ruina.cards.SkillPrescript;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.bremen.netzachCards.BalefulBrand;
import ruina.monsters.uninvitedGuests.bremen.netzachCards.Faith;
import ruina.monsters.uninvitedGuests.bremen.netzachCards.Will;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Erosion;
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
    public final int BLOCK = 13;
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

        cardList.add(new Will(this));
        cardList.add(new BalefulBrand(this));
        cardList.add(new Faith(this));

        this.allyIcon = makeUIPath("NetzachIcon.png");
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Bremen) {
                bremen = (Bremen)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            final ArrayList<AbstractCard> prescripts = new ArrayList<>();

            @Override
            public void atStartOfTurn() {
                if (prescripts.isEmpty()) {
                    prescripts.add(new AttackPrescript());
                    prescripts.add(new SkillPrescript());
                    //prescripts.add(new PowerPrescript());
                }
                AbstractCard chosenCard = prescripts.remove(AbstractDungeon.monsterRng.random(prescripts.size() - 1));
                makeInHand(chosenCard);
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
                blockAnimation();
                block(adp(), BLOCK);
                atb(new AllyGainBlockAction(this, this, BLOCK));
                applyToTarget(adp(), this, new DrawCardNextTurnPower(adp(), DRAW));
                resetIdle();
                break;
            }
            case BALEFUL: {
                specialAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new Erosion(target, EROSION));
                resetIdle(1.0f);
                break;
            }
            case BLIND_FAITH: {
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
        animationAction("Special", "YanBrand", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "YanVert", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "YanStab", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

}