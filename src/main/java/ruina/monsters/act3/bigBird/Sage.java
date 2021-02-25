package ruina.monsters.act3.bigBird;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
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
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte RING = 0;
    private static final byte SMACK = 1;

    private final int RITUAL = 2;

    private static final int DAMAGE_CAP = 1;

    public BigBird bigBird;
    private int dialogNum;

    public static final String POWER_ID = RuinaMod.makeID("Unruffled");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final Texture targetTexture = TexLoader.getTexture(makeUIPath("WrathIcon.png"));

    public Sage() {
        this(0.0f, 0.0f, 0);
    }

    public Sage(final float x, final float y, int dialogNum) {
        super(NAME, ID, 300, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ServantOfWrath/Spriter/Wrath.scml"));
        this.animation.setFlip(true, false);
        this.dialogNum = dialogNum;
        this.setHp(maxHealth);

        addMove(RING, Intent.BUFF);
        addMove(SMACK, Intent.ATTACK, 6);

        this.allyIcon = makeUIPath("WrathIcon.png");
        this.isTargetableByPlayer = true;
    }

    @Override
    public void usePreBattleAction() {
        atb(new TalkAction(this, DIALOG[dialogNum]));
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof BigBird) {
                bigBird = (BigBird)mo;
            }
        }
        applyToTarget(this, this, new Unruffled(this, DAMAGE_CAP));
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
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

        AbstractCreature target = bigBird;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case RING: {
                applyToTarget(this, this, new RitualPower(this, RITUAL, false));
                break;
            }
            case SMACK: {
                dmg(target, info);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(RING, MOVES[RING]);
        } else {
            setMoveShortcut(SMACK, MOVES[SMACK]);
        }
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1 || bigBird.isDeadOrEscaped()) {
            super.applyPowers();
            return;
        }
        applyPowers(bigBird);
    }

    public void onBigBirdDeath() {
        atb(new TalkAction(this, DIALOG[dialogNum + 2]));
        atb(new VFXAction(new WaitEffect(), 1.0F));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                Sage.this.isDead = true;
                this.isDone = true;
            }
        });
    }

    private void normal1Animation(AbstractCreature enemy) {
        animationAction("Normal1", "WrathAtk1", enemy, this);
    }

    private void normal2Animation(AbstractCreature enemy) {
        animationAction("Normal2", "WrathAtk2", enemy, this);
    }

    private void big1Animation(AbstractCreature enemy) {
        animationAction("Big1", "WrathStrong1", enemy, this);
    }

    private void big2Animation(AbstractCreature enemy) {
        animationAction("Big2", "WrathStrong2", enemy, this);
    }

    private void big3Animation(AbstractCreature enemy) {
        animationAction("Big3", "WrathStrong3", enemy, this);
    }

}