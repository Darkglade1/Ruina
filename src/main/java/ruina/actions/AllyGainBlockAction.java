package ruina.actions;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import ruina.RuinaMod;

public class AllyGainBlockAction extends AbstractGameAction {

    public AllyGainBlockAction(AbstractCreature target, int amount) {
        this.target = target;
        this.amount = amount;
        this.actionType = ActionType.BLOCK;
        this.duration = 0.25F;
        this.startDuration = 0.25F;
    }

    public AllyGainBlockAction(AbstractCreature target, AbstractCreature source, int amount) {
        this.setValues(target, source, amount);
        this.actionType = ActionType.BLOCK;
        this.duration = 0.25F;
        this.startDuration = 0.25F;
    }

    public AllyGainBlockAction(AbstractCreature target, int amount, boolean superFast) {
        this(target, amount);
        if (superFast) {
            this.duration = this.startDuration = Settings.ACTION_DUR_XFAST;
        }

    }

    public AllyGainBlockAction(AbstractCreature target, AbstractCreature source, int amount, boolean superFast) {
        this(target, source, amount);
        if (superFast) {
            this.duration = this.startDuration = Settings.ACTION_DUR_XFAST;
        }

    }

    public void update() {
        if (!this.target.isDying && !this.target.isDead && this.duration == this.startDuration) {
            if (RuinaMod.isMultiplayerConnected()) {
                addToTop(new GainBlockAction(target, RuinaMod.getMultiplayerEnemyHealthScaling(amount)));
            } else {
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SHIELD));

                boolean effect = target.currentBlock == 0;

                //we're not using addBlock here so that NOTHING can modify this block gain, looking at you Stiletto from Reliquary
                this.target.currentBlock += this.amount;

                if (effect && target.currentBlock > 0) {
                    ReflectionHacks.privateMethod(AbstractCreature.class, "gainBlockAnimation").invoke(this.target);
                } else if (amount > 0) {
                    Color tmpCol = Settings.GOLD_COLOR.cpy();
                    Color blockTextColor = ReflectionHacks.getPrivate(target, AbstractCreature.class, "blockTextColor");
                    tmpCol.a = blockTextColor.a;
                    ReflectionHacks.setPrivate(target, AbstractCreature.class, "blockTextColor", tmpCol);
                    ReflectionHacks.setPrivate(target, AbstractCreature.class, "blockScale", 5.0F);
                }

                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                    c.applyPowers();
                }
            }
        }
        this.tickDuration();
    }
}
