package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.cards.Gift;
import ruina.monsters.act1.laetitia.GiftFriend;
import ruina.monsters.act1.laetitia.WitchFriend;

import static ruina.util.Wiz.*;

public class SurprisePresent extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(SurprisePresent.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SurprisePresent(AbstractCreature owner, int amount, int kaboomDamage) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        amount2 = kaboomDamage;
        updateDescription();
    }

    public void duringTurn() {
        if (this.amount == 1 && !this.owner.isDying) {
            atb(new VFXAction(new ExplosionSmallEffect(this.owner.hb.cX, this.owner.hb.cY), 0.1F));
            atb(new SuicideAction((AbstractMonster) this.owner));
            DamageInfo damageInfo = new DamageInfo(this.owner, amount2, DamageInfo.DamageType.THORNS);
            dmg(adp(), damageInfo, AbstractGameAction.AttackEffect.FIRE);
            atb(new MakeTempCardInHandAction(new Gift(), 1));
            if(owner instanceof GiftFriend){
                AbstractMonster giftFriend1 = new WitchFriend(((GiftFriend) owner).storedX, 0.0f, ((GiftFriend) owner).parent);
                atb(new SpawnMonsterAction(giftFriend1, true));
                atb(new UsePreBattleActionAction(giftFriend1));
            }
        } else {
            atb(new ReducePowerAction(owner, owner, this, 1));
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        description = amount == 1 ? String.format(DESCRIPTIONS[1], amount, amount2) : String.format(DESCRIPTIONS[0], amount, amount2);
    }
}