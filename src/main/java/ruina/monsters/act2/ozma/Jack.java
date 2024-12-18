package ruina.monsters.act2.ozma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.JackStealAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Jack extends AbstractRuinaMonster
{
    public static final String ID = makeID(Jack.class.getSimpleName());

    private static final byte ATTACK = 0;
    private static final byte MULTI_ATTACK = 1;

    public ArrayList<AbstractCard> stolenCards = new ArrayList<>();
    private final boolean startSingle;
    private Ozma ozma;

    public static final String POWER_ID = makeID("Steal");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Jack(final float x, final float y, boolean startSingle) {
        super(ID, ID, 100, -5.0F, 0, 135.0f, 160.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Jack/Spriter/Jack.scml"));
        setHp(100);
        addMove(ATTACK, Intent.ATTACK, calcAscensionSpecial(3));
        addMove(MULTI_ATTACK, Intent.ATTACK, calcAscensionSpecial(2), 2);
        this.startSingle = startSingle;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Ozma) {
                ozma = (Ozma) mo;
            }
        }
        addPower(new MinionPower(this));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (owner instanceof Jack && target == adp()) {
                    atb(new JackStealAction((Jack) owner));
                }
            }

            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (damageAmount > 0 && stolenCards.size() > 0) {
                    flash();
                    atb(new MakeTempCardInHandAction(stolenCards.remove(0)));
                }
                return damageAmount;
            }
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case ATTACK: {
                bluntAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case MULTI_ATTACK: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(adp());
                    } else {
                        slashAnimation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        float drawScale = 0.20F;
        float offsetX1 = 100.0F * Settings.scale;
        float offsetY = 100.0F * Settings.scale;
        for (int i = 0; i < stolenCards.size(); i++) {
            AbstractCard card = stolenCards.get(i);
            card.drawScale = drawScale;
            card.current_x = this.hb.x + offsetX1;
            card.current_y = this.hb.y + offsetY * (i + 2);
            card.render(sb);
        }
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            if (startSingle) {
                setMoveShortcut(ATTACK);
            } else {
                setMoveShortcut(MULTI_ATTACK);
            }
        } else {
            if (this.lastMove(ATTACK)) {
                setMoveShortcut(MULTI_ATTACK);
            } else {
                setMoveShortcut(ATTACK);
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        int previousHealth = currentHealth;
        super.damage(info);
        int nextHealth = currentHealth;
        int difference = previousHealth - nextHealth;
        if (difference > 0) {
            if (ozma != null) {
                atb(new LoseHPAction(ozma, ozma, difference));
            }
        }
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "WoodStrike", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "WoodStrike", enemy, this);
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "WoodStrike", enemy, this);
    }

}