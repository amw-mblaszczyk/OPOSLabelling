package amw.opos.labelling.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import amw.opos.labeling.CSVParser;
import amw.opos.labeling.LabelRegion;
import amw.opos.labeling.OPOSLabel;
import amw.opos.labelling.gui.utils.GuiUtils;

public class OPOSLabelingGUI {
	//USER VARIBLES
	public final String CSV_SEPARATOR = ";";
	private String image_filename = null;
	private String labels_filename = null;
	private File image_file = null;
	private File regions_file = null;
	private List<OPOSLabel> opos_labels;
	private Map<Integer,OPOSLabel> opos_labels_map;
	private List<LabelRegion> regions_list = new ArrayList<LabelRegion>();
	private List<Rectangle> rectangles_list = new ArrayList<Rectangle>();
	private int selected_rectangle = -1;
	private DefaultListModel<LabelRegion> regionsListModel = new DefaultListModel<LabelRegion>();
	private DefaultComboBoxModel<OPOSLabel> labelsComboBoxModel = new DefaultComboBoxModel<OPOSLabel>();
	private boolean labels_loaded = false;
	private boolean image_loaded = false;
	private boolean regions_changed = false;
	
	//selection rectangle
	private Rectangle selectionRect = new Rectangle();
	private int sourceX=0, sourceY=0, destX=0, destY=0;
	private boolean drawSelection = false;
	/*private BasicStroke bs = new BasicStroke (2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
            0, new float [] { 12, 12 }, 0);
            */
	private BasicStroke bs = new BasicStroke (1f);
	private GradientPaint gp = new GradientPaint (0.0f, 0.0f, new Color(120,120,120,80), 1.0f, 1.0f, Color.white, true);
	
	//END USER VARIBLES
	
	private JFrame frmMapFilesHandler;
	private final JToolBar toolBar = new JToolBar();
	private JMenuItem mntmLoadImage;
	private JLabel lblMapPreview;
	private JTextField textFieldX0;
	private JTextField textFieldY0;
	private JTextField textFieldX1;
	private JTextField textFieldY1;
	private JButton btnAddRegion;
	private JLabel lblStatus;
	private JComboBox<OPOSLabel> comboBox;
	private JPanel panelRegionParams;
	private JButton btnSave;
	private JList listRegions;
	private JMenuItem mntmRemove;
	private JMenuItem mntmEdit;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OPOSLabelingGUI window = new OPOSLabelingGUI();
					GuiUtils.displayCenter(window.frmMapFilesHandler);
					window.frmMapFilesHandler.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public OPOSLabelingGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMapFilesHandler = new JFrame();
		frmMapFilesHandler.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				loadLabelsFromFile();
			}
		});
		frmMapFilesHandler.setTitle("Images labelling");
		frmMapFilesHandler.setBounds(100, 100, 1000, 750);
		frmMapFilesHandler.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmMapFilesHandler.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File"); 
		menuBar.add(mnFile);
		
		mntmLoadImage = new JMenuItem("Load image");
		mntmLoadImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openMapFile();
			}
		});
		mntmLoadImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_MASK));
		mnFile.add(mntmLoadImage);
		JMenuItem mntmSave = new JMenuItem("Save regions");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveMapHandlerFile();
			}

		});
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
		mnFile.add(mntmSave);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmMapFilesHandler.dispose();
			}
		});
		
		JMenuItem mntmLoadLabels = new JMenuItem("Load labels");
		mntmLoadLabels.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_MASK));
		mnFile.add(mntmLoadLabels);
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
		mntmExit.setActionCommand("");
		mnFile.add(mntmExit);
		frmMapFilesHandler.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton btnLoadLabels = new JButton("");
		btnLoadLabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadLabelsFromFile();
			}
		});
		btnLoadLabels.setFocusable(false);
		btnLoadLabels.setMargin(new Insets(2, 8, 2, 8));
		btnLoadLabels.setBorderPainted(false);
		btnLoadLabels.setToolTipText("open labels def.");
		btnLoadLabels.setIcon(new ImageIcon(GuiUtils.class.getResource("openlbl32.png")));
		toolBar.add(btnLoadLabels);
		
		JButton btnOpenReg = new JButton("");
		btnOpenReg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openMapFile();
			}
		});
		btnOpenReg.setFocusable(false);
		btnOpenReg.setBorderPainted(false);
		btnOpenReg.setMargin(new Insets(2, 8, 2, 8));
		btnOpenReg.setToolTipText("load image");
		btnOpenReg.setIcon(new ImageIcon(GuiUtils.class.getResource("openimg32.png")));
		btnOpenReg.setActionCommand("");
		toolBar.add(btnOpenReg);
		
		btnSave = new JButton("");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveRegions();
			}
		});
		btnSave.setFocusable(false);
		btnSave.setMargin(new Insets(2, 8, 2, 8));
		btnSave.setBorderPainted(false);
		btnSave.setToolTipText("save regions");
		btnSave.setIcon(new ImageIcon(GuiUtils.class.getResource("save32.png")));
		toolBar.add(btnSave);
		
		JPanel JPanelMain = new JPanel();
		frmMapFilesHandler.getContentPane().add(JPanelMain, BorderLayout.CENTER);
		JPanelMain.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPaneMap = new JSplitPane();
		JPanelMain.add(splitPaneMap, BorderLayout.CENTER);
		
		JScrollPane scrollPaneMap = new JScrollPane();
		splitPaneMap.setRightComponent(scrollPaneMap);
		
		lblMapPreview = new JLabel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (drawSelection)
					paintSelection(g);
				paintRegions(g); 
			}
		};
		lblMapPreview.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sourceX = e.getX();
				sourceY = e.getY();
				destX = e.getX();
				destY = e.getY();
				drawSelection = false;

				textFieldX0.setText(""+e.getX());
				textFieldY0.setText(""+e.getY());
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				enableSaveRegion();
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			int s = selectRectangle(e.getX(), e.getY());
			/*if (s != selected_rectangle) {
				selected_rectangle = s;
				getLblMapPreview().repaint();
			}*/
			}
		});
		lblMapPreview.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				getLblStatus().setText("  x:"+e.getX()+"    y:"+e.getY());
				
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				destX = e.getX();
				destY = e.getY();

				textFieldX0.setText(""+sourceX);
				textFieldY0.setText(""+sourceY);
				textFieldX1.setText(""+destX);
				textFieldY1.setText(""+destY);
				
				if ((destX != sourceX) && (destY != destX)) {
					drawSelection = true;
					setRegionDefEnabled(true);
					lblMapPreview.repaint();
				}
			}
		});
		scrollPaneMap.setViewportView(lblMapPreview);
		
		JPanel panelLeft = new JPanel();
		splitPaneMap.setLeftComponent(panelLeft);
		panelLeft.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneRegions = new JScrollPane();
		scrollPaneRegions.setBorder(new TitledBorder(null, "Regions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLeft.add(scrollPaneRegions);
		
		listRegions = new JList<LabelRegion>(regionsListModel);
		listRegions.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				//System.out.println("Selection changed");
				listRegionsSelectionChanged();
			}
		});
		scrollPaneRegions.setViewportView(listRegions);
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(listRegions, popupMenu);
		
		mntmRemove = new JMenuItem("remove");
		mntmRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeSelectedRegion();
			}
		});
		mntmRemove.setEnabled(false);
		popupMenu.add(mntmRemove);
		
		mntmEdit = new JMenuItem("edit");
		mntmEdit.setEnabled(false);
		popupMenu.add(mntmEdit);
		
		JMenuItem mntmAdd = new JMenuItem("add");
		popupMenu.add(mntmAdd);
		
		JPanel panelRegion = new JPanel();
		panelRegion.setBorder(new TitledBorder(null, "Region definition", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelRegion.setPreferredSize(new Dimension(500, 200));
		panelLeft.add(panelRegion, BorderLayout.SOUTH);
		panelRegion.setLayout(new BorderLayout(0, 0));
		
		panelRegionParams = new JPanel();
		panelRegion.add(panelRegionParams);
		GridBagLayout gbl_panelRegionParams = new GridBagLayout();
		gbl_panelRegionParams.columnWidths = new int[]{23, 62, 23, 77, 0};
		gbl_panelRegionParams.rowHeights = new int[]{26, 26, 20, 26, 0};
		gbl_panelRegionParams.columnWeights = new double[]{1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelRegionParams.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelRegionParams.setLayout(gbl_panelRegionParams);
		
		JLabel labelX0 = new JLabel("x0:");
		GridBagConstraints gbc_labelX0 = new GridBagConstraints();
		gbc_labelX0.anchor = GridBagConstraints.WEST;
		gbc_labelX0.insets = new Insets(0, 0, 5, 5);
		gbc_labelX0.gridx = 0;
		gbc_labelX0.gridy = 0;
		panelRegionParams.add(labelX0, gbc_labelX0);
		
		textFieldX0 = new JTextField();
		GridBagConstraints gbc_textFieldX0 = new GridBagConstraints();
		gbc_textFieldX0.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldX0.anchor = GridBagConstraints.NORTH;
		gbc_textFieldX0.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldX0.gridx = 1;
		gbc_textFieldX0.gridy = 0;
		panelRegionParams.add(textFieldX0, gbc_textFieldX0);
		textFieldX0.setColumns(4);
		
		JLabel labelY0 = new JLabel("y0:");
		GridBagConstraints gbc_labelY0 = new GridBagConstraints();
		gbc_labelY0.anchor = GridBagConstraints.WEST;
		gbc_labelY0.insets = new Insets(0, 0, 5, 5);
		gbc_labelY0.gridx = 2;
		gbc_labelY0.gridy = 0;
		panelRegionParams.add(labelY0, gbc_labelY0);
		
		textFieldY0 = new JTextField();
		textFieldY0.setColumns(4);
		GridBagConstraints gbc_textFieldY0 = new GridBagConstraints();
		gbc_textFieldY0.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldY0.anchor = GridBagConstraints.NORTH;
		gbc_textFieldY0.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldY0.gridx = 3;
		gbc_textFieldY0.gridy = 0;
		panelRegionParams.add(textFieldY0, gbc_textFieldY0);
		
		JLabel labelX1 = new JLabel("x1:");
		GridBagConstraints gbc_labelX1 = new GridBagConstraints();
		gbc_labelX1.anchor = GridBagConstraints.WEST;
		gbc_labelX1.insets = new Insets(0, 0, 5, 5);
		gbc_labelX1.gridx = 0;
		gbc_labelX1.gridy = 1;
		panelRegionParams.add(labelX1, gbc_labelX1);
		
		textFieldX1 = new JTextField();
		textFieldX1.setColumns(4);
		GridBagConstraints gbc_textFieldX1 = new GridBagConstraints();
		gbc_textFieldX1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldX1.anchor = GridBagConstraints.NORTH;
		gbc_textFieldX1.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldX1.gridx = 1;
		gbc_textFieldX1.gridy = 1;
		panelRegionParams.add(textFieldX1, gbc_textFieldX1);
		
		JLabel labelY1 = new JLabel("y1:");
		GridBagConstraints gbc_labelY1 = new GridBagConstraints();
		gbc_labelY1.anchor = GridBagConstraints.WEST;
		gbc_labelY1.insets = new Insets(0, 0, 5, 5);
		gbc_labelY1.gridx = 2;
		gbc_labelY1.gridy = 1;
		panelRegionParams.add(labelY1, gbc_labelY1);
		
		textFieldY1 = new JTextField();
		textFieldY1.setColumns(4);
		GridBagConstraints gbc_textFieldY1 = new GridBagConstraints();
		gbc_textFieldY1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldY1.anchor = GridBagConstraints.NORTH;
		gbc_textFieldY1.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldY1.gridx = 3;
		gbc_textFieldY1.gridy = 1;
		panelRegionParams.add(textFieldY1, gbc_textFieldY1);
		
		JLabel lblLabelDef = new JLabel("Label:");
		lblLabelDef.setPreferredSize(new Dimension(200, 20));
		GridBagConstraints gbc_lblLabelDef = new GridBagConstraints();
		gbc_lblLabelDef.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblLabelDef.insets = new Insets(0, 0, 5, 0);
		gbc_lblLabelDef.gridwidth = 4;
		gbc_lblLabelDef.gridx = 0;
		gbc_lblLabelDef.gridy = 2;
		panelRegionParams.add(lblLabelDef, gbc_lblLabelDef);
		
		comboBox = new JComboBox<OPOSLabel>(labelsComboBoxModel);
		comboBox.setPreferredSize(new Dimension(200, 26));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.anchor = GridBagConstraints.NORTH;
		gbc_comboBox.gridwidth = 4;
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 3;
		panelRegionParams.add(comboBox, gbc_comboBox);
		
		JPanel panelRegionButtons = new JPanel();
		panelRegionButtons.setPreferredSize(new Dimension(500, 40));
		panelRegion.add(panelRegionButtons, BorderLayout.SOUTH);
		
		btnAddRegion = new JButton("Add region");
		btnAddRegion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addRegion();
			}
		});
		btnAddRegion.setEnabled(false);
		panelRegionButtons.add(btnAddRegion);
		
		splitPaneMap.setDividerLocation(220);
		
		lblStatus = new JLabel("x:\t y:");
		frmMapFilesHandler.getContentPane().add(lblStatus, BorderLayout.SOUTH);
	}


	protected void removeSelectedRegion() {
		if (selected_rectangle >= 0) {
			rectangles_list.remove(selected_rectangle);
			regions_list.remove(selected_rectangle);
			
			// it has to be last remove as it generates listRegions.valuechanged event and sets selected_rectangle to -1
			regionsListModel.removeElementAt(selected_rectangle);  
			
			selected_rectangle = -1;
			
			getMenuItemEditRegion().setEnabled(false);
			getMenuItemRemoveRegion().setEnabled(false);
			
			lblMapPreview.repaint();
		}
		
	}

	protected void listRegionsSelectionChanged() {
		if (getJListRegions().getSelectedIndex() >= 0) {
			getMenuItemEditRegion().setEnabled(true);
			getMenuItemRemoveRegion().setEnabled(true);
			selected_rectangle = getJListRegions().getSelectedIndex();
			fillRegionDefinition((LabelRegion)getJListRegions().getSelectedValue());
		}else {
			getMenuItemEditRegion().setEnabled(false);
			getMenuItemRemoveRegion().setEnabled(false);
			selected_rectangle = -1;
		}
		lblMapPreview.repaint();
		System.out.println("Selected rect:"+selected_rectangle);
		
	}

	

	private void fillRegionDefinition(LabelRegion selectedValue) {
		if (selectedValue != null) {
			getTextFieldX0().setText(""+selectedValue.getX0());
			getTextFieldY0().setText(""+selectedValue.getY0());
			getTextFieldX1().setText(""+selectedValue.getX1());
			getTextFieldY1().setText(""+selectedValue.getY1());
			getComboBoxLabelType().setSelectedItem(selectedValue.getOlabel());
		}
		
	}

	protected int selectRectangle(int x, int y) {
		boolean selected = false;
		int i =0;
		Iterator<Rectangle> iterator = rectangles_list.iterator();
		while (!selected && iterator.hasNext()) {
			Rectangle r = iterator.next();
			if ((x>=r.x) && (x<=r.x+r.width))
				if ((y>=r.y) && (y<=r.y+r.height)) {
					getJListRegions().setSelectedIndex(i);
					getJListRegions().ensureIndexIsVisible(i);
					return i;
				}
			i++;
		}
		return -1;
	}

	protected void paintRegions(Graphics g) {
		int i = 0;
		for (Iterator<Rectangle> iterator = rectangles_list.iterator(); iterator.hasNext();) {
			Rectangle label_region = (Rectangle) iterator.next();
			if (i++ == getSelected_rectangle())
				paintRegion(label_region, g, true);
			else
				paintRegion(label_region, g, false);
		}
		
	}
	
	private Rectangle labelRegion2Rectangle(LabelRegion label_region) {
		if (label_region != null) {
			// Compute upper-left and lower-right coordinates for selection
			// rectangle corners.

			int x1 = (label_region.getX0() < label_region.getX1()) ? label_region.getX0() : label_region.getX1();
			int y1 = (label_region.getY0() < label_region.getY1()) ? label_region.getY0() : label_region.getY1();

			int x2 = (label_region.getX0() > label_region.getX1()) ? label_region.getX0() : label_region.getX1();
			int y2 = (label_region.getY0() > label_region.getY1()) ? label_region.getY0() : label_region.getY1();
			int w = (x2 - x1) + 1;
			int h = (y2 - y1) + 1;
			
			Rectangle rectangle = new Rectangle(x1, y1, w, h);
			return rectangle;
		}
		return null;
	}

	private void paintRegion(Rectangle rectangle, Graphics g, boolean selected) {
		if (rectangle != null) {
			
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(bs);
			// g2d.setPaint (gp);
			g2d.setColor(new Color(200, 200, 200, 100));
			g2d.fill(rectangle);
			g2d.setColor(Color.blue);
			if (selected)
				g2d.setColor(Color.orange);
			g2d.draw(rectangle);
		}
	}

	public boolean isLabels_loaded() {
		return labels_loaded;
	}

	public boolean isRegions_changed() {
		return regions_changed;
	}

	public void setRegions_changed(boolean regions_changed) {
		this.regions_changed = regions_changed;
		if (isRegions_changed())
			btnSave.setIcon(new ImageIcon(GuiUtils.class.getResource("save32mono.png")));
		else
			btnSave.setIcon(new ImageIcon(GuiUtils.class.getResource("save32.png")));
	}

	public int getSelected_rectangle() {
		return selected_rectangle;
	}

	public void setSelected_rectangle(int selected_rectangle) {
		this.selected_rectangle = selected_rectangle;
	}

	public void setLabels_loaded(boolean labels_loaded) {
		this.labels_loaded = labels_loaded;
		if (isLabels_loaded() && isImage_loaded()) {
			setRegionDefEnabled(true);
		}
		else
			setRegionDefEnabled(false);
		enableSaveRegion();
	}
	
	private void setRegionDefEnabled(boolean enabled) {
		for (int i = 0; i < getPanelRegionParams().getComponentCount(); i++) {
			getPanelRegionParams().getComponent(i).setEnabled(enabled);
		}
		getBtnAddRegion().setEnabled(enabled);
	}

	public boolean isImage_loaded() {
		return image_loaded;
	}

	public void setImage_loaded(boolean image_loaded) {
		this.image_loaded = image_loaded;
		if (isLabels_loaded() && isImage_loaded())
			setRegionDefEnabled(false);
		else {
			setRegionDefEnabled(true);
			getBtnAddRegion().setEnabled(false);
		}
		
	}

	protected void addRegion() {
		int x0 = Integer.parseInt(getTextFieldX0().getText());
		int y0 = Integer.parseInt(getTextFieldY0().getText());
		int x1 = Integer.parseInt(getTextFieldX1().getText());
		int y1 = Integer.parseInt(getTextFieldY1().getText());
		LabelRegion newRegion = new LabelRegion(x0, y0, x1, y1, (OPOSLabel)getComboBoxLabelType().getSelectedItem());
		regions_list.add(newRegion);
		regionsListModel.addElement(newRegion);
		rectangles_list.add(labelRegion2Rectangle(newRegion));
		
		drawSelection = false;
		getBtnAddRegion().setEnabled(false);
		setRegions_changed(true);
		
		getLblMapPreview().repaint();
	}
	
	protected void saveRegions() {
		try {
			String csv_regions = LabelRegion.printToStrig(regions_list, CSV_SEPARATOR);
			CSVParser.writeCSV(regions_file.getAbsolutePath(), csv_regions);
			setRegions_changed(false);
			JOptionPane.showMessageDialog(frmMapFilesHandler, 
					"<html>Regions saved succesfully to file: <br>"
					+regions_file.getAbsolutePath()+"</html>", 
					"Regions file saved.", 
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frmMapFilesHandler, 
					"<html>Saving regions to file error. "
					+ "<BR>The original message is:"+e.getMessage()+
					"<br>The regions file not updated!</html>", 
					"Regions file saving error.", 
					JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void enableSaveRegion() {
		if (isImage_loaded() && isLabels_loaded() && drawSelection)
			getBtnAddRegion().setEnabled(true);
		
	}

	protected void paintSelection(Graphics g) {
		if (sourceX != destX || sourceY != destY)
		{
		    // Compute upper-left and lower-right coordinates for selection
		    // rectangle corners.

		    int x1 = (sourceX < destX) ? sourceX : destX;
		    int y1 = (sourceY < destY) ? sourceY : destY;

		    int x2 = (sourceX > destX) ? sourceX : destX;
		    int y2 = (sourceY > destY) ? sourceY : destY;

		    // Establish selection rectangle origin.

		    selectionRect.x = x1;
		    selectionRect.y = y1;

		    // Establish selection rectangle extents.

		    selectionRect.width = (x2-x1)+1;
		    selectionRect.height = (y2-y1)+1;

		    // Draw selection rectangle.

		    Graphics2D g2d = (Graphics2D) g;
		    g2d.setStroke (bs);
		    //g2d.setPaint (gp);
		    g2d.setColor(new Color(200,200,200,100));
		    g2d.fill (selectionRect);
		    g2d.setColor(Color.red);
		    g2d.draw(selectionRect);
		}
		
	}

	protected void loadLabelsFromFile() {
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setDialogTitle("Open labels definition file");
		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		FileFilter ff = new FileNameExtensionFilter("labels definition files", "csv");
		fileChooser.addChoosableFileFilter(ff);//ff added to filechooser
		fileChooser.setFileFilter(ff);//st ff as default selection
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);//user must select a file not folder
		fileChooser.setMultiSelectionEnabled(false);//disabled selection of multiple files
		GuiUtils.displayCenter(fileChooser, this.frmMapFilesHandler);
		int wyn = fileChooser.showOpenDialog(frmMapFilesHandler);
		if (wyn == JFileChooser.APPROVE_OPTION) {
			try {
				labels_filename = fileChooser.getSelectedFile().getAbsolutePath();
				if (opos_labels != null) opos_labels.clear();
				opos_labels = OPOSLabel.parseCSV(labels_filename, ";");
				OPOSLabel.sortByNames(opos_labels);
				labelsComboBoxModel.removeAllElements();
				for (Iterator iterator = opos_labels.iterator(); iterator.hasNext();) {
					OPOSLabel oposLabel = (OPOSLabel) iterator.next();
					labelsComboBoxModel.addElement(oposLabel);
				}
				if (opos_labels_map != null) opos_labels_map.clear();
				opos_labels_map = OPOSLabel.list2map(opos_labels);
				setLabels_loaded(true);
				JOptionPane.showMessageDialog(frmMapFilesHandler, 
						"<html>Parsing labels definition success. <BR>"
						+ labelsComboBoxModel.getSize()+" labels found. ", 
						"Labels file parsing.", 
						JOptionPane.INFORMATION_MESSAGE);
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(frmMapFilesHandler, 
						"<html>Parsing labels definition file error. "
						+ "<BR>The original message is:"+e.getMessage()+
						"<br>You can still load labes definition using menu->load_labels option.</html>", 
						"Labels file parsing error.", 
						JOptionPane.ERROR_MESSAGE);
			}
		}else {
			JOptionPane.showMessageDialog(frmMapFilesHandler, 
					"<html>Labels definition not loaded. <br>You can still load labes definition using menu->load_labels option.</html>", 
					"Labels file not loaded", 
					JOptionPane.WARNING_MESSAGE);
		}
		setLabels_loaded(false);
		
	}

	protected void saveMapHandlerFile() {
		System.err.println("MapHandlerGUI:saveMapFile not defined.");
		
	}

	protected void redrawPreview() {
		if (image_filename != null) {
			int w = getLblMapPreview().getWidth();
			int h = getLblMapPreview().getHeight();
			getLblMapPreview().setIcon(new ImageIcon(
					new ImageIcon(image_filename).getImage().getScaledInstance(w, w, Image.SCALE_DEFAULT)));
			System.out.println("PREVIEW for " + image_filename);
		}
	}
	
	protected void displayMap() {
		if (image_filename != null) {
			ImageIcon image = new ImageIcon(image_filename);
			int w = image.getImage().getWidth(null);
			int h = image.getImage().getHeight(null);
			getLblMapPreview().setPreferredSize(new Dimension(w, h));
			getLblMapPreview().setIcon(image);
			System.out.println("PREVIEW for " + image_filename);
		}
	}

	protected void openMapFile() {
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		/*fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		FileFilter ff = new FileNameExtensionFilter("pliki definicji map", "json");
		fileChooser.addChoosableFileFilter(ff);//ff added to filechooser
		fileChooser.setFileFilter(ff);//st ff as default selection
*/		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);//user must select a file not folder
		fileChooser.setMultiSelectionEnabled(false);//disabled selection of multiple files
		GuiUtils.displayCenter(fileChooser, this.frmMapFilesHandler);
		int wyn = fileChooser.showOpenDialog(frmMapFilesHandler);
		if (wyn == JFileChooser.APPROVE_OPTION) {
			image_filename = fileChooser.getSelectedFile().getAbsolutePath();
			displayMap();
			setImage_loaded(true);
			image_file = fileChooser.getSelectedFile();
			loadRegionsForImage(image_file);
			//readRegions();
		}
		
	}
	
	private int loadRegionsForImage(File image_file) {
		int regions = -1;
		String filename = image_file.getName();
		String pathname = image_file.getParentFile().getAbsolutePath();
		String name = filename.substring(0, filename.lastIndexOf('.'));
		String regions_name = pathname+File.separator+name+".csv";
		regions_file = new File(regions_name);
		if (regions_file.exists()) {
			try {
				regions_list.clear();
				rectangles_list.clear();
				regions_list = LabelRegion.parseCSV(regions_name, ";", opos_labels_map);
				regionsListModel.removeAllElements();
				for (Iterator<LabelRegion> iterator = regions_list.iterator(); iterator.hasNext();) {
					LabelRegion region = (LabelRegion) iterator.next();
					regionsListModel.addElement(region);
					rectangles_list.add(labelRegion2Rectangle(region));
				}
				lblMapPreview.repaint();
				
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(frmMapFilesHandler, 
						"<html>Parsing regions file error. "
						+ "<BR>The original message is:"+e.getMessage()+
						"<br>The regions file will be repelaced.</html>", 
						"Labels file parsing error.", 
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		return regions;
	}
	
	
	public JLabel getLblMapPreview() {
		return lblMapPreview;
	}
	public JTextField getTextFieldX0() {
		return textFieldX0;
	}
	public JTextField getTextFieldY0() {
		return textFieldY0;
	}
	public JTextField getTextFieldX1() {
		return textFieldX1;
	}
	public JTextField getTextFieldY1() {
		return textFieldY1;
	}
	public JButton getBtnAddRegion() {
		return btnAddRegion;
	}
	public JLabel getLblStatus() {
		return lblStatus;
	}
	public JComboBox getComboBoxLabelType() {
		return comboBox;
	}
	public JPanel getPanelRegionParams() {
		return panelRegionParams;
	}
	public JButton getBtnSave() {
		return btnSave;
	}
	public JList getJListRegions() {
		return listRegions;
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	public JMenuItem getMenuItemRemoveRegion() {
		return mntmRemove;
	}
	public JMenuItem getMenuItemEditRegion() {
		return mntmEdit;
	}
}
