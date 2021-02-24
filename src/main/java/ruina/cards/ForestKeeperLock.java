package ruina.cards;

import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cardmods.UnplayableMod;
import ruina.monsters.act3.punishingBird.PunishingBird;
import ruina.powers.PunishingBirdPunishmentPower;
import ruina.util.Wiz;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.monsterList;

public class ForestKeeperLock extends AbstractRuinaCard {
    public static final String ID = makeID(ForestKeeperLock.class.getSimpleName());
    private int BLOCK = 0;
    public ForestKeeperLock() {
        super(ID, 0, AbstractCard.CardType.STATUS, AbstractCard.CardRarity.SPECIAL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = BLOCK;
        selfRetain = true;
        exhaust = true;
    }
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Wiz.block(p, magicNumber);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                for(AbstractMonster m: monsterList()){
                    if(m instanceof PunishingBird){
                        PunishingBirdPunishmentPower punishment = (PunishingBirdPunishmentPower) m.getPower(PunishingBirdPunishmentPower.POWER_ID);
                        if(punishment != null){ punishment.setPunishment(false); }
                        m.takeTurn();
                        m.createIntent();
                        ((PunishingBird) m).decreaseChains();
                    }
                }
                this.isDone = true;
            }
        });
    }
    @Override
    public void applyPowers(){
        magicNumber = baseMagicNumber;
        int bonusBlock = 0;
        if(upgraded) {
            for (AbstractMonster m : monsterList()) {
                if (!m.isDead && !m.isDying) {
                    if (isAttackIntent(m.intent)) { bonusBlock += getEnemyDamage(m); }
                }
            }
            magicNumber += bonusBlock;
        }
        super.applyPowers();
        isMagicNumberModified = magicNumber != baseMagicNumber;
        if(isMagicNumberModified){ this.rawDescription = this.upgraded ? cardStrings.UPGRADE_DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0] : cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0]; }
        else { this.rawDescription = this.upgraded ? cardStrings.UPGRADE_DESCRIPTION: cardStrings.DESCRIPTION; }
        this.initializeDescription();
    }

    public void calculateCardDamage(AbstractMonster mo){
        magicNumber = baseMagicNumber;
        int bonusBlock = 0;
        if(!upgraded){ bonusBlock += getEnemyDamage(mo); }
        magicNumber += bonusBlock;
        super.calculateCardDamage(mo);
        isMagicNumberModified = magicNumber != baseMagicNumber;
        if(isMagicNumberModified){ this.rawDescription = this.upgraded ? cardStrings.UPGRADE_DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0] : cardStrings.DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0]; }
        else { this.rawDescription = this.upgraded ? cardStrings.UPGRADE_DESCRIPTION: cardStrings.DESCRIPTION; }
    }

    public int getEnemyDamage(AbstractMonster m) {
        int damage = 0;
        damage = (int) ReflectionHacks.getPrivate(m, AbstractMonster.class, "intentDmg");
        if (ReflectionHacks.getPrivate(m, AbstractMonster.class, "isMultiDmg")) { damage *= (int) ReflectionHacks.getPrivate(m, AbstractMonster.class, "intentMultiAmt"); }
        return damage;
    }

    @Override
    public void upp() {
        this.target = AbstractCard.CardTarget.ALL_ENEMY;
        rawDescription = cardStrings.UPGRADE_DESCRIPTION;
        this.initializeDescription();
    }

    public static boolean isAttackIntent(AbstractMonster.Intent intent) {
        return
                intent == AbstractMonster.Intent.ATTACK ||
                        intent == AbstractMonster.Intent.ATTACK_BUFF ||
                        intent == AbstractMonster.Intent.ATTACK_DEBUFF ||
                        intent == AbstractMonster.Intent.ATTACK_DEFEND;
    }
}