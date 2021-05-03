package ruina.util;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.actions.chr_angela.DamageAllAction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;

public class actionShortcuts {
    /** General Utils */
    // A simple way to refer to the player.
    public static AbstractPlayer p() { return AbstractDungeon.player; }
    // A simple way to call addToBottom for actions.
    public static void atb(AbstractGameAction action) { AbstractDungeon.actionManager.addToBottom(action); }
    // A simple way to call AddToTop for actions.
    public static void att(AbstractGameAction action) { AbstractDungeon.actionManager.addToTop(action); }
    // Various ways of dealing damage
    public static void doDmg(AbstractCreature target, int amount) { doDmg(target, amount, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE); }
    public static void doDmg(AbstractCreature target, int amount, DamageInfo.DamageType dt) { doDmg(target, amount, dt, AbstractGameAction.AttackEffect.NONE); }
    public static void doDmg(AbstractCreature target, int amount, AbstractGameAction.AttackEffect ae) { doDmg(target, amount, DamageInfo.DamageType.NORMAL, ae); }
    public static void doDmg(AbstractCreature target, int amount, DamageInfo.DamageType dt, AbstractGameAction.AttackEffect ae) { doDmg(target, amount, dt, ae, false); }
    public static void doDmg(AbstractCreature target, int amount, DamageInfo.DamageType dt, AbstractGameAction.AttackEffect ae, boolean fast) { doDmg(target, amount, dt, ae, fast, false); }
    public static void doDmg(AbstractCreature target, int amount, DamageInfo.DamageType dt, AbstractGameAction.AttackEffect ae, boolean fast, boolean top) {
        if (top) { att(new DamageAction(target, new DamageInfo(p(), amount, dt), ae, fast));
        } else { atb(new DamageAction(target, new DamageInfo(p(), amount, dt), ae, fast)); }
    }
    // A simple way to deal damage to all enemies.
    public static void doAllDmg(int amount, AbstractGameAction.AttackEffect ae, boolean top) {
        if (top) { att(new DamageAllAction(p(), amount, false, DamageInfo.DamageType.NORMAL, ae, false));
        } else { atb(new DamageAllAction(p(), amount, false, DamageInfo.DamageType.NORMAL, ae, false)); }
    }

    public static void doAllDmg(int amount, DamageInfo.DamageType dt, AbstractGameAction.AttackEffect ae, boolean top) {
        if (top) { att(new DamageAllAction(p(), amount, false, dt, ae, false));
        } else { atb(new DamageAllAction(p(), amount, false, dt, ae, false)); }
    }

    // A simple way to Gain Block.
    public static void doDef(int amount) { doDef(amount, false); }
    public static void doDef(int amount, boolean top) {
        if (top) { att(new GainBlockAction(p(), p(), amount));
        } else { atb(new GainBlockAction(p(), p(), amount)); }
    }
    // Various ways of applying powers.
    public static void doPow(AbstractCreature target, AbstractPower p) { doPow(target, p, false); }
    public static void doPow(AbstractCreature target, AbstractPower p, boolean top) { doPow(p(), target, p, top); }
    public static void doPow(AbstractCreature source, AbstractCreature target, AbstractPower p, boolean top) {
        if (top) { att(new ApplyPowerAction(target, source, p, p.amount));
        } else { atb(new ApplyPowerAction(target, source, p, p.amount)); }
    }
    public static void doDraw(int number) { doDraw(number, false); }
    public static void doDraw(int number, boolean top) {
        if (top) { att(new DrawCardAction(number));
        } else { atb(new DrawCardAction(number)); }
    }
    // Various ways of displaying VFX.
    public static void doVfx(AbstractGameEffect gameEffect) { atb(new VFXAction(gameEffect)); }
    public static void doVfx(AbstractGameEffect gameEffect, boolean top) { att(new VFXAction(gameEffect)); }
    public static void doVfx(AbstractGameEffect gameEffect, float duration) { atb(new VFXAction(gameEffect, duration)); }
    // Plays a card again, used for repeating effects
    public static void playCardAdditionalTime(AbstractCard card, AbstractMonster target) {
        AbstractCard tmp = card.makeSameInstanceOf();
        p().limbo.addToBottom(tmp);
        tmp.current_x = card.current_x;
        tmp.current_y = card.current_y;
        tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
        tmp.target_y = (float)Settings.HEIGHT / 2.0F;
        if (tmp.cost > 0) { tmp.freeToPlayOnce = true; }
        if (target != null) { tmp.calculateCardDamage(target); }
        tmp.purgeOnUse = true;
        AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, target, card.energyOnUse, true, true), true);
    }
    /** Combat Utils */
    // Fetches all alive monsters.
    public static ArrayList<AbstractMonster> getAliveMonsters() { return AbstractDungeon.getMonsters().monsters.stream().filter(m -> !m.isDeadOrEscaped()).collect(Collectors.toCollection(ArrayList::new)); }
    // Fetches a random monster who is alive.
    public static AbstractMonster getRandomAliveMonster(MonsterGroup group, Predicate<AbstractMonster> isCandidate, Random rng) { return getRandomMonster(group, m -> (!m.halfDead && !m.isDying && !m.isEscaping && isCandidate.test(m)), rng); }
    // Fetches a random monster (No Predicate)
    public static AbstractMonster getRandomAliveMonster(MonsterGroup group, Random rng) { return getRandomMonster(group, m -> (!m.halfDead && !m.isDying && !m.isEscaping), rng); }
    // Fetches a random monster.
    public static AbstractMonster getRandomMonster(MonsterGroup group, Predicate<AbstractMonster> isCandidate, Random rng) {
        List<AbstractMonster> candidates = group.monsters.stream().filter(isCandidate).collect(Collectors.toList());
        if (candidates.isEmpty()) { return null; }
        return candidates.get(rng.random(0, candidates.size() - 1));
    }
    // Fetches a random monster.
    public static int getLogicalCardCost(AbstractCard c) {
        if (c.costForTurn > 0 && !c.freeToPlayOnce) { return c.costForTurn; }
        return 0;
    }
    /** Intent Utilities */
    // Searches for Debuff Intents.
    public static boolean isDebuffIntent(Intent intent) {
        return
                intent == Intent.STRONG_DEBUFF ||
                        intent == Intent.ATTACK_DEBUFF ||
                        intent == Intent.DEBUFF ||
                        intent == Intent.DEFEND_DEBUFF;
    }
    // Searches for Attack Intents.
    public static boolean isAttackIntent(Intent intent) {
        return
                intent == Intent.ATTACK ||
                        intent == Intent.ATTACK_BUFF ||
                        intent == Intent.ATTACK_DEBUFF ||
                        intent == Intent.ATTACK_DEFEND;
    }
    public static boolean isBlockIntent(Intent intent) {
        return
                intent == Intent.DEFEND ||
                        intent == Intent.DEFEND_BUFF ||
                        intent == Intent.DEFEND_DEBUFF ||
                        intent == Intent.ATTACK_DEFEND;
    }
    // Searches for Buff Intents.
    public static boolean isBuffIntent(Intent intent) {
        return
                intent == Intent.BUFF ||
                        intent == Intent.ATTACK_BUFF ||
                        intent == Intent.DEFEND_BUFF;
    }

    public static int getPercentageInc(float val) { return MathUtils.floor((val - 1.0F) * 100.0F); }
}

