package ruina.monsters.uninvitedGuests.normal.clown;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.normal.clown.tiphCards.Confrontation;
import ruina.monsters.uninvitedGuests.normal.clown.tiphCards.Kick;
import ruina.powers.Protection;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Tiph extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Tiph.class.getSimpleName());

    private static final byte AUGURY_KICK = 0;
    private static final byte CONFRONTATION = 1;

    public final int STRENGTH = 2;
    public final int PROTECTION = 2;

    public Tiph() {
        this(0.0f, 0.0f);
    }

    public Tiph(final float x, final float y) {
        super(ID, ID, 300, -5.0F, 0, 200, 260.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Tiph/Spriter/Tiph.scml"));
        this.animation.setFlip(true, false);
        this.setHp(300);

        addMove(AUGURY_KICK, Intent.ATTACK_BUFF, 10);
        addMove(CONFRONTATION, Intent.ATTACK_DEFEND, 7);

        cardList.add(new Kick(this));
        cardList.add(new Confrontation(this));

        this.icon = TexLoader.getTexture(makeUIPath("TiphIcon.png"));
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
                target = (Oswald)mo;
            }
        }
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();

        AbstractCreature target;
        if (isAlly) {
            target = this.target;
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
                    applyToTarget(this.target, this, new StrengthPower(this.target, STRENGTH));
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
                    applyToTarget(this.target, this, new Protection(this.target, PROTECTION));
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