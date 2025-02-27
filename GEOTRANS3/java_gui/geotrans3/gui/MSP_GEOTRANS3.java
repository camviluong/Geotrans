// CLASSIFICATION: UNCLASSIFIED

/******************************************************************************
* Filename: MSP_GEOTRANS3.java
*
* Copyright BAE Systems Inc. 2012 ALL RIGHTS RESERVED
*
* MODIFICATION HISTORY
*
* DATE      NAME        DR#          DESCRIPTION
*
* 08/13/10  S Gillis    BAEts27457   Update to GeoTrans 3.1
* 05/31/11  K. Lam      BAEts28657   Update version to 3.2
* 08/09/11  K. Swanson  BAEts29073   Moved pack after setSize
*                                    since gui didn't display on Solaris 8
* 11/18/11  K. Lam      MSP_029475   Update version to 3.3
* 07/18/12  S. Gillis   MSP_00029550 Updated exception handling 
* 07/31/13  K. Lam      MSP_00029595 Fix Load/Save Settings &
*                                    update version to 3.4
* 06/24/15  K. Lam      MSP_00023924 Enhance Load/Save Settings
*                                    allowing users to specify filename
* 01/12/16  K. Chen     MSP_00030518 Add US Survey Feet Support
* 11/07/17  D. Hoang                 Update version to 3.7.1
* 04/29/19  K. Lam      GTR-28       Update version to 3.8
* 04/06/20  E. Poon     GTR-53       Add support of EPSG codes
* 01/27/22  K. Lam      GTRU-15      Update version to 3.9
*****************************************************************************/

package geotrans3.gui;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import geotrans3.misc.AOI;
import geotrans3.misc.FormatOptions;
import geotrans3.utility.ReadEnv;
import geotrans3.utility.Platform;
import geotrans3.utility.Directory;
import geotrans3.utility.DATFileFilter;
import geotrans3.utility.Utility;
import geotrans3.jni.*;
import geotrans3.coordinates.Accuracy;
import geotrans3.coordinates.ConvertResults;
import geotrans3.coordinates.CoordinateTuple;
import geotrans3.enumerations.*;
import geotrans3.exception.CoordinateConversionException;
import geotrans3.misc.StringHandler;
import geotrans3.parameters.*;
import geotrans3.utility.*;
import geotrans3.utility.color.*;


public class MSP_GEOTRANS3 extends javax.swing.JFrame 
{ 
  private JNICoordinateConversionService jniCoordinateConversionService;
  private Directory          currentDir;
  private StringHandler      stringHandler;
  private java.awt.Color     defaultSelectedColor;
  private java.awt.Color     currentColor;
  private java.awt.Color     currentColorUp;
  private java.awt.Color     currentColorDown;
  private java.awt.Component prevFocus;
  private MasterPanel        upperMasterPanel;
  private MasterPanel        lowerMasterPanel;
  private FormatOptions      formatOptions;
  private java.lang.String   currLookAndFeel;
  private String             dataDirPathName;
  private java.io.File       defaultFile;
  private LoadSettings       defaultSettings;
  private String[]           currentDatum;
  private CoordinateSystemParameters[] currentParameters;
  private Accuracy[]                   currentAccuracy;
  
  private Map<String, Map<String, String>> epsgData = new HashMap<>();
  
  private static final String EPSG_CSV_FILE = "EPSG_codes_TO_GeoTrans_fields.csv";
  private static final String GEOTRANS_DATA_DIR_ENV_VAR = "MSPCCS_DATA";
  

 /** Creates new form MSP_GEOTRANS3 */
  public MSP_GEOTRANS3()
  {
    currentDir           = new Directory(".");
    stringHandler        = new StringHandler();
    defaultSelectedColor = new java.awt.Color(0, 0, 0);
    defaultSelectedColor = (java.awt.Color)javax.swing.UIManager.get("Button.select");
    currentColor         = Green.VALUE;
    currentColorUp       = Green.VALUE;
    currentColorDown     = Green.VALUE;
    currLookAndFeel      = "Java";
    dataDirPathName      = "";

    currentDatum         = new String[2];
    currentParameters    = new CoordinateSystemParameters[2];
    currentAccuracy      = new Accuracy[2];
    
    initComponents();

    setIcons();

    javax.swing.ButtonGroup lookAndFeelGroup = new javax.swing.ButtonGroup();
    lookAndFeelGroup.add(metalRadioButtonMenuItem);
    lookAndFeelGroup.add(unixRadioButtonMenuItem);
    lookAndFeelGroup.add(windowsRadioButtonMenuItem);

    Utility.setIcon(this, "/geotrans3/gui/icons/geotrans_logo.gif");

    if (Platform.isUnix)
       setSize(new java.awt.Dimension(484, 679));
    
    try
    {
       java.util.Properties p = ReadEnv.getEnvVars();
       dataDirPathName = p.getProperty("MSPCCS_DATA");
    }
    catch(Throwable e)
    {
       e.printStackTrace();
    }
    
    if(dataDirPathName != null && dataDirPathName.length() > 0)
    {
       dataDirPathName += "/";
    }
    else
    {
       dataDirPathName = "../../data/";
    }
    dataDirPathName += "default.xml";

    defaultFile = new java.io.File(dataDirPathName);

    try
    {
       if(defaultFile.exists())
       {
          defaultSettings = new LoadSettings(this, defaultFile);
          defaultSettings.readDefaults();
       }
    }
    catch(Exception e)
    {
       stringHandler.displayErrorMsg(this, e.getMessage());
       defaultSettings = null;
    }

    initialize();
    
    // do pack at end; after gui is setup and size is set
    pack();

    // center needs to come after pack so the gui comes 
    // up in the center when using Windows
    Utility.center(null, this);

  }

  static 
  {
    // Load JNI Native library.  .dll or .so
    System.loadLibrary("jnimsp_ccs");
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    toolBar = new javax.swing.JToolBar();
    cutButton = new javax.swing.JButton();
    copyButton = new javax.swing.JButton();
    pasteButton = new javax.swing.JButton();
    formatButton = new javax.swing.JButton();
    toolBarSeparator = new javax.swing.JSeparator();
    upperPanel = new javax.swing.JPanel();
    convertPanel = new javax.swing.JPanel();
    convertDownButton = new javax.swing.JButton();
    convertUpButton = new javax.swing.JButton();
    lowerPanel = new javax.swing.JPanel();
    menuBar = new javax.swing.JMenuBar();
    fileMenu = new javax.swing.JMenu();
    openMenuItem = new javax.swing.JMenuItem();
    createFileHeaderMenuItem = new javax.swing.JMenuItem();
    loadSettingsMenuItem = new javax.swing.JMenuItem();
    saveSettingsMenuItem = new javax.swing.JMenuItem();
    fileMenuSeparator = new javax.swing.JSeparator();
    exitMenuItem = new javax.swing.JMenuItem();
    editMenu = new javax.swing.JMenu();
    cutMenuItem = new javax.swing.JMenuItem();
    copyMenuItem = new javax.swing.JMenuItem();
    pasteMenuItem = new javax.swing.JMenuItem();
    optionsMenu = new javax.swing.JMenu();
    formatMenuItem = new javax.swing.JMenuItem();
    lookAndFeelMenu = new javax.swing.JMenu();
    metalRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
    unixRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
    windowsRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
    datumMenu = new javax.swing.JMenu();
    createDatumMenuItem = new javax.swing.JMenuItem();
    deleteDatumMenuItem = new javax.swing.JMenuItem();
    ellipsoidMenu = new javax.swing.JMenu();
    createEllipsoidMenuItem = new javax.swing.JMenuItem();
    deleteEllipsoidMenuItem = new javax.swing.JMenuItem();
    convertMenu = new javax.swing.JMenu();
    convertDownMenuItem = new javax.swing.JMenuItem();
    convertMenuSeparator = new javax.swing.JSeparator();
    convertUpMenuItem = new javax.swing.JMenuItem();
    helpMenu = new javax.swing.JMenu();
    contentsMenuItem = new javax.swing.JMenuItem();
    helpMenuSeparator = new javax.swing.JSeparator();
    aboutMenuItem = new javax.swing.JMenuItem();

    setTitle("MSP GEOTRANS 3.9");
    setResizable(false);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });
    getContentPane().setLayout(new java.awt.GridBagLayout());

    toolBar.setFloatable(false);
    toolBar.setAlignmentX(0.0F);
    toolBar.setMinimumSize(new java.awt.Dimension(25, 25));
    toolBar.setPreferredSize(new java.awt.Dimension(25, 25));

    cutButton.setToolTipText("Cut");
    cutButton.setMaximumSize(new java.awt.Dimension(25, 30));
    cutButton.setMinimumSize(new java.awt.Dimension(17, 21));
    cutButton.setPreferredSize(new java.awt.Dimension(27, 27));
    cutButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cutActionPerformed(evt);
      }
    });
    cutButton.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        editButtonMouseEntered(evt);
      }
    });
    toolBar.add(cutButton);

    copyButton.setToolTipText("Copy");
    copyButton.setMaximumSize(new java.awt.Dimension(25, 30));
    copyButton.setMinimumSize(new java.awt.Dimension(17, 21));
    copyButton.setPreferredSize(new java.awt.Dimension(27, 27));
    copyButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        copyActionPerformed(evt);
      }
    });
    copyButton.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        editButtonMouseEntered(evt);
      }
    });
    toolBar.add(copyButton);

    pasteButton.setToolTipText("Paste");
    pasteButton.setMaximumSize(new java.awt.Dimension(25, 30));
    pasteButton.setMinimumSize(new java.awt.Dimension(17, 21));
    pasteButton.setPreferredSize(new java.awt.Dimension(27, 27));
    pasteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        pasteActionPerformed(evt);
      }
    });
    pasteButton.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        editButtonMouseEntered(evt);
      }
    });
    toolBar.add(pasteButton);

    formatButton.setToolTipText("Format Options");
    formatButton.setMaximumSize(new java.awt.Dimension(25, 30));
    formatButton.setMinimumSize(new java.awt.Dimension(17, 21));
    formatButton.setPreferredSize(new java.awt.Dimension(27, 27));
    formatButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        formatActionPerformed(evt);
      }
    });
    toolBar.add(formatButton);

    toolBarSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);
    toolBar.add(toolBarSeparator);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    getContentPane().add(toolBar, gridBagConstraints);

    upperPanel.setBackground(java.awt.Color.lightGray);
    upperPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0))));
    upperPanel.setMinimumSize(new java.awt.Dimension(469, 284));
    upperPanel.setOpaque(false);
    upperPanel.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.weighty = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    getContentPane().add(upperPanel, gridBagConstraints);

    convertPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    convertPanel.setLayout(new java.awt.GridBagLayout());

    convertDownButton.setFont(new java.awt.Font("Dialog", 0, 11));
    convertDownButton.setText("Convert Upper -> Lower");
    convertDownButton.setToolTipText("Convert Upper to Lower");
    convertDownButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    convertDownButton.setMaximumSize(new java.awt.Dimension(197, 24));
    convertDownButton.setMinimumSize(new java.awt.Dimension(197, 24));
    convertDownButton.setPreferredSize(new java.awt.Dimension(197, 24));
    convertDownButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        convertDownActionPerformed(evt);
      }
    });
    convertDownButton.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        convertButtonMousePressed(evt);
        convertDownButtonMousePressed(evt);
      }
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        convertButtonMouseReleased(evt);
        convertDownButtonMouseReleased(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 12);
    convertPanel.add(convertDownButton, gridBagConstraints);

    convertUpButton.setFont(new java.awt.Font("Dialog", 0, 11));
    convertUpButton.setText("Convert Lower -> Upper");
    convertUpButton.setToolTipText("Convert Lower to Upper");
    convertUpButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    convertUpButton.setMaximumSize(new java.awt.Dimension(197, 24));
    convertUpButton.setMinimumSize(new java.awt.Dimension(197, 24));
    convertUpButton.setPreferredSize(new java.awt.Dimension(197, 24));
    convertUpButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        convertUpActionPerformed(evt);
      }
    });
    convertUpButton.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        convertButtonMousePressed(evt);
        convertUpButtonMousePressed(evt);
      }
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        convertButtonMouseReleased(evt);
        convertUpButtonMouseReleased(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(3, 12, 3, 0);
    convertPanel.add(convertUpButton, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
    getContentPane().add(convertPanel, gridBagConstraints);

    lowerPanel.setBackground(java.awt.Color.lightGray);
    lowerPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0))));
    lowerPanel.setMinimumSize(new java.awt.Dimension(469, 284));
    lowerPanel.setOpaque(false);
    lowerPanel.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.weighty = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    getContentPane().add(lowerPanel, gridBagConstraints);

    menuBar.setMinimumSize(new java.awt.Dimension(285, 23));

    fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
    fileMenu.setText("File");

    openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
    openMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_O);
    openMenuItem.setText("Open . . .");
    openMenuItem.setToolTipText("Open");
    openMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        openActionPerformed(evt);
      }
    });
    fileMenu.add(openMenuItem);

    createFileHeaderMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
    createFileHeaderMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_C);
    createFileHeaderMenuItem.setText("Create File Header . . .");
    createFileHeaderMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        createFileHeaderActionPerformed(evt);
      }
    });
    fileMenu.add(createFileHeaderMenuItem);

    loadSettingsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
    loadSettingsMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_L);
    loadSettingsMenuItem.setText("Load Settings");
    loadSettingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        loadSettingsActionPerformed(evt);
      }
    });
    fileMenu.add(loadSettingsMenuItem);

    saveSettingsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
    saveSettingsMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_S);
    saveSettingsMenuItem.setText("Save Settings");
    saveSettingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveSettingsActionPerformed(evt);
      }
    });
    fileMenu.add(saveSettingsMenuItem);
    fileMenu.add(fileMenuSeparator);

    exitMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_X);
    exitMenuItem.setText("Exit");
    exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exitActionPerformed(evt);
      }
    });
    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    editMenu.setMnemonic(java.awt.event.KeyEvent.VK_E);
    editMenu.setText("Edit");

    cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
    cutMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_T);
    cutMenuItem.setText("Cut");
    cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cutActionPerformed(evt);
      }
    });
    editMenu.add(cutMenuItem);

    copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
    copyMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_C);
    copyMenuItem.setText("Copy");
    copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        copyActionPerformed(evt);
      }
    });
    editMenu.add(copyMenuItem);

    pasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
    pasteMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_P);
    pasteMenuItem.setText("Paste");
    pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        pasteActionPerformed(evt);
      }
    });
    editMenu.add(pasteMenuItem);

    menuBar.add(editMenu);

    optionsMenu.setMnemonic(java.awt.event.KeyEvent.VK_O);
    optionsMenu.setText("Options");

    formatMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
    formatMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_F);
    formatMenuItem.setText("Format . . .");
    formatMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        formatActionPerformed(evt);
      }
    });
    optionsMenu.add(formatMenuItem);

    lookAndFeelMenu.setMnemonic(java.awt.event.KeyEvent.VK_L);
    lookAndFeelMenu.setText("Look and Feel");

    metalRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.CTRL_MASK));
    metalRadioButtonMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_J);
    metalRadioButtonMenuItem.setSelected(true);
    metalRadioButtonMenuItem.setText("Java");
    metalRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookAndFeelActionPerformed(evt);
      }
    });
    lookAndFeelMenu.add(metalRadioButtonMenuItem);

    unixRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
    unixRadioButtonMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_U);
    unixRadioButtonMenuItem.setText("Unix");
    unixRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookAndFeelActionPerformed(evt);
      }
    });
    lookAndFeelMenu.add(unixRadioButtonMenuItem);

    windowsRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
    windowsRadioButtonMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_W);
    windowsRadioButtonMenuItem.setText("Windows");
    windowsRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lookAndFeelActionPerformed(evt);
      }
    });
    lookAndFeelMenu.add(windowsRadioButtonMenuItem);

    optionsMenu.add(lookAndFeelMenu);

    menuBar.add(optionsMenu);

    datumMenu.setMnemonic(java.awt.event.KeyEvent.VK_D);
    datumMenu.setText("Datum");

    createDatumMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
    createDatumMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_C);
    createDatumMenuItem.setText("Create . . .");
    createDatumMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        createDatumActionPerformed(evt);
      }
    });
    datumMenu.add(createDatumMenuItem);

    deleteDatumMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
    deleteDatumMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_D);
    deleteDatumMenuItem.setText("Delete . . .");
    deleteDatumMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteDatumActionPerformed(evt);
      }
    });
    datumMenu.add(deleteDatumMenuItem);

    menuBar.add(datumMenu);

    ellipsoidMenu.setMnemonic(java.awt.event.KeyEvent.VK_L);
    ellipsoidMenu.setText("Ellipsoid");

    createEllipsoidMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
    createEllipsoidMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_C);
    createEllipsoidMenuItem.setText("Create . . .");
    createEllipsoidMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        createEllipsoidActionPerformed(evt);
      }
    });
    ellipsoidMenu.add(createEllipsoidMenuItem);

    deleteEllipsoidMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
    deleteEllipsoidMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_D);
    deleteEllipsoidMenuItem.setText("Delete . . .");
    deleteEllipsoidMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteEllipsoidActionPerformed(evt);
      }
    });
    ellipsoidMenu.add(deleteEllipsoidMenuItem);

    menuBar.add(ellipsoidMenu);

    convertMenu.setMnemonic(java.awt.event.KeyEvent.VK_C);
    convertMenu.setText("Convert");

    convertDownMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
    convertDownMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_U);
    convertDownMenuItem.setLabel(" Upper to Lower");
    convertDownMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        convertDownActionPerformed(evt);
      }
    });
    convertMenu.add(convertDownMenuItem);
    convertMenu.add(convertMenuSeparator);

    convertUpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
    convertUpMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_L);
    convertUpMenuItem.setLabel("Lower to Upper");
    convertUpMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        convertUpActionPerformed(evt);
      }
    });
    convertMenu.add(convertUpMenuItem);

    menuBar.add(convertMenu);

    helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);
    helpMenu.setText("Help");

    contentsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
    contentsMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_C);
    contentsMenuItem.setText("Contents");
    contentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        helpContentsActionPerformed(evt);
      }
    });
    helpMenu.add(contentsMenuItem);
    helpMenu.add(helpMenuSeparator);

    aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
    aboutMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_A);
    aboutMenuItem.setText("About . . .");
    aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        aboutMenuItemActionPerformed(evt);
      }
    });
    helpMenu.add(aboutMenuItem);

    menuBar.add(helpMenu);

    setJMenuBar(menuBar);

  }// </editor-fold>//GEN-END:initComponents

  private void loadSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSettingsActionPerformed
    // MSP_0023924 - Allow user to load settings from file
    javax.swing.JFileChooser jChooser1 = new javax.swing.JFileChooser(currentDir.getDirectory());
    jChooser1.addChoosableFileFilter(new XMLFileFilter());
    jChooser1.setSelectedFile(new java.io.File("*.xml"));
    int state = jChooser1.showOpenDialog(this);
    if(state == javax.swing.JFileChooser.APPROVE_OPTION)
    {
      java.io.File file = jChooser1.getSelectedFile();
      currentDir.setDirectory(file.getAbsolutePath());
      if(file != null)
      {
        defaultSettings = new LoadSettings(this, file);
      }
    }
    
    try
    {
      defaultSettings.readDefaults();

      formatOptions = defaultSettings.getDefaultFormatOptions();

      updateDefaultSettings();
    }
    catch(Exception e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }
}//GEN-LAST:event_loadSettingsActionPerformed

  private void saveSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSettingsActionPerformed
    // MSP_0023924 - Allow user to save settings to file
    javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser(currentDir.getDirectory());
    fileChooser.setDialogTitle("Save As");
    fileChooser.addChoosableFileFilter(new XMLFileFilter()); 
    fileChooser.setSelectedFile(new java.io.File("*.xml"));
    int state = fileChooser.showSaveDialog(this);   
    if( state == javax.swing.JFileChooser.APPROVE_OPTION )
    {
        java.io.File file = fileChooser.getSelectedFile();
        currentDir.setDirectory(file.getAbsolutePath());
        if( file != null )
        {
            new SaveSettings(this, file, upperMasterPanel, lowerMasterPanel, formatOptions);
        }
    }
}//GEN-LAST:event_saveSettingsActionPerformed

    private void convertUpButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_convertUpButtonMouseReleased
    convertUpButton.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
    }//GEN-LAST:event_convertUpButtonMouseReleased

    private void convertUpButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_convertUpButtonMousePressed
    convertUpButton.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
    }//GEN-LAST:event_convertUpButtonMousePressed

    private void convertDownButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_convertDownButtonMouseReleased
    convertDownButton.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
    }//GEN-LAST:event_convertDownButtonMouseReleased

    private void convertDownButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_convertDownButtonMousePressed
    convertDownButton.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
    }//GEN-LAST:event_convertDownButtonMousePressed

    private void convertButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_convertButtonMouseReleased
    javax.swing.UIManager.put("Button.select", defaultSelectedColor);
    }//GEN-LAST:event_convertButtonMouseReleased

    private void convertButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_convertButtonMousePressed
    javax.swing.UIManager.put("Button.select", currentColor);
    }//GEN-LAST:event_convertButtonMousePressed

  private void createFileHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createFileHeaderActionPerformed
    new CreateFileHeaderDlg (jniCoordinateConversionService, this, true, currentDir, formatOptions, stringHandler).show ();
  }//GEN-LAST:event_createFileHeaderActionPerformed

  private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
    new AboutDlg(this, true).show();
  }//GEN-LAST:event_aboutMenuItemActionPerformed

  private void helpContentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpContentsActionPerformed
   java.io.File helpFile = new java.io.File ("..");
   try
   {
       if (StartBrowser.isWindowsPlatform())
           StartBrowser.displayURL(this, "file:" + helpFile.getCanonicalPath() + "\\help\\contents.htm");
       else
           StartBrowser.displayURL(this, "file:" + helpFile.getCanonicalPath() + "/help/contents.htm");
   }
   catch(Exception e)
   {
        stringHandler.displayErrorMsg(this, "Could not invoke browser");
   }
  }//GEN-LAST:event_helpContentsActionPerformed

  private void lookAndFeelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lookAndFeelActionPerformed
    try
    {
        if (metalRadioButtonMenuItem.isSelected())
        {
            currLookAndFeel = "Java";
            javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        }
        else if (unixRadioButtonMenuItem.isSelected())
        {
            currLookAndFeel = "Unix";
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        }
        else if (windowsRadioButtonMenuItem.isSelected())
        {
            String osName = System.getProperty("os.name");
            if ((osName != null) && (osName.indexOf("Windows") != -1))
            {
                currLookAndFeel = "Windows";
                javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
            else
            {
                resetLookAndFeel();
                stringHandler.displayErrorMsg(this, "This option is only available on a Windows platform");
            }
        }
        javax.swing.SwingUtilities.updateComponentTreeUI(this);
        upperMasterPanel.setLookAndFeel(currLookAndFeel);
        lowerMasterPanel.setLookAndFeel(currLookAndFeel);
    }
    catch (Exception e)
    {
        stringHandler.displayErrorMsg(this, e.getMessage());
    }

  }//GEN-LAST:event_lookAndFeelActionPerformed

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    jniCoordinateConversionService.destroy();
    dispose();
    System.exit(0);
  }//GEN-LAST:event_formWindowClosing

  /** Exit the Application in response to File menu - "exit" event.*/
  private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
    jniCoordinateConversionService.destroy();
    dispose();
    System.exit(0);
  }//GEN-LAST:event_exitActionPerformed

  /** Open a File for multi-point conversions. */
  private void openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openActionPerformed
    javax.swing.JFileChooser jChooser1 = new javax.swing.JFileChooser(currentDir.getDirectory());
    jChooser1.addChoosableFileFilter(new DATFileFilter());
    jChooser1.setSelectedFile(new java.io.File("*.dat"));
    int state = jChooser1.showOpenDialog(this);
    if(state == javax.swing.JFileChooser.APPROVE_OPTION)
    {
      java.io.File file = jChooser1.getSelectedFile();
      currentDir.setDirectory(file.getAbsolutePath());
      if(file != null)
      {
        JNIFiomeths jniFiomeths = null;
        try
        {
          jniFiomeths = new JNIFiomeths(file.getPath());

          FileDlg fileDlg = new FileDlg(jniCoordinateConversionService, this, true, currentDir, formatOptions, jniFiomeths, stringHandler);
//////////          fileDlg.convertCollection(false);
          fileDlg.show();
        }
        catch(Exception e)
        {
          stringHandler.displayErrorMsg(this, e.getMessage());
       ///   jniFiomeths.JNICloseInputFile();
        }
        finally
        {
          if(jniFiomeths != null)
            jniFiomeths.destroy();          
        }
      }
    }
  }//GEN-LAST:event_openActionPerformed

  /** Open "Format Options" dialog in response to Options menu - "Format" event.
  *   Specifies output precision and angle measure (Latitude/Longitude/Orientation)
  *   format in terms of units, range, sign and separator.*/
  private void formatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formatActionPerformed
    FormatOptionsDlg formatOptionsDlg = new FormatOptionsDlg(this, true, formatOptions);
    formatOptionsDlg.show();
  }//GEN-LAST:event_formatActionPerformed

  /** Converts Lower Projection Coordinates to Upper Projection Coordinates. */
  private void convertUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convertUpActionPerformed
    try
    {
        // Clear the top EPSG code if not a valid code
        if (lowerMasterPanel.isEpsgCodeActive() == false) {
            lowerMasterPanel.clearEpsgChoice();
        }
        
        
      if(checkParametersChanged(SourceOrTarget.TARGET, SourceOrTarget.SOURCE))      
      {
        CoordinateTuple sourceCoordinates = lowerMasterPanel.getCoordinates();
        Accuracy sourceAccuracy = lowerMasterPanel.getAccuracy();
        if (stringHandler.getError())
        {
            stringHandler.displayErrorMsg(this, SourceOrTarget.SOURCE, lowerMasterPanel.getProjectionType());
        }
        else
        {
            CoordinateTuple targetCoordinates = upperMasterPanel.initTargetCoordinates();
            Accuracy targetAccuracy = new Accuracy();

            // for UTM zone override calling convertSourceToTarget works over convertTargetToSource.
            ConvertResults convertResults = jniCoordinateConversionService.convertSourceToTarget(sourceCoordinates, sourceAccuracy, targetCoordinates, targetAccuracy);

            targetCoordinates = convertResults.getCoordinateTuple();
            targetAccuracy = convertResults.getAccuracy();

            // Display any warning messages
            String warningMessage = targetCoordinates.getWarningMessage();
            if (warningMessage.length() > 0)
                stringHandler.displayWarningMsg(this, warningMessage);

            upperMasterPanel.setCoordinates(targetCoordinates);

            upperMasterPanel.setAccuracy(targetAccuracy);
        }
      }
      ////convertTargetToSourceCollection();
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }
  }//GEN-LAST:event_convertUpActionPerformed

  /** Converts Upper Projection Coordinates to Lower Projection Coordinates. */
  private void convertDownActionPerformed(java.awt.event.ActionEvent evt) {
    try
    {
        // Clear the top EPSG code if not a valid code
        if (upperMasterPanel.isEpsgCodeActive() == false) {
            upperMasterPanel.clearEpsgChoice();
        }
        
        
      if(checkParametersChanged(SourceOrTarget.SOURCE, SourceOrTarget.TARGET))
      {
        CoordinateTuple sourceCoordinates = upperMasterPanel.getCoordinates();
        Accuracy sourceAccuracy = upperMasterPanel.getAccuracy();      

        if(stringHandler.getError())
        {
          stringHandler.displayErrorMsg(this, SourceOrTarget.SOURCE, 
              upperMasterPanel.getProjectionType());
        }
        else
        {
          CoordinateTuple targetCoordinates = lowerMasterPanel.initTargetCoordinates();
          Accuracy targetAccuracy = new Accuracy();
          
          ConvertResults convertResults = jniCoordinateConversionService.
              convertSourceToTarget(sourceCoordinates, sourceAccuracy, 
              targetCoordinates, targetAccuracy);
          
          targetCoordinates = convertResults.getCoordinateTuple();
          targetAccuracy = convertResults.getAccuracy();
          
          // Display any warning messages
          String warningMessage = targetCoordinates.getWarningMessage();
          if(warningMessage.length() > 0)
            stringHandler.displayWarningMsg(this, warningMessage);

          lowerMasterPanel.setCoordinates(targetCoordinates);

          lowerMasterPanel.setAccuracy(targetAccuracy);
        }
      }
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }
  }                                           

  private void deleteEllipsoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEllipsoidActionPerformed
    try
    {
      // The DeleteDlg class is used for both Delete Ellipsoid and Delete Datum commands.
      new DeleteDlg(jniCoordinateConversionService, this, true, ListType.ELLIPSOID).show();
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }  
  }//GEN-LAST:event_deleteEllipsoidActionPerformed

  private void createEllipsoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createEllipsoidActionPerformed
    try
    {
      new CreateEllipsoidDlg(jniCoordinateConversionService, this, true).show();
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }
  }//GEN-LAST:event_createEllipsoidActionPerformed

  private void deleteDatumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDatumActionPerformed
    try
    {
      // The DeleteDlg class is used for both Delete Ellipsoid and Delete Datum commands.
      DeleteDlg deleteDlg = new DeleteDlg(jniCoordinateConversionService, this, true, ListType.DATUM);
      deleteDlg.show();
      if(deleteDlg.getDeleted())
      {
        int indexOfDeletedDatum = deleteDlg.getIndex();
        // Update the index of the current datum in case it is the one being deleted
        // Then delete the datum from each panels list to prevent checkValidConversion
        // from using the index of the deleted datum
        upperMasterPanel.updateCurrentDatumIndex(indexOfDeletedDatum);
        lowerMasterPanel.updateCurrentDatumIndex(indexOfDeletedDatum);

        upperMasterPanel.deleteDatumFromList();
        lowerMasterPanel.deleteDatumFromList();
      }
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }
  }//GEN-LAST:event_deleteDatumActionPerformed

  private void createDatumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createDatumActionPerformed
    try
    {
      CreateDatumDlg createDatumDlg = new CreateDatumDlg(jniCoordinateConversionService, this, true);
      createDatumDlg.show();
      if(createDatumDlg.getDatumCreated())
      {
        upperMasterPanel.addDatumToList();
        lowerMasterPanel.addDatumToList();
      }
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }    
  }//GEN-LAST:event_createDatumActionPerformed

  private void editButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editButtonMouseEntered
    prevFocus = getFocusOwner();
  }//GEN-LAST:event_editButtonMouseEntered

  private void pasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteActionPerformed
    java.awt.Component focus = getFocusOwner();
    if (focus instanceof javax.swing.text.JTextComponent)
        ((javax.swing.text.JTextComponent)focus).paste();
    else //else if ((focus == pasteButton) || (focus == pasteMenuItem))
        if (prevFocus instanceof javax.swing.text.JTextComponent)
            ((javax.swing.text.JTextComponent)prevFocus).paste();
  }//GEN-LAST:event_pasteActionPerformed

  private void cutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutActionPerformed
    java.awt.Component focus = getFocusOwner();
    if (focus instanceof javax.swing.text.JTextComponent)
        ((javax.swing.text.JTextComponent)focus).cut();
    else //else if ((focus == cutButton) || (focus == cutMenuItem))
        if (prevFocus instanceof javax.swing.text.JTextComponent)
            ((javax.swing.text.JTextComponent)prevFocus).cut();
  }//GEN-LAST:event_cutActionPerformed

  private void copyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyActionPerformed
    java.awt.Component focus = getFocusOwner();
    if (focus instanceof javax.swing.text.JTextComponent)
        ((javax.swing.text.JTextComponent)focus).copy();
    else // if ((focus == copyButton) || (focus == copyMenuItem))
        if (prevFocus instanceof javax.swing.text.JTextComponent)
            ((javax.swing.text.JTextComponent)prevFocus).copy();
  }//GEN-LAST:event_copyActionPerformed


  /**
  * @param args the command line arguments
  */
  public static void main (String args[]) {
 //   new MSP_GEOTRANS3().show ();
 //   return;
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() 
      {
      MSP_GEOTRANS3 geoTrans =  new MSP_GEOTRANS3();
          geoTrans.setVisible(true);
      }
    });
  }

  // Test if a 3D conversion is possible
  // If it is, enable the geodetic height fields
  // If it is not, set the geodetic height fields to read only
  public void check3DConversion()
  {
  
      boolean _3dConversion = false;
      if (upperMasterPanel.getProjectionType() == CoordinateType.GEODETIC)
      {
        if ((lowerMasterPanel.getProjectionType() == CoordinateType.GEODETIC) || 
            (lowerMasterPanel.getProjectionType() == CoordinateType.GEOCENTRIC) || 
            (lowerMasterPanel.getProjectionType() == CoordinateType.LOCCART) || 
            (lowerMasterPanel.getProjectionType() == CoordinateType.LOCSPHER) ||
            (lowerMasterPanel.getProjectionType() == CoordinateType.SPHERICAL))
        {
            upperMasterPanel.enableHeightComboBox(true);
            upperMasterPanel.selectEllipsoidHeightButton();
           _3dConversion = true;
            upperMasterPanel.setHeightFieldEditable(true);

            if (lowerMasterPanel.getProjectionType() == CoordinateType.GEODETIC)
            {
                lowerMasterPanel.enableHeightComboBox(true);
                lowerMasterPanel.selectEllipsoidHeightButton();
                lowerMasterPanel.setHeightFieldEditable(true);
            }
        }
        else
        {
            upperMasterPanel.selectNoHeightButton();
            upperMasterPanel.setHeightText("0");
            upperMasterPanel.setHeightFieldEditable(false);
            upperMasterPanel.enableHeightComboBox(false);
        }
      }
      else if (lowerMasterPanel.getProjectionType() == CoordinateType.GEODETIC)
      {

        if ( (upperMasterPanel.getProjectionType() == CoordinateType.GEODETIC) || 
             (upperMasterPanel.getProjectionType() == CoordinateType.GEOCENTRIC) || 
             (upperMasterPanel.getProjectionType() == CoordinateType.LOCCART) || 
             (upperMasterPanel.getProjectionType() == CoordinateType.LOCSPHER) ||
             (upperMasterPanel.getProjectionType() == CoordinateType.SPHERICAL))
        {
            lowerMasterPanel.enableHeightComboBox(true);
            lowerMasterPanel.selectEllipsoidHeightButton();
            _3dConversion = true;
            lowerMasterPanel.setHeightFieldEditable(true);
        }
        else
        {
            lowerMasterPanel.selectNoHeightButton();
            lowerMasterPanel.setHeightText("0");
            lowerMasterPanel.setHeightFieldEditable(false);
            lowerMasterPanel.enableHeightComboBox(false);
        }
      }
      else if (((upperMasterPanel.getProjectionType() == CoordinateType.GEOCENTRIC) ||
                (upperMasterPanel.getProjectionType() == CoordinateType.LOCCART) ||
                (upperMasterPanel.getProjectionType() == CoordinateType.LOCSPHER) ||
                (upperMasterPanel.getProjectionType() == CoordinateType.SPHERICAL)) &&
               ((lowerMasterPanel.getProjectionType() == CoordinateType.GEOCENTRIC) || 
                (lowerMasterPanel.getProjectionType() == CoordinateType.LOCCART) ||
                (lowerMasterPanel.getProjectionType() == CoordinateType.LOCSPHER) ||
                (lowerMasterPanel.getProjectionType() == CoordinateType.SPHERICAL)))
      {
        _3dConversion = true;
      }

      upperMasterPanel.updateSrcErrors(_3dConversion);
      lowerMasterPanel.updateSrcErrors(_3dConversion);
  }

  // Color code the convert buttons based on the current input/output datum & coordinate system combination
  public void checkValidConversion()
  {
    try
    {
      JNIDatumLibrary jniDatumLibrary = new JNIDatumLibrary(jniCoordinateConversionService.getDatumLibrary());
      long sourceDatumIndex = upperMasterPanel.getDatumIndex();
      long targetDatumIndex = lowerMasterPanel.getDatumIndex();
      
      String sourceEllipsoidCode = jniDatumLibrary.getDatumInfo(sourceDatumIndex).getDatumEllipsoidCode();
      String targetEllipsoidCode = jniDatumLibrary.getDatumInfo(targetDatumIndex).getDatumEllipsoidCode();
      
      int sourceCoordinateSystem = upperMasterPanel.getProjectionType();
      int targetCoordinateSystem = lowerMasterPanel.getProjectionType();
      
      int validColorUp   = Red.ID;
      int validColorDown = Red.ID;
      
      // If British National Grid is chosen, ellipsoid should be Airy
      // If Web Mercator is chosen, ellipsoid should be WGS84
      // If New Zealand Map Grid is chosen, ellipsoid should be International
      if((sourceCoordinateSystem == CoordinateType.BNG  && !sourceEllipsoidCode.equalsIgnoreCase("AA")) ||
         (targetCoordinateSystem == CoordinateType.BNG  && !targetEllipsoidCode.equalsIgnoreCase("AA")) ||
         (sourceCoordinateSystem == CoordinateType.WEBMERCATOR  && !sourceEllipsoidCode.equalsIgnoreCase("WE")) ||
         (targetCoordinateSystem == CoordinateType.WEBMERCATOR  && !targetEllipsoidCode.equalsIgnoreCase("WE")) ||
         (sourceCoordinateSystem == CoordinateType.WEBMERCATOR  && targetCoordinateSystem != CoordinateType.GEODETIC ) ||
         (targetCoordinateSystem == CoordinateType.WEBMERCATOR  && sourceCoordinateSystem != CoordinateType.GEODETIC ) ||
         (sourceCoordinateSystem == CoordinateType.NZMG && !sourceEllipsoidCode.equalsIgnoreCase("IN")) ||
         (targetCoordinateSystem == CoordinateType.NZMG && !targetEllipsoidCode.equalsIgnoreCase("IN")))
      {
        validColorUp   = Red.ID;
        validColorDown = Red.ID;
      }
      else if(sourceCoordinateSystem == CoordinateType.WEBMERCATOR  && targetCoordinateSystem == CoordinateType.GEODETIC )
      {
        validColorUp   = Red.ID;
        validColorDown = Green.ID;
      }
      else if(targetCoordinateSystem == CoordinateType.WEBMERCATOR  && sourceCoordinateSystem == CoordinateType.GEODETIC )
      {
        validColorUp   = Green.ID;
        validColorDown = Red.ID;
      }
      else
      {
        AOI sourceAOI = jniDatumLibrary.getDatumValidRectangle(sourceDatumIndex);
        AOI targetAOI = jniDatumLibrary.getDatumValidRectangle(targetDatumIndex);

        // Bounding rectangles don't overlap
        if((sourceAOI.getWestLongitude() >= targetAOI.getEastLongitude()) ||
           (sourceAOI.getEastLongitude() <= targetAOI.getWestLongitude()) ||
           (sourceAOI.getSouthLatitude() >= targetAOI.getNorthLatitude()) ||
           (sourceAOI.getNorthLatitude() <= targetAOI.getSouthLatitude()))
        {
          validColorUp   = Yellow.ID;
          validColorDown = Yellow.ID;
        }
        else
        {
          validColorUp   = Green.ID;
          validColorDown = Green.ID;
        }
      }
      
      if(validColorUp == Red.ID)  // Errors
        currentColorUp = Red.VALUE;
      else if(validColorUp == Yellow.ID)  // Warnings
        currentColorUp = Yellow.VALUE;
      else if(validColorUp == Green.ID)  // No errors or warnings
        currentColorUp = Green.VALUE;

      if(validColorDown == Red.ID)  // Errors
        currentColorDown = Red.VALUE;
      else if(validColorDown == Yellow.ID)  // Warnings
        currentColorDown = Yellow.VALUE;
      else if(validColorDown == Green.ID)  // No errors or warnings
        currentColorDown = Green.VALUE;

      convertUpButton.setBackground(currentColorUp);
      convertDownButton.setBackground(currentColorDown);
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, "checkValidConversion: " + e.getMessage());
    }
  }

  // Creates projection panels, coordinate & parameter objects,
  // and initializes CCS via JNI functions.
  private void initialize() {
    boolean error = false;

    try
    {
      if(defaultSettings != null)
      {
        formatOptions = defaultSettings.getDefaultFormatOptions();

        currentDatum[SourceOrTarget.SOURCE] = defaultSettings.getDefaultSourceDatumCode();
        currentDatum[SourceOrTarget.TARGET] = defaultSettings.getDefaultTargetDatumCode();
        currentParameters[SourceOrTarget.SOURCE] = defaultSettings.getDefaultSourceParameters();
        currentParameters[SourceOrTarget.TARGET] = defaultSettings.getDefaultTargetParameters();
      }
      else
      {
        formatOptions = new FormatOptions();

        currentDatum[SourceOrTarget.SOURCE] = "WGE";
        currentDatum[SourceOrTarget.TARGET] = "WGE";
        currentParameters[SourceOrTarget.SOURCE] = new GeodeticParameters(CoordinateType.GEODETIC, HeightType.NO_HEIGHT);
        currentParameters[SourceOrTarget.TARGET] = new UTMParameters(CoordinateType.UTM, 0, 0);
      }
      
      loadEpsgData();

      stringHandler = new StringHandler(formatOptions.getStringToVal());

  ///    if(jniCoordinateConversionService != null)
  ///      jniCoordinateConversionService.destroy();
      jniCoordinateConversionService = new JNICoordinateConversionService(currentDatum[SourceOrTarget.SOURCE], currentParameters[SourceOrTarget.SOURCE], currentDatum[SourceOrTarget.TARGET], currentParameters[SourceOrTarget.TARGET]);

      upperMasterPanel = new MasterPanel(jniCoordinateConversionService, ConversionState.INTERACTIVE, SourceOrTarget.SOURCE, formatOptions, stringHandler, epsgData);
      upperMasterPanel.setDefaults(SourceOrTarget.SOURCE, jniCoordinateConversionService, formatOptions, stringHandler);

      if(upperMasterPanel == null)
      {
        stringHandler.displayErrorMsg(this, "Unable to initialize GEOTRANS");
        System.exit(0);
      }

      lowerMasterPanel = new MasterPanel(jniCoordinateConversionService, ConversionState.INTERACTIVE, SourceOrTarget.TARGET, formatOptions, stringHandler, epsgData);
      lowerMasterPanel.setDefaults(SourceOrTarget.TARGET, jniCoordinateConversionService, formatOptions, stringHandler);

      if(lowerMasterPanel == null)
      {
        stringHandler.displayErrorMsg(this, "Unable to initialize GEOTRANS");
        System.exit(0);
      }

      upperMasterPanel.setParent(this);
      lowerMasterPanel.setParent(this);

      check3DConversion();
      checkValidConversion();

      upperPanel.setLayout(new java.awt.GridBagLayout());

      java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      upperPanel.add(upperMasterPanel, gridBagConstraints);
      gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
      lowerPanel.add(lowerMasterPanel, gridBagConstraints);
    }
    catch(Exception e)
    {
      stringHandler.displayErrorMsg(this, "Initialization error: \n\n" + e.getMessage());
      error = true;
    }
    catch(java.lang.UnsatisfiedLinkError e)
    {
      stringHandler.displayErrorMsg(this, "Unsatisfied Link Error: " + e.getMessage());
      error = true;
    }

    if(error)
    {
      dispose();
      System.exit(0);
    }
  }

  private void resetLookAndFeel()
  {
      if (currLookAndFeel.equals("Java"))
        metalRadioButtonMenuItem.setSelected(true);
      else if (currLookAndFeel.equals("Unix"))
        unixRadioButtonMenuItem.setSelected(true);
      else if (currLookAndFeel.equals("Windows"))
        windowsRadioButtonMenuItem.setSelected(true);
  }
  
  // Check if any of the parameters have changed
  // If they have, create a new Coordinate Conversion Service
  private boolean checkParametersChanged(int sourceDirection, int targetDirection) throws CoordinateConversionException
  {
    try
    {
       String sourceDatum;
       String targetDatum;
       CoordinateSystemParameters sourceParameters;
       CoordinateSystemParameters targetParameters;

       // Need to set the source and target correctly.  They depend upon if you're converting
       // from lower to upper (up) or upper to lower (down)
       if ( sourceDirection == SourceOrTarget.SOURCE )
       {
      // direction is upper to lower
          sourceDatum = upperMasterPanel.getDatumCode();
          targetDatum = lowerMasterPanel.getDatumCode();
          sourceParameters = upperMasterPanel.getParameters();
          if(stringHandler.getError())
             stringHandler.displayErrorMsg(this, sourceDirection, upperMasterPanel.getProjectionType());
          targetParameters = lowerMasterPanel.getParameters();
          if(stringHandler.getError())
          {
             stringHandler.displayErrorMsg(this, targetDirection, lowerMasterPanel.getProjectionType());
             return false;
          }
       }
       else
       {
      // direction is lower to upper
          sourceDatum = lowerMasterPanel.getDatumCode();
          targetDatum = upperMasterPanel.getDatumCode();
          sourceParameters = lowerMasterPanel.getParameters();
          if(stringHandler.getError())
             stringHandler.displayErrorMsg(this, sourceDirection, lowerMasterPanel.getProjectionType());
          targetParameters = upperMasterPanel.getParameters();
          if(stringHandler.getError())
          {
             stringHandler.displayErrorMsg(this, targetDirection, upperMasterPanel.getProjectionType());
             return false;
          }
       }


        boolean parametersChanged = false;

        if(!currentDatum[SourceOrTarget.SOURCE].equalsIgnoreCase(sourceDatum) || !currentDatum[SourceOrTarget.TARGET].equalsIgnoreCase(targetDatum) ||
                currentParameters[SourceOrTarget.SOURCE].getCoordinateType() != sourceParameters.getCoordinateType() || currentParameters[SourceOrTarget.TARGET].getCoordinateType() != targetParameters.getCoordinateType())
          parametersChanged = true;    
        else // Same datum & coordinate system, check if parameters have changed
        {
          if(coordinateSystemParametersChanged(currentParameters[SourceOrTarget.SOURCE], sourceParameters))
            parametersChanged = true;
          else
          {
            if(coordinateSystemParametersChanged(currentParameters[SourceOrTarget.TARGET], targetParameters))
              parametersChanged = true;
          }
        }

        if(parametersChanged)
        {
          
          JNICoordinateConversionService tempJNICoordinateConversionService = new JNICoordinateConversionService(sourceDatum, sourceParameters, targetDatum, targetParameters);
          if(jniCoordinateConversionService != null)
            jniCoordinateConversionService.destroy();
          jniCoordinateConversionService = tempJNICoordinateConversionService;

          currentDatum[SourceOrTarget.SOURCE] = sourceDatum;
          currentDatum[SourceOrTarget.TARGET] = targetDatum;
          currentParameters[SourceOrTarget.SOURCE] = sourceParameters;
          currentParameters[SourceOrTarget.TARGET] = targetParameters;

          upperMasterPanel.setCoordinateConversionService(jniCoordinateConversionService);
          lowerMasterPanel.setCoordinateConversionService(jniCoordinateConversionService);
        }
        
        return true;
    }
    catch(Exception e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
      return false;
    }
  }
  
  
  private boolean coordinateSystemParametersChanged(CoordinateSystemParameters currentParameters, CoordinateSystemParameters parameters) throws CoordinateConversionException
  {
    int coordinateType = parameters.getCoordinateType();
    
    switch(coordinateType)
    {
      case CoordinateType.BNG:
      case CoordinateType.GARS:
      case CoordinateType.GEOCENTRIC:
      case CoordinateType.GEOREF:
      case CoordinateType.F16GRS:
      case CoordinateType.MGRS:
      case CoordinateType.NZMG:
      case CoordinateType.UPS:
      case CoordinateType.USNG:
      case CoordinateType.WEBMERCATOR:
      case CoordinateType.SPHERICAL:
      {
        if(currentParameters.equal(parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.GEODETIC:
      {
        if(((GeodeticParameters)currentParameters).equal((GeodeticParameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.ECKERT4:
      case CoordinateType.ECKERT6:
      case CoordinateType.MILLER:
      case CoordinateType.MOLLWEIDE:
      case CoordinateType.SINUSOIDAL:
      case CoordinateType.GRINTEN:
      {
        if(((MapProjection3Parameters) currentParameters).equal((MapProjection3Parameters) parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.AZIMUTHAL:
      case CoordinateType.BONNE:
      case CoordinateType.CASSINI:
      case CoordinateType.CYLEQA:
      case CoordinateType.GNOMONIC:
      case CoordinateType.ORTHOGRAPHIC:
      case CoordinateType.POLYCONIC:
      case CoordinateType.STEREOGRAPHIC:
      {
        if(((MapProjection4Parameters) currentParameters).equal((MapProjection4Parameters) parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.LAMBERT_1:
      case CoordinateType.TRCYLEQA:
      case CoordinateType.TRANMERC:
      {
        if(((MapProjection5Parameters)currentParameters).equal((MapProjection5Parameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.ALBERS:
      case CoordinateType.LAMBERT_2:
      {
        if(((MapProjection6Parameters)currentParameters).equal((MapProjection6Parameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.EQDCYL:
      {
        if(((EquidistantCylindricalParameters)currentParameters).equal((EquidistantCylindricalParameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.LOCCART:
      case CoordinateType.LOCSPHER:
      {
        if(((LocalCartesianParameters)currentParameters).equal((LocalCartesianParameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.MERCATOR_SP:
      {
        if(((MercatorStandardParallelParameters)currentParameters).equal((MercatorStandardParallelParameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.MERCATOR_SF:
      {
        if(((MercatorScaleFactorParameters)currentParameters).equal((MercatorScaleFactorParameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.NEYS:
      {
        if(((NeysParameters)currentParameters).equal((NeysParameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.OMERC:
      {
        if(((ObliqueMercatorParameters)currentParameters).equal((ObliqueMercatorParameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.POLARSTEREO_SP:
      {
        if(((PolarStereographicStandardParallelParameters)currentParameters).equal((PolarStereographicStandardParallelParameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.POLARSTEREO_SF:
      {
        if(((PolarStereographicScaleFactorParameters)currentParameters).equal((PolarStereographicScaleFactorParameters)parameters))
          return false;
        else
          return true;
      }
      case CoordinateType.UTM:
      {
        if(((UTMParameters)currentParameters).equal((UTMParameters)parameters))
          return false;
        else
          return true;
      }
      default:
        throw new CoordinateConversionException("Invalid coordinate type");
    }
  }

  private void updateDefaultSettings()
  {
    try
    {
      formatOptions = defaultSettings.getDefaultFormatOptions();

      currentDatum[SourceOrTarget.SOURCE] = defaultSettings.getDefaultSourceDatumCode();
      currentDatum[SourceOrTarget.TARGET] = defaultSettings.getDefaultTargetDatumCode();
      currentParameters[SourceOrTarget.SOURCE] = defaultSettings.getDefaultSourceParameters();
      currentParameters[SourceOrTarget.TARGET] = defaultSettings.getDefaultTargetParameters();

      stringHandler = new StringHandler(formatOptions.getStringToVal());

      JNICoordinateConversionService tempJNICoordinateConversionService = new JNICoordinateConversionService(currentDatum[SourceOrTarget.SOURCE], currentParameters[SourceOrTarget.SOURCE], currentDatum[SourceOrTarget.TARGET], currentParameters[SourceOrTarget.TARGET]);
      if(jniCoordinateConversionService != null)
        jniCoordinateConversionService.destroy();
      jniCoordinateConversionService = tempJNICoordinateConversionService;

      upperMasterPanel.setDefaults(SourceOrTarget.SOURCE, jniCoordinateConversionService, formatOptions, stringHandler);
      lowerMasterPanel.setDefaults(SourceOrTarget.TARGET, jniCoordinateConversionService, formatOptions, stringHandler);
    }
    catch(Exception e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
      defaultSettings = null;
    }
  }

  void setIcons()
  {
    cutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Cut16.gif"))); // NOI18N
    copyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Copy16.gif"))); // NOI18N
    pasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Paste16.gif"))); // NOI18N
    formatButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Preferences16.gif"))); // NOI18N
    openMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Open16.gif"))); // NOI18N
    createFileHeaderMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Create16.gif"))); // NOI18N
    loadSettingsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Open16.gif"))); // NOI18N
    saveSettingsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Save16.gif"))); // NOI18N
    exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Stop16.gif"))); // NOI18N
    cutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Cut16.gif"))); // NOI18N
    copyMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Copy16.gif"))); // NOI18N
    pasteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Paste16.gif"))); // NOI18N
    formatMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Preferences16.gif"))); // NOI18N
    convertDownMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Down16.gif"))); // NOI18N
    convertUpMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geotrans3/gui/icons/Up16.gif"))); // NOI18N

  }
  
    /** 
    *   This method stores the EPSG data stored in a csv file
    *   in the data folder into epsgData
    */
    private void loadEpsgData() {
        String epsgCodeFilePath;
        
        try {
            final java.util.Properties p = ReadEnv.getEnvVars();
            epsgCodeFilePath = p.getProperty(GEOTRANS_DATA_DIR_ENV_VAR) + "/" + EPSG_CSV_FILE;
        } catch (final Throwable e) {
            System.err.println("ERROR: Failed getting EPSG Code file path: " + e.getMessage());
            stringHandler.displayErrorMsg(this, "ERROR: Failed getting EPSG Code file path: " + e.getMessage());
            return;
        }
        
        final List<String> warnings = new ArrayList<>();
        
        try {
            epsgData = EpsgReader.readFile(epsgCodeFilePath, warnings);
        } catch (final Throwable e) {
            System.err.println("ERROR: Failed reading EPSG codes CSV file '" + epsgCodeFilePath + "': " + e.getMessage());
            stringHandler.displayErrorMsg(this, "ERROR: Failed reading EPSG codes CSV file '" + epsgCodeFilePath + "': " + e.getMessage());
            e.printStackTrace();
        }
        
        for (final String warning : warnings) {
            System.err.println("EPSG CSV warning: " + warning);
        }
    }
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenuItem aboutMenuItem;
  private javax.swing.JMenuItem contentsMenuItem;
  private javax.swing.JButton convertDownButton;
  private javax.swing.JMenuItem convertDownMenuItem;
  private javax.swing.JMenu convertMenu;
  private javax.swing.JSeparator convertMenuSeparator;
  private javax.swing.JPanel convertPanel;
  private javax.swing.JButton convertUpButton;
  private javax.swing.JMenuItem convertUpMenuItem;
  private javax.swing.JButton copyButton;
  private javax.swing.JMenuItem copyMenuItem;
  private javax.swing.JMenuItem createDatumMenuItem;
  private javax.swing.JMenuItem createEllipsoidMenuItem;
  private javax.swing.JMenuItem createFileHeaderMenuItem;
  private javax.swing.JButton cutButton;
  private javax.swing.JMenuItem cutMenuItem;
  private javax.swing.JMenu datumMenu;
  private javax.swing.JMenuItem deleteDatumMenuItem;
  private javax.swing.JMenuItem deleteEllipsoidMenuItem;
  private javax.swing.JMenu editMenu;
  private javax.swing.JMenu ellipsoidMenu;
  private javax.swing.JMenuItem exitMenuItem;
  private javax.swing.JMenu fileMenu;
  private javax.swing.JSeparator fileMenuSeparator;
  private javax.swing.JButton formatButton;
  private javax.swing.JMenuItem formatMenuItem;
  private javax.swing.JMenu helpMenu;
  private javax.swing.JSeparator helpMenuSeparator;
  private javax.swing.JMenuItem loadSettingsMenuItem;
  private javax.swing.JMenu lookAndFeelMenu;
  private javax.swing.JPanel lowerPanel;
  private javax.swing.JMenuBar menuBar;
  private javax.swing.JRadioButtonMenuItem metalRadioButtonMenuItem;
  private javax.swing.JMenuItem openMenuItem;
  private javax.swing.JMenu optionsMenu;
  private javax.swing.JButton pasteButton;
  private javax.swing.JMenuItem pasteMenuItem;
  private javax.swing.JMenuItem saveSettingsMenuItem;
  private javax.swing.JToolBar toolBar;
  private javax.swing.JSeparator toolBarSeparator;
  private javax.swing.JRadioButtonMenuItem unixRadioButtonMenuItem;
  private javax.swing.JPanel upperPanel;
  private javax.swing.JRadioButtonMenuItem windowsRadioButtonMenuItem;
  // End of variables declaration//GEN-END:variables

} 

// CLASSIFICATION: UNCLASSIFIED
