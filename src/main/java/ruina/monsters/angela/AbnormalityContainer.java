package ruina.monsters.angela;

import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.blackSilence.blackSilence3.BlackSilence3;
import ruina.monsters.uninvitedGuests.normal.greta.Greta;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.SoulLink;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.makeInHand;

public abstract class AbnormalityContainer extends AbstractRuinaMonster
{

    protected AbstractMonster abnormality;
    protected AbstractMonster abnormalityBG;
    protected AbstractMonster abnormalityEncyclopedia;
    protected AbstractMonster abnormalityWarning;
    protected AbstractMonster staticDischarge;

    protected int timesBreached = 0;
    protected boolean currentlyBreaching;

    public AbnormalityContainer(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("AbnormalityContainer/ContainmentUnit/Spriter/AbnormalityContainer.scml"));
        setupAbnormality();
    }

    public AbnormalityContainer(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("AbnormalityContainer/ContainmentUnit/Spriter/AbnormalityContainer.scml"));
        setupAbnormality();
    }

    public AbnormalityContainer(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("AbnormalityContainer/ContainmentUnit/Spriter/AbnormalityContainer.scml"));
        setupAbnormality();
    }

    protected abstract void setupAbnormality();
    protected abstract void getAbnormality(int timesBreached);

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) { p.onDeath(); }
            for (AbstractRelic r : AbstractDungeon.player.relics) { r.onMonsterDeath(this); }
            if(!currentlyBreaching){
                currentlyBreaching = true;
                getAbnormality(timesBreached++);
            }
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    currentHealth = maxHealth;
                    healthBarUpdatedEvent();
                    halfDead = false;
                    isDone = true;
                }
            });
        }
    }

    @Override
    public void die(){

    }

    public void properDie(){ super.die(); }

    public void render(SpriteBatch sb){
        super.render(sb);
        if(currentlyBreaching && staticDischarge != null){ staticDischarge.render(sb); }
        else {
            System.out.println("hi?");
            if(abnormalityWarning != null){ abnormalityWarning.render(sb); }
            if(abnormalityEncyclopedia != null){ abnormalityEncyclopedia.render(sb); }
            if(abnormalityBG != null){ abnormalityBG.render(sb); }
            if(abnormality != null){ abnormality.render(sb); }
        }
    }
}