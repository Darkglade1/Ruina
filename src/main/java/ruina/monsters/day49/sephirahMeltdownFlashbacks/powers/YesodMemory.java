package ruina.monsters.day49.sephirahMeltdownFlashbacks.powers;

import basemod.helpers.ScreenPostProcessorManager;
import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.watcher.EnergyDownPower;
import ruina.RuinaMod;
import ruina.patches.PostProcessorPatch;
import ruina.powers.AbstractUnremovablePower;
import ruina.shaders.Yesod.YesodPostProcessor;
import ruina.shaders.Yesod.YesodShader;

import java.util.ArrayList;
import java.util.Objects;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class YesodMemory extends AbstractUnremovablePower {

    public static final String POWER_ID = RuinaMod.makeID(YesodMemory.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean pixelRender = false;
    private float timer = 15f;

    public YesodMemory(AbstractCreature owner, boolean effect) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        pixelRender = effect;
        updateDescription();
        loadRegion("combust");
    }


    @Override
    public void updateDescription() {
        description = pixelRender ? DESCRIPTIONS[0] : DESCRIPTIONS[1];
    }

    @Override
    public void atEndOfRound() {
        super.atEndOfRound();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList <AbstractCard> randomCard = CardLibrary.getAllCards();
                for(AbstractCard c: adp().masterDeck.group){
                    c.name = randomCard.get(AbstractDungeon.miscRng.random(1, randomCard.size() - 1)).name;
                }
                for(AbstractCard c: adp().drawPile.group){
                    c.name = randomCard.get(AbstractDungeon.miscRng.random(1, randomCard.size() - 1)).name;
                }
                for(AbstractCard c: adp().discardPile.group){
                    c.name = randomCard.get(AbstractDungeon.miscRng.random(1, randomCard.size() - 1)).name;
                }
                for(AbstractCard c: adp().exhaustPile.group){
                    c.name = randomCard.get(AbstractDungeon.miscRng.random(1, randomCard.size() - 1)).name;
                }
                isDone = true;
            }
        });
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if(pixelRender){
            timer -= Gdx.graphics.getDeltaTime();
            if(timer <= 0f) {
                AbstractDungeon.effectsQueue.add(new YesodShader());
                timer = 15f;
            }
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        for (ScreenPostProcessor processor : PostProcessorPatch.postProcessors) {
            if(processor instanceof YesodPostProcessor){
                try { ScreenPostProcessorManager.removePostProcessor(processor); }
                catch (Exception e){}
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList <AbstractCard> randomCard = CardLibrary.getAllCards();
                for(AbstractCard c: adp().masterDeck.group){
                    c.name = c.originalName;
                }
                for(AbstractCard c: adp().drawPile.group){
                    c.name = c.originalName;
                }
                for(AbstractCard c: adp().discardPile.group){
                    c.name = c.originalName;
                }
                for(AbstractCard c: adp().exhaustPile.group){
                    c.name = c.originalName;
                }
                isDone = true;
            }
        });
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList <AbstractCard> randomCard = CardLibrary.getAllCards();
                for(AbstractCard c: adp().masterDeck.group){
                    c.name = randomCard.get(AbstractDungeon.miscRng.random(1, randomCard.size() - 1)).name;
                }
                for(AbstractCard c: adp().drawPile.group){
                    c.name = randomCard.get(AbstractDungeon.miscRng.random(1, randomCard.size() - 1)).name;
                }
                for(AbstractCard c: adp().discardPile.group){
                    c.name = randomCard.get(AbstractDungeon.miscRng.random(1, randomCard.size() - 1)).name;
                }
                for(AbstractCard c: adp().exhaustPile.group){
                    c.name = randomCard.get(AbstractDungeon.miscRng.random(1, randomCard.size() - 1)).name;
                }
                isDone = true;
            }
        });
    }

}