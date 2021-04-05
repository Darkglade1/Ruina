package ruina.monsters.uninvitedGuests.clown;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.clown.tiphCards.Confrontation;
import ruina.monsters.uninvitedGuests.clown.tiphCards.Kick;
import ruina.powers.Protection;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Tiph extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Tiph.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte AUGURY_KICK = 0;
    private static final byte CONFRONTATION = 1;

    public final int STRENGTH = 3;
    public final int PROTECTION = 2;

    public Oswald oswald;

    public Tiph() {
        this(0.0f, 0.0f);
    }

    public Tiph(final float x, final float y) {
        super(NAME, ID, 190, -5.0F, 0, 200, 260.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Tiph/Spriter/Tiph.scml"));
        this.animation.setFlip(true, false);

        this.setHp(maxHealth);
        this.type = EnemyType.BOSS;

        addMove(AUGURY_KICK, Intent.ATTACK_BUFF, 10);
        addMove(CONFRONTATION, Intent.ATTACK_DEFEND, 7);

        cardList.add(new Kick(this));
        cardList.add(new Confrontation(this));

        this.allyIcon = makeUIPath("TiphIcon.png");
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Oswald) {
                oswald = (Oswald)mo;
            }
        }
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

        AbstractCreature target;
        if (isAlly) {
            target = oswald;
        } else {
            target = adp();
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case AUGURY_KICK: {
                bluntAnimation(target);
                dmg(target, info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                if (isAlly) {
                    applyToTarget(adp(), this, new StrengthPower(adp(), STRENGTH));
                } else {
                    applyToTarget(oswald, this, new StrengthPower(oswald, STRENGTH));
                }
                resetIdle();
                break;
            }
            case CONFRONTATION: {
                pierceAnimation(target);
                dmg(target, info);
                applyToTarget(this, this, new Protection(this, PROTECTION));
                if (isAlly) {
                    applyToTarget(adp(), this, new Protection(adp(), PROTECTION));
                } else {
                    applyToTarget(oswald, this, new Protection(oswald, PROTECTION));
                }
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (isAlly) {
            if (this.lastMove(AUGURY_KICK)) {
                setMoveShortcut(CONFRONTATION, MOVES[CONFRONTATION], cardList.get(CONFRONTATION));
            } else {
                setMoveShortcut(AUGURY_KICK, MOVES[AUGURY_KICK], cardList.get(AUGURY_KICK));
            }
        } else {
            if (this.lastMove(CONFRONTATION)) {
                setMoveShortcut(AUGURY_KICK, MOVES[AUGURY_KICK], cardList.get(AUGURY_KICK));
            } else {
                setMoveShortcut(CONFRONTATION, MOVES[CONFRONTATION], cardList.get(CONFRONTATION));
            }
        }
    }

    public void onBrainwashed() {
        setMoveShortcut(CONFRONTATION, MOVES[CONFRONTATION], cardList.get(CONFRONTATION));
        createIntent();
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1 || !isAlly) {
            super.applyPowers();
            return;
        }
        applyPowers(oswald);
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

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "HanaBlunt", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "HanaStab", enemy, this);
    }

}