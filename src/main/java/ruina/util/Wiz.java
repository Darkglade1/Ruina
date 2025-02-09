package ruina.util;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.actions.ApplyPowerActionButItCanFizzle;
import ruina.actions.MakeTempCardInDiscardActionButItCanFizzle;
import ruina.actions.MakeTempCardInDrawPileActionButItCanFizzle;
import ruina.patches.RenderHandPatch;
import ruina.powers.AbstractUnremovablePower;
import ruina.powers.LosePowerPower;
import ruina.powers.NextTurnPowerPower;
import spireTogether.patches.combatsync.ActionPatches;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Wiz {
    //The wonderful Wizard of Oz allows access to most easy compilations of data, or functions.

    public static AbstractPlayer adp() {
        return AbstractDungeon.player;
    }

    public static void forAllCardsInList(Consumer<AbstractCard> consumer, ArrayList<AbstractCard> cardsList) {
        for (AbstractCard c : cardsList) {
            consumer.accept(c);
        }
    }

    public static ArrayList<AbstractCard> getAllCardsInCardGroups(boolean includeHand, boolean includeExhaust) {
        ArrayList<AbstractCard> masterCardsList = new ArrayList<>();
        masterCardsList.addAll(AbstractDungeon.player.drawPile.group);
        masterCardsList.addAll(AbstractDungeon.player.discardPile.group);
        if (includeHand) {
            masterCardsList.addAll(AbstractDungeon.player.hand.group);
        }
        if (includeExhaust) {
            masterCardsList.addAll(AbstractDungeon.player.exhaustPile.group);
        }
        return masterCardsList;
    }

    public static void forAllMonstersLiving(Consumer<AbstractMonster> consumer) {
        for (AbstractMonster m : monsterList()) {
            consumer.accept(m);
        }
    }

    public static ArrayList<AbstractMonster> monsterList() {
        ArrayList<AbstractMonster> monsters = new ArrayList<>(AbstractDungeon.getMonsters().monsters);
        monsters.removeIf(m -> m.isDead || m.isDying);
        return monsters;
    }

    public static ArrayList<AbstractCard> getCardsMatchingPredicate(Predicate<AbstractCard> pred) {
        return getCardsMatchingPredicate(pred, false);
    }

    public static ArrayList<AbstractCard> getCardsMatchingPredicate(Predicate<AbstractCard> pred, boolean allcards) {
        if (allcards) {
            ArrayList<AbstractCard> cardsList = new ArrayList<>();
            for (AbstractCard c : CardLibrary.getAllCards()) {
                if (pred.test(c)) cardsList.add(c.makeStatEquivalentCopy());
            }
            return cardsList;
        } else {
            ArrayList<AbstractCard> cardsList = new ArrayList<>();
            for (AbstractCard c : AbstractDungeon.srcCommonCardPool.group) {
                if (pred.test(c)) cardsList.add(c.makeStatEquivalentCopy());
            }
            for (AbstractCard c : AbstractDungeon.srcUncommonCardPool.group) {
                if (pred.test(c)) cardsList.add(c.makeStatEquivalentCopy());
            }
            for (AbstractCard c : AbstractDungeon.srcRareCardPool.group) {
                if (pred.test(c)) cardsList.add(c.makeStatEquivalentCopy());
            }
            return cardsList;
        }
    }

    public static AbstractCard returnTrulyRandomPrediCardInCombat(Predicate<AbstractCard> pred, boolean allCards) {
        return getRandomItem(getCardsMatchingPredicate(pred, allCards));
    }


    public static AbstractCard returnTrulyRandomPrediCardInCombat(Predicate<AbstractCard> pred) {
        return returnTrulyRandomPrediCardInCombat(pred, false);
    }

    public static <T> T getRandomItem(ArrayList<T> list) {
        return list.isEmpty() ? null : list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
    }

    public static boolean isInCombat() {
        return CardCrawlGame.isInARun() && AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    public static void atb(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    public static void att(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    public static void vfx(AbstractGameEffect gameEffect) {
        atb(new VFXAction(gameEffect));
    }

    public static void vfx(AbstractGameEffect gameEffect, float duration) {
        atb(new VFXAction(gameEffect, duration));
    }

    public static void makeInHand(AbstractCard c, int i) {
        atb(new MakeTempCardInHandAction(c, i));
    }

    public static void makeInHandSameUUID(AbstractCard c) {
        atb(new MakeTempCardInHandAction(c, true, true));
    }

    public static void makeInHand(AbstractCard c) {
        makeInHand(c, 1);
    }

    public static void shuffleIn(AbstractCard c, int i) {
        atb(new MakeTempCardInDrawPileAction(c, i, true, true));
    }

    public static void shuffleIn(AbstractCard c) {
        shuffleIn(c, 1);
    }

    public static void intoDiscard(AbstractCard c, int i) {
        atb(new MakeTempCardInDiscardAction(c, i));
    }

    public static void intoDrawMo(AbstractCard c, int i, AbstractMonster source) {
        atb(new MakeTempCardInDrawPileActionButItCanFizzle(c, i, true, true, source));
    }

    public static void intoDiscardMo(AbstractCard c, int i, AbstractMonster source) {
        //because for some reason the action is HARDCODED to only take up to FIVE
        if (i > 5) {
            int times = i / 5;
            int remainder = i % 5;
            for (int count = 0; count < times; count++) {
                atb(new MakeTempCardInDiscardActionButItCanFizzle(c, 5, source));
            }
            atb(new MakeTempCardInDiscardActionButItCanFizzle(c, remainder, source));
        } else {
            atb(new MakeTempCardInDiscardActionButItCanFizzle(c, i, source));
        }
    }

    public static void topDeck(AbstractCard c, int i) {
        atb(new MakeTempCardInDrawPileAction(c, i, false, true));
    }

    public static void topDeck(AbstractCard c) {
        topDeck(c, 1);
    }

    public static void applyToTarget(AbstractCreature target, AbstractCreature source, AbstractPower po) {
        atb(new ApplyPowerActionButItCanFizzle(target, source, po, po.amount));
    }
    public static void applyToTargetTop(AbstractCreature target, AbstractCreature source, AbstractPower po) {
        att(new ApplyPowerActionButItCanFizzle(target, source, po, po.amount));
    }
    public static void applyToEnemy(AbstractMonster m, AbstractPower po) {
        atb(new ApplyPowerAction(m, AbstractDungeon.player, po, po.amount));
    }

    public static void applyToEnemyTop(AbstractMonster m, AbstractPower po) {
        att(new ApplyPowerAction(m, AbstractDungeon.player, po, po.amount));
    }

    public static void applyToSelf(AbstractPower po) {
        atb(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, po, po.amount));
    }

    public static void applyToSelfTop(AbstractPower po) {
        att(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, po, po.amount));
    }

    public static void applyToEnemyTemp(AbstractMonster m, AbstractPower po) {
        atb(new ApplyPowerAction(m, AbstractDungeon.player, po, po.amount));
        atb(new ApplyPowerAction(m, AbstractDungeon.player, new LosePowerPower(po.owner, po, po.amount)));
    }

    public static void applyToSelfTemp(AbstractPower po) {
        atb(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, po, po.amount));
        atb(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new LosePowerPower(AbstractDungeon.player, po, po.amount)));
    }

    public static void applyToSelfNextTurn(AbstractPower po) {
        atb(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new NextTurnPowerPower(AbstractDungeon.player, po)));
    }

    public static void applyToTargetNextTurn(AbstractCreature target, AbstractPower po) {
        atb(new ApplyPowerAction(target, target, new NextTurnPowerPower(target, po)));
    }

    public static void applyToTargetNextTurn(AbstractCreature target, AbstractCreature source, AbstractPower po) {
        atb(new ApplyPowerAction(target, source, new NextTurnPowerPower(target, po)));
    }

    public static void applyToTargetNextTurnTop(AbstractCreature target, AbstractPower po) {
        att(new ApplyPowerAction(target, target, new NextTurnPowerPower(target, po)));
    }

    public static void applyToTargetNextTurnTop(AbstractCreature target, AbstractCreature source, AbstractPower po) {
        att(new ApplyPowerAction(target, source, new NextTurnPowerPower(target, po)));
    }

    public static void dmg(AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect) {
        DamageAction action = new DamageAction(target, info, effect);
        if (RuinaMod.isMultiplayerConnected() && info.owner instanceof AbstractMonster) {
            ActionPatches.markActionForNoDamageSync(action);
            ActionPatches.markActionForNoDataSync(action);
        }
        atb(action);
    }

    public static void dmg(AbstractCreature target, DamageInfo info) {
        dmg(target, info, AbstractGameAction.AttackEffect.NONE);
    }

    public static void block(AbstractCreature target, int amount) {
        atb(new GainBlockAction(target, amount));
    }

    public static void flashImageVfx(Texture image, float duration) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                RenderHandPatch.plsDontRenderHandOrTips = true;
                AbstractDungeon.overlayMenu.hideCombatPanels();
                this.isDone = true;
            }
        });
        AbstractGameEffect appear = new VfxBuilder(image, (float) Settings.WIDTH / 2, (float)Settings.HEIGHT / 2, duration)
                .fadeIn(0.25f)
                .fadeOut(0.5f)
                .build();
        atb(new VFXAction(appear, duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                RenderHandPatch.plsDontRenderHandOrTips = false;
                AbstractDungeon.overlayMenu.showCombatPanels();
                this.isDone = true;
            }
        });
    }

    public static void fullScreenAnimation(ArrayList<Texture> frames, float frameDuration, float animationLength) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                RenderHandPatch.plsDontRenderHandOrTips = true;
                AbstractDungeon.overlayMenu.hideCombatPanels();
                this.isDone = true;
            }
        });
        VfxBuilder effect = new VfxBuilder(frames.get(0), (float) Settings.WIDTH / 2, (float)Settings.HEIGHT / 2, animationLength);
        for (int i = 1; i < frames.size(); i++) {
            int finalI = i;
            effect.triggerVfxAt(frameDuration * i, 1, new BiFunction<Float, Float, AbstractGameEffect>() {
                @Override
                public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                    return new VfxBuilder(frames.get(finalI), (float) Settings.WIDTH / 2, (float)Settings.HEIGHT / 2, frameDuration).build();
                }
            });
        }
        AbstractGameEffect finalEffect = effect.build();
        atb(new VFXAction(finalEffect, animationLength));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                RenderHandPatch.plsDontRenderHandOrTips = false;
                AbstractDungeon.overlayMenu.showCombatPanels();
                this.isDone = true;
            }
        });
    }

    public static void makePowerRemovable(AbstractCreature owner, String powerID) {
        AbstractPower power = owner.getPower(powerID);
        if (power instanceof AbstractUnremovablePower) {
            ((AbstractUnremovablePower) power).isUnremovable = false;
        }
    }

    public static void makePowerRemovable(AbstractPower power) {
        if (power instanceof AbstractUnremovablePower) {
            ((AbstractUnremovablePower) power).isUnremovable = false;
        }
    }
}
