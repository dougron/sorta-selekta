package sorta_selekta.setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import DataObjects.ableton_device_control_utils.DeviceParamInfo;
import DataObjects.ableton_device_control_utils.PanInfo;
import DataObjects.ableton_device_control_utils.SendInfo;
import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.combo_variables.IntAndString;
import PlugIns.ED;
import PlugIns.PlugInBassAddEmbellishmentOne;
import PlugIns.PlugInBassFromRhythmBuffer;
import PlugIns.PlugInEscapeTone;
import PlugIns.PlugInHatFromRhythmBuffer;
import PlugIns.PlugInHatOff;
import PlugIns.PlugInHatOffAccent;
import PlugIns.PlugInHatOn;
import PlugIns.PlugInKeysPad;
import PlugIns.PlugInKik58Euclidean;
import PlugIns.PlugInKikFourOnFloor;
import PlugIns.PlugInKikFromAccentTemplate;
import PlugIns.PlugInKikFunkOne;
import PlugIns.PlugInKikTwoOnFloor;
import PlugIns.PlugInLegato;
import PlugIns.PlugInMelodyContourGuideToneEveryBar;
import PlugIns.PlugInMelodyContourGuideToneFromPosList;
import PlugIns.PlugInNoOverlaps;
import PlugIns.PlugInRandomBendOnLongNote;
import PlugIns.PlugInSlowWah;
import PlugIns.PlugInSnr58Euclidean;
import PlugIns.PlugInSnrBackBeat;
import PlugIns.PlugInSnrHalfBackBeat;
import PlugIns.PlugInSnrToRim;
import PlugIns.PlugInSyncopate;
import ResourceUtils.AccentTemplate;
import ResourceUtils.ChordForm;
import ResourceUtils.ContourData;
import TestUtils.TestData;
import sorta_selekta.clip_injector.ClipInjectorObject;
import sorta_selekta.node_list_generator.NodeListGenerator;

public class Setup_SelektaAnalysis_001 extends NodeManagerSetupObject{
	
	
	
	
	private String chordPath = "D:/Documents/repos/ChordProgressionTestFiles/";
	private String chordsExtension = ".chords.liveclip";
	private String[] chordProgressionArr = new String[] {
//			"LongDescendingFlamenco", 
			"SensitiveWomanLong"
	};

	
	public ArrayList<NodeListGenerator> makeNLGList(){
		ArrayList<NodeListGenerator> nlgList = new ArrayList<NodeListGenerator>();
		ArrayList<AccentTemplate> atList = getATList();
		ArrayList<ChordForm> cfList = getCFList();
		ArrayList<ContourData> cdList = getCDList();

		nlgList.add(makeKikNLG(atList, cfList));
		nlgList.add(makeSnrNLG(atList, cfList));
		nlgList.add(makeHatNLG(atList, cfList));
		nlgList.add(makeBassNLG(atList, cfList));
		nlgList.add(makeKeysNLG(atList, cfList, "Keys", 4, 10));
		nlgList.add(makeKeysNLG(atList, cfList, "Keys2", 5, 11));		// two keys channel and no melody

//		nlgList.add(makeMelodyNLG(atList, cfList, cdList));		// concurrency error here.....
		
		return nlgList;
	}
	
	
// privates ----------------------------------------------------------------
	private void addControllers(ClipInjectorObject cio){

		cio.controllerList.add(new SendInfo("delaySend", cio.trackIndex, DeviceParamInfo.defaultDelaySendIndex, DeviceParamInfo.defaultDelayOffValue, cio.trackType));
		cio.controllerList.add(new DeviceParamInfo(DeviceParamInfo.default_HP, cio.trackIndex, cio.trackType));
	}
	
	
	private void addKeysControllers(ClipInjectorObject cio){
		cio.controllerList.add(new SendInfo("delaySend", cio.trackIndex, DeviceParamInfo.defaultDelaySendIndex, DeviceParamInfo.defaultDelayOffValue, cio.trackType));
		cio.controllerList.add(new DeviceParamInfo(DeviceParamInfo.default_HP, cio.trackIndex, cio.trackType));
		cio.controllerList.add(new PanInfo("pan", cio.trackIndex, DeviceParamInfo.defaultPanOffValue, cio.trackType));
//		cio.controllerList.add(new VolumeInfo("vol", cio.trackIndex, 1.0, cio.trackType));		// 1.0 test value to see if this is proportionate to the level set on the mixer or an absolute level
	}
	
	
	private NodeListGenerator makeMelodyNLG(ArrayList<AccentTemplate> atList, ArrayList<ChordForm> cfList, ArrayList<ContourData> cdList){
		NodeListGenerator nlg = new NodeListGenerator("Melody", "M");
		nlg.atList = atList;
		nlg.cfList = cfList;
		nlg.cdList = cdList;
		addMelodyGenerators(nlg);
		addMelodyProcessors(nlg);		
		setMelodySortOptions(nlg);	// for now using the same
		nlg.makeNodeList();
		nlg.playInject = new ClipInjectorObject(5, 0, ClipInjectorObject.ofTrackType);
		nlg.cueInject = new ClipInjectorObject(11, 0, ClipInjectorObject.ofTrackType);
		addControllers(nlg.playInject);
		addControllers(nlg.cueInject);
		return nlg;
	}
	
	
	private NodeListGenerator makeKeysNLG(ArrayList<AccentTemplate> atList, ArrayList<ChordForm> cfList, String name, int trackIndex, int cueTrackIndex){
		NodeListGenerator nlg = new NodeListGenerator(name, "Y");
		nlg.atList = atList;
		nlg.cfList = cfList;
		addKeysGenerators(nlg);
		addKeysProcessors(nlg);
		addKeysAlwaysInPlugs(nlg);
		setKikSortOptions(nlg);	// for now using the same
		nlg.makeNodeList();
		nlg.playInject = new ClipInjectorObject(trackIndex, 0, ClipInjectorObject.ofTrackType);
		nlg.cueInject = new ClipInjectorObject(cueTrackIndex, 0, ClipInjectorObject.ofTrackType);
		addKeysControllers(nlg.playInject);
		addKeysControllers(nlg.cueInject);
		return nlg;
	}
	
	
	private void addKeysAlwaysInPlugs(NodeListGenerator nlg) {
		nlg.alwaysInList.add(new PlugInNoOverlaps());
	}
	

	private NodeListGenerator makeBassNLG(ArrayList<AccentTemplate> atList, ArrayList<ChordForm> cfList){
		NodeListGenerator nlg = new NodeListGenerator("Bass", "B");
		nlg.atList = atList;
		nlg.cfList = cfList;
		addBassGenerators(nlg);
		addBassProcessors(nlg);
		setKikSortOptions(nlg);	// for now using the same
		nlg.makeNodeList();
		nlg.playInject = new ClipInjectorObject(3, 0, ClipInjectorObject.ofTrackType);
		nlg.cueInject = new ClipInjectorObject(9, 0, ClipInjectorObject.ofTrackType);
		addControllers(nlg.playInject);
		addControllers(nlg.cueInject);
		return nlg;
	}
	
	
	private NodeListGenerator makeHatNLG(ArrayList<AccentTemplate> atList, ArrayList<ChordForm> cfList){
		NodeListGenerator nlg = new NodeListGenerator("Hat", "H");
		nlg.atList = atList;
		nlg.cfList = cfList;
		addHatGenerators(nlg);
//		addHatProcessors(nlg);
		addKikProcessors(nlg);
		setKikSortOptions(nlg);	// for now using the same
		nlg.makeNodeList();
		nlg.playInject = new ClipInjectorObject(2, 0, ClipInjectorObject.ofTrackType);
		nlg.cueInject = new ClipInjectorObject(8, 0, ClipInjectorObject.ofTrackType);
		addControllers(nlg.playInject);
		addControllers(nlg.cueInject);
		return nlg;
	}
	
	
	private NodeListGenerator makeSnrNLG(ArrayList<AccentTemplate> atList, ArrayList<ChordForm> cfList){
		NodeListGenerator nlg = new NodeListGenerator("Snr", "S");
		nlg.atList = atList;
		nlg.cfList = cfList;
		addSnrGenerators(nlg);
		addKikProcessors(nlg);
		setKikSortOptions(nlg);	// for now using the same
		nlg.makeNodeList();
		nlg.playInject = new ClipInjectorObject(1, 0, ClipInjectorObject.ofTrackType);
		nlg.cueInject = new ClipInjectorObject(7, 0, ClipInjectorObject.ofTrackType);
		addControllers(nlg.playInject);
		addControllers(nlg.cueInject);
		return nlg;
	}
	
	
	public NodeListGenerator makeKikNLG(ArrayList<AccentTemplate> atList, ArrayList<ChordForm> cfList){
		NodeListGenerator nlg = new NodeListGenerator("Kik", "K");
		nlg.atList = atList;
		nlg.cfList = cfList;
		addKikGenerators(nlg);
		addKikProcessors(nlg);
		setKikSortOptions(nlg);
		nlg.makeNodeList();
		nlg.playInject = new ClipInjectorObject(0, 0, ClipInjectorObject.ofTrackType);
		nlg.cueInject = new ClipInjectorObject(6, 0, ClipInjectorObject.ofTrackType);
		addControllers(nlg.playInject);
		addControllers(nlg.cueInject);
		return nlg;
	}
	

	private void setMelodySortOptions(NodeListGenerator nlg){
		nlg.sortItemList = new IntAndString[]{
				LiveClip.scoreNameObjectMap.get(LiveClip.SYNC),
				LiveClip.scoreNameObjectMap.get(LiveClip.RDENS),
				LiveClip.scoreNameObjectMap.get(LiveClip.REGUL),
				LiveClip.scoreNameObjectMap.get(LiveClip.SIMIL),
				LiveClip.scoreNameObjectMap.get(LiveClip.EVEN_4_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.ODD_4_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.EVEN_8_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.ODD_8_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.EVEN_16_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.ODD_16_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.ZONE_2_COUNT),
		};
	}
	
	
	private void setKikSortOptions(NodeListGenerator nlg){
		nlg.sortItemList = new IntAndString[]{
				LiveClip.scoreNameObjectMap.get(LiveClip.SYNC),
				LiveClip.scoreNameObjectMap.get(LiveClip.RDENS),
				LiveClip.scoreNameObjectMap.get(LiveClip.REGUL),
				LiveClip.scoreNameObjectMap.get(LiveClip.SIMIL),
				LiveClip.scoreNameObjectMap.get(LiveClip.EVEN_4_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.ODD_4_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.EVEN_8_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.ODD_8_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.EVEN_16_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.ODD_16_POSITION),
				LiveClip.scoreNameObjectMap.get(LiveClip.ZONE_2_COUNT),
		};
	}
	

	private void addMelodyGenerators(NodeListGenerator nlg){
		nlg.genList.add(new PlugInMelodyContourGuideToneEveryBar());
		nlg.genList.add(new PlugInMelodyContourGuideToneFromPosList(new double[]{3.0, 2.0, 3.0}));
	}
	private void addKeysGenerators(NodeListGenerator nlg){
		nlg.genList.add(new PlugInKeysPad());
//		nlg.genList.add(new PlugInKeysFromInterlockBuffer());	// figure out how this works with AccentTemplate
	}
	
	
	private void addBassGenerators(NodeListGenerator nlg){
		nlg.genList.add(new PlugInBassFromRhythmBuffer());
	}	
	
	
	private void addHatGenerators(NodeListGenerator nlg){
		nlg.genList.add(new PlugInHatOn());
		nlg.genList.add(new PlugInHatOff());
		nlg.genList.add(new PlugInHatFromRhythmBuffer());
	}
	
	
	private void addSnrGenerators(NodeListGenerator nlg){
		nlg.genList.add(new PlugInSnrHalfBackBeat());
		nlg.genList.add(new PlugInSnrBackBeat());
		nlg.genList.add(new PlugInSnrToRim());
		nlg.genList.add(new PlugInSnr58Euclidean());
	}

	
	private void addMelodyProcessors(NodeListGenerator nlg){
//		nlg.proList.add(new PlugInEscapeTone(
//				new double[]{-0.5, -1.0},
//				new double[]{1.0, 1.0},
//				0.5,
//				new ED("s", 1),
//				new ED("s", -1)
//				));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.5, -1.0, -1.5, -2.0},
				new double[]{1.0, 1.0, 1.0, 1.0},
				0.8,
				new ED[]{new ED("s", 0), new ED("d", 1), new ED("d", -1)},
				new double[]{1.0, 1.0, 1.0, 1.0, 1.0}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.5, -1.0, -1.5, -2.0},
				new double[]{1.0, 1.0, 1.0, 1.0},
				0.8,
				new ED[]{new ED("s", 0), new ED("d", 1), new ED("d", -1)},
				new double[]{1.0, 1.0, 1.0, 1.0, 1.0}));
		nlg.proList.add(new PlugInEscapeTone(
				new double[]{-0.25, -0.5},
				new double[]{1.0, 2.0},
				0.5,
				new ED("d", 1),
				new ED("s", -1)
				));
		nlg.proList.add(new PlugInSyncopate(
				new double[]{-1.0, 1.0, 1.5, -1.5},
				new double[]{1.0, 1.0, 1.0, 1.0},
				1.0));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.5, -1.0},
				new double[]{1.0, 3.0},
				1.0,
				new ED[]{new ED("s", 0), new ED("d", 1), new ED("s", -1)},
				new double[]{1.0, 1.0, 0.5}));
		nlg.proList.add(new PlugInLegato(new double[]{1.0}));
		nlg.proList.add(new PlugInLegato(new double[]{1.0, 0.0}));
		nlg.proList.add(new PlugInRandomBendOnLongNote(4.0, 1.0));
	}
	
	
	private void addKeysProcessors(NodeListGenerator nlg){
//		nlg.proList.add(new PlugInSyncopate(
//				new double[]{-0.25, -0.5, 0.25, 0.5},
//				new double[]{1.0, 1.0, 1.0, 1.0},
//				0.5));
//		nlg.proList.add(new PlugInSyncopate(
//				new double[]{-0.5, -1.0, -1.5, 1.0},
//				new double[]{1.0, 1.0, 1.0, 1.0},
//				0.5));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-1.0, -2.0, -3.0, -4.0},
				new double[]{1.0, 1.0, 1.0, 1.0},
				1.0,
				new ED[]{new ED("s", 0), new ED("c", 1), new ED("c", -1)},
				new double[]{1.0, 1.0, 1.0}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.25, -0.5, -0.75, -1.0},
				new double[]{1.0, 3.0, 1.0, 3.0},
				1.0,
				new ED[]{new ED("s", 0), new ED("d", 1), new ED("s", -1)},
				new double[]{1.0, 1.0, 0.5}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.5, -1.0, -1.5, -2.0},
				new double[]{1.0, 1.0, 1.0, 1.0},
				1.0,
				new ED[]{new ED("s", 0), new ED("d", 1), new ED("c", -1), new ED("c", 1)},
				new double[]{1.0, 1.0, 1.0, 1.0}));
		nlg.proList.add(new PlugInLegato(new double[]{0.0}));
		nlg.proList.add(new PlugInLegato(new double[]{1.0, 0.0}));
		nlg.proList.add(new PlugInLegato(new double[]{1.0, 1.0, 0.0}));	
		nlg.proList.add(new PlugInRandomBendOnLongNote(4.0, 1.0));
		nlg.proList.add(new PlugInSlowWah(1, "HP"));
//		nlg.proList.add(new PlugInSlowWah(0, "delaySend"));		// this is hacking the PlugInSlowWah to send to the 'delaySend' item, to test the sends 
//		nlg.proList.add(new PlugInSlowWah(0, "pan"));		// this is hacking the PlugInSlowWah to send to the 'delaySend' item, to test the sends 
//		nlg.proList.add(new PlugInSlowWah(0, "vol"));		// volume controller plugins are a bad idea

	}
	
	
	private void addBassProcessors(NodeListGenerator nlg){
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.25, -0.5},
				new double[]{1.0, 3.0},
				1.0,
				new ED[]{new ED("s", 0)},
				new double[]{1.0}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.25, -0.5},
				new double[]{1.0, 3.0},
				1.0,
				new ED[]{new ED("s", 0), new ED("s", -1)},
				new double[]{1.0, 1.0}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.25, -0.5},
				new double[]{1.0, 3.0},
				1.0,
				new ED[]{new ED("s", 0), new ED("s", -1), new ED("s", -5)},
				new double[]{1.0, 1.0, 1.0}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.5, -1.0},
				new double[]{1.0, 3.0},
				1.0,
				new ED[]{new ED("s", 0), new ED("s", -1), new ED("s", -5)},
				new double[]{1.0, 1.0, 1.0}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.75, -1.0, -1.25},
				new double[]{1.0, 3.0, 1.5},
				1.0,
				new ED[]{new ED("s", 0), new ED("s", -1), new ED("s", -5)},
				new double[]{1.0, 1.0, 1.0}));
		nlg.proList.add(new PlugInLegato(new double[]{1.0, 0.0}));
		nlg.proList.add(new PlugInLegato(new double[]{1.0, 1.0, 0.0}));	
		nlg.proList.add(new PlugInRandomBendOnLongNote(1.0, 1.0));
	}
	
	
	private void addHatProcessors(NodeListGenerator nlg){
		nlg.proList.add(new PlugInHatOffAccent());
//		nlg.proList.add(new PlugInHatOn());
//		nlg.proList.add(new PlugInHatOff());
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.25, -0.5},
				new double[]{1.0, 1.0},
				1.0,
				new ED[]{new ED("s", 0)},
				new double[]{1.0}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.75, -1.0},
				new double[]{1.0, 1.0},
				1.0,
				new ED[]{new ED("s", 0)},
				new double[]{1.0}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.5, -1.0},
				new double[]{1.0, 1.0},
				1.0,
				new ED[]{new ED("s", 0)},
				new double[]{1.0}));
	}
	
	
	private void addKikProcessors(NodeListGenerator nlg){
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.5},
				new double[]{1.0},
				0.5,
				new ED[]{new ED("s", 0)},
				new double[]{1.0},
				"_8ths"));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.25},
				new double[]{1.0},
				0.5,
				new ED[]{new ED("s", 0)},
				new double[]{1.0},
				"_16ths"));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.75, -1.0},
				new double[]{1.0, 3.0},
				0.35,
				new ED[]{new ED("s", 0)},
				new double[]{1.0}));
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.75, -1.0},
				new double[]{1.0, 3.0},
				1.0,
				new ED[]{new ED("s", 0)},
				new double[]{1.0}));
	}
	
	
	private void addKikGenerators(NodeListGenerator nlg){
		nlg.genList.add(new PlugInKikTwoOnFloor());
		nlg.genList.add(new PlugInKikFunkOne());
		nlg.genList.add(new PlugInKikFromAccentTemplate());
		nlg.genList.add(new PlugInKikFourOnFloor());
		nlg.genList.add(new PlugInKik58Euclidean());
	}
	
	
	private ArrayList<AccentTemplate> getATList(){
		ArrayList<AccentTemplate> atList = new ArrayList<AccentTemplate>();
		atList.add(new AccentTemplate(TestData.accentTemplateOne()));
		//nlg.atList.add(new AccentTemplate(TestData.accentTemplateTwo()));
		return atList;
	}
	
	
	private ArrayList<ChordForm> getCFList(){
		ArrayList<ChordForm> cfList = new ArrayList<ChordForm>();
		for (String fileName: chordProgressionArr) {
			try {
				String path = chordPath + fileName + chordsExtension;
				File file = new File(path);
				LiveClip lc = new LiveClip(0, 0);
				BufferedReader b = new BufferedReader(new FileReader(file));
				lc.instantiateClipFromBufferedReader(b);
				cfList.add(new ChordForm(lc));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		cfList.add(new ChordForm(TestData.liveClipForTestForm()));
		
		return cfList;
	}
	
	
	private ArrayList<ContourData> getCDList(){
		ArrayList<ContourData> cdList = new ArrayList<ContourData>();
		cdList.add(TestData.upContour());
		cdList.add(TestData.downContour());
		cdList.add(TestData.updownContour());
		cdList.add(TestData.downupContour());
		return cdList;
	}
}
