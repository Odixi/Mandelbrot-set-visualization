import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.MouseInfo;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.text.Format;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

public class UIHandler implements MouseListener, ActionListener, ItemListener{
	
	public static final int BLACK_WHITE = 0;
	public static final int COLORFUL = 1;
	public static final int CONSTANT_COLORING = 2;
	
	private final int RIGHT_PANEL_WIDTH = 256;
	private final int MIN_HEIGHT = 600;
	private final int MIN_WIDHT = 700;
	private final int HEIGHT_ADDITIONAL = 130;
	
	private BufferedImage image;
	
	private ImageHandler imageHandler;
	private ImageCalcThread imageCalcThread;
	private JFrame frame;
	private JPanel panelMain;
	private JPanel panelRight;
	private JPanel panelLeft;
	private JPanel panelZoomBtns;
	private JTextField resWidthTextField;
	private JTextField resHeightTextField;
	private JTextField textFieldReal;
	private JTextField textFieldImaginary;
	private JTextField zoomStepField;
	private JLabel imgLabel;
	private JLabel zoomText;
	private JRadioButton typeDoubleButton;
	private JRadioButton typeBDButton;
	private ButtonGroup typeButtonGroup;
	private JRadioButton colorButtonBlackWhite;
	private JRadioButton colorButtonColorSYC;
	private JRadioButton colorButtonCONST;
	private ButtonGroup colorButtonGroup;
	private JTextField iterationsTextField;
	private JTextField gotoRealField;
	private JTextField gotoImagField;
	private JTextField gotoZoomField;
	private JButton setResolutionBtn;
	private JButton zoomInBtn;
	private JButton zoomOutBtn;
	private JButton renderBtn;
	private JButton stopBtn;
	private JButton gotoBtn;
	
	private JFileChooser fileChooser;
	private JTextField chooseDirTextField;
	private JTextField imageFileNameTextField;
	private JButton browseDirBtn;
	private JButton saveImgBtn;
	
	private ComplexDouble newMiddle;
	private ComplexBigDecimal newMiddleBD;
	private double newZoom;
	private BigDecimal newZoomBD;
	
	private boolean useBigDecimal;
	
	public UIHandler(ImageHandler imgHandler){
		
		useBigDecimal = false;
		
		newZoom = 0.5;
		newZoomBD = new BigDecimal(0.5);
		
		imageHandler = imgHandler;
		imgHandler.setUIHandler(this);
		imageCalcThread = new ImageCalcThread(imageHandler, this);
		newMiddle = new ComplexDouble(0, 0);
		newMiddleBD = new ComplexBigDecimal(new BigDecimal(0), new BigDecimal(0));
		frame = new JFrame("Mandelbrot set tools");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(imageHandler.getWidth() + RIGHT_PANEL_WIDTH + 34, imageHandler.getHeight() + HEIGHT_ADDITIONAL); //TODO
		frame.setMaximumSize(new Dimension(5000, 5000));
		frame.setResizable(true);
		
		panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.X_AXIS));
		panelMain.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		
		
		frame.add(panelMain);
		
		// ------- Left side (image) ------- //
		
		panelLeft = new JPanel();
		panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
		panelLeft.setAlignmentY(0);
		
		panelMain.add(panelLeft);
//		panelLeft.setMaximumSize(new Dimension(imageHandler.getWidth(), imageHandler.getHeight()+200));
//		panelLeft.setMinimumSize(new Dimension(imageHandler.getWidth(),imageHandler.getHeight()+200));
		
		BufferedImage img = new BufferedImage(imageHandler.getWidth(), imageHandler.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		// ---Image---
		imgLabel = new JLabel(new ImageIcon(img));
		imgLabel.setForeground(Color.WHITE);
		imgLabel.setHorizontalTextPosition(JLabel.CENTER);
		panelLeft.add(imgLabel);
		
		// --- Zoom ---
		zoomText = new JLabel("Zoom: " + String.valueOf(imageHandler.getZoom())); // TODO BD
		panelLeft.add(zoomText);
		
		// ---- Koordinate fiedls ----
		JPanel koordPanel = new JPanel();
		koordPanel.setLayout(new BoxLayout(koordPanel, BoxLayout.X_AXIS));
		
		JPanel koordLabelPanel = new JPanel();
		koordLabelPanel.setLayout(new BoxLayout(koordLabelPanel, BoxLayout.Y_AXIS));
		koordLabelPanel.add(new JLabel("x: "));
		koordLabelPanel.add(new JLabel("y: "));
		
		JPanel koordInputPanel = new JPanel();
		koordInputPanel.setLayout(new BoxLayout(koordInputPanel, BoxLayout.Y_AXIS));
		textFieldReal = new JTextField();
		textFieldReal.setMaximumSize(new Dimension(9000, 20));
		textFieldReal.setEditable(false);
		textFieldImaginary = new JTextField();
		textFieldImaginary.setMaximumSize(new Dimension(9000, 20));
		textFieldImaginary.setEditable(false);
		textFieldReal.setText("0");
		textFieldImaginary.setText("0");
		koordInputPanel.add(textFieldReal);
		koordInputPanel.add(textFieldImaginary);
		
		koordPanel.add(koordLabelPanel);
		koordPanel.add(koordInputPanel);
		panelLeft.add(koordPanel);
		
		
		
		// -------------------- Right side --------------------- //
		
		panelRight = new JPanel();
		panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
		panelRight.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		panelMain.add(panelRight);
		panelRight.setAlignmentY(0);
		
		JComponent emptyBoxHor4 = (JComponent)Box.createRigidArea(new Dimension(RIGHT_PANEL_WIDTH, 10));
		emptyBoxHor4.setAlignmentX(0);
		panelRight.add(emptyBoxHor4);
		
		// ---- Resolution panel ---- //
		
		JLabel resolutionLabel = new JLabel("Resolution");
		panelRight.add(resolutionLabel);
		resolutionLabel.setAlignmentX(0);
		
		JPanel panelResolution = new JPanel();
		panelResolution.setLayout(new BoxLayout(panelResolution, BoxLayout.X_AXIS));
		panelResolution.setAlignmentX(0);
		
		JLabel resXLabel = new JLabel("Width:");
		panelResolution.add(resXLabel);
		resWidthTextField = new JTextField();
		resWidthTextField.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 20));
		resWidthTextField.setText("1280");
		panelResolution.add(resWidthTextField);
		JLabel resYLabel = new JLabel("Height:");
		panelResolution.add(resYLabel);
		resHeightTextField = new JTextField();
		resHeightTextField.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 20));
		resHeightTextField.setText("720");
		panelResolution.add(resHeightTextField);
		
		panelRight.add(panelResolution);
		
		setResolutionBtn = new JButton("Set resolution");
		setResolutionBtn.addActionListener(this);
		setResolutionBtn.setActionCommand("resolution");
		setResolutionBtn.setAlignmentX(0);
		panelRight.add(setResolutionBtn);
		
		JComponent emptyBoxHor5 = (JComponent)Box.createRigidArea(new Dimension(RIGHT_PANEL_WIDTH, 10));
		emptyBoxHor5.setAlignmentX(0);
		panelRight.add(emptyBoxHor5);
		
		
		// ---- Select type panel ---- //
		JPanel typeChoosingPanel = new JPanel();
		typeChoosingPanel.setLayout(new BoxLayout(typeChoosingPanel, BoxLayout.Y_AXIS));
		typeChoosingPanel.setBorder(BorderFactory.createEtchedBorder());
		
		JLabel typeChooseTextLabel = new JLabel("Choose type");
		typeChooseTextLabel.setAlignmentX(0);
		typeChooseTextLabel.setToolTipText("Double is much faster but has limited precision.");
		
		typeButtonGroup = new ButtonGroup();
		typeBDButton = new JRadioButton("BigDecimal");
		typeDoubleButton = new JRadioButton("Double");
		
		typeDoubleButton.addItemListener(this);
		typeBDButton.addItemListener(this);
		
		typeButtonGroup.add(typeBDButton);
		typeButtonGroup.add(typeDoubleButton);
		typeBDButton.setAlignmentX(0);
		typeDoubleButton.setAlignmentX(0);
		typeDoubleButton.setSelected(true);
		
		typeChoosingPanel.add(typeChooseTextLabel);
		typeChoosingPanel.add(typeDoubleButton);
		typeChoosingPanel.add(typeBDButton);
		JComponent emptyBox = (JComponent)Box.createRigidArea(new Dimension(RIGHT_PANEL_WIDTH, 0));
		typeChoosingPanel.add(emptyBox);
		emptyBox.setAlignmentX(0);
		
		panelRight.add(typeChoosingPanel);
		typeChoosingPanel.setAlignmentX(0);
		
		JComponent emptyBoxHor = (JComponent)Box.createRigidArea(new Dimension(RIGHT_PANEL_WIDTH, 10));
		emptyBoxHor.setAlignmentX(0);
		panelRight.add(emptyBoxHor);
		
		// --- Zoom panel --- ///
		
		panelZoomBtns = new JPanel();
		panelZoomBtns.setLayout(new BoxLayout(panelZoomBtns, BoxLayout.X_AXIS));
		
		zoomInBtn = new JButton("Zoom in");
		zoomInBtn.setActionCommand("zoomIn");

		zoomInBtn.addActionListener(this);
		
		zoomOutBtn = new JButton("Zoom out");
		zoomOutBtn.setActionCommand("zoomOut");
		zoomOutBtn.addActionListener(this);
		
		panelZoomBtns.add(zoomInBtn);
		panelZoomBtns.add((JComponent)Box.createRigidArea(new Dimension(10, 40)));
		panelZoomBtns.add(zoomOutBtn);
		
		panelRight.add(panelZoomBtns);
		panelZoomBtns.setAlignmentX(0);
		
		// --- Zoom step field --- //
		
		JLabel zoomStepFieldlabel = new JLabel("Zoom step multiplier");
		zoomStepField = new JTextField();
		
		panelRight.add(zoomStepFieldlabel);
		zoomStepFieldlabel.setAlignmentX(0);
		panelRight.add(zoomStepField);
		zoomStepField.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 20));
		zoomStepField.setAlignmentX(0);
		zoomStepField.setText("2");
		
		JComponent emptyBoxHor2 = (JComponent)Box.createRigidArea(new Dimension(RIGHT_PANEL_WIDTH, 20));
		emptyBoxHor2.setAlignmentX(0);
		panelRight.add(emptyBoxHor2);
		
		// ---------- Color panel ---------- //
		
		JPanel panelColor = new JPanel();
		panelColor.setLayout(new BoxLayout(panelColor, BoxLayout.Y_AXIS));
		panelColor.setAlignmentX(0);
		panelColor.setBorder(BorderFactory.createEtchedBorder());
		
		JLabel coloringLabel = new JLabel("Coloring method");
		panelColor.add(coloringLabel);
		coloringLabel.setAlignmentX(0);
		
		colorButtonBlackWhite = new JRadioButton("Black and white");
		colorButtonBlackWhite.addItemListener(this);
		colorButtonColorSYC = new JRadioButton("Colorful");
		colorButtonColorSYC.addItemListener(this);
		colorButtonCONST = new JRadioButton("Constatnt coloring");
		colorButtonCONST.addItemListener(this);
		
		panelColor.add(colorButtonBlackWhite);
		colorButtonBlackWhite.setAlignmentX(0);
		panelColor.add(colorButtonColorSYC);
		colorButtonColorSYC.setAlignmentX(0);
		panelColor.add(colorButtonCONST);
		colorButtonCONST.setAlignmentX(0);
		
		colorButtonGroup = new ButtonGroup();
		colorButtonGroup.add(colorButtonBlackWhite);
		colorButtonGroup.add(colorButtonColorSYC);
		colorButtonGroup.add(colorButtonCONST);
		colorButtonColorSYC.setSelected(true);
		
		JLabel iterTFLabel = new JLabel("Iteration addition level");
		iterTFLabel.setToolTipText("Iterations = log(zoom) * (this field value)");
		panelColor.add(iterTFLabel);
		iterTFLabel.setAlignmentX(0);
		iterTFLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		
		iterationsTextField = new JTextField();
		iterationsTextField.setText("100");
		panelColor.add(iterationsTextField);
		iterationsTextField.setAlignmentX(0);
		iterationsTextField.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 20));
		
		panelRight.add(panelColor);
		
		JComponent emptyBoxHor3 = (JComponent)Box.createRigidArea(new Dimension(RIGHT_PANEL_WIDTH, 20));
		emptyBoxHor3.setAlignmentX(0);
		panelRight.add(emptyBoxHor3);
		
		// ----- GoTo panel
		
		JPanel panelGoto = new JPanel();
		panelGoto.setLayout(new BoxLayout(panelGoto, BoxLayout.Y_AXIS));
		panelGoto.setBorder(BorderFactory.createEtchedBorder());
		panelGoto.setAlignmentX(0);
		JPanel panelGotoTitle = new JPanel();
		panelGotoTitle.setLayout(new BoxLayout(panelGotoTitle, BoxLayout.X_AXIS));
		panelGotoTitle.setAlignmentX(0);
		JLabel gotoTitle = new JLabel("Go to a specific zoom and point");
		panelGoto.add(gotoTitle);
		JPanel panelGoto1 = new JPanel();
		panelGoto1.setLayout(new BoxLayout(panelGoto1, BoxLayout.X_AXIS));
		panelGoto1.setAlignmentX(0);
		JPanel panelGoto2 = new JPanel();
		panelGoto2.setLayout(new BoxLayout(panelGoto2, BoxLayout.X_AXIS));
		panelGoto2.setAlignmentX(0);
		JPanel panelGoto3 = new JPanel();
		panelGoto3.setLayout(new BoxLayout(panelGoto3, BoxLayout.X_AXIS));
		panelGoto3.setAlignmentX(0);
			
		JLabel gotoZoom = new JLabel("Zoom                 ");
		JLabel gotoReal = new JLabel("Real part           ");
		JLabel gotoImag = new JLabel("Imaginary part ");
		
		panelGoto1.add(gotoZoom);
		panelGoto2.add(gotoReal);
		panelGoto3.add(gotoImag);
		
		gotoZoomField = new JTextField();
		gotoZoomField.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 20));
		gotoZoomField.setText("5.0E-1");
		panelGoto1.add(gotoZoomField);
		gotoRealField = new JTextField();
		gotoRealField.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 20));
		gotoRealField.setText("0");
		panelGoto2.add(gotoRealField);
		gotoImagField = new JTextField();
		gotoImagField.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 20));
		gotoImagField.setText("0");
		panelGoto3.add(gotoImagField);
		
		gotoBtn = new JButton("GoTo");
		gotoBtn.setActionCommand("GoTo");
		gotoBtn.addActionListener(this);
		
		panelGoto.add(panelGotoTitle);
		panelGoto.add(panelGoto1);
		panelGoto.add(panelGoto2);
		panelGoto.add(panelGoto3);
		panelGoto.add(gotoBtn);
		panelRight.add(panelGoto);
		
		JComponent emptyBoxHorGoto = (JComponent)Box.createRigidArea(new Dimension(RIGHT_PANEL_WIDTH, 20));
		emptyBoxHorGoto.setAlignmentX(0);
		panelRight.add(emptyBoxHorGoto);
		
		// ----- Render & Stop buttons ---- //
		
		JPanel panelRenderStop = new JPanel();
		panelRenderStop.setLayout(new BoxLayout(panelRenderStop, BoxLayout.X_AXIS));
		panelRenderStop.setAlignmentX(0);
		
		renderBtn = new JButton("Render");
		renderBtn.setActionCommand("render");
		panelRenderStop.add(renderBtn);
		renderBtn.addActionListener(this);

		panelRenderStop.add((JComponent)Box.createRigidArea(new Dimension(10, 0)));
		
		stopBtn = new JButton("Stop");
		stopBtn.setActionCommand("stop");
		panelRenderStop.add(stopBtn);
		stopBtn.addActionListener(this);
		stopBtn.setEnabled(false);
		
		panelRight.add(panelRenderStop);
		
		JComponent emptyBoxHorrs = (JComponent)Box.createRigidArea(new Dimension(RIGHT_PANEL_WIDTH, 20));
		emptyBoxHorrs.setAlignmentX(0);
		panelRight.add(emptyBoxHorrs);
		
		// ------- Save Image Panel ------- //
		
		JPanel savePanel = new JPanel();
		savePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		savePanel.setAlignmentX(0);
		savePanel.setBorder(BorderFactory.createEtchedBorder());
		
		JLabel saveLabText = new JLabel("Save image:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0; c.gridy = 0;
		savePanel.add(saveLabText, c);
		
		c.gridx = 0; c.gridy = 1; c.weightx = 0.01;
		savePanel.add(new JLabel("File name: "),c);
		
		c.gridx = 1; c.gridy = 1; c.weightx = 0.99;
		imageFileNameTextField = new JTextField("img");
		savePanel.add(imageFileNameTextField,c);
		
		c.gridx = 0; c.gridy = 2; c.weightx = 0.01;
		savePanel.add(new JLabel("Directory: "),c);
		
		c.gridx = 1; c.gridy = 2; c.weightx = 0.9;
		chooseDirTextField = new JTextField(new File(".").getAbsolutePath());
		savePanel.add(chooseDirTextField,c);
		
		c.gridx = 0; c.gridy = 3; c.weightx = 0.5; c.gridwidth = 2;
		JPanel sspan = new JPanel();
		sspan.setLayout(new BoxLayout(sspan, BoxLayout.X_AXIS));
		savePanel.add(sspan, c);
		browseDirBtn = new JButton("Browse dir");
		browseDirBtn.setActionCommand("browse");
		browseDirBtn.addActionListener(this);
		sspan.add(browseDirBtn);
		
		sspan.add((JComponent)Box.createRigidArea(new Dimension(10, 0)));

		saveImgBtn = new JButton("Save image");
		saveImgBtn.setActionCommand("save");
		saveImgBtn.addActionListener(this);
		sspan.add(saveImgBtn);
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setApproveButtonText("Select directory");
		fileChooser.setCurrentDirectory(new File("."));
		
		savePanel.setMaximumSize(new Dimension(RIGHT_PANEL_WIDTH, 100));
		panelRight.add(savePanel);
		
		for (Component jc : panelRight.getComponents()){
			((JComponent)jc).setAlignmentX(0);
		}
		
		// <-<-->-> //
		frame.setVisible(true);
		imgLabel.addMouseListener(this);
		updateImage();
		
	}
	
	private void updateImage(){
		
		try{
			getIterLevel();
		}catch (Exception e){
			iterationsTextField.setText("Invalid input! Input has to be int > 0");
			return;
		}
		
		imageHandler.setMiddle(newMiddle);
		imageHandler.setMiddleBD(newMiddleBD);
		imageHandler.setZoom(newZoom);
		imageHandler.setZoomBD(newZoomBD);
		imageCalcThread.start();
		disableStuff();
	}
	
	// When calculating images we should disable some buttons and stuff
	private void disableStuff(){
		zoomInBtn.setEnabled(false);
		zoomOutBtn.setEnabled(false);
		typeDoubleButton.setEnabled(false);
		typeBDButton.setEnabled(false);
		
		colorButtonBlackWhite.setEnabled(false);
		colorButtonColorSYC.setEnabled(false);
		colorButtonCONST.setEnabled(false);
		renderBtn.setEnabled(false);
		stopBtn.setEnabled(true);
		setResolutionBtn.setEnabled(false);
		gotoBtn.setEnabled(false);
		
	}
	
	private void enableStuff(){
		zoomInBtn.setEnabled(true);
		zoomOutBtn.setEnabled(true);
		typeDoubleButton.setEnabled(true);
		typeBDButton.setEnabled(true);
		
		colorButtonBlackWhite.setEnabled(true);
		colorButtonColorSYC.setEnabled(true);
		colorButtonCONST.setEnabled(true);
		renderBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		setResolutionBtn.setEnabled(true);
		gotoBtn.setEnabled(true);
	}
	
	public void reciveImage(BufferedImage img){
		image = img;
		imgLabel.setIcon(new ImageIcon(img));
		zoomText.setText("Zoom: " + String.valueOf(imageHandler.getZoom()));
		imageCalcThread = new ImageCalcThread(imageHandler, this);
		enableStuff();
	}
	
	public void setLoadingText(String state){
		imgLabel.setText(state);
		frame.setTitle(" Mandelbrot set tools " + state);
	}
	
	public boolean getUseBigDecimal(){
		return useBigDecimal;
	}
	
	public int getColorMode(){
		
		int colormode = colorButtonBlackWhite.isSelected() ? BLACK_WHITE : COLORFUL;
		colormode = colorButtonCONST.isSelected() ? CONSTANT_COLORING : colormode;
		
		return colormode;
	}
	
	public int getIterLevel() throws Exception{
		
		String st = iterationsTextField.getText();
		int value = 100;
		
		try{
			value = Integer.valueOf(st);
		}catch (Exception e){
			throw e;
		}
		if (value <= 0){
			throw new Exception();
		}
		return value;
	}
	
	private double parseZoomFieldValue() throws NullPointerException, NumberFormatException{
		
		String value = zoomStepField.getText();
		double dv = 2;
		
		try{
			dv = Double.valueOf(value);
		}catch (NullPointerException e) {
			throw e;
		}catch (NumberFormatException e) {
			throw e;
		}
		
		return dv;
	}
	
	private void setResolution(){
		
		int width = 1280;
		int height = 720;
		
		try{
			width = Integer.valueOf(resWidthTextField.getText());
			height = Integer.valueOf(resHeightTextField.getText());
		} catch (Exception e){
			resWidthTextField.setText("pls...");
			resHeightTextField.setText("try...");
			return;
		}
		
		if (width <= 0 || height <= 0){
			resWidthTextField.setText("pls...");
			resHeightTextField.setText("try...");
			return;
		}
		
		imageHandler.setWidth(width);
		imageHandler.setHeight(height);
		double xmax = 1;
		double xmin = -1;
		double ymax = ((double)height/(double)width)*(xmax-xmin)/2;
		double ymin = -((double)height/(double)width)*(xmax-xmin)/2;
		
		imageHandler.setXYMinMax(xmin, xmax, ymin, ymax);
		
		imgLabel.setIcon(new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR)));
		int frameWidth = (width + RIGHT_PANEL_WIDTH + 34 < MIN_WIDHT) ? MIN_WIDHT : width + RIGHT_PANEL_WIDTH + 34;
		int frameHeight = (height + HEIGHT_ADDITIONAL < MIN_HEIGHT) ? MIN_HEIGHT : height + HEIGHT_ADDITIONAL;
		frame.setSize(frameWidth, frameHeight);
		updateImage();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, imgLabel);
		p.x = p.x - ((int) (imgLabel.getSize().getWidth() - imgLabel.getIcon().getIconWidth()))/2;
		p.y = p.y - ((int) (imgLabel.getSize().getHeight() - imgLabel.getIcon().getIconHeight()))/2;
		
		if (p.x >= 0 && p.x <= imgLabel.getIcon().getIconWidth() && p.y >= 0 && p.y <= imgLabel.getIcon().getIconHeight()){
				newMiddle = imageHandler.TransformCoordinateToComplex(p.x, p.y);
				zoomText.setText("Zoom: " + String.valueOf(imageHandler.getZoom()));
				newMiddleBD = imageHandler.TransformCoordinateToComplexBD(p.x, p.y);
				if (useBigDecimal){
					textFieldReal.setText(String.valueOf(newMiddleBD.rp));
					textFieldImaginary.setText(String.valueOf(newMiddleBD.ip));
				}else{
					textFieldReal.setText(String.valueOf(newMiddle.rp));
					textFieldImaginary.setText(String.valueOf(newMiddle.ip));
				}
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	//Button actions
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		double zoomStep = 2;
		
		try{
			zoomStep = parseZoomFieldValue();
		} catch(Exception a){
			zoomStepField.setText("Ivalid format!");
			JOptionPane.showMessageDialog(null, "Invalid format");
			return;
		}
		if (zoomStep == 0){
			zoomStepField.setText("0 is not allowed!");
			JOptionPane.showMessageDialog(null, "0 is not allowed!");
			return;
		}
		
		switch (action) {
		case "save":
			File dir = fileChooser.getSelectedFile();
			try {
				ImageIO.write(image, "PNG", new File(dir, imageFileNameTextField.getText() + ".png"));
				JOptionPane.showMessageDialog(null, "Image saved succesfully");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Error");
				e1.printStackTrace();	
				return;
			}
			break;
		
		case "browse":
			int returnVal = fileChooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				chooseDirTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
			break;
			
		case "resolution":
			setResolution();
			break;
			
		case "zoomIn":
			newZoom = imageHandler.getZoom()*zoomStep;
			newZoomBD = imageHandler.getZoomBD().multiply(new BigDecimal(zoomStep));
			updateImage();
			break;

		case "zoomOut":
			newZoom = imageHandler.getZoom()/zoomStep;
			newZoomBD = imageHandler.getZoomBD().multiply(new BigDecimal(1.0/zoomStep));
			updateImage();
			break;
			
		case "render":
			updateImage();
			break;
			
		case "GoTo":
			String zoom, real, imag;
			
			zoom = gotoZoomField.getText();
			real = gotoRealField.getText();
			imag = gotoImagField.getText();
			
			if (useBigDecimal){
				BigDecimal zoomBD,realBD,imagBD;
				try{
					zoomBD = new BigDecimal(zoom);
				}catch (Exception exc){
					gotoZoomField.setText("Not valid format!");
					return;
				}
				try{
					realBD = new BigDecimal(real);
				}catch (Exception exc){
					gotoRealField.setText("Not valid format!");
					return;
				}
				try{
					imagBD = new BigDecimal(imag);
				}catch (Exception exc){
					gotoImagField.setText("Not valid format!");
					return;
				}
				newZoomBD = zoomBD;
				newZoom = zoomBD.doubleValue();
				
				newMiddleBD = new ComplexBigDecimal(realBD, imagBD);
				newMiddle = new ComplexDouble(realBD.doubleValue(), imagBD.doubleValue());
				textFieldReal.setText(String.valueOf(newMiddleBD.rp));
				textFieldImaginary.setText(String.valueOf(newMiddleBD.ip));
				zoomText.setText("Zoom: " + newZoom);
			}else{
				double zoomD,realD,imagD;
				try{
					zoomD = Double.valueOf(zoom);
				}catch (Exception exc){
					gotoZoomField.setText("Not valid format!");
					return;
				}
				try{
					realD = Double.valueOf(real);
				}catch (Exception exc){
					gotoRealField.setText("Not valid format!");
					return;
				}
				try{
					imagD = Double.valueOf(imag);
				}catch (Exception exc){
					gotoImagField.setText("Not valid format!");
					return;
				}
				newZoom = zoomD;
				newZoomBD = new BigDecimal(zoomD);
				
				newMiddle = new ComplexDouble(realD, imagD);
				newMiddleBD = new ComplexBigDecimal(new BigDecimal(realD), new BigDecimal(imag));
				textFieldReal.setText(String.valueOf(newMiddle.rp));
				textFieldImaginary.setText(String.valueOf(newMiddle.ip));
				zoomText.setText("Zoom: " + newZoom);
			}
			break;
			
		case "stop":
			
		}
		
	}

	// Check box listener
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (typeDoubleButton.isSelected()){
			useBigDecimal = false;
		}else{
			useBigDecimal = true;
		}
		
	}

}
