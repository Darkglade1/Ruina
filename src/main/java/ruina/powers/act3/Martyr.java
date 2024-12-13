package ruina.powers.act3;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.DarkSmokePuffEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokingEmberEffect;
import ruina.RuinaMod;
import ruina.monsters.act3.blueStar.Worshipper;
import ruina.powers.AbstractEasyPower;

import static ruina.monsters.AbstractRuinaMonster.playSound;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class Martyr extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID(Martyr.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Martyr(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void onDeath() {
        if (owner instanceof Worshipper) {
            if (((Worshipper) owner).triggerMartyr) {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        playSound("WorshipperExplode");
                        AbstractDungeon.effectsQueue.add(new DarkSmokePuffEffect(owner.hb.cX, owner.hb.cY));
                        for (int i = 0; i < 12; ++i) {
                            AbstractDungeon.effectsQueue.add(new SmokingEmberEffect(owner.hb.cX + MathUtils.random(-50.0F, 50.0F) * Settings.scale, owner.hb.cY + MathUtils.random(-50.0F, 50.0F) * Settings.scale));
                        }
                        this.isDone = true;
                    }
                });
                DamageInfo damageInfo = new DamageInfo(this.owner, amount, DamageInfo.DamageType.THORNS);
                atb(new DamageAction(adp(), damageInfo, AbstractGameAction.AttackEffect.NONE, true));
            }
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
    }
}
