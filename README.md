This is a project from 2015 that explores an algorithmic music generator combined with a graphic interface that facilitates traversing the possible musical material in some ordered fashion. The application runs in Java and transfers music to Ableton via UDP and the Max4Live Live Object Model.

The Java app

Referring to the 'SortaSelekta_kik_panel_example.png':  (yeah I know its not beautiful….)



1	List of generation plugins for the instrumental part (5) included in '8'. More about plugins below.
2	List of generation plugins to exclude from list. This thins out the number of nodes in '8'. 
3	y axis sorting algorithm for 8
4	x axis sorting algorithm for 8
5	Instrument part selector (each instrument has its own list of plugins)
6	the 'Play' button. Does what it says on the box. Also 'Stop' button
7	The undo list. 
8	The node panel. Each node (blue dot) represents a combination of plugins. Can be selected with the mouse or traversed with the numpad. Red circled node is the currently playing node for the selected instrument. Green circle (not visible in the above graphic) is the cued node (more on this in the rundown on the ableton set that this app talks to). 'OFF' node bottom left sends no notes and behaves like any other node


The intention (back when I thought this would be the be all and end all of live performance interfaces) was to not use the mouse, so there are some important keys:

numpad 	- move the cued node up/down/left/right and diagonally
P 	- make all cued nodes into played nodes
wasd	- left/right changes instrument selection
	-up/down cues items from the undo list
Enter	 - play/stop
	

Plugins

The musical material in generated using a pipeline of processes ('plugins'). These are divided up as follows, and are not apparent from the names in lists 1 and 2:

	1. Resources - structural information such as chord progressions and accent templates
	2. Generators - generate basic parts
	3. Processors - add extra notes or controller informations to a part

In the case of the kik instrument in 'SortaSelekta_kik_panel_example.png':

Bbmin-F#maj-C#maj-G#Maj				chord progression
JJHD						accent template
Kik_2on4 and all the items starting 'Kik'	generators of kik parts of various patterns
'Emb' prefix					adds extra notes

Referring to the 'SortaSelekta_keys_panel_example.png', there are many similar items. What is new is the following:

Keys_Pad	a pad part generator
Leg<xxx>	a processor that applies legato/staccato patterns
PBLong<xxx>	a processor that applies pitch bend
SloWah		a processor that applies a filter sweep



In general, this algorithmic model has been explored only to the depth of proving it functions.

The Live Set



Above is the Ableton Live project. 

	- The first 6 tracks correspond to the instruments in the app.
	- The next six tracks receive data from the cued nodes, so of you set up your routing correctly, you can hear what you have cued in your headphones before you play from your speakers. This will depend on whether your sound card can do this kind of separation in the first place. I was working on a Focusrite Sapphire PRO 24 DSP at the time and it was possible, but something like a Focusrite 2i2 would not be able to do this 
	- The 'SortaSelekta' track contains the Max4Live patch that handles the UPD input. (screen shot='SortaSelekta_UDP_receiver_max4live_patch.png'
	
	
	
	It is configured by messages from the Java app when the java app is opened. Nothing needs to be set or tweaked, unless there is an issue with the port number (7800). This can be changed manually in the max patch, but will need to be changed in the SortaSelekta.java file (round about line 17):
	
	public static int sendPort = 7800; - change to equivalent port set in the max patch 
	
Takeaways from the project:

Ordering the material, even in the relatively simple ways done in this project, allows for significantly better than random traversal of the output parameter space
Even if it is not beautiful, I certainly learned some nifty new graphics programming.
The various instrument pipelines to not interact or have a model for interation when generating musical material, so parts do not neccessarily work naturally together. This was something that needed significant extra work which was beyond the scope of this project
While it could be said that this is a 'clever' idea, it is still interfacing with a representation of the musical material on quite a basic level, and not really displaying any 'intelligent' behaviour. Given the rapidly emerging capabilities of machine learning and artificial intelligence, it is inescapable when looking at the issue of iteracting creatively with a software application to not be including the ability for the software to learn from the interaction with the user.

Possible future work:
A potential application of learning from user interaction is the ability to cue possible options to headphone before committing them to the main output. It would not be amiss to assume that anything committed with the 'P' key after having been cued and listened to could be assumed to indicate a positive preference. This could be the beginnings of a dataset (probably only user specific) that could differentiate 'better' and 'worse' combinations of material, and could be used to further sort material to enable more engaging interaction 
