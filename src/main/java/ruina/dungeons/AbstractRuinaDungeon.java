package ruina.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import ruina.RuinaMod;
import ruina.monsters.act1.Orchestra;
import ruina.monsters.act1.blackSwan.BlackSwan;
import ruina.monsters.act1.fairyFestival.FairyQueen;
import ruina.monsters.act2.Jester.JesterOfNihil;
import ruina.monsters.act2.Ozma;
import ruina.monsters.act3.Twilight;
import ruina.monsters.act3.seraphim.Prophet;
import ruina.monsters.act3.silentGirl.SilentGirl;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.theHead.Baral;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Argalia;
import ruina.scenes.RuinaScene;

import java.util.ArrayList;

public class AbstractRuinaDungeon extends CustomDungeon {
    public Floor floor;

    public AbstractRuinaDungeon(String NAME, String ID, String event, boolean genericEvents, int weak, int strong, int elite) {
        super(NAME, ID, event, genericEvents, weak, strong, elite);
        AbstractDungeon.shrineList.clear(); //No shrines from other mods please
    }

    public AbstractRuinaDungeon(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
        AbstractDungeon.shrineList.clear();
    }

    public AbstractRuinaDungeon(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
        AbstractDungeon.shrineList.clear();
    }

    public enum Floor {
        MALKUTH,
        YESOD,
        HOD,
        NETZACH,
        TIPHERETH,
        GEBURA,
        CHESED,
        BINAH,
        HOKMA,
        ROLAND,
        GUESTS,
        BLACK_SILENCE
    }

    public void setFloor() {
        if (bossKey != null) {
            if (bossKey.equals(FairyQueen.ID)) {
                floor = Floor.MALKUTH;
            } else if (bossKey.equals(EncounterIDs.NOTHING_DER)) {
                floor = Floor.YESOD;
            } else if (bossKey.equals(BlackSwan.ID)) {
                floor = Floor.HOD;
            } else if (bossKey.equals(Orchestra.ID)) {
                floor = Floor.NETZACH;
            } else if (bossKey.equals(JesterOfNihil.ID)) {
                floor = Floor.TIPHERETH;
            } else if (bossKey.equals(EncounterIDs.RED_AND_WOLF)) {
                floor = Floor.GEBURA;
            } else if (bossKey.equals(Ozma.ID)){
                floor = Floor.CHESED;
            } else if (bossKey.equals(Twilight.ID)){
                floor = Floor.BINAH;
            } else if (bossKey.equals(Prophet.ID)){
                floor = Floor.HOKMA;
            } else if (bossKey.equals(SilentGirl.ID)){
                floor = Floor.ROLAND;
            } else if (bossKey.equals(Argalia.ID)){
                floor = Floor.GUESTS;
            } else if (bossKey.equals(BlackSilence4.ID) || bossKey.equals(Baral.ID)){
                floor = Floor.BLACK_SILENCE;
            } else {
                floor = Floor.GEBURA;
            }
            setMusic();
            if (AbstractDungeon.currMapNode != null) {
                AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom());
            }
        }
    }

    public void setMusic() {
        if (floor != null) {
            switch (floor) {
                case MALKUTH:
                    this.setMainMusic(RuinaMod.makeMusicPath("Malkuth2.ogg"));
                    break;
                case YESOD:
                    this.setMainMusic(RuinaMod.makeMusicPath("Yesod2.ogg"));
                    break;
                case HOD:
                    this.setMainMusic(RuinaMod.makeMusicPath("Hod1.ogg"));
                    break;
                case NETZACH:
                    this.setMainMusic(RuinaMod.makeMusicPath("Netzach2.ogg"));
                    break;
                case TIPHERETH:
                    this.setMainMusic(RuinaMod.makeMusicPath("Tiphereth2.ogg"));
                    break;
                case GEBURA:
                    this.setMainMusic(RuinaMod.makeMusicPath("Gebura2.ogg"));
                    break;
                case CHESED:
                    this.setMainMusic(RuinaMod.makeMusicPath("Chesed2.ogg"));
                    break;
                case BINAH:
                    this.setMainMusic(RuinaMod.makeMusicPath("Binah2.ogg"));
                    break;
                case HOKMA:
                    this.setMainMusic(RuinaMod.makeMusicPath("Hokma2.ogg"));
                    break;
                case ROLAND:
                    this.setMainMusic(RuinaMod.makeMusicPath("Keter1.ogg"));
                    break;
                case GUESTS:
                    this.setMainMusic(RuinaMod.makeMusicPath("Lobby.ogg"));
                    break;
                case BLACK_SILENCE:
                    this.setMainMusic(RuinaMod.makeMusicPath("Calm.ogg"));
                    break;
                default:
                    this.setMainMusic(RuinaMod.makeMusicPath("Gebura2.ogg"));
                    break;
            }
        }
    }

    @Override
    public AbstractScene DungeonScene() {
        return new RuinaScene();
    }

    @Override
    protected void initializeShrineList() {
    }

    @Override
    protected void initializeEventList() {
        // Events are added via BaseMod in receivePostInitialize()
    }
}