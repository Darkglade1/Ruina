package ruina.monsters.act3.bigBird;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.RitualPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.Unruffled;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Sage extends AbstractAllyMonster
{
    public static final String ID = RuinaMod.makeID(Sage.class.getSimpleName());

    private static final byte RING = 0;
    private static final byte SMACK = 1;

    private final int RITUAL = 1;

    private static final int DAMAGE_CAP = 1;
    private final int dialogNum;

    public static final String POWER_ID = RuinaMod.makeID("Unruffled");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Sage() {
        this(0.0f, 0.0f, 0);
    }

    public Sage(final float x, final float y, int dialogNum) {
        super(ID, ID, 500, -5.0F, 0, 200.0f, 220.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Keeper/Spriter/Keeper.scml"));
        this.animation.setFlip(true, false);
        this.dialogNum = dialogNum;
        this.setHp(500);

        addMove(RING, Intent.BUFF);
        addMove(SMACK, Intent.ATTACK, 6);

        this.icon = TexLoader.getTexture(makeUIPath("SageIcon.png"));
        this.isTargetableByPlayer = true;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        atb(new TalkAction(this, DIALOG[dialogNum]));
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof BigBird) {
                target = (BigBird)mo;
            }
        }
        applyToTarget(this, this, new Unruffled(this, DAMAGE_CAP));
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case RING: {
                specialAnimation();
                applyToTarget(this, this, new RitualPower(this, RITUAL, false));
                resetIdle();
                break;
            }
            case SMACK: {
                attackAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(RING);
        } else {
            setMoveShortcut(SMACK);
        }
    }

    public void onBigBirdDeath() {
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[dialogNum + 2]));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    disappear();
                    this.isDone = true;
                }
            });
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Smack", "BluntBlow", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Ring", "BossBirdSpecial", this);
    }

}