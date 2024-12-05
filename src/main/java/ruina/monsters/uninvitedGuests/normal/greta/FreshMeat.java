package ruina.monsters.uninvitedGuests.normal.greta;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.util.TexLoader;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class FreshMeat extends AbstractRuinaMonster
{
    public static final String ID = makeID(FreshMeat.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;

    private static final byte NONE = 0;
    private final int HEAL = calcAscensionSpecial(100);
    private final AbstractCard card;
    private Greta greta;
    private boolean gaveCard = false;

    public static final String POWER_ID = makeID("FreshMeat");
    public static final PowerStrings PowerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = PowerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = PowerStrings.DESCRIPTIONS;

    public FreshMeat(final float x, final float y, AbstractCard card, Greta greta) {
        super(NAME, ID, 80, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("FreshMeat/Spriter/FreshMeat.scml"));
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 9) {
            setHp(60);
        } else {
            setHp(80);
        }
        this.card = card;
        this.greta = greta;
        this.icon = TexLoader.getTexture(makeUIPath("MeatIcon.png"));
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, HEAL) {
            @Override
            public void updateDescription() {
                String cardName = FontHelper.colorString(card.name, "y");
                description = POWER_DESCRIPTIONS[0] + HEAL + POWER_DESCRIPTIONS[1] + cardName + POWER_DESCRIPTIONS[2] + cardName + POWER_DESCRIPTIONS[3];
            }
        });
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
                AbstractCard masterCard = StSLib.getMasterDeckEquivalent(card);
                if (masterCard != null) {
                    adp().masterDeck.removeCard(masterCard);
                }
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

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        greta.meat = null;
    }
}