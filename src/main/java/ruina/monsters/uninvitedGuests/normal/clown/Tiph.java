package ruina.monsters.uninvitedGuests.normal.clown;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.normal.clown.tiphCards.Confrontation;
import ruina.monsters.uninvitedGuests.normal.clown.tiphCards.Kick;
import ruina.monsters.uninvitedGuests.normal.clown.tiphCards.Trigram;
import ruina.powers.act4.FourTrigrams;
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
    private static final byte TRIGRAM = 2;

    public final int STRENGTH = 2;
    public final int BLOCK = 10;
    public final int trigramHits = 2;

    public static final int GEON = 1;
    public static final int GON = 2;
    public static final int RI = 3;

    public static final int geonDamageBonus = 50;
    public static final int gonDamageReduction = 50;
    public static final int riDrawBonus = 1;

    public Tiph() {
        this(0.0f, 0.0f);
    }

    public Tiph(final float x, final float y) {
        super(ID, ID, 300, -5.0F, 0, 200, 260.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Tiph/Spriter/Tiph.scml"));
        this.animation.setFlip(true, false);
        this.setHp(calcAscensionTankiness(160));

        addMove(AUGURY_KICK, Intent.ATTACK_BUFF, 10);
        addMove(CONFRONTATION, Intent.ATTACK_DEFEND, 16);
        addMove(TRIGRAM, Intent.ATTACK, 12, trigramHits);

        cardList.add(new Kick(this));
        cardList.add(new Confrontation(this));
        cardList.add(new Trigram(this));

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
        addPower(new FourTrigrams(this, GEON));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower power = getPower(FourTrigrams.POWER_ID);
                if (power instanceof FourTrigrams) {
                    if (RuinaMod.isMultiplayerConnected()) {
                        ((FourTrigrams) power).changeTrigram(phase);
                    } else {
                        ((FourTrigrams) power).changeTrigram(GEON);
                    }
                }
                this.isDone = true;
            }
        });
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        switch (this.nextMove) {
            case AUGURY_KICK: {
                bluntAnimation(target);
                dmg(target, info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(adp(), this, new StrengthPower(adp(), STRENGTH));
                resetIdle();
                break;
            }
            case CONFRONTATION: {
                pierceAnimation(target);
                dmg(target, info);
                atb(new AllyGainBlockAction(this, this, BLOCK));
                block(adp(), BLOCK);
                resetIdle();
                break;
            }
            case TRIGRAM: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        special1Animation(target);
                        waitAnimation(1.0f);
                        special2Animation(target);
                    } else {
                        special1AnimationNoSound(target);
                        waitAnimation();
                        special4Animation(target);
                    }
                    dmg(target, info);
                    waitAnimation();
                }
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(TRIGRAM)) {
            setMoveShortcut(CONFRONTATION);
        } else if (this.lastMove(CONFRONTATION)) {
            setMoveShortcut(AUGURY_KICK);
        } else {
            setMoveShortcut(TRIGRAM);
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

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "HanaBlunt", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "HanaStab", enemy, this);
    }

    private void special1Animation(AbstractCreature enemy) {
        animationAction("Special1", "HanaStrongCharge", enemy, this);
    }

    private void special1AnimationNoSound(AbstractCreature enemy) {
        animationAction("Special1", null, enemy, this);
    }

    private void special2Animation(AbstractCreature enemy) {
        animationAction("Special2", "HanaStrongStart", enemy, this);
    }

    private void special4Animation(AbstractCreature enemy) {
        animationAction("Special4", "HanaStrongFin", enemy, this);
    }

}