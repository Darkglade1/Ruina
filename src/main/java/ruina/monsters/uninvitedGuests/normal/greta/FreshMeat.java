package ruina.monsters.uninvitedGuests.normal.greta;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.TexLoader;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class FreshMeat extends AbstractRuinaMonster
{
    public static final String ID = makeID(FreshMeat.class.getSimpleName());

    private static final byte NONE = 0;
    private final int HEAL = calcAscensionSpecial(100);
    private final AbstractCard card;
    private boolean gaveCard = false;

    public FreshMeat(final float x, final float y, AbstractCard card) {
        super(ID, ID, 80, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("FreshMeat/Spriter/FreshMeat.scml"));
        setHp(80);
        this.card = card;
        this.icon = TexLoader.getTexture(makeUIPath("MeatIcon.png"));
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new ruina.powers.act4.FreshMeat(this, HEAL, card));
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
        setMove(NONE, Intent.NONE);
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.isDead || this.isDying || this.currentHealth <= 0) {
            if (info.owner instanceof Greta) {
                atb(new HealAction(info.owner, info.owner, HEAL));
                adp().hand.moveToExhaustPile(card);
            } else {
                if (!gaveCard) {
                    makeInHand(card);
                    gaveCard = true;
                }
            }
        }
        AbstractDungeon.onModifyPower();
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        super.renderTip(sb);
        tips.add(new CardPowerTip(card.makeStatEquivalentCopy()));
    }
}