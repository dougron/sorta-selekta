package sorta_selekta.clip_injector;
import java.util.ArrayList;

import DataObjects.ableton_device_control_utils.DeviceParamInfo;
import DataObjects.ableton_device_control_utils.controller.ControllerInfo;
import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import DataObjects.ableton_live_clip.controller_data_clips.ControllerClip;
import DataObjects.ableton_live_clip.controller_data_clips.FunctionBreakPoint;
import DataObjects.ableton_live_clip.controller_data_clips.PitchBendClip;
import UDPUtils.OSCMessMaker;
import UDPUtils.StaticUDPConnection;
import sorta_selekta.SortaSelekta;
import sorta_selekta.node_list_generator.node.Node;

/*
 * version developed for the SortaSelekta, 2016, probably more awesome than the ClipInjector,
 * but all ultimately neccessary to replace with a thing which is less fiddly, like a LOMObject 
 * which can just do anything.......
 */
public class ClipInjectorObject {
	
	public int trackIndex;
	public int clipIndex;
	public int trackType = 0;
	public ArrayList<ControllerInfo> controllerList = new ArrayList<ControllerInfo>();
	public static StaticUDPConnection conn = new StaticUDPConnection(SortaSelekta.sendPort);


	
	public ClipInjectorObject(int trackIndex, int clipIndex, int trackType){
		this.trackIndex = trackIndex;
		this.clipIndex = clipIndex;
		this.trackType = trackType;
	}
	public void sendNode(Node node){
//		System.out.println(pno.toString() + " rendered");
//		receivedPNO = pno;
		OSCMessMaker mess;
//		LiveClip lc = node.clip;
//		node.clip.name = node.IDname + PlayletObject.nextRenderNumber();
//		lc.clipObjectIndex = getClipObjectIndex();
		mess = new OSCMessMaker();
//		System.out.println("ClipInjectorObject:---" + node.clip.toString());
		try{
			node.clip.name = node.ID;
		} catch (Exception ex){
			System.out.println("ClipInjectorObject.sendNote try/catch========");
			System.out.println("node:----\n" + node.toString());
			System.out.println("node.clip:----\n" + node.clip.toString());
			System.out.println("node.ID:------\n" + node.ID);
		}
		
		mess.addItem(injectMessage);
		addLiveClipToOSCMessage(node.clip, mess);
		conn.sendUDPMessage(mess);		
		sendControllerMessages(node.pnl.cList);
		
		mess = new OSCMessMaker();
		mess.addItem(DeviceParamInfo.injectMessage);
		if (trackType == DeviceParamInfo.ofTrackType){
			addPitchBendClipToOSCMessage(node.pnl.pb, mess);
			conn.sendUDPMessage(mess);
		}
		
	}
	public void sendClip(LiveClip lc){
		OSCMessMaker mess = new OSCMessMaker();
		mess.addItem(injectMessage);
		addLiveClipToOSCMessage(lc, mess);
		conn.sendUDPMessage(mess);		
	}
//	public void sendInitializationMessage(){
//		OSCMessMaker mm = new OSCMessMaker();
//		instrumentInitializationMessage(mm);
//		conn.sendUDPMessage(mm);
//	}
	
	public void instrumentInitializationMessage(OSCMessMaker mess){
		mess.addItem(newClipObjectString);
		mess.addItem(trackTypeArr[trackType]);
		if (trackType == ofMasterTrackType){
			mess.addItem(masterTrackString);
		} else {
			mess.addItem(trackIndex);			
		}
		mess.addItem(clipIndex);
	}
	public static void sendResetInitializationMessage(){
		// will reset the entire batch of ClipObjects in Live, hence static
		OSCMessMaker mess = new OSCMessMaker();	
    	mess.addItem(DeviceParamInfo.initString);
    	mess.addItem(DeviceParamInfo.resetString);
    	conn.sendUDPMessage(mess);
	}
	public void sendControllerInitializationMessages(){
		OSCMessMaker mess = new OSCMessMaker();	
		for (ControllerInfo ci: controllerList){
			mess = ci.makeOSCMessage();
			mess.addItem(DeviceParamInfo.controllerString, 0);
			mess.addItem(DeviceParamInfo.initString, 0);
//			System.out.println("ClipInjectorObject.sendControllerInitializationMessages:--------");
//			System.out.println(mess.toString());
			conn.sendUDPMessage(mess);
		}
	}
// privates-----------------------------------------------------------------
	public void addLiveClipToOSCMessage(LiveClip lc, OSCMessMaker mess){	//
		// format for injection, makes 'notes' and 'param' messages.....
		// live clip always gets a param message, but param messages can be sent on their own as well

		// notes message
		mess.addItem(DeviceParamInfo.trackTypeArr[trackType]);
		mess.addItem(trackIndex);
		mess.addItem(notesMessage);
		addLCNotesToOSCMess(lc, mess);
		mess.addItem(lc.noteList.size());		
		// param message
		mess.addItem(paramMessage);
		paramListForNotesMessageToOSCMessage(lc, mess);

	}
	private void addLCNotesToOSCMess(LiveClip lc, OSCMessMaker mess){		// for inject
		for (LiveMidiNote lmn: lc.noteList){
			mess.addItem(lmn.note);
			mess.addItem(lmn.position);
			mess.addItem(lmn.length);
			mess.addItem(lmn.velocity);
			mess.addItem(lmn.mute);
		}
	}
	private void paramListForNotesMessageToOSCMessage(LiveClip lc, OSCMessMaker mess){

		mess.addItem(lc.length);
		mess.addItem(lc.loopStart);
		mess.addItem(lc.loopEnd);
		mess.addItem(lc.startMarker);
		mess.addItem(lc.endMarker);
		mess.addItem(lc.signatureNumerator);
		mess.addItem(lc.signatureDenominator);
		mess.addItem(lc.offset);
		mess.addItem(lc.clip);
		mess.addItem(lc.track);
		mess.addItem(lc.name);
	}	
	private void sendControllerMessages(ArrayList<ControllerClip> cList){
		OSCMessMaker mess;
		ArrayList<String> hasList = new ArrayList<String>();
		if (cList.size() > 0){
			for (ControllerClip cc: cList){
				mess = new OSCMessMaker();
				mess.addItem(DeviceParamInfo.injectMessage);
				addControllerClipToOSCMess(cc, mess);
				conn.sendUDPMessage(mess);
				hasList.add(cc.name);
			}
		}
		
		for (ControllerInfo ci: controllerList){
			if (!hasList.contains(ci.name())){
				mess = ci.makeOffOSCMessage();
				conn.sendUDPMessage(mess);
			}
		}
	}
	public void addControllerClipsToOSCMessage(ArrayList<ControllerClip> cList, OSCMessMaker mess){
		ArrayList<Integer> indexList = new ArrayList<Integer>();
 		if (cList.size() > 0){
			for (ControllerClip cc: cList){
				addControllerClipToOSCMess(cc, mess);
				indexList.add(cc.controllerIndex);
			}
		}
 		// controller off messages
 //		for (ControllerInfo ci: controllerList){
 //			if (!indexList.contains(ci.controllerIndex())){
 //				ci.addOffMessageToOSC(mess, clipObjectIndex);
 //			}
 //		}
 		
	}
	private void addControllerClipToOSCMess(ControllerClip cc, OSCMessMaker mess){
		mess.addItem(DeviceParamInfo.trackTypeArr[trackType]);
		if (trackType == DeviceParamInfo.ofMasterTrackType){
			mess.addItem(trackTypeArr[trackType]);		
		} else {
			mess.addItem(trackIndex);
		}
		mess.addItem(controllerMessage);
//		mess.addItem(cc.controllerIndex);
		mess.addItem(cc.name);
		mess.addItem(cc.onOff);
		mess.addItem(cc.offValue);
		mess.addItem(cc.length);
		mess.addItem(cc.offset);
		mess.addItem(cc.resolution);
		for (FunctionBreakPoint fbp: cc.fbpList){
			mess.addItem(fbp.position);
			mess.addItem(fbp.value);
		}		
	}
	public void addPitchBendClipToOSCMessage(PitchBendClip pb, OSCMessMaker mess){
		if (pb == null){
			mess.addItem(pitchbendMessage);
			mess.addItem(trackIndex);			// clipObjectIndex is of no use to the pitch bend unit as it is on the actual track that the virtual instrument is on in Live
			mess.addItem(0);			// this is an off message 0 means off.
			mess.addItem(DeviceParamInfo.defaultPitchBendOffValue);
		} else {
			mess.addItem(pitchbendMessage);
			mess.addItem(trackIndex);
			mess.addItem(pb.onOff);
			mess.addItem(pb.offValue);
			mess.addItem(pb.length);
			mess.addItem(pb.offset);
			mess.addItem(pb.resolution);
			mess.addItem(pb.pitchBendRange);
			for (FunctionBreakPoint fbp: pb.fbpList){
				mess.addItem(fbp.position);
				mess.addItem(fbp.value);
			}
		}
		
	}
	
// constants ---------------------------------------------------------------
	public static final String injectMessage = "inject";
	public static final String notesMessage = "notes";
	public static final String paramMessage = "param";
	public static final String controllerMessage = "controller";
	public static final String pitchbendMessage = "pitchbend";
	public static final String clipObjInitMessage = "clipObjInit";
	
	public static final String newClipObjectString = "newClipObject";
	
	public static final String trackString = "track";
	public static final String returnTrackString = "returntrack";
	public static final String masterTrackString = "master";
	public static final String GLOBAL = "global";
	public static final String TEMPO = "tempo";
	
	public static final int ofTrackType = 0;
	public static final int ofReturnTrackType = 1;
	public static final int ofMasterTrackType = 2;
	public static final String[] trackTypeArr = new String[]{trackString, returnTrackString, masterTrackString};

	
}
