package sorta_selekta.gui_objects;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import PlugIns.Pluggable;
import UndoList.UndoListInterface;
import UndoList.UndoListPanel;
import sorta_selekta.node_list_generator.NodeListGenerator;

public class NodeListDisplay extends JPanel{
	
	// manages a NodeList: 
	// generates tempNodeList from sort criteria
	// contains buttons for sorting and NodeDisplay organizing
	// 
	public NodeListGenerator nlg;
	private JTextField nameField;
	private JPanel includeListPanel;
	private JPanel excludeListPanel;
	private JPanel xSortPanel;
	private JPanel ySortPanel;
	private JPanel pipelinePanel;
	private UndoListPanel undoPanel;
	private JLabel filterListPlaceholder = new JLabel("------------------------------");
	private Random rnd = new Random();
	private NodeUIPanel uiPanel = new NodeUIPanel(this);
//	private int undoListLength = 10;
//	private Color undoListSelectBG = new Color(200, 200, 0);

	
	private int xSortListWrapLength = 5;
	private int ySortListWrapLength = 12;
	
//	private JRadioButton[] includeListButtonArr;

	public NodeListDisplay(Color bg){
		setBackground(bg);
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		addNameTextField(gbc);

		setBackGroundColorsToUniveralBG(bg);
		
		addIncludeList();
		addExcludeList();
		addYSortPanel();
		addXSortPanel();
		addUIPanel();
		addPipelineAndUndoPanel();
	}
	public NodeUIPanel getUIPanel(){
		return uiPanel;
	}
	

	private void setBackGroundColorsToUniveralBG(Color bg) {
		includePanelBGColor = bg;
		excludePanelBGColor = bg;
		xSortPanelBGColor = bg;
		ySortPanelBGColor = bg;
		
	}

	public void setNodeList(NodeListGenerator nlg){
		this.nlg = nlg;
		nameField.setText(nlg.name + ":- " + nlg.nodeList.size() + " items");		
		updateIncludeListPanel(nlg);
		updateExcludeListPanel(nlg);
		updateXSortPanel(nlg);
		updateYSortPanel(nlg);
		nlg.setXSortIndex();
		nlg.setYSortIndex();
//		nlg.setNodePositions();
		uiPanel.setNodeListGenerator(nlg);
//		nlg.consoleOutNodeList();
	}
	public void setUIShift(boolean b){
		uiPanel.setShift(b);
	}
//	public void dealWithNodeOverlap(Node node){
//		uiPanel.dealWithOverlappingNodes(node);
//	}
	
	
// privates -------------------------------------------------------------
	private void addPipelineAndUndoPanel() {
		//System.out.println("addPipelineAndUndoPanel Panel,   .........................called");
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 100, 0));
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;		
		panel.add(makePipelineListPanel(), g);
		g.gridy = 1;
		panel.add(makeUndoPanel(), g);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 1;
		add(panel, gbc);
		panel.revalidate();
	}
	private Component makeUndoPanel() {
		//System.out.println("makeUndoPanel Panel,   .........................called");

		undoPanel = new UndoListPanel();
		return undoPanel;
	}



	private JPanel makePipelineListPanel() {
		//System.out.println("makePipelineList Panel,   .........................called");
		pipelinePanel = new JPanel();
		pipelinePanel.setLayout(new GridBagLayout());
		pipelinePanel.setBackground(this.getBackground());
		return pipelinePanel;
	}
	public void updatePipelinePanel(){
//		System.out.println("NodeListDisplay.updatePipelinePanel called");
		if (nlg != null){
			pipelinePanel.removeAll();
			pipelinePanel.repaint();
			if (nlg.cuedNode != null){
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = 0;
				JLabel label = new JLabel(nlg.cuedNode.ID + " PlugList:-------");
				label.setBackground(this.getBackground());
				label.setOpaque(true);
				pipelinePanel.add(label, gbc);
				gbc.gridy++;
				for (Pluggable plug: nlg.cuedNode.pipe.plugList){
					label = new JLabel(plug.originalName());
					label.setBackground(this.getBackground());
					label.setOpaque(true);
//					System.out.println("NodeListDisplay.updatePipelinePanel: " + plug.originalName());
					pipelinePanel.add(label, gbc);
					gbc.gridy++;
				}
				pipelinePanel.revalidate();
			}
		}
		
	}
	public void updateUndoPanel(ArrayList<UndoListInterface> uList, int playIndex, int cuedIndex){
		undoPanel.bgColor = undoPanelBGColor;
		undoPanel.updatePanel(uList, playIndex, cuedIndex);
	}
	private void addUIPanel() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		uiPanel = new NodeUIPanel(this);
		add(uiPanel, gbc);
	}
	private void updateXSortPanel(NodeListGenerator nlg){
		removeItemsFromPanel(xSortPanel);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		int x = 0;
		int y = 0;
		
		for (SortRadioButton srb: nlg.xSortButtonArr){
			gbc.gridx = x;
			gbc.gridy = y;
			srb.setBackground(xSortPanelBGColor);
			xSortPanel.add(srb, gbc);
			x++;
			if (x >= xSortListWrapLength){
				x = 0;
				y++;
			}
			xSortPanel.updateUI();
		}
	}
	private void updateYSortPanel(NodeListGenerator nlg){
		removeItemsFromPanel(ySortPanel);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		int x = 0;
		int y = 0;
		for (SortRadioButton srb: nlg.ySortButtonArr){
			gbc.gridx = x;
			gbc.gridy = y;
			srb.setBackground(ySortPanelBGColor);
			ySortPanel.add(srb, gbc);
			y++;
			if (y >= ySortListWrapLength){
				y = 0;
				x++;
			}
			
		}
//		gbc.gridx = x;
//		gbc.gridy = y;
//		ySortPanel.add(nlg.offClipButton, gbc);
		ySortPanel.updateUI();
	}
	private void addYSortPanel(){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;		
		ySortPanel = new JPanel();
		ySortPanel.setBackground(ySortPanelBGColor);
		ySortPanel.setLayout(new GridBagLayout());
		add(ySortPanel, gbc);	
	}
	private void addXSortPanel(){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 2;
		xSortPanel = new JPanel();
		xSortPanel.setBackground(xSortPanelBGColor);
		xSortPanel.setLayout(new GridBagLayout());
		add(xSortPanel, gbc);	
	}
 	private void addIncludeList(){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;		
		includeListPanel = new JPanel();
		includeListPanel.setBackground(includePanelBGColor);
		includeListPanel.setLayout(new GridBagLayout());
		add(includeListPanel, gbc);		
	}
	private void addExcludeList(){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;		
		excludeListPanel = new JPanel();
		excludeListPanel.setBackground(excludePanelBGColor);
		excludeListPanel.setLayout(new GridBagLayout());
		add(excludeListPanel, gbc);		
	}
	private void updateIncludeListPanel(NodeListGenerator nlg){
		removeItemsFromPanel(includeListPanel);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		int x = 0;
		int y = 0;
		includeListPanel.add(new JLabel("Include: " + nlg.filterObjectList.size()), gbc);
		y++;
		for (JRadioButton jrb: nlg.includeButtonList){
			gbc.gridx = x;
			gbc.gridy = y;
			jrb.setBackground(includePanelBGColor);
			includeListPanel.add(jrb, gbc);
			y++;
		}
		gbc.gridy = y;
		includeListPanel.add(includeListALLButton(), gbc);
		y++;
		gbc.gridy = y;
		includeListPanel.add(includeListNONEButton(), gbc);
		y++;
		gbc.gridy = y;
		includeListPanel.add(nlg.sizeLabel, gbc);
		includeListPanel.updateUI();
	}
	private void updateExcludeListPanel(NodeListGenerator nlg){
		removeItemsFromPanel(excludeListPanel);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		int x = 0;
		int y = 0;
		excludeListPanel.add(new JLabel("Exclude: " + nlg.filterObjectList.size()), gbc);
		y++;
		for (JRadioButton jrb: nlg.excludeButtonList){
			gbc.gridx = x;
			gbc.gridy = y;
			jrb.setBackground(excludePanelBGColor);
			excludeListPanel.add(jrb, gbc);
			y++;
		}
		gbc.gridy = y;
		excludeListPanel.add(excludeListALLButton(), gbc);
		y++;
		gbc.gridy = y;
		excludeListPanel.add(excludeListNONEButton(), gbc);
		y++;
		gbc.gridy = y;
		excludeListPanel.add(filterListPlaceholder, gbc);
		excludeListPanel.updateUI();
	}
	private JButton excludeListALLButton(){
		JButton button = new JButton("ALL");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				for (FilterRadioButton jrb: nlg.excludeButtonList){
					jrb.setSelected(true);
				}
				nlg.makeFilteredNodeList();
				nlg.setSizeLabel();

				//filterListSizeLabel.setText(filterListSizeString + nlg.makeFilteredNodeList());
			}				
		});
		return button;
	}
	private JButton excludeListNONEButton(){
		JButton button = new JButton("NONE");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				for (FilterRadioButton jrb: nlg.excludeButtonList){
					if (jrb.isSelected()){
						jrb.getGroup().clearSelection();
					}
				}
				nlg.makeFilteredNodeList();
				nlg.setSizeLabel();
				//filterListSizeLabel.setText(filterListSizeString + nlg.makeFilteredNodeList());
			}				
		});
		return button;
	}
	private JButton includeListALLButton(){
		JButton button = new JButton("ALL");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				for (FilterRadioButton jrb: nlg.includeButtonList){
					jrb.setSelected(true);
				}
				nlg.makeFilteredNodeList();
				nlg.setSizeLabel();
				//filterListSizeLabel.setText(filterListSizeString + nlg.makeFilteredNodeList());
			}				
		});
		return button;
	}
	private JButton includeListNONEButton(){
		JButton button = new JButton("NONE");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				for (FilterRadioButton jrb: nlg.includeButtonList){
					if (jrb.isSelected()){
						jrb.getGroup().clearSelection();
					}
				}
				nlg.makeFilteredNodeList();
				nlg.setSizeLabel();
				//filterListSizeLabel.setText(filterListSizeString + nlg.makeFilteredNodeList());
			}				
		});
		return button;
	}
	private void removeItemsFromPanel(JPanel panel){
		for (Component comp: panel.getComponents()){
			panel.remove(comp);
		}
	}
	private void addNameTextField(GridBagConstraints gbc){
		gbc.gridx = 0;
		gbc.gridy = 0;
		nameField = new JTextField("---------------");
		add(nameField, gbc);
	}
//	private void addFilterListSize(GridBagConstraints gbc) {
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		
//		add(filterListSizeLabel, gbc);
//		
//	}


	private static final String filterListSizeString = "filterListSize=";
	public static Color includePanelBGColor = new Color(200, 200, 200);
	public static Color excludePanelBGColor = new Color(200, 200, 200);
	public static Color xSortPanelBGColor = new Color(200, 200, 200);
	public static Color ySortPanelBGColor = new Color(200, 200, 200);
	public static Color undoPanelBGColor = new Color(100, 150, 200);
	
	
}
