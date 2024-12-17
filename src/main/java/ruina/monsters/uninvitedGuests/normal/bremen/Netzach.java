package ruina.monsters.uninvitedGuests.normal.bremen;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.normal.bremen.netzachCards.BalefulBrand;
import ruina.monsters.uninvitedGuests.normal.bremen.netzachCards.Faith;
import ruina.monsters.uninvitedGuests.normal.bremen.netzachCards.Will;
import ruina.powers.Erosion;
import ruina.powers.act4.Messenger;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Netzach extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Netzach.class.getSimpleName());

    private static final byte WILL = 0;
    private static final byte BALEFUL = 1;
    private static final byte BLIND_FAITH = 2;

    public final int EROSION = 3;
    public final int BLOCK = 13;
    public final int DRAW = 2;
    public final int faithHits = 2;

    public Netzach() {
        this(0.0f, 0.0f);
    }

    public Netzach(final float x, final float y) {
        super(ID, ID, 160, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Netzach/Spriter/Netzach.scml"));
        this.animation.setFlip(true, false);
        this.setHp(calcAscensionTankiness(160));

        addMove(WILL, Intent.DEFEND_BUFF);
        addMove(BALEFUL, Intent.ATTACK_DEBUFF, 17);
        addMove(BLIND_FAITH, Intent.ATTACK, 13, faithHits);

        cardList.add(new Will(this));
        cardList.add(new BalefulBrand(this));
        cardList.add(new Faith(this));

        this.icon = TexLoader.getTexture(makeUIPath("NetzachIcon.png"));
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
                target = (Bremen)mo;
            }
        }
        addPower(new Messenger(this));
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
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
        byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
        setMoveShortcut(move, cardList.get(move));
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