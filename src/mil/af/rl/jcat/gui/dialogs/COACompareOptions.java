
package mil.af.rl.jcat.gui.dialogs;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

import mil.af.rl.jcat.plan.COA;
import mil.af.rl.jcat.plan.PlanItem;
import mil.af.rl.jcat.util.Guid;


public class COACompareOptions extends JDialog implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private JList selectedItemView, itemListView, selectedCoaView, coaListView;
	private JButton itemAdd, itemRemove, coaAdd, coaRemove, compareButton;
	private Vector<COA> coaList, selectedCOAs, hiddenCOAs;
	private Vector<PlanItem> itemList, selectedItems, hiddenItems;
	private boolean accepted = false;
	private JButton cancelButton;
	private JRadioButton colorOpt2;
	
	
	/**
	 * @param owner
	 * @param fullCOAList a list of COAs to select from
	 * @param fullItemList a list of items to select from
	 */
	public COACompareOptions(Frame owner, Vector<COA> fullCOAList, Vector<PlanItem> fullItemList)
	{
		super(owner);
		
		coaList = (Vector<COA>)fullCOAList.clone();
		itemList = (Vector<PlanItem>)fullItemList.clone();
		selectedItems = new Vector<PlanItem>();
		selectedCOAs = new Vector<COA>();
		hiddenItems = new Vector<PlanItem>();
		hiddenCOAs = new Vector<COA>();
		
		initialize();
		
		setLocationRelativeTo(owner);
		setModal(true);
	}
	
	/**
	 * @param owner
	 * @param fullCOAList a list of COAs to select from
	 * @param fullItemList a list of items to select from
	 * @param coaSelection indicies from the coa list that should be selected initially
	 * @param itemSelection indicies from the item list that should be selected initially
	 */
	public COACompareOptions(Frame owner, Vector<COA> fullCOAList, Vector<PlanItem> fullItemList, List<COA> coaSelection, List<PlanItem> itemSelection)
	{
		this(owner, fullCOAList, fullItemList);
		
		//add the currently selected coas and items specified
		if(coaSelection != null)
			for(COA x : coaSelection)
				addCOA(coaList.indexOf(x));
		if(itemSelection != null)
			for(PlanItem x : itemSelection)
				addItem(itemList.indexOf(x));
	}


	//should filter available coa list when items are added/removed
	//and filter avail item list when coas are added/removed
	private void filter()
	{
		coaList.addAll(hiddenCOAs);
		hiddenCOAs.clear();
		//filter coa list with items
		for(PlanItem item : selectedItems)
		{
			Iterator<COA> coas = coaList.iterator();
			while(coas.hasNext())
			{
				COA thisCOA = coas.next();
				if(!thisCOA.containsItem(item.getGuid()))
				{
					hiddenCOAs.add(thisCOA);
					coas.remove();
				}
			}
		}
		
		itemList.addAll(hiddenItems);
		hiddenItems.clear();
		//filter item list with coa
		for(COA coa : selectedCOAs)
		{
			Iterator<PlanItem> items = itemList.iterator();
			while(items.hasNext())
			{
				PlanItem thisItem = items.next();
				if(!coa.containsItem(thisItem.getGuid()))
				{
					hiddenItems.add(thisItem);
					items.remove();
				}
			}
		}
		
	}

	private void initialize()
	{
		this.setSize(468, 388);
		this.setTitle("COA Compare");
		
		JPanel jContentPane = new JPanel();
		jContentPane.setLayout(new BorderLayout());
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(2);
		gridLayout.setHgap(10);
		gridLayout.setVgap(5);
		JPanel listPane = new JPanel();
		listPane.setLayout(gridLayout);
		listPane.add(createItemPanel(), null);
		listPane.add(createCoaPanel(), null);
		
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		gridBagConstraints10.insets = new Insets(5, 2, 5, 2);
		JLabel topLabel = new JLabel(" ");
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		topPanel.add(topLabel, gridBagConstraints10);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new GridBagLayout());
		JRadioButton colorOpt1 = new JRadioButton("Line color by item", true);
		colorOpt2 = new JRadioButton("Line color by COA");
		ButtonGroup opts = new ButtonGroup();
		opts.add(colorOpt1);
		opts.add(colorOpt2);
		optionPanel.add(colorOpt1, new GridBagConstraints());
		optionPanel.add(colorOpt2, new GridBagConstraints());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		compareButton = new JButton("Compare");
		compareButton.setFont(compareButton.getFont().deriveFont(1));
		compareButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(compareButton, null);
		buttonPanel.add(cancelButton);		
		
		bottomPanel.add(optionPanel, BorderLayout.NORTH);
		bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		
		jContentPane.add(listPane, BorderLayout.CENTER);
		jContentPane.add(topPanel, BorderLayout.NORTH);
		jContentPane.add(bottomPanel, BorderLayout.SOUTH);
		
		this.setContentPane(jContentPane);
	}

	private JPanel createItemPanel()
	{
		GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
		gridBagConstraints71.gridx = 3;
		gridBagConstraints71.anchor = GridBagConstraints.EAST;
		gridBagConstraints71.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints71.gridy = 0;
		JLabel allItemsLbl = new JLabel("Available Items");
		GridBagConstraints gridBagConstraints62 = new GridBagConstraints();
		gridBagConstraints62.gridx = 0;
		gridBagConstraints62.anchor = GridBagConstraints.WEST;
		gridBagConstraints62.insets = new Insets(0, 10, 0, 0);
		gridBagConstraints62.gridy = 0;
		JLabel itemLbl = new JLabel("Items to compare");
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 2;
		gridBagConstraints5.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints5.ipady = 0;
		gridBagConstraints5.ipadx = 10;
		gridBagConstraints5.gridy = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.gridx = 3;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.insets = new Insets(2, 0, 5, 10);
		gridBagConstraints1.weightx = 1.0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(2, 10, 5, 0);
		gridBagConstraints.gridx = 0;
		JPanel itemPanel = new JPanel();
		itemPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.insets = new Insets(5, 0, 0, 0);
		gridBagConstraints4.fill = GridBagConstraints.NONE;
		gridBagConstraints4.gridy = 1;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = -1;
		gridBagConstraints6.ipady = 0;
		gridBagConstraints6.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints6.fill = GridBagConstraints.NONE;
		gridBagConstraints6.gridy = -1;
		JPanel itemButtonPanel = new JPanel();
		itemButtonPanel.setLayout(new GridBagLayout());
		itemAdd = new JButton("Add");
		itemAdd.setMargin(new Insets(1, 1, 1, 4));
//		itemAdd.setPreferredSize(new Dimension(84, 27));
		itemAdd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("arrow_l.png")));
		itemAdd.addActionListener(this);
		itemRemove = new JButton("Remove");
		itemRemove.setMargin(new Insets(1, 1, 1, 1));
//		itemRemove.setPreferredSize(new Dimension(84, 27));
		itemRemove.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("arrow_r.png")));
		itemRemove.addActionListener(this);
		itemAdd.setPreferredSize(itemRemove.getPreferredSize());
		
		itemButtonPanel.add(itemAdd, gridBagConstraints6);
		itemButtonPanel.add(itemRemove, gridBagConstraints4);
		
		selectedItemView = new JList(selectedItems);
		itemListView = new JList(itemList);
		
		itemPanel.add(new JScrollPane(selectedItemView), gridBagConstraints);
		itemPanel.add(new JScrollPane(itemListView), gridBagConstraints1);
		itemPanel.add(itemButtonPanel, gridBagConstraints5);
		itemPanel.add(itemLbl, gridBagConstraints62);
		itemPanel.add(allItemsLbl, gridBagConstraints71);

		return itemPanel;
	}

	private JPanel createCoaPanel()
	{
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		gridBagConstraints9.gridx = 2;
		gridBagConstraints9.anchor = GridBagConstraints.EAST;
		gridBagConstraints9.insets = new Insets(0, 0, 0, 10);
		gridBagConstraints9.gridy = 0;
		JLabel allCOALbl = new JLabel("Available COAs");
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.gridx = 0;
		gridBagConstraints8.anchor = GridBagConstraints.WEST;
		gridBagConstraints8.insets = new Insets(0, 10, 0, 0);
		gridBagConstraints8.gridy = 0;
		JLabel coaLbl = new JLabel("COAs to compare");
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 1;
		gridBagConstraints7.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints7.ipadx = 10;
		gridBagConstraints7.gridy = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.BOTH;
		gridBagConstraints3.weighty = 1.0;
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.insets = new Insets(2, 0, 5, 10);
		gridBagConstraints3.weightx = 1.0;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.weighty = 1.0;
		gridBagConstraints2.insets = new Insets(2, 10, 5, 0);
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.weightx = 1.0;
		JPanel coaPanel = new JPanel();
		coaPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
		gridBagConstraints41.fill = GridBagConstraints.NONE;
		gridBagConstraints41.gridx = 0;
		gridBagConstraints41.gridy = 1;
		gridBagConstraints41.insets = new Insets(5, 0, 0, 0);
		GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
		gridBagConstraints61.fill = GridBagConstraints.NONE;
		gridBagConstraints61.gridx = -1;
		gridBagConstraints61.gridy = -1;
		gridBagConstraints61.ipady = 0;
		gridBagConstraints61.insets = new Insets(0, 0, 5, 0);
		JPanel coaButtonPanel = new JPanel();
		coaButtonPanel.setLayout(new GridBagLayout());
		coaRemove = new JButton();
		coaRemove.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("arrow_r.png")));
		coaRemove.setText("Remove");
		coaRemove.setMargin(new Insets(1, 1, 1, 1));
//		coaRemove.setPreferredSize(new Dimension(84, 27));
		coaRemove.addActionListener(this);
		coaAdd = new JButton("Add");
//		coaAdd.setPreferredSize(new Dimension(84, 27));
		coaAdd.setMargin(new Insets(1, 1, 1, 4));
		coaAdd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("arrow_l.png")));
		coaAdd.addActionListener(this);
		coaAdd.setPreferredSize(coaRemove.getPreferredSize());
		
		coaButtonPanel.add(coaAdd, gridBagConstraints61);
		coaButtonPanel.add(coaRemove, gridBagConstraints41);
		
		selectedCoaView = new JList(selectedCOAs);
		coaListView = new JList(coaList);
		
		coaPanel.add(new JScrollPane(selectedCoaView), gridBagConstraints2);
		coaPanel.add(new JScrollPane(coaListView), gridBagConstraints3);
		coaPanel.add(coaButtonPanel, gridBagConstraints7);
		coaPanel.add(coaLbl, gridBagConstraints8);
		coaPanel.add(allCOALbl, gridBagConstraints9);

		return coaPanel;
	}

	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == compareButton)
		{
			accepted = true;
			dispose();
		}
		else if(event.getSource() == cancelButton)
			dispose();
		else if(event.getSource() == itemAdd)
		{
			addItem(itemListView.getSelectedIndex());
		}
		else if(event.getSource() == itemRemove)
		{
			if(!selectedItems.isEmpty() && selectedItemView.getSelectedIndex() >= 0)
				itemList.add(selectedItems.remove(selectedItemView.getSelectedIndex()));
			itemListView.updateUI();
			selectedItemView.repaint();
			filter();
			coaListView.repaint();
		}
		else if(event.getSource() == coaAdd)
		{
			addCOA(coaListView.getSelectedIndex());
		}
		else if(event.getSource() == coaRemove)
		{
			if(!selectedCOAs.isEmpty() && selectedCoaView.getSelectedIndex() >= 0)
				coaList.add(selectedCOAs.remove(selectedCoaView.getSelectedIndex()));
			coaListView.updateUI();
			selectedCoaView.repaint();
			filter();
			itemListView.repaint();
		}

	}
	
	private void addCOA(int index)
	{
		if(!coaList.isEmpty() && index >= 0 && index <= coaList.size())
			selectedCOAs.add(coaList.remove(index));
		selectedCoaView.updateUI();
		coaListView.repaint();
		filter();
		itemListView.repaint();
	}

	private void addItem(int index)
	{
		if(!itemList.isEmpty() && index >= 0 && index <= itemList.size())
			selectedItems.add(itemList.remove(index));
		selectedItemView.updateUI();
		itemListView.repaint();
		filter();
		coaListView.repaint();
	}

	public boolean getWasAccepted()
	{
		return accepted;
	}

	public List<COA> getSelectedCOAs()
	{
		return selectedCOAs;
	}

	public List<Guid> getSelectedItems()
	{
		Vector<Guid> sItems = new Vector<Guid>();
		
		for(PlanItem item : selectedItems)
			sItems.add(item.getGuid());
		
		return sItems;
	}


	
	public boolean getReverseColors()
	{
		return colorOpt2.isSelected();
	}

}
