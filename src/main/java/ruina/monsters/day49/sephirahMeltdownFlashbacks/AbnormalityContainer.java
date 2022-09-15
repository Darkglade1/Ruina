package ruina.monsters.day49.sephirahMeltdownFlashbacks;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.Color;
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
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
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
    protected String abnoID;
    protected String warningTier;

    public AbnormalityContainer(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, String abnormalityID, String threatLevel) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Day49/AbnormalityContainer/ContainmentUnit/Spriter/AbnormalityContainer.scml"));
        abnoID = abnormalityID;
        warningTier = threatLevel;
    }

    public AbnormalityContainer(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights, String abnormalityID, String threatLevel) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Day49/AbnormalityContainer/ContainmentUnit/Spriter/AbnormalityContainer.scml"));
        abnoID = abnormalityID;
        warningTier = threatLevel;
    }

    public AbnormalityContainer(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, String abnormalityID, String threatLevel) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Day49/AbnormalityContainer/ContainmentUnit/Spriter/AbnormalityContainer.scml"));
        abnoID = abnormalityID;
        warningTier = threatLevel;
    }

    protected void setupAbnormality() {
        abnormality = new AbnormalityUnit(abnoID);
        abnormality.drawX = adp().drawX;
        abnormalityBG = new AbnormalityBackground(abnoID);
        abnormalityBG.drawX = adp().drawX;
        abnormalityEncyclopedia = new AbnormalityEncyclopedia(abnoID);
        abnormalityEncyclopedia.drawX = adp().drawX;
        abnormalityWarning = new AbnormalityWarning(warningTier);
        abnormalityWarning.drawX = adp().drawX;
        staticDischarge = new Static();
        staticDischarge.drawX = adp().drawX;
    }

    public void usePreBattleAction(){
        this.drawX = adp().drawX;
        setupAbnormality();
    }

    protected abstract void getAbnormality(int timesBreached);
    protected void prepareBreach(){
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.topLevelEffectsQueue.add(new BorderFlashEffect(Color.RED));
                this.isDone = true;
            }
        });
        switch (warningTier){
            default:
                CustomDungeon.playTempMusicInstantly("Trumpet1");
                break;
            case "WAW":
                CustomDungeon.playTempMusicInstantly("Trumpet2");
                break;
            case "ALEPH":
                CustomDungeon.playTempMusicInstantly("Trumpet3");
                break;
        }
    }
    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            this.hideHealthBar();
            for (AbstractPower p : this.powers) { p.onDeath(); }
            for (AbstractRelic r : AbstractDungeon.player.relics) { r.onMonsterDeath(this); }
            if(!currentlyBreaching){
                currentlyBreaching = true;
                prepareBreach();
                getAbnormality(timesBreached++);
            }
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    currentHealth = maxHealth;
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
        if(currentlyBreaching && staticDischarge != null){ staticDischarge.render(sb); }
        else {
            if(abnormalityBG != null){ abnormalityBG.render(sb); }
            if(abnormality != null){ abnormality.render(sb); }
        }
        super.render(sb);
        if(abnormalityWarning != null){ abnormalityWarning.render(sb); }
        if(abnormalityEncyclopedia != null){ abnormalityEncyclopedia.render(sb); }
    }
}