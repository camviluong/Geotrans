// CLASSIFICATION: UNCLASSIFIED

/******************************************************************************
* Filename: MasterPanel.java
*
* Copyright BAE Systems Inc. 2012 ALL RIGHTS RESERVED
*
* MODIFICATION HISTORY
*
* DATE      NAME        DR#          DESCRIPTION
*
* 05/12/10  S Gillis    BAEts26542   MSP TS MSL-HAE conversion
*                                    should use CCS
* 01/10/11  J Chelos    BAEts26267   Added EGM2008
* 01/04/12  K Lam       BAEts29500   Remove recommend from EGM 96
* 01/24/12  S Gillis    BAEts29500   Reorder EGM2008
* 07/18/12  S. Gillis   MSP_00029550 Updated exception handling
* 01/12/16  K Chen      MSP_00030518 Add US Survey Feet Support
* 04/06/20  E. Poon     GTR-53       Add support of EPSG codes
*****************************************************************************/

package geotrans3.gui;

import geotrans3.coordinates.Accuracy;
import geotrans3.coordinates.CoordinateTuple;
import geotrans3.enumerations.*;
import geotrans3.exception.CoordinateConversionException;
import geotrans3.jni.*;
import geotrans3.jni.FillList;
import geotrans3.misc.FormatOptions;
import geotrans3.misc.Info;
import geotrans3.misc.Searchable;
import geotrans3.misc.StringSearchable;
import geotrans3.misc.StringHandler;
import geotrans3.parameters.*;
import geotrans3.utility.Constants;
import geotrans3.utility.Platform;
import geotrans3.utility.ReadEnv;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.SwingConstants;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.text.BadLocationException;

import java.awt.Component;
import javax.swing.text.JTextComponent;


public class MasterPanel extends javax.swing.JPanel {
  private MSP_GEOTRANS3 parent;
  private JNIDatumLibrary jniDatumLibrary;
  private JNIEllipsoidLibrary jniEllipsoidLibrary;
  private JNICoordinateConversionService jniCoordinateConversionService;
  private FillList datumList;
  private StringHandler stringHandler;
  private int projectionType;
  private long datumIndex;
  private int direction;
  private int state;
  private CoordPanel coordPanel;
  private int heightType;
  private long override;
  private FormatOptions formatOptions;
  private boolean createHeader;
  private boolean useNSEW;
  private boolean useMinutes;
  private boolean useSeconds;
  private String currLookAndFeel;
  private CoordinateSystemParameters coordinateSystemParameters;
  private String datumCode;
  private int inputFileType;
  private Map<String, Map<String, String>> epsgCodesAllData;
  private Map<String, String> epsgCodeData = new HashMap<>();
  private String currentEpsgCode;
  private boolean datumChanged;
  private boolean projectionChanged;
  private boolean heightChanged;
  private static final String EPSG_LABEL_TEXT = "EPSG CRS Code (Optional):";


  /** Creates new form MasterPanel */
  public MasterPanel(
    final JNICoordinateConversionService _jniCoordinateConversionService,
    final int stat,
    final int dir,
    final FormatOptions _formatOptions,
    final StringHandler _stringHandler,
    final Map<String, Map<String, String>> epsgData) {

    try {
      jniCoordinateConversionService = _jniCoordinateConversionService;
      jniDatumLibrary = new JNIDatumLibrary(_jniCoordinateConversionService.getDatumLibrary());
      jniEllipsoidLibrary = new JNIEllipsoidLibrary(_jniCoordinateConversionService.getEllipsoidLibrary());
      stringHandler = _stringHandler;
      projectionType = CoordinateType.GEODETIC;
      inputFileType = projectionType;
      datumIndex = 0;
      currLookAndFeel = "Java";
      epsgCodesAllData = epsgData;

      if(stat == ConversionState.CREATE_HEADER)
      {
        createHeader = true;
        state = ConversionState.FILE;    // Set state to file for ccs calls

      }
      else
      {
        createHeader = false;
        state = stat;
      }

      direction = dir;
      formatOptions = _formatOptions;

      if(state == ConversionState.FILE)
        init();
      else
      {
        coordPanel = new CoordPanel(_formatOptions, stringHandler);
        init();
        java.awt.GridBagConstraints gridBagConstraints7 = new java.awt.GridBagConstraints();
        gridBagConstraints7.gridx = 0;
        gridBagConstraints7.gridy = 6;
        coordPanel.setPreferredSize(new java.awt.Dimension(469, 78));
        coordPanel.setMinimumSize(new java.awt.Dimension(469, 78));
        add(coordPanel, gridBagConstraints7);
      }
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }
  }

  public MasterPanel(JNICoordinateConversionService _jniCoordinateConversionService, int dir, FormatOptions _formatOptions, StringHandler _stringHandler, int _inputFileType)
  {
    try
    {
      jniCoordinateConversionService = _jniCoordinateConversionService;
      jniDatumLibrary = new JNIDatumLibrary(_jniCoordinateConversionService.getDatumLibrary());
      jniEllipsoidLibrary = new JNIEllipsoidLibrary(_jniCoordinateConversionService.getEllipsoidLibrary());
      stringHandler = _stringHandler;
      projectionType = CoordinateType.GEODETIC;
      inputFileType = _inputFileType;
      datumIndex = 0;
      currLookAndFeel = "Java";

      state = ConversionState.FILE;
      createHeader = false;

      direction = dir;
      formatOptions = _formatOptions;

      init();
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }
  }

  public MasterPanel(JNICoordinateConversionService _jniCoordinateConversionService, String _datumCode, CoordinateSystemParameters _coordinateSystemParameters, int stat, int dir, FormatOptions _formatOptions, StringHandler _stringHandler) throws CoordinateConversionException
  {
  /*  try
    {*/
      datumCode = _datumCode;
      coordinateSystemParameters = _coordinateSystemParameters;
      jniCoordinateConversionService = _jniCoordinateConversionService;
      jniDatumLibrary = new JNIDatumLibrary(_jniCoordinateConversionService.getDatumLibrary());
      jniEllipsoidLibrary = new JNIEllipsoidLibrary(
          _jniCoordinateConversionService.getEllipsoidLibrary());
      stringHandler = _stringHandler;
      projectionType = _coordinateSystemParameters.getCoordinateType();
      inputFileType = projectionType;
      datumIndex = 0;
      currLookAndFeel = "Java";
      createHeader = false;
      state = stat;
      direction = dir;
      formatOptions = _formatOptions;

      init();
  /*  }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }*/
  }


    private void init() throws CoordinateConversionException
    {
        initComponents();

        setFormat();
        if ((direction == SourceOrTarget.SOURCE) && (state == ConversionState.FILE))
            initFileInputPanel();
        else
            initMasterPanel();

        if (Platform.isJavaV1_3)
        {
            datumLabel.setForeground(java.awt.Color.black);
            ellipsoidLabel.setForeground(java.awt.Color.black);
            inputProjectionLabel.setForeground(java.awt.Color.black);
            centralMeridianNeysParamsLabel.setForeground(java.awt.Color.black);
            originLatitudeNeysParamsLabel.setForeground(java.awt.Color.black);
            stdParallel2NeysParamsLabel.setForeground(java.awt.Color.black);
            heightLabel.setForeground(java.awt.Color.black);
            hemiBoxPanel.setForeground(java.awt.Color.black);
            _3ParamFieldsRow1LabelA.setForeground(java.awt.Color.black);
            _3ParamFieldsRow1LabelB.setForeground(java.awt.Color.black);
            _3ParamFieldsRow1LabelC.setForeground(java.awt.Color.black);
            _3ParamFieldsRow2LabelA.setForeground(java.awt.Color.black);
            _3ParamFieldsRow2LabelB.setForeground(java.awt.Color.black);
            _3ParamFieldsRow2LabelC.setForeground(java.awt.Color.black);
            _4ParamFieldsRow1LabelA.setForeground(java.awt.Color.black);
            _4ParamFieldsRow1LabelB.setForeground(java.awt.Color.black);
            _4ParamFieldsRow1LabelC.setForeground(java.awt.Color.black);
            _4ParamFieldsRow1LabelD.setForeground(java.awt.Color.black);
            _4ParamFieldsRow2LabelA.setForeground(java.awt.Color.black);
            _4ParamFieldsRow2LabelB.setForeground(java.awt.Color.black);
            _4ParamFieldsRow2LabelC.setForeground(java.awt.Color.black);
            _4ParamFieldsRow2LabelD.setForeground(java.awt.Color.black);
            zoneLabel.setForeground(java.awt.Color.black);
            zoneRangeLabel.setForeground(java.awt.Color.black);
        }
        if (Platform.isUnix)
        {
            datumLabel.setFont(new java.awt.Font("Dialog", 1, 10));
            ellipsoidLabel.setFont(new java.awt.Font("Dialog", 1, 10));
            inputProjectionLabel.setFont(new java.awt.Font("Dialog", 1, 10));
            centralMeridianNeysParamsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
            originLatitudeNeysParamsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
            stdParallel2NeysParamsLabel.setFont(new java.awt.Font("Dialog", 1, 10));
            heightLabel.setFont(new java.awt.Font("Dialog", 1, 10));
            hemiBoxPanel.setFont(new java.awt.Font("Dialog", 1, 10));
            _3ParamFieldsRow1LabelA.setFont(new java.awt.Font("Dialog", 1, 10));
            _3ParamFieldsRow1LabelB.setFont(new java.awt.Font("Dialog", 1, 10));
            _3ParamFieldsRow1LabelC.setFont(new java.awt.Font("Dialog", 1, 10));
            _3ParamFieldsRow2LabelA.setFont(new java.awt.Font("Dialog", 1, 10));
            _3ParamFieldsRow2LabelB.setFont(new java.awt.Font("Dialog", 1, 10));
            _3ParamFieldsRow2LabelC.setFont(new java.awt.Font("Dialog", 1, 10));
            _4ParamFieldsRow1LabelA.setFont(new java.awt.Font("Dialog", 1, 10));
            _4ParamFieldsRow1LabelB.setFont(new java.awt.Font("Dialog", 1, 10));
            _4ParamFieldsRow1LabelC.setFont(new java.awt.Font("Dialog", 1, 10));
            _4ParamFieldsRow1LabelD.setFont(new java.awt.Font("Dialog", 1, 10));
            _4ParamFieldsRow2LabelA.setFont(new java.awt.Font("Dialog", 1, 10));
            _4ParamFieldsRow2LabelB.setFont(new java.awt.Font("Dialog", 1, 10));
            _4ParamFieldsRow2LabelC.setFont(new java.awt.Font("Dialog", 1, 10));
            _4ParamFieldsRow2LabelD.setFont(new java.awt.Font("Dialog", 1, 10));
            zoneLabel.setFont(new java.awt.Font("Dialog", 1, 10));
            zoneRangeLabel.setFont(new java.awt.Font("Dialog", 1, 10));
            zoneRadioButton.setFont(new java.awt.Font("Dialog", 1, 10));
            nHemiRadioButton.setFont(new java.awt.Font("Dialog", 1, 10));
            sHemiRadioButton.setFont(new java.awt.Font("Dialog", 1, 10));
            neys71RadioButton.setFont(new java.awt.Font("Dialog", 1, 10));
            neys74RadioButton.setFont(new java.awt.Font("Dialog", 1, 10));
            datumComboBox.setFont(new java.awt.Font("Dialog", 1, 10));
            projectionComboBox.setFont(new java.awt.Font("Dialog", 1, 10));
            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                   javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10), java.awt.Color.black));
            stdParallel1NeysParamsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Std. Parallel 1:", javax.swing.border.TitledBorder.CENTER,
                                                  javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10), java.awt.Color.black));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {
        try {
            initMemberComponents();

            setAlignmentX(1.0F);
            setAlignmentY(1.0F);
            setLayout(new java.awt.GridBagLayout());
            
            GridBagConstraints c = new java.awt.GridBagConstraints();
            
            // Only initialize and add EPSG data elements if available.
            // Not all users of MasterPanel need it. And some don't 
            // render properly with it (FileDlg, CreateFileHeaderDlg).
            if (null != epsgCodesAllData) {
                initEpsgPanel();
                c.gridx = 0;
                c.gridy = 0;
                c.anchor = GridBagConstraints.EAST;
                c.insets = new java.awt.Insets(5, 0, 0, 5);
                add(epsgPanel, c);
            }
            
            initDatumPanel();
            c = new java.awt.GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            c.insets = new java.awt.Insets(5, 2, 1, 2);
            add(datumPanel, c);

            initProjectionPanel();
            c = new java.awt.GridBagConstraints();
            c.gridx = 0;
            c.gridy = 3;
            c.insets = new java.awt.Insets(2, 2, 1, 2);
            add(projectionPanel, c);

            initParamRow1Panel();

            c = new java.awt.GridBagConstraints();
            c.gridx = 0;
            c.gridy = 4;
            c.insets = new java.awt.Insets(2, 2, 1, 2);
            add(paramFieldsRow1LayeredPane, c);

            initParamRow2Panel();

            c = new java.awt.GridBagConstraints();
            c.gridx = 0;
            c.gridy = 5;
            c.insets = new java.awt.Insets(1, 2, 1, 2);
            add(paramFieldsRow2LayeredPane, c);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMemberComponents() {
        inputProjectionLabel = new JLabel();
        projectionComboBox = new javax.swing.JComboBox();
        paramFieldsRow1LayeredPane = new JLayeredPane();
        _3ParamFieldsRow1Panel = new JPanel();
        _3ParamFieldsRow1LabelA = new JLabel();
        _3ParamFieldsRow1LabelB = new JLabel();
        _3ParamFieldsRow1LabelC = new JLabel();
        _3ParamFieldsRow1TextFieldA = new javax.swing.JTextField();
        _3ParamFieldsRow1TextFieldB = new javax.swing.JTextField();
        _3ParamFieldsRow1TextFieldC = new javax.swing.JTextField();
        _4ParamFieldsRow1Panel = new JPanel();
        _4ParamFieldsRow1LabelA = new JLabel();
        _4ParamFieldsRow1LabelB = new JLabel();
        _4ParamFieldsRow1LabelC = new JLabel();
        _4ParamFieldsRow1LabelD = new JLabel();
        _4ParamFieldsRow1TextFieldA = new javax.swing.JTextField();
        _4ParamFieldsRow1TextFieldB = new javax.swing.JTextField();
        _4ParamFieldsRow1TextFieldC = new javax.swing.JTextField();
        _4ParamFieldsRow1TextFieldD = new javax.swing.JTextField();
        neysParamsRow1Panel = new JPanel();
        centralMeridianNeysParamsPanel = new JPanel();
        centralMeridianNeysParamsLabel = new JLabel();
        centralMeridianNeysParamsTextField = new javax.swing.JTextField();
        originLatitudeNeysParamsPanel = new JPanel();
        originLatitudeNeysParamsLabel = new JLabel();
        originLatitudeNeysParamsTextField = new javax.swing.JTextField();
        stdParallel1NeysParamsPanel = new JPanel();
        neys71RadioButton = new javax.swing.JRadioButton();
        neys74RadioButton = new javax.swing.JRadioButton();
        stdParallel2NeysParamsPanel = new JPanel();
        stdParallel2NeysParamsLabel = new JLabel();
        stdParallel2NeysParamsTextField = new javax.swing.JTextField();
        heightLabelPanel = new JPanel();
        heightLabel = new JLabel();
        heightPanel = new JPanel();
        heightComboBox = new javax.swing.JComboBox();
        _3ParamFieldsRow1PS_SFPanel = new JPanel();
        _3ParamFieldsRow1PS_SFLabelA = new JLabel();
        _3ParamFieldsRow1PS_SFLabelB = new JLabel();
        row1HemiBoxPanel = new JPanel();
        row1NHemiRadioButton = new javax.swing.JRadioButton();
        row1SHemiRadioButton = new javax.swing.JRadioButton();
        _3ParamFieldsRow1PS_SFTextFieldA = new javax.swing.JTextField();
        _3ParamFieldsRow1PS_SFTextFieldB = new javax.swing.JTextField();
        paramFieldsRow2LayeredPane = new JLayeredPane();
        _3ParamFieldsRow2Panel = new JPanel();
        _3ParamFieldsRow2LabelA = new JLabel();
        _3ParamFieldsRow2LabelB = new JLabel();
        _3ParamFieldsRow2LabelC = new JLabel();
        _3ParamFieldsRow2TextFieldA = new javax.swing.JTextField();
        _3ParamFieldsRow2TextFieldB = new javax.swing.JTextField();
        _3ParamFieldsRow2TextFieldC = new javax.swing.JTextField();
        _4ParamFieldsRow2Panel = new JPanel();
        _4ParamFieldsRow2LabelA = new JLabel();
        _4ParamFieldsRow2LabelB = new JLabel();
        _4ParamFieldsRow2LabelC = new JLabel();
        _4ParamFieldsRow2LabelD = new JLabel();
        _4ParamFieldsRow2TextFieldA = new javax.swing.JTextField();
        _4ParamFieldsRow2TextFieldB = new javax.swing.JTextField();
        _4ParamFieldsRow2TextFieldC = new javax.swing.JTextField();
        _4ParamFieldsRow2TextFieldD = new javax.swing.JTextField();
        zoneHemiPanel = new JPanel();
        zoneBoxPanel = new JPanel();
        zoneRadioButton = new javax.swing.JRadioButton();
        zoneLabel = new JLabel();
        zoneTextField = new javax.swing.JTextField();
        tempZoneBoxLabel = new JLabel();
        zoneRangeLabel = new JLabel();
        hemiBoxPanel = new JPanel();
        nHemiRadioButton = new javax.swing.JRadioButton();
        sHemiRadioButton = new javax.swing.JRadioButton();
        geodeticCoordinateOrderPanel = new JPanel();
        _geodeticCoordinateOrderPanel = new JPanel();
        latitudeLongitudeRadioButton = new javax.swing.JRadioButton();
        longitudeLatitudeRadioButton = new javax.swing.JRadioButton();
    }


    private void initEpsgPanel() {
        currentEpsgCode = null;
        datumChanged = false;
        projectionChanged = false;
        heightChanged = false;
        
        epsgPanel = new JPanel();
        epsgPanel.setLayout(new GridBagLayout());
        
        final JLabel epsgLabel = new JLabel(EPSG_LABEL_TEXT);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.gridwidth = GridBagConstraints.REMAINDER;
        epsgPanel.add(epsgLabel, c);
        
        epsgCombobox = new AutocompleteCombobox(new StringSearchable(new ArrayList<String>(epsgCodesAllData.keySet())));
        epsgCombobox.setMaximumRowCount(30);
        epsgCombobox.setPreferredSize(new Dimension(85, 20));
        epsgCombobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                currentEpsgCode = null;
            }
        });
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        epsgPanel.add(epsgCombobox, c);
        
        epsgApplyBtn = new JButton();
        epsgApplyBtn.setFont(new java.awt.Font("Dialog", 0, 11));
        epsgApplyBtn.setText("Apply");
        epsgApplyBtn.setToolTipText("Apply EPSG CRS Code");
        
        epsgApplyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                epsgApplyBtnHandler();
            }
        });
        
        c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_END;
        epsgPanel.add(epsgApplyBtn, c);
    }


    private void initDatumPanel() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0,0,0,0);

        datumPanel = new JPanel();
        datumLabel = new JLabel();
        ellipsoidLabel = new JLabel();
        datumSelectLayeredPane = new JLayeredPane();
        datumTextField = new JTextField();
        datumComboBox = new JComboBox();
        ellipsoidTextField = new JTextField();
        projectionPanel = new JPanel();

        datumPanel.setMinimumSize(new Dimension(460, 40));
        datumPanel.setPreferredSize(new Dimension(460, 40));
        datumPanel.setLayout(new GridBagLayout());

        datumLabel.setText("Datum:");
        datumLabel.setMaximumSize(new Dimension(270, 16));
        datumLabel.setMinimumSize(new Dimension(270, 16));
        datumLabel.setPreferredSize(new Dimension(270, 16));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 9);
        datumPanel.add(datumLabel, gridBagConstraints);

        ellipsoidLabel.setText("Ellipsoid:");
        ellipsoidLabel.setMaximumSize(new Dimension(172, 16));
        ellipsoidLabel.setMinimumSize(new Dimension(172, 16));
        ellipsoidLabel.setPreferredSize(new Dimension(172, 16));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new Insets(0, 7, 0, 0);
        datumPanel.add(ellipsoidLabel, gridBagConstraints);

        datumSelectLayeredPane.setMinimumSize(new Dimension(270, 20));

        datumTextField.setEditable(false);
        datumTextField.setText("jTextField1");
        datumTextField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        datumTextField.setMinimumSize(new Dimension(320, 20));
        datumTextField.setPreferredSize(new Dimension(320, 20));
        datumTextField.setBounds(0, 0, 270, 20);
        datumSelectLayeredPane.add(datumTextField, JLayeredPane.DEFAULT_LAYER);

        datumComboBox.setMinimumSize(new Dimension(320, 20));
        datumComboBox.setPreferredSize(new Dimension(320, 20));
        datumComboBox.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            datumComboBoxActionPerformed(evt);
          }
        });
        datumComboBox.setBounds(0, 0, 270, 20);
        datumSelectLayeredPane.add(datumComboBox, JLayeredPane.DEFAULT_LAYER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 9);
        datumPanel.add(datumSelectLayeredPane, gridBagConstraints);

        ellipsoidTextField.setEditable(false);
        ellipsoidTextField.setText("jTextField2");
        ellipsoidTextField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        ellipsoidTextField.setMinimumSize(new Dimension(172, 20));
        ellipsoidTextField.setPreferredSize(new Dimension(172, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new Insets(0, 7, 0, 0);
        datumPanel.add(ellipsoidTextField, gridBagConstraints);
    }

    private void initProjectionPanel() {
        // projectionPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
        projectionPanel.setMinimumSize(new Dimension(460, 31));
        projectionPanel.setPreferredSize(new Dimension(460, 31));
        projectionPanel.setLayout(new GridBagLayout());

        inputProjectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputProjectionLabel.setText("jLabel7");
        projectionPanel.add(inputProjectionLabel, new GridBagConstraints());

        projectionComboBox.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            projectionComboBoxActionPerformed(evt);
          }
        });
        projectionPanel.add(projectionComboBox, new GridBagConstraints());
    }

    private void initParamRow1Panel() {
        GridBagConstraints gridBagConstraints;

        paramFieldsRow1LayeredPane.setMaximumSize(new Dimension(460, 40));
        paramFieldsRow1LayeredPane.setMinimumSize(new Dimension(460, 40));
        // paramFieldsRow1LayeredPane.setBorder(BorderFactory.createLineBorder(Color.green));


        _3ParamFieldsRow1Panel.setMaximumSize(new Dimension(460, 40));
        _3ParamFieldsRow1Panel.setMinimumSize(new Dimension(460, 40));
        _3ParamFieldsRow1Panel.setOpaque(false);
        _3ParamFieldsRow1Panel.setPreferredSize(new Dimension(460, 40));
        _3ParamFieldsRow1Panel.setLayout(new java.awt.GridLayout(2, 3, 12, 0));

        _3ParamFieldsRow1LabelA.setText("Longitude");
        _3ParamFieldsRow1LabelA.setMaximumSize(new Dimension(105, 16));
        _3ParamFieldsRow1LabelA.setMinimumSize(new Dimension(105, 16));
        _3ParamFieldsRow1LabelA.setOpaque(true);
        _3ParamFieldsRow1LabelA.setPreferredSize(new Dimension(105, 16));
        _3ParamFieldsRow1Panel.add(_3ParamFieldsRow1LabelA);

        _3ParamFieldsRow1LabelB.setText("Latitude");
        _3ParamFieldsRow1LabelB.setMaximumSize(new Dimension(105, 16));
        _3ParamFieldsRow1LabelB.setMinimumSize(new Dimension(105, 16));
        _3ParamFieldsRow1LabelB.setOpaque(true);
        _3ParamFieldsRow1LabelB.setPreferredSize(new Dimension(105, 16));
        _3ParamFieldsRow1Panel.add(_3ParamFieldsRow1LabelB);

        _3ParamFieldsRow1LabelC.setText("Scale Factor");
        _3ParamFieldsRow1LabelC.setMaximumSize(new Dimension(105, 16));
        _3ParamFieldsRow1LabelC.setMinimumSize(new Dimension(105, 16));
        _3ParamFieldsRow1LabelC.setOpaque(true);
        _3ParamFieldsRow1LabelC.setPreferredSize(new Dimension(105, 16));
        _3ParamFieldsRow1Panel.add(_3ParamFieldsRow1LabelC);

        _3ParamFieldsRow1TextFieldA.setText("0 0 0.00E");
        _3ParamFieldsRow1TextFieldA.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _3ParamFieldsRow1TextFieldA.setMinimumSize(new Dimension(138, 20));
        _3ParamFieldsRow1TextFieldA.setPreferredSize(new Dimension(138, 20));
        _3ParamFieldsRow1TextFieldA.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            epsgAttributeChangeActionPerformed(evt);
          }
        });
        _3ParamFieldsRow1Panel.add(_3ParamFieldsRow1TextFieldA);
        

        _3ParamFieldsRow1TextFieldB.setText("0 0 0.0N");
        _3ParamFieldsRow1TextFieldB.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _3ParamFieldsRow1TextFieldB.setMinimumSize(new Dimension(138, 20));
        _3ParamFieldsRow1TextFieldB.setPreferredSize(new Dimension(138, 20));
        _3ParamFieldsRow1TextFieldB.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            epsgAttributeChangeActionPerformed(evt);
          }
        });
        _3ParamFieldsRow1Panel.add(_3ParamFieldsRow1TextFieldB);

        _3ParamFieldsRow1TextFieldC.setText("1.00000");
        _3ParamFieldsRow1TextFieldC.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _3ParamFieldsRow1TextFieldC.setMinimumSize(new Dimension(138, 20));
        _3ParamFieldsRow1TextFieldC.setPreferredSize(new Dimension(138, 20));
        _3ParamFieldsRow1Panel.add(_3ParamFieldsRow1TextFieldC);

        _3ParamFieldsRow1Panel.setBounds(4, 2, 460, 40);
        paramFieldsRow1LayeredPane.add(_3ParamFieldsRow1Panel, JLayeredPane.DEFAULT_LAYER);

        _4ParamFieldsRow1Panel.setMinimumSize(new Dimension(1, 1));
        _4ParamFieldsRow1Panel.setPreferredSize(new Dimension(466, 40));
        _4ParamFieldsRow1Panel.setLayout(new java.awt.GridLayout(2, 4, 12, 0));

        _4ParamFieldsRow1LabelA.setText("Latitude");
        _4ParamFieldsRow1LabelA.setMinimumSize(new Dimension(4, 10));
        _4ParamFieldsRow1Panel.add(_4ParamFieldsRow1LabelA);

        _4ParamFieldsRow1LabelB.setText("Longitude");
        _4ParamFieldsRow1LabelB.setMinimumSize(new Dimension(4, 10));
        _4ParamFieldsRow1Panel.add(_4ParamFieldsRow1LabelB);

        _4ParamFieldsRow1LabelC.setText("False Easting");
        _4ParamFieldsRow1LabelC.setMinimumSize(new Dimension(4, 10));
        _4ParamFieldsRow1LabelC.setPreferredSize(new Dimension(66, 16));
        _4ParamFieldsRow1Panel.add(_4ParamFieldsRow1LabelC);

        _4ParamFieldsRow1LabelD.setText("False Northing");
        _4ParamFieldsRow1LabelD.setMinimumSize(new Dimension(4, 10));
        _4ParamFieldsRow1Panel.add(_4ParamFieldsRow1LabelD);

        _4ParamFieldsRow1TextFieldA.setText("0 0 0.00N");
        _4ParamFieldsRow1TextFieldA.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _4ParamFieldsRow1Panel.add(_4ParamFieldsRow1TextFieldA);

        _4ParamFieldsRow1TextFieldB.setText("0 0 0.00W");
        _4ParamFieldsRow1TextFieldB.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _4ParamFieldsRow1TextFieldB.setMinimumSize(new Dimension(4, 10));
        _4ParamFieldsRow1Panel.add(_4ParamFieldsRow1TextFieldB);

        _4ParamFieldsRow1TextFieldC.setText("200000");
        _4ParamFieldsRow1TextFieldC.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _4ParamFieldsRow1TextFieldC.setMinimumSize(new Dimension(4, 10));
        _4ParamFieldsRow1TextFieldC.setPreferredSize(new Dimension(40, 20));
        _4ParamFieldsRow1Panel.add(_4ParamFieldsRow1TextFieldC);

        _4ParamFieldsRow1TextFieldD.setText("400000");
        _4ParamFieldsRow1TextFieldD.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _4ParamFieldsRow1TextFieldD.setMinimumSize(new Dimension(4, 10));
        _4ParamFieldsRow1Panel.add(_4ParamFieldsRow1TextFieldD);

        _4ParamFieldsRow1Panel.setBounds(4, 2, 460, 40);
        paramFieldsRow1LayeredPane.add(_4ParamFieldsRow1Panel, JLayeredPane.DEFAULT_LAYER);

        neysParamsRow1Panel.setMinimumSize(new Dimension(461, 55));
        neysParamsRow1Panel.setPreferredSize(new Dimension(461, 55));
        neysParamsRow1Panel.setLayout(new GridBagLayout());

        centralMeridianNeysParamsPanel.setMinimumSize(new Dimension(106, 36));
        centralMeridianNeysParamsPanel.setPreferredSize(new Dimension(106, 36));
        centralMeridianNeysParamsPanel.setLayout(new java.awt.GridLayout(2, 1, 12, 0));

        centralMeridianNeysParamsLabel.setText("Central Meridian:");
        centralMeridianNeysParamsLabel.setMinimumSize(new Dimension(4, 10));
        centralMeridianNeysParamsPanel.add(centralMeridianNeysParamsLabel);

        centralMeridianNeysParamsTextField.setText("0 0 0.00E");
        centralMeridianNeysParamsTextField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        centralMeridianNeysParamsTextField.setMinimumSize(new Dimension(4, 10));
        centralMeridianNeysParamsPanel.add(centralMeridianNeysParamsTextField);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 0, 6);
        neysParamsRow1Panel.add(centralMeridianNeysParamsPanel, gridBagConstraints);

        originLatitudeNeysParamsPanel.setMinimumSize(new Dimension(106, 36));
        originLatitudeNeysParamsPanel.setPreferredSize(new Dimension(106, 36));
        originLatitudeNeysParamsPanel.setLayout(new java.awt.GridLayout(2, 1, 12, 0));

        originLatitudeNeysParamsLabel.setText("Origin Latitude:");
        originLatitudeNeysParamsLabel.setMinimumSize(new Dimension(4, 10));
        originLatitudeNeysParamsPanel.add(originLatitudeNeysParamsLabel);

        originLatitudeNeysParamsTextField.setText("0 0 0.00N");
        originLatitudeNeysParamsTextField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        originLatitudeNeysParamsTextField.setMinimumSize(new Dimension(4, 10));
        originLatitudeNeysParamsPanel.add(originLatitudeNeysParamsTextField);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 6, 0, 6);
        neysParamsRow1Panel.add(originLatitudeNeysParamsPanel, gridBagConstraints);

        stdParallel1NeysParamsPanel.setBorder(BorderFactory.createTitledBorder(null, "Std. Parallel 1:", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        stdParallel1NeysParamsPanel.setAlignmentX(0.0F);
        stdParallel1NeysParamsPanel.setAlignmentY(0.0F);
        stdParallel1NeysParamsPanel.setMaximumSize(new Dimension(107, 39));
        stdParallel1NeysParamsPanel.setMinimumSize(new Dimension(107, 39));
        stdParallel1NeysParamsPanel.setPreferredSize(new Dimension(107, 39));
        stdParallel1NeysParamsPanel.setLayout(new GridBagLayout());

        neys71RadioButton.setText("71");
        neys71RadioButton.setAlignmentY(0.0F);
        neys71RadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        neys71RadioButton.setMargin(new Insets(0, 0, 0, 0));
        neys71RadioButton.setMaximumSize(new Dimension(30, 17));
        neys71RadioButton.setMinimumSize(new Dimension(30, 17));
        neys71RadioButton.setModel(nHemiRadioButton.getModel());
        neys71RadioButton.setPreferredSize(new Dimension(40, 17));
        neys71RadioButton.setVerticalAlignment(SwingConstants.TOP);
        neys71RadioButton.setVerticalTextPosition(SwingConstants.TOP);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(0, 5, 5, 0);
        stdParallel1NeysParamsPanel.add(neys71RadioButton, gridBagConstraints);

        neys74RadioButton.setText("74");
        neys74RadioButton.setAlignmentY(0.0F);
        neys74RadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        neys74RadioButton.setMargin(new Insets(0, 0, 0, 0));
        neys74RadioButton.setMaximumSize(new Dimension(30, 17));
        neys74RadioButton.setMinimumSize(new Dimension(30, 17));
        neys74RadioButton.setModel(sHemiRadioButton.getModel());
        neys74RadioButton.setPreferredSize(new Dimension(40, 17));
        neys74RadioButton.setVerticalAlignment(SwingConstants.TOP);
        neys74RadioButton.setVerticalTextPosition(SwingConstants.TOP);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new Insets(0, 2, 5, 0);
        stdParallel1NeysParamsPanel.add(neys74RadioButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new Insets(0, 6, 0, 6);
        neysParamsRow1Panel.add(stdParallel1NeysParamsPanel, gridBagConstraints);

        stdParallel2NeysParamsPanel.setMinimumSize(new Dimension(106, 36));
        stdParallel2NeysParamsPanel.setPreferredSize(new Dimension(106, 36));
        stdParallel2NeysParamsPanel.setLayout(new java.awt.GridLayout(2, 1, 12, 0));

        stdParallel2NeysParamsLabel.setText("Std. Parallel 2:");
        stdParallel2NeysParamsLabel.setMinimumSize(new Dimension(4, 10));
        stdParallel2NeysParamsPanel.add(stdParallel2NeysParamsLabel);

        stdParallel2NeysParamsTextField.setEditable(false);
        stdParallel2NeysParamsTextField.setText("89 59 59.0");
        stdParallel2NeysParamsTextField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        stdParallel2NeysParamsTextField.setMinimumSize(new Dimension(4, 10));
        stdParallel2NeysParamsPanel.add(stdParallel2NeysParamsTextField);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 6, 0, 0);
        neysParamsRow1Panel.add(stdParallel2NeysParamsPanel, gridBagConstraints);

        neysParamsRow1Panel.setBounds(0, 2, 460, 40);
        paramFieldsRow1LayeredPane.add(neysParamsRow1Panel, JLayeredPane.DEFAULT_LAYER);

        heightLabelPanel.setMinimumSize(new Dimension(213, 20));
        heightLabelPanel.setPreferredSize(new Dimension(213, 20));
        heightLabelPanel.setLayout(new GridBagLayout());

        heightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        heightLabel.setText("jLabel3");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        heightLabelPanel.add(heightLabel, gridBagConstraints);

        heightLabelPanel.setBounds(4, 2, 460, 40);
        paramFieldsRow1LayeredPane.add(heightLabelPanel, JLayeredPane.DEFAULT_LAYER);

        heightPanel.setAlignmentX(0.0F);
        heightPanel.setAlignmentY(0.0F);
        heightPanel.setMinimumSize(new Dimension(469, 50));
        heightPanel.setOpaque(false);
        heightPanel.setPreferredSize(new Dimension(469, 50));
        heightPanel.setLayout(new GridBagLayout());

        heightComboBox.setMinimumSize(new Dimension(276, 21));
        heightComboBox.setPreferredSize(new Dimension(276, 21));
        heightComboBox.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            heightComboBoxActionPerformed(evt);
          }
        });
        heightPanel.add(heightComboBox, new GridBagConstraints());

        heightPanel.setBounds(0, 0, 462, 45);
        paramFieldsRow1LayeredPane.add(heightPanel, JLayeredPane.DEFAULT_LAYER);

        _3ParamFieldsRow1PS_SFPanel.setMaximumSize(new Dimension(460, 40));
        _3ParamFieldsRow1PS_SFPanel.setMinimumSize(new Dimension(460, 40));
        _3ParamFieldsRow1PS_SFPanel.setOpaque(false);
        _3ParamFieldsRow1PS_SFPanel.setPreferredSize(new Dimension(460, 40));
        _3ParamFieldsRow1PS_SFPanel.setLayout(new GridBagLayout());

        _3ParamFieldsRow1PS_SFLabelA.setText("Longitude");
        _3ParamFieldsRow1PS_SFLabelA.setMaximumSize(new Dimension(105, 16));
        _3ParamFieldsRow1PS_SFLabelA.setMinimumSize(new Dimension(105, 16));
        _3ParamFieldsRow1PS_SFLabelA.setOpaque(true);
        _3ParamFieldsRow1PS_SFLabelA.setPreferredSize(new Dimension(105, 16));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 0, 15);
        _3ParamFieldsRow1PS_SFPanel.add(_3ParamFieldsRow1PS_SFLabelA, gridBagConstraints);

        _3ParamFieldsRow1PS_SFLabelB.setText("Latitude");
        _3ParamFieldsRow1PS_SFLabelB.setMaximumSize(new Dimension(105, 16));
        _3ParamFieldsRow1PS_SFLabelB.setMinimumSize(new Dimension(105, 16));
        _3ParamFieldsRow1PS_SFLabelB.setOpaque(true);
        _3ParamFieldsRow1PS_SFLabelB.setPreferredSize(new Dimension(105, 16));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 15, 0, 15);
        _3ParamFieldsRow1PS_SFPanel.add(_3ParamFieldsRow1PS_SFLabelB, gridBagConstraints);

        row1HemiBoxPanel.setBorder(BorderFactory.createTitledBorder(null, "Hemisphere:", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        row1HemiBoxPanel.setMaximumSize(new Dimension(101, 41));
        row1HemiBoxPanel.setMinimumSize(new Dimension(101, 41));
        row1HemiBoxPanel.setPreferredSize(new Dimension(101, 41));
        row1HemiBoxPanel.setRequestFocusEnabled(false);
        row1HemiBoxPanel.setLayout(new GridBagLayout());

        row1NHemiRadioButton.setText("N");
        row1NHemiRadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        row1NHemiRadioButton.setMargin(new Insets(0, 0, 0, 0));
        row1NHemiRadioButton.setMaximumSize(new Dimension(28, 20));
        row1NHemiRadioButton.setModel(nHemiRadioButton.getModel());
        row1NHemiRadioButton.setPreferredSize(new Dimension(28, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new Insets(0, 0, 3, 7);
        row1HemiBoxPanel.add(row1NHemiRadioButton, gridBagConstraints);

        row1SHemiRadioButton.setText("S");
        row1SHemiRadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        row1SHemiRadioButton.setMargin(new Insets(0, 0, 0, 0));
        row1SHemiRadioButton.setMaximumSize(new Dimension(28, 20));
        row1SHemiRadioButton.setModel(sHemiRadioButton.getModel());
        row1SHemiRadioButton.setPreferredSize(new Dimension(28, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new Insets(0, 7, 3, 0);
        row1HemiBoxPanel.add(row1SHemiRadioButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 15, 0, 0);
        _3ParamFieldsRow1PS_SFPanel.add(row1HemiBoxPanel, gridBagConstraints);

        _3ParamFieldsRow1PS_SFTextFieldA.setText("0 0 0.0E");
        _3ParamFieldsRow1PS_SFTextFieldA.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _3ParamFieldsRow1PS_SFTextFieldA.setMaximumSize(new Dimension(138, 22));
        _3ParamFieldsRow1PS_SFTextFieldA.setMinimumSize(new Dimension(138, 22));
        _3ParamFieldsRow1PS_SFTextFieldA.setPreferredSize(new Dimension(138, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(3, 0, 0, 15);
        _3ParamFieldsRow1PS_SFPanel.add(_3ParamFieldsRow1PS_SFTextFieldA, gridBagConstraints);

        _3ParamFieldsRow1PS_SFTextFieldB.setText("0 0 0.0N");
        _3ParamFieldsRow1PS_SFTextFieldB.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _3ParamFieldsRow1PS_SFTextFieldB.setMaximumSize(new Dimension(138, 22));
        _3ParamFieldsRow1PS_SFTextFieldB.setMinimumSize(new Dimension(138, 22));
        _3ParamFieldsRow1PS_SFTextFieldB.setPreferredSize(new Dimension(138, 22));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(3, 15, 0, 15);
        _3ParamFieldsRow1PS_SFPanel.add(_3ParamFieldsRow1PS_SFTextFieldB, gridBagConstraints);

        _3ParamFieldsRow1PS_SFPanel.setBounds(4, 2, 460, 40);
        paramFieldsRow1LayeredPane.add(_3ParamFieldsRow1PS_SFPanel, JLayeredPane.DEFAULT_LAYER);
    }

    private void initParamRow2Panel() {
        GridBagConstraints gridBagConstraints;

        paramFieldsRow2LayeredPane.setMinimumSize(new Dimension(460, 55));
        // paramFieldsRow2LayeredPane.setBorder(BorderFactory.createLineBorder(Color.yellow));

        _3ParamFieldsRow2Panel.setOpaque(false);
        _3ParamFieldsRow2Panel.setLayout(new java.awt.GridLayout(2, 3, 12, 0));

        _3ParamFieldsRow2LabelA.setText("Latitude");
        _3ParamFieldsRow2LabelA.setOpaque(true);
        _3ParamFieldsRow2Panel.add(_3ParamFieldsRow2LabelA);

        _3ParamFieldsRow2LabelB.setText("Longitude");
        _3ParamFieldsRow2LabelB.setOpaque(true);
        _3ParamFieldsRow2Panel.add(_3ParamFieldsRow2LabelB);

        _3ParamFieldsRow2LabelC.setText("Scale Factor");
        _3ParamFieldsRow2LabelC.setOpaque(true);
        _3ParamFieldsRow2Panel.add(_3ParamFieldsRow2LabelC);

        _3ParamFieldsRow2TextFieldA.setText("0 0 0.00S");
        _3ParamFieldsRow2TextFieldA.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _3ParamFieldsRow2Panel.add(_3ParamFieldsRow2TextFieldA);

        _3ParamFieldsRow2TextFieldB.setText("0 0 0.0E");
        _3ParamFieldsRow2TextFieldB.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _3ParamFieldsRow2Panel.add(_3ParamFieldsRow2TextFieldB);

        _3ParamFieldsRow2TextFieldC.setText("4500");
        _3ParamFieldsRow2TextFieldC.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _3ParamFieldsRow2Panel.add(_3ParamFieldsRow2TextFieldC);

        _3ParamFieldsRow2Panel.setBounds(0, 0, 462, 38);
        paramFieldsRow2LayeredPane.add(_3ParamFieldsRow2Panel, JLayeredPane.DEFAULT_LAYER);

        _4ParamFieldsRow2Panel.setMinimumSize(new Dimension(460, 40));
        _4ParamFieldsRow2Panel.setPreferredSize(new Dimension(460, 40));
        _4ParamFieldsRow2Panel.setLayout(new java.awt.GridLayout(2, 4, 12, 0));

        _4ParamFieldsRow2LabelA.setText("Latitude");
        _4ParamFieldsRow2LabelA.setMaximumSize(new Dimension(100, 16));
        _4ParamFieldsRow2LabelA.setMinimumSize(new Dimension(100, 16));
        _4ParamFieldsRow2LabelA.setPreferredSize(new Dimension(100, 16));
        _4ParamFieldsRow2Panel.add(_4ParamFieldsRow2LabelA);

        _4ParamFieldsRow2LabelB.setText("Longitude");
        _4ParamFieldsRow2LabelB.setMaximumSize(new Dimension(100, 16));
        _4ParamFieldsRow2LabelB.setMinimumSize(new Dimension(100, 16));
        _4ParamFieldsRow2LabelB.setPreferredSize(new Dimension(100, 16));
        _4ParamFieldsRow2Panel.add(_4ParamFieldsRow2LabelB);

        _4ParamFieldsRow2LabelC.setText("False Easting");
        _4ParamFieldsRow2LabelC.setMaximumSize(new Dimension(100, 16));
        _4ParamFieldsRow2LabelC.setMinimumSize(new Dimension(100, 16));
        _4ParamFieldsRow2LabelC.setPreferredSize(new Dimension(100, 16));
        _4ParamFieldsRow2Panel.add(_4ParamFieldsRow2LabelC);

        _4ParamFieldsRow2LabelD.setText("False Northing");
        _4ParamFieldsRow2LabelD.setMaximumSize(new Dimension(100, 16));
        _4ParamFieldsRow2LabelD.setMinimumSize(new Dimension(100, 16));
        _4ParamFieldsRow2LabelD.setPreferredSize(new Dimension(100, 16));
        _4ParamFieldsRow2Panel.add(_4ParamFieldsRow2LabelD);

        _4ParamFieldsRow2TextFieldA.setText("0 0 0.00N");
        _4ParamFieldsRow2TextFieldA.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _4ParamFieldsRow2TextFieldA.setMinimumSize(new Dimension(100, 20));
        _4ParamFieldsRow2TextFieldA.setPreferredSize(new Dimension(100, 20));
        _4ParamFieldsRow2Panel.add(_4ParamFieldsRow2TextFieldA);

        _4ParamFieldsRow2TextFieldB.setText("0 0 0.00W");
        _4ParamFieldsRow2TextFieldB.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _4ParamFieldsRow2TextFieldB.setMinimumSize(new Dimension(100, 20));
        _4ParamFieldsRow2TextFieldB.setPreferredSize(new Dimension(100, 20));
        _4ParamFieldsRow2TextFieldB.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            epsgAttributeChangeActionPerformed(evt);
          }
        });
        _4ParamFieldsRow2Panel.add(_4ParamFieldsRow2TextFieldB);

        _4ParamFieldsRow2TextFieldC.setText("200000");
        _4ParamFieldsRow2TextFieldC.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _4ParamFieldsRow2TextFieldC.setMinimumSize(new Dimension(100, 20));
        _4ParamFieldsRow2TextFieldC.setPreferredSize(new Dimension(100, 20));
        _4ParamFieldsRow2TextFieldC.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            epsgAttributeChangeActionPerformed(evt);
          }
        });
        _4ParamFieldsRow2Panel.add(_4ParamFieldsRow2TextFieldC);

        _4ParamFieldsRow2TextFieldD.setText("400000");
        _4ParamFieldsRow2TextFieldD.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        _4ParamFieldsRow2TextFieldD.setMinimumSize(new Dimension(100, 20));
        _4ParamFieldsRow2TextFieldD.setPreferredSize(new Dimension(100, 20));
        _4ParamFieldsRow2Panel.add(_4ParamFieldsRow2TextFieldD);

        _4ParamFieldsRow2Panel.setBounds(4, 2, 460, 40);
        paramFieldsRow2LayeredPane.add(_4ParamFieldsRow2Panel, JLayeredPane.DEFAULT_LAYER);

        zoneHemiPanel.setMinimumSize(new Dimension(220, 60));
        zoneHemiPanel.setOpaque(false);
        zoneHemiPanel.setPreferredSize(new Dimension(220, 60));
        zoneHemiPanel.setLayout(new GridBagLayout());

        zoneBoxPanel.setMinimumSize(new Dimension(175, 58));
        zoneBoxPanel.setPreferredSize(new Dimension(175, 58));
        zoneBoxPanel.setLayout(new GridBagLayout());

        zoneRadioButton.setText("Override:");
        zoneRadioButton.setActionCommand("( set )");
        zoneRadioButton.setHorizontalTextPosition(SwingConstants.LEFT);
        zoneRadioButton.setMargin(new Insets(0, 0, 0, 0));
        zoneRadioButton.setVerticalAlignment(SwingConstants.TOP);
        zoneRadioButton.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            zoneActionPerformed(evt);
          }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 0, 0, 8);
        zoneBoxPanel.add(zoneRadioButton, gridBagConstraints);

        zoneLabel.setText("Zone");
        zoneLabel.setOpaque(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        zoneBoxPanel.add(zoneLabel, gridBagConstraints);

        zoneTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        zoneTextField.setText("0");
        zoneTextField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        zoneTextField.setMinimumSize(new Dimension(35, 19));
        zoneTextField.setPreferredSize(new Dimension(41, 19));
        zoneTextField.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            epsgAttributeChangeActionPerformed(evt);
          }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 11, 0);
        zoneBoxPanel.add(zoneTextField, gridBagConstraints);

        tempZoneBoxLabel.setOpaque(true);
        zoneBoxPanel.add(tempZoneBoxLabel, new GridBagConstraints());

        zoneRangeLabel.setText("(1-60):");
        zoneRangeLabel.setOpaque(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        zoneBoxPanel.add(zoneRangeLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        zoneHemiPanel.add(zoneBoxPanel, gridBagConstraints);

        hemiBoxPanel.setBorder(BorderFactory.createTitledBorder(null, "Hemisphere:", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        hemiBoxPanel.setMaximumSize(new Dimension(101, 45));
        hemiBoxPanel.setMinimumSize(new Dimension(101, 45));
        hemiBoxPanel.setPreferredSize(new Dimension(101, 45));
        hemiBoxPanel.setLayout(new GridBagLayout());

        nHemiRadioButton.setText("N");
        nHemiRadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        nHemiRadioButton.setMargin(new Insets(0, 0, 0, 0));
        nHemiRadioButton.setMaximumSize(new Dimension(28, 20));
        nHemiRadioButton.setModel(nHemiRadioButton.getModel());
        nHemiRadioButton.setPreferredSize(new Dimension(28, 20));
        nHemiRadioButton.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            epsgAttributeChangeActionPerformed(evt);
          }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new Insets(0, 0, 5, 7);
        hemiBoxPanel.add(nHemiRadioButton, gridBagConstraints);

        sHemiRadioButton.setText("S");
        sHemiRadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        sHemiRadioButton.setMargin(new Insets(0, 0, 0, 0));
        sHemiRadioButton.setMaximumSize(new Dimension(28, 20));
        sHemiRadioButton.setModel(sHemiRadioButton.getModel());
        sHemiRadioButton.setPreferredSize(new Dimension(28, 20));
        sHemiRadioButton.addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            epsgAttributeChangeActionPerformed(evt);
          }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new Insets(0, 7, 5, 0);
        hemiBoxPanel.add(sHemiRadioButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(14, 0, 4, 55);
        zoneHemiPanel.add(hemiBoxPanel, gridBagConstraints);

        zoneHemiPanel.setBounds(0, 0, 462, 55);
        paramFieldsRow2LayeredPane.add(zoneHemiPanel, JLayeredPane.DEFAULT_LAYER);

        geodeticCoordinateOrderPanel.setMaximumSize(new Dimension(200, 60));
        geodeticCoordinateOrderPanel.setMinimumSize(new Dimension(200, 60));
        geodeticCoordinateOrderPanel.setPreferredSize(new Dimension(200, 60));
        geodeticCoordinateOrderPanel.setLayout(new GridBagLayout());

        _geodeticCoordinateOrderPanel.setBorder(BorderFactory.createTitledBorder(null, "Coordinate Order", TitledBorder.CENTER, TitledBorder.TOP));
        _geodeticCoordinateOrderPanel.setMinimumSize(new Dimension(173, 50));
        _geodeticCoordinateOrderPanel.setPreferredSize(new Dimension(173, 50));
        _geodeticCoordinateOrderPanel.setLayout(new GridBagLayout());

        latitudeLongitudeRadioButton.setText("Latitude-Longitude");
        latitudeLongitudeRadioButton.setHorizontalAlignment(SwingConstants.CENTER);
        latitudeLongitudeRadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        latitudeLongitudeRadioButton.setMargin(new Insets(0, 0, 0, 0));
        latitudeLongitudeRadioButton.setMaximumSize(new Dimension(161, 16));
        latitudeLongitudeRadioButton.setMinimumSize(new Dimension(161, 16));
        latitudeLongitudeRadioButton.setModel(latitudeLongitudeRadioButton.getModel());
        latitudeLongitudeRadioButton.setPreferredSize(new Dimension(161, 16));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        _geodeticCoordinateOrderPanel.add(latitudeLongitudeRadioButton, gridBagConstraints);

        longitudeLatitudeRadioButton.setText("Longitude-Latitude");
        longitudeLatitudeRadioButton.setHorizontalAlignment(SwingConstants.CENTER);
        longitudeLatitudeRadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        longitudeLatitudeRadioButton.setMargin(new Insets(0, 0, 0, 0));
        longitudeLatitudeRadioButton.setMaximumSize(new Dimension(161, 16));
        longitudeLatitudeRadioButton.setMinimumSize(new Dimension(161, 16));
        longitudeLatitudeRadioButton.setModel(longitudeLatitudeRadioButton.getModel());
        longitudeLatitudeRadioButton.setPreferredSize(new Dimension(161, 16));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        _geodeticCoordinateOrderPanel.add(longitudeLatitudeRadioButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        geodeticCoordinateOrderPanel.add(_geodeticCoordinateOrderPanel, gridBagConstraints);

        geodeticCoordinateOrderPanel.setBounds(0, 0, 462, 55);
        paramFieldsRow2LayeredPane.add(geodeticCoordinateOrderPanel, JLayeredPane.DEFAULT_LAYER);
    }

    // sets the Height for Geodetic Coordinate System based on the parameter input
    private void setGeodeticHeightType(String geodeticHeightType) {
        switch (geodeticHeightType) {
            case "No Height":
                heightComboBox.setSelectedItem(geodeticHeightType);
                break;
            case "Ellipsoid Height":
                heightComboBox.setSelectedItem(geodeticHeightType);
                break;
            case "MSL-EGM96-15M-BL Height":
                heightComboBox.setSelectedItem(geodeticHeightType);
                break;
            case "MSL-EGM96-VG-NS Height":
                heightComboBox.setSelectedItem(geodeticHeightType);
                break;
            case "MSL-EGM84-10D-BL Height":
                heightComboBox.setSelectedItem(geodeticHeightType);
                break;
            case "MSL-EGM84-10D-NS Height":
                heightComboBox.setSelectedItem(geodeticHeightType);
                break;
            case "MSL-EGM84-30M-BL Height":
                heightComboBox.setSelectedItem(geodeticHeightType);
                break;
            case "MSL-EGM2008-2.5M-BCS Height":
                heightComboBox.setSelectedItem(geodeticHeightType);
                break;
            default:
                stringHandler.displayErrorMsg(this, "GeoTrans does not handle this height type [" + geodeticHeightType + "]");
        }
        return;
    }
    
    // sets the Hemisphere Radio Button based on the parameter input
    private void setHemiRadioButton(String direction) {
        if (direction.equals("N")){
            nHemiRadioButton.setSelected(true);
        } else if (direction.equals("S")){
            sHemiRadioButton.setSelected(true);
        }
        return;
    }
    
    boolean isEpsgCodeActive() {
        return null != currentEpsgCode;
    }
    
    String getUserEnteredEpsgCode() {
        return currentEpsgCode;
    }

    // displays EPSG data for the EPSG code that was selected
    private void epsgApplyBtnHandler() {
        try {
            final Object selectedItem = epsgCombobox.getSelectedItem();
            if (null == selectedItem) {
                currentEpsgCode = null;
                return;
            }
            
            currentEpsgCode = epsgCombobox.getSelectedItemString();
            if (null == currentEpsgCode) {
                stringHandler.displayErrorMsg(this, "Unsupported EPSG Code [" + selectedItem.toString() + "]");
                clearEpsgChoice();
                return;
            }
            
            // retrieve the EPSG code data for the current selected EPSG code
            epsgCodeData = epsgCodesAllData.get(currentEpsgCode);
            
            // Set values
            // System.out.println("VALID VALUE '" + selectedItem + "'");
            
            datumChanged = true;
            long datumIndex = jniDatumLibrary.getDatumIndex(epsgCodeData.get("Datum"));
            datumComboBox.setSelectedIndex((int)datumIndex);
              
            heightChanged = true;
            projectionChanged = true;            
            final String coordSys = epsgCodeData.get("Coord_Sys"); 
            projectionComboBox.setSelectedItem(coordSys);

            // based on the coordinate system, set the associated parameters.
            switch (coordSys) {
              case "Universal Transverse Mercator (UTM)":
                  zoneTextField.setText(epsgCodeData.get("Zone (1-60)"));

                  if ((epsgCodeData.get("Override")).equals("Off")){
                      override = 0;
                  } else {
                      override = 1;
                  }

                  setHemiRadioButton(epsgCodeData.get("Hemisphere"));
                  break;
              case "Mercator (Standard Parallel)":
                  _3ParamFieldsRow1TextFieldA.setText(epsgCodeData.get("Central Meridian"));
                  _3ParamFieldsRow1TextFieldB.setText(epsgCodeData.get("Standard Parallel"));
                  _4ParamFieldsRow2TextFieldB.setText(epsgCodeData.get("False Easting (m)"));
                  _4ParamFieldsRow2TextFieldC.setText(epsgCodeData.get("False Northing (m)"));   
                  break;
              case "Geodetic":
                  heightChanged = true;
                  setGeodeticHeightType(epsgCodeData.get("Height Type"));
                  break;
              case "Universal Polar Stereographic (UPS)":
                  setHemiRadioButton(epsgCodeData.get("Hemisphere"));
                  break;
              case "Geocentric":
                  break;
              case "Web Mercator (S)":
                  break;
              default:
                  stringHandler.displayErrorMsg(this, "GeoTrans does not currently support this coordinate system [" + coordSys + "] for EPSG codes");
            }
        } catch (final Exception e) {
            currentEpsgCode = null;
            stringHandler.displayErrorMsg(this, "Error updating fields with EPSG values: " + e.toString());
            e.printStackTrace();
        }
    }

    // This method will clear the epsgCombobox
    void clearEpsgChoice () {
        epsgCombobox.clearSelection();
        currentEpsgCode = null;
    }
    
    private void heightComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightComboBoxActionPerformed    
        if (isEpsgCodeActive() && heightChanged) {
            heightChanged = false;
        } else if (isEpsgCodeActive() && !heightChanged) {
            clearEpsgChoice();
        }
         
         heightType = heightComboBox.getSelectedIndex();

         //Reindex the height type since "MSL-EGM2008-2.5M-BCS Height"
         //is third in the list (index 2) on the GUI but is still index 7
         //in the HeightType enum. It is still index 7 in the HeightType enum to
         //prevent API changes in the C++ code for the release.
         if (heightType == 2)
         {
             heightType = 7;
         }
         else if (heightType > 1)
         {
             heightType--;
         }

         if( state != ConversionState.FILE )
         {
           if(heightComboBox.getSelectedIndex() == HeightType.NO_HEIGHT)
              setHeightFieldEditable(false);
           else
              setHeightFieldEditable(true);
         }
    }//GEN-LAST:event_heightComboBoxActionPerformed
   
      
    private void epsgAttributeChangeActionPerformed(java.awt.event.ActionEvent evt) {
        clearEpsgChoice();   
    }

  
    private void zoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoneActionPerformed
        clearEpsgChoice();   

        if (zoneRadioButton.isSelected() == true)
          override = 1;
        else if (zoneRadioButton.isSelected() == false)
          override = 0;
        return;
    }//GEN-LAST:event_zoneActionPerformed

    private void projectionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectionComboBoxActionPerformed
        if (isEpsgCodeActive() && projectionChanged) {
            projectionChanged = false;
        } else if (isEpsgCodeActive() && !projectionChanged) {
            clearEpsgChoice();
        }
        
        projectionType = CoordinateType.index((java.lang.String)projectionComboBox.getSelectedItem());
        createMasterPanel();
        if (state != ConversionState.FILE)
        {
            coordPanel.createCoordPanel(projectionType);
            if (parent!= null)
                parent.checkValidConversion();
        }
        if (parent != null) {
          parent.check3DConversion();
        }
    }//GEN-LAST:event_projectionComboBoxActionPerformed

    private void datumComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if (isEpsgCodeActive() && datumChanged) {
            datumChanged = false;
        } else if (isEpsgCodeActive() && !datumChanged) {
            clearEpsgChoice();
        }  
        
        try {
            datumIndex = datumComboBox.getSelectedIndex();
            java.lang.String datumEllipsoidCode = jniDatumLibrary.getDatumInfo(datumIndex).getDatumEllipsoidCode();
            long inputEllipsoidIndex = jniEllipsoidLibrary.getEllipsoidIndex(datumEllipsoidCode);
            java.lang.String inputEllipsoidName = jniEllipsoidLibrary.getEllipsoidInfo(inputEllipsoidIndex).getName();
            ellipsoidTextField.setText(datumEllipsoidCode + ":  " +  inputEllipsoidName);
            ellipsoidTextField.setCaretPosition(0);
            
            if (parent != null) {
                parent.checkValidConversion();
            }
        } catch(CoordinateConversionException e){
            stringHandler.displayErrorMsg(this, e.getMessage());
        }
    }


    private void initFileInputPanel() throws CoordinateConversionException
    {
        datumComboBox.setVisible(false);
        projectionComboBox.setVisible(false);

    ///    try
        {
          datumIndex = jniDatumLibrary.getDatumIndex(datumCode);
          Info datumInfo = jniDatumLibrary.getDatumInfo(datumIndex);
          String datumName = datumInfo.getName();
          datumTextField.setText(datumCode + ":  " + datumName);

          String datumEllipsoidCode = datumInfo.getDatumEllipsoidCode();
          long ellipsoidIndex = jniEllipsoidLibrary.getEllipsoidIndex(datumEllipsoidCode);
          String ellipsoidName = jniEllipsoidLibrary.getEllipsoidInfo(ellipsoidIndex).getName();
          ellipsoidTextField.setText(datumEllipsoidCode + ":  " + ellipsoidName);

            switch(projectionType)
            {
              case CoordinateType.ALBERS:
              case CoordinateType.LAMBERT_2:
              {
                  if(projectionType == CoordinateType.ALBERS)
                    inputProjectionLabel.setText("Albers Equal Area Conic Projection");
                  else
                    inputProjectionLabel.setText("Lambert Conformal Conic (2 Standard Parallel) Projection");

                  MapProjection6Parameters _parameters = (MapProjection6Parameters)coordinateSystemParameters;

                  setSixParams();
                  setSixParamsUnedit();
                  _4ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                  _4ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(_parameters.getOriginLatitude()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                  _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(_parameters.getStandardParallel1()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                  _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(_parameters.getStandardParallel2()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                  _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                  _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));

                  break;
              }
              case CoordinateType.ECKERT4:
              case CoordinateType.ECKERT6:
              case CoordinateType.MILLER:
              case CoordinateType.MOLLWEIDE:
              case CoordinateType.SINUSOIDAL:
              case CoordinateType.GRINTEN:
              {
                switch(projectionType)
                {
                  case CoordinateType.ECKERT4:
                    inputProjectionLabel.setText("Eckert IV Projection");
                    break;
                  case CoordinateType.ECKERT6:
                    inputProjectionLabel.setText("Eckert VI Projection");
                    break;
                  case CoordinateType.MILLER:
                    inputProjectionLabel.setText("Miller Cylindrical Projection");
                    break;
                  case CoordinateType.MOLLWEIDE:
                    inputProjectionLabel.setText("Mollweide Projection");
                    break;
                  case CoordinateType.SINUSOIDAL:
                    inputProjectionLabel.setText("Sinusoidal Projection");
                    break;
                  case CoordinateType.GRINTEN:
                    inputProjectionLabel.setText("Van der Grinten Projection");
                    break;
                }

                  MapProjection3Parameters _parameters = (MapProjection3Parameters)coordinateSystemParameters;

                  setThreeParams();
                  setThreeParamsUnedit();
                  _3ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                  _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                  _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));
                  break;
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
                switch(projectionType)
                {
                  case CoordinateType.AZIMUTHAL:
                    inputProjectionLabel.setText("Azimuthal Equidistant Projection");
                    break;
                  case CoordinateType.BONNE:
                    inputProjectionLabel.setText("Bonne Projection");
                    break;
                  case CoordinateType.CASSINI:
                    inputProjectionLabel.setText("Cassini Projection");
                    break;
                  case CoordinateType.CYLEQA:
                    inputProjectionLabel.setText("Cylindrical Equal Area Projection");
                    break;
                  case CoordinateType.GNOMONIC:
                    inputProjectionLabel.setText("Gnomonic Projection");
                    break;
                  case CoordinateType.ORTHOGRAPHIC:
                    inputProjectionLabel.setText("Orthographic Projection");
                    break;
                  case CoordinateType.POLYCONIC:
                    inputProjectionLabel.setText("Polyconic Projection");
                    break;
                  case CoordinateType.STEREOGRAPHIC:
                    inputProjectionLabel.setText("Stereographic Projection");
                    break;
                }

                MapProjection4Parameters _parameters = (MapProjection4Parameters)coordinateSystemParameters;

                setFourParams();
                setFourParamsUnedit();
                _4ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(_parameters.getOriginLatitude()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));

                break;
              }
              case CoordinateType.LAMBERT_1:
              {
                inputProjectionLabel.setText("Lambert Conformal Conic (1 Standard Parallel) Projection");

                MapProjection5Parameters _parameters = (MapProjection5Parameters)coordinateSystemParameters;

                setFiveParams();
                _3ParamFieldsRow1LabelB.setText("Origin Latitude:");
                setFiveParamsUnedit();
                _3ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(_parameters.getOriginLatitude()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));

                stringHandler.setNumberFormat(_3ParamFieldsRow1TextFieldC, _parameters.getScaleFactor(), 5);

                _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));
                break;
              }
              case CoordinateType.TRCYLEQA:
              case CoordinateType.TRANMERC:
              {
                switch(projectionType)
                {
                  case CoordinateType.TRCYLEQA:
                    inputProjectionLabel.setText("Transverse Cylindrical Equal Area Projection");
                    break;
                  case CoordinateType.TRANMERC:
                    inputProjectionLabel.setText("Transverse Mercator Projection");
                    break;
                }

                MapProjection5Parameters _parameters = (MapProjection5Parameters)coordinateSystemParameters;

                setFiveParams();
                setFiveParamsUnedit();
                _3ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(_parameters.getOriginLatitude()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));

                stringHandler.setNumberFormat(_3ParamFieldsRow1TextFieldC, _parameters.getScaleFactor(), 5);

                _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));
                break;
              }
              case CoordinateType.BNG:
              case CoordinateType.F16GRS:
              case CoordinateType.GARS:
              case CoordinateType.GEOCENTRIC:
              case CoordinateType.GEOREF:
              case CoordinateType.MGRS:
              case CoordinateType.NZMG:
              case CoordinateType.UPS:
              case CoordinateType.USNG:
              case CoordinateType.UTM:
              case CoordinateType.WEBMERCATOR:
              case CoordinateType.SPHERICAL:
              {
                switch(projectionType)
                {
                  case CoordinateType.BNG:
                    inputProjectionLabel.setText("British National Grid Coordinates");
                    break;
                  case CoordinateType.F16GRS:
                    break;
                  case CoordinateType.GARS:
                     inputProjectionLabel.setText("GARS Coordinates");
                    break;
                  case CoordinateType.GEOCENTRIC:
                    inputProjectionLabel.setText("Geocentric Coordinates");
                    break;
                  case CoordinateType.GEOREF:
                    inputProjectionLabel.setText("GEOREF Coordinates");
                    break;
                  case CoordinateType.MGRS:
                    inputProjectionLabel.setText("MGRS Coordinates");
                    break;
                  case CoordinateType.NZMG:
                    inputProjectionLabel.setText("New Zealand Map Grid Projection");
                    break;
                  case CoordinateType.UPS:
                      inputProjectionLabel.setText("Universal Polar Stereographic (UPS) Projection");
                      break;
                  case CoordinateType.USNG:
                    inputProjectionLabel.setText("USNG Coordinates");
                    break;
                  case CoordinateType.UTM:
                      inputProjectionLabel.setText("Universal Transverse Mercator (UTM) Projection");
                      break;
                  case CoordinateType.WEBMERCATOR:
                    inputProjectionLabel.setText("Web Mercator Projection");
                    break;
                  case CoordinateType.SPHERICAL:
                    inputProjectionLabel.setText("Geocentric Spherical");
                    break;
                }

                hideParams();
                break;
              }
              case CoordinateType.EQDCYL:
              {
                  inputProjectionLabel.setText("Equidistant Cylindrical");

                  EquidistantCylindricalParameters _parameters = (EquidistantCylindricalParameters)coordinateSystemParameters;

                  setFourParams();
                  _4ParamFieldsRow1LabelC.setText("Standard Parallel:");
                  setFourParamsUnedit();
                  _4ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                  _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(_parameters.getStandardParallel()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                  _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                  _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));
                  break;
              }
                case CoordinateType.GEODETIC:
                {
                    inputProjectionLabel.setText("Geodetic Coordinates");

                    GeodeticParameters _parameters = (GeodeticParameters)coordinateSystemParameters;

                    hideParams();
                    heightLabelPanel.setVisible(true);
                    paramFieldsRow1LayeredPane.moveToFront(heightLabelPanel);
                    int height_Type = _parameters.getHeightType();
                    if (height_Type == HeightType.MSL_EGM96_15M_BL_HEIGHT)
                        heightLabel.setText("MSL-EGM96-15M-BL Height");
                    else if (height_Type == HeightType.MSL_EGM96_VG_NS_HEIGHT)
                        heightLabel.setText("MSL-EGM96-VG-NS Height");
                    else if (height_Type == HeightType.MSL_EGM84_10D_BL_HEIGHT)
                        heightLabel.setText("MSL-EGM84-10D-BL Height");
                    else if (height_Type == HeightType.MSL_EGM84_10D_NS_HEIGHT)
                        heightLabel.setText("MSL-EGM84-10D-NS Height");
                    else if (height_Type == HeightType.MSL_EGM84_30M_BL_HEIGHT)
                        heightLabel.setText("MSL-EGM84-30M-BL Height");
                    else if (height_Type == HeightType.MSL_EGM2008_TWOPOINTFIVEM_BCS_HEIGHT)
                        heightLabel.setText("MSL-EGM2008-2.5M-BCS Height");
                    else if (height_Type == HeightType.NO_HEIGHT)
                        heightLabel.setText("No Height");
                    else
                        heightLabel.setText("Ellipsoid Height");
                   break;
                }
                case CoordinateType.LOCCART:
                case CoordinateType.LOCSPHER:
                {
                    switch(projectionType)
                    {
                        case CoordinateType.LOCCART:
                            inputProjectionLabel.setText("Local Cartesian Coordinates");
                            break;
                        case CoordinateType.LOCSPHER:
                            inputProjectionLabel.setText("Local Spherical Coordinates");
                            break;
                    }
                    LocalCartesianParameters _parameters = (LocalCartesianParameters)coordinateSystemParameters;

                    setLCFourParams();
                    setLCFourParamsUnedit();
                    _3ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(_parameters.getLongitude()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(_parameters.getLatitude()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    _3ParamFieldsRow1TextFieldC.setText(stringHandler.meterToString(_parameters.getHeight()));
                    _3ParamFieldsRow2TextFieldB.setText(stringHandler.longitudeToString(_parameters.getOrientation()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    break;
                }
                case CoordinateType.MERCATOR_SP:
                {
                  inputProjectionLabel.setText("Mercator (Standard Parallel) Projection");

                  MercatorStandardParallelParameters _parameters = (MercatorStandardParallelParameters)coordinateSystemParameters;

                  setFiveParams();
                  _3ParamFieldsRow1LabelB.setText("Standard Parallel:");
                  setFiveParamsUnedit();
                  _3ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                  _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(_parameters.getStandardParallel()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));

                  stringHandler.setNumberFormat(_3ParamFieldsRow1TextFieldC, _parameters.getScaleFactor(), 5);

                  _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                  _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));

                  break;
                }
                case CoordinateType.MERCATOR_SF:
                {
                  inputProjectionLabel.setText("Mercator (Scale Factor) Projection");

                  MercatorScaleFactorParameters _parameters = (MercatorScaleFactorParameters)coordinateSystemParameters;

                  setFourParams();
                  _4ParamFieldsRow1LabelC.setText("Scale Factor:");
                  setFourParamsUnedit();
                  _4ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));

                  stringHandler.setNumberFormat(_4ParamFieldsRow1TextFieldC, _parameters.getScaleFactor(), 5);

                  _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                  _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));

                  break;
                }
                case CoordinateType.NEYS:
                {
                    inputProjectionLabel.setText("Ney's (Modified Lambert Conformal Conic) Projection");

                    boolean northHemi = true;
                    NeysParameters _parameters = (NeysParameters)coordinateSystemParameters;

                    setSixParams();
                    setSixParamsUnedit();
                    double olat = _parameters.getOriginLatitude();
                    if (olat < 0)
                        northHemi = false;
                    _4ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    _4ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(olat*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    if (northHemi)
                        _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(_parameters.getStandardParallel1()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    else
                        _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(_parameters.getStandardParallel1()*-Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    /* std parallel 2 = 89 59 59.0 */
                    if (northHemi)
                        _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(89.99944444444444, useNSEW, useMinutes, useSeconds));
                    else
                        _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(-89.99944444444444, useNSEW, useMinutes, useSeconds));
                    /* std parallel 2 = 89 59 59.0
                    if (northHemi)
                        _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(89.99972222222222, useNSEW, useMinutes, useSeconds));
                    else
                        _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(-89.99972222222222, useNSEW, useMinutes, useSeconds));*/

                    _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                    _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));
                    break;
                }
                case CoordinateType.OMERC:
                {
                    inputProjectionLabel.setText("Oblique Mercator Projection");

                    ObliqueMercatorParameters _parameters = (ObliqueMercatorParameters)coordinateSystemParameters;

                    setEightParams();
                    setEightParamsUnedit();
                    _4ParamFieldsRow1TextFieldA.setText(stringHandler.latitudeToString(_parameters.getOriginLatitude()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));

                    stringHandler.setNumberFormat(_4ParamFieldsRow1TextFieldB, _parameters.getScaleFactor(), 5);

                    _4ParamFieldsRow1TextFieldC.setText(stringHandler.longitudeToString(_parameters.getLongitude1()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(_parameters.getLatitude1()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    _4ParamFieldsRow2TextFieldA.setText(stringHandler.longitudeToString(_parameters.getLongitude2()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    _4ParamFieldsRow2TextFieldB.setText(stringHandler.latitudeToString(_parameters.getLatitude2()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                    _4ParamFieldsRow2TextFieldD.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));
                    break;
                }
                case CoordinateType.POLARSTEREO_SP:
                {
                    inputProjectionLabel.setText("Polar Stereographic (Standard Parallel) Projection");

                    PolarStereographicStandardParallelParameters _parameters = (PolarStereographicStandardParallelParameters)coordinateSystemParameters;

                    setFourParams();
                    _4ParamFieldsRow1LabelB.setText("Central Meridian:");
                    _4ParamFieldsRow1LabelC.setText("Standard Parallel:");
                    setFourParamsUnedit();
                    _4ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(_parameters.getStandardParallel()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                    _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));
                    break;
                }
                case CoordinateType.POLARSTEREO_SF:
                {
                    inputProjectionLabel.setText("Polar Stereographic (Scale Factor) Projection");

                    PolarStereographicScaleFactorParameters _parameters = (PolarStereographicScaleFactorParameters)coordinateSystemParameters;

                    setPS_SFFourParams();
                   /// setFourParams();
                    _3ParamFieldsRow1PS_SFLabelA.setText("Central Meridian:");
                    _3ParamFieldsRow1PS_SFLabelB.setText("Scale Factor:");
                    setPS_SFFourParamsUnedit();
                    _3ParamFieldsRow1PS_SFTextFieldA.setText(stringHandler.longitudeToString(_parameters.getCentralMeridian()*Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
                    stringHandler.setNumberFormat(_3ParamFieldsRow1PS_SFTextFieldB, _parameters.getScaleFactor(), 5);
                    char hemisphere = _parameters.getHemisphere();

                    if (hemisphere == 'N')
                    {
                      row1NHemiRadioButton.setSelected(true);
                      row1SHemiRadioButton.setSelected(false);
                    }
                    else
                    {
                      row1NHemiRadioButton.setSelected(false);
                      row1SHemiRadioButton.setSelected(true);
                    }
                    _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(_parameters.getFalseEasting()));
                    _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(_parameters.getFalseNorthing()));
                    break;
                }
                default:
                    break;
            }
        }
    /*    catch(CoordinateConversionException e)
        {
            stringHandler.displayErrorMsg(this, e.getMessage());
        }*/
    }


  private void initMasterPanel()
  {
    try
    {
      datumTextField.setVisible(false);
      inputProjectionLabel.setVisible(false);

      fillHeightList();
      datumList = new FillList(jniDatumLibrary, jniEllipsoidLibrary, datumComboBox, ListType.DATUM);
      fillProjectionList();

      if(state == ConversionState.FILE)
      {
        datumIndex = 0;
        projectionType = CoordinateType.GEODETIC;
      }
      else
      {
        datumIndex = jniDatumLibrary.getDatumIndex(jniCoordinateConversionService.getDatum(direction));
        projectionType = jniCoordinateConversionService.getCoordinateSystem(direction).getCoordinateType();
      }

      datumComboBox.setSelectedIndex((int)datumIndex);
      projectionComboBox.setSelectedItem(CoordinateType.name(projectionType));

      if (state == ConversionState.INTERACTIVE)
        coordPanel.createCoordPanel(projectionType);

      createButtonGroups();
      createMasterPanel();
    }
    catch (CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }
  }

    private void createMasterPanel()
    {
        setFormat();
        switch (projectionType)
        {
            case CoordinateType.GEODETIC:
            { // height type
                hideParams();
                setHeight();
                if ((state == ConversionState.FILE) && /*(!createHeader) &&*/ (direction == SourceOrTarget.TARGET))
                {
                  geodeticCoordinateOrderPanel.setVisible(true);
                  latitudeLongitudeRadioButton.setSelected(true);

                }
                break;
            }
            case CoordinateType.BNG:
            case CoordinateType.F16GRS:
            case CoordinateType.GARS:
            case CoordinateType.GEOCENTRIC:
            case CoordinateType.GEOREF:
            case CoordinateType.MGRS:
            case CoordinateType.USNG:
            case CoordinateType.NZMG:
            case CoordinateType.WEBMERCATOR:
            case CoordinateType.SPHERICAL:
            {
                hideParams();
                break;
            }
            case CoordinateType.UPS:
            {
                if (state == ConversionState.INTERACTIVE)
                {
                    setZone_Hemi();
                    zoneHemiPanel.setBounds(90, 0, 462, 55);
                    zoneBoxPanel.setVisible(false);
                }
                else
                    hideParams();
                if (!Platform.isJavaV1_3)
                {
                    if (Platform.isUnix)
                    {
                    if (currLookAndFeel.equals("Java"))
                        hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                           javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10), java.awt.Color.black));
                    else
                        hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                           javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), java.awt.Color.black));
                    }
                    else
                    {
                        if (currLookAndFeel.equals("Java"))
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                                   javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));
                        else
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                                   javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12)));
                    }
                }
                break;
            }
            case CoordinateType.UTM:
            {
                // Hemisphere  // Zone
                setZone_Hemi();
                zoneHemiPanel.setBounds(0, 0, 462, 55);
                if (state == ConversionState.INTERACTIVE)
                    hemiBoxPanel.setVisible(true);
                else
                    hemiBoxPanel.setVisible(false);

                zoneBoxPanel.setVisible(true);
                if (!Platform.isJavaV1_3)
                {
                    if (Platform.isUnix)
                    {
                        if (currLookAndFeel.equals("Java"))
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                               javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10), java.awt.Color.black));
                        else
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                               javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), java.awt.Color.black));
                    }
                    else
                    {
                        if (currLookAndFeel.equals("Java"))
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                                   javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));
                        else
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                                   javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12)));
                    }
                }
                break;
            }
            case CoordinateType.LOCCART:
            case CoordinateType.LOCSPHER:
            {
                setLCFourParams();
                _3ParamFieldsRow1TextFieldC.setEditable(true);
                break;
            }
            case CoordinateType.OMERC:
            {
                setEightParams();
                break;
            }
            case CoordinateType.ALBERS:
            case CoordinateType.LAMBERT_2:
            {
                setSixParams();
                break;
            }
            case CoordinateType.NEYS:
            {
                setSixParams();
                _4ParamFieldsRow1Panel.setVisible(false);
                neysParamsRow1Panel.setVisible(true);
                neys71RadioButton.setSelected(true);
                try
                {
                    centralMeridianNeysParamsTextField.setText(stringHandler.longitudeToString(0, useNSEW, useMinutes, useSeconds));
                    originLatitudeNeysParamsTextField.setText(stringHandler.latitudeToString(80, useNSEW, useMinutes, useSeconds));
                    //2nd Std Parallel - set to read only at 89 59 58.0
                    stdParallel2NeysParamsTextField.setText(stringHandler.latitudeToString(89.99944444444444, false, useMinutes, useSeconds));
                    //2nd Std Parallel - set to read only at 89 59 59.0
               /////     stdParallel2NeysParamsTextField.setText(stringHandler.latitudeToString(89.99972222222222, false, useMinutes, useSeconds));
                }
                catch(CoordinateConversionException e)
                {
                    stringHandler.displayErrorMsg(this, e.getMessage());
                }

                if (!Platform.isJavaV1_3)
                {
                    if (Platform.isUnix)
                    {
                        if (currLookAndFeel.equals("Java"))
                            stdParallel1NeysParamsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Std. Parallel 1:", javax.swing.border.TitledBorder.CENTER,
                                                      javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10), java.awt.Color.black));
                        else
                            stdParallel1NeysParamsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Std. Parallel 1:", javax.swing.border.TitledBorder.CENTER,
                                                      javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), java.awt.Color.black));
                    }
                    else
                    {
                        if (currLookAndFeel.equals("Java"))
                            stdParallel1NeysParamsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Std. Parallel 1:", javax.swing.border.TitledBorder.CENTER,
                                                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));
                        else
                            stdParallel1NeysParamsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Std. Parallel 1:", javax.swing.border.TitledBorder.CENTER,
                                                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12)));
                    }
                }
                break;
            }
            case CoordinateType.AZIMUTHAL:
            case CoordinateType.CASSINI:
            case CoordinateType.CYLEQA:
            case CoordinateType.GNOMONIC:
            case CoordinateType.ORTHOGRAPHIC:
            case CoordinateType.POLYCONIC:
            case CoordinateType.STEREOGRAPHIC:
            {
                setFourParams();
                break;
            }
            case CoordinateType.BONNE:
            {
                setFourParams();
                try
                {
                    _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(45, useNSEW, useMinutes, useSeconds));
                }
                catch(CoordinateConversionException e)
                {
                    stringHandler.displayErrorMsg(this, e.getMessage());
                }
                break;
            }
            case CoordinateType.EQDCYL:
            {
                setFourParams();
                _4ParamFieldsRow1LabelC.setText("Standard Parallel:");
                break;
            }
            case CoordinateType.POLARSTEREO_SP:
            {
                setFourParams();
                try
                {
                    _4ParamFieldsRow1LabelB.setText("Central Meridian:");
                    _4ParamFieldsRow1LabelC.setText("Standard Parallel:");
                    _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(90, useNSEW, useMinutes, useSeconds));
                }
                catch(CoordinateConversionException e)
                {
                    stringHandler.displayErrorMsg(this, e.getMessage());
                }
                break;
            }
            case CoordinateType.POLARSTEREO_SF:
            {
                setPS_SFFourParams();
             //   _4ParamFieldsRow1LabelB.setText("Central Meridian:");
             //   _4ParamFieldsRow1LabelC.setText("Scale Factor:");
             //   _4ParamFieldsRow1TextFieldC.setText( "1.00000" );
                break;
            }
            case CoordinateType.ECKERT4:
            case CoordinateType.ECKERT6:
            case CoordinateType.MILLER:
            case CoordinateType.MOLLWEIDE:
            case CoordinateType.SINUSOIDAL:
            case CoordinateType.GRINTEN:
            {
                setThreeParams();
                break;
            }
            case CoordinateType.MERCATOR_SP:
            {
                setFiveParams();
                _3ParamFieldsRow1LabelB.setText("Standard Parallel:");
                _3ParamFieldsRow1TextFieldC.setEditable(false);
                break;
            }
            case CoordinateType.MERCATOR_SF:
            {
                setFourParams();
                _4ParamFieldsRow1LabelC.setText("Scale Factor:");
                _4ParamFieldsRow1TextFieldC.setText( "1.00000" );
                break;
            }
            case CoordinateType.LAMBERT_1:
            {
                setFiveParams();
                _3ParamFieldsRow1LabelB.setText("Origin Latitude:");
                _3ParamFieldsRow1TextFieldC.setEditable(true);
                try
                {
                    _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(45, useNSEW, useMinutes, useSeconds));
                }
                catch(CoordinateConversionException e)
                {
                    stringHandler.displayErrorMsg(this, e.getMessage());
                }
                break;
            }
            case CoordinateType.TRCYLEQA:
            case CoordinateType.TRANMERC:
            {
                setFiveParams();
                _3ParamFieldsRow1TextFieldC.setEditable(true);
                break;
            }
            default:
            {
                break;
            }
        }
    }

    // Get the parameters
    public CoordinateSystemParameters getParameters() throws CoordinateConversionException
    {
      switch(projectionType)
      {
         case CoordinateType.ECKERT4:
         case CoordinateType.ECKERT6:
         case CoordinateType.MILLER:
         case CoordinateType.MOLLWEIDE:
         case CoordinateType.SINUSOIDAL:
         case CoordinateType.GRINTEN:
          {
              return new MapProjection3Parameters(projectionType,
                                                    stringHandler.stringToLongitude(_3ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Central Meridian"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
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
              return new MapProjection4Parameters(projectionType,
                                                   stringHandler.stringToLongitude(_4ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Central Meridian"),
                                                   stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Origin Latitude"),
                                                   stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                   stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
          case CoordinateType.LAMBERT_1:
          {
              return new MapProjection5Parameters(projectionType,
                                                    stringHandler.stringToLongitude(_3ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Central Meridian"),
                                                    stringHandler.stringToLatitude(_3ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Origin Latitude"),
                                                    stringHandler.stringToDouble(_3ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Scale Factor"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
          case CoordinateType.TRCYLEQA:
          case CoordinateType.TRANMERC:
          {
              return new MapProjection5Parameters(projectionType,
                                                    stringHandler.stringToLongitude(_3ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Central Meridian"),
                                                    stringHandler.stringToLatitude(_3ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Origin Latitude"),
                                                    stringHandler.stringToDouble(_3ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Scale Factor"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
          case CoordinateType.ALBERS:
          case CoordinateType.LAMBERT_2:
          {
              return new MapProjection6Parameters(projectionType,
                                                   stringHandler.stringToLongitude(_4ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Central Meridian"),
                                                   stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Origin Latitude"),
                                                   stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldC.getText().trim(), "Invalid 1st Standard Parallel"),
                                                   stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldD.getText().trim(), "Invalid 2nd Standard Parallel"),
                                                   stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                   stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
         case CoordinateType.EQDCYL:
          {
              return new EquidistantCylindricalParameters(projectionType,
                                                           stringHandler.stringToLongitude(_4ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Central Meridian"),
                                                           stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Standard Parallel"),
                                                           stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                           stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
          case CoordinateType.GEODETIC:
          {
              return new GeodeticParameters(projectionType, heightType);
          }
          case CoordinateType.LOCCART:
          case CoordinateType.LOCSPHER:
          {
              return new LocalCartesianParameters(projectionType,
                                                    stringHandler.stringToLongitude(_3ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Origin Longitude"),
                                                    stringHandler.stringToLatitude(_3ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Origin Latitude"),
                                                    stringHandler.stringToDouble(_3ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Origin Height"),
                                                    stringHandler.stringToLongitude(_3ParamFieldsRow2TextFieldB.getText().trim(), "Invalid Orientation"));
          }
          case CoordinateType.MERCATOR_SP:
          {
              return new MercatorStandardParallelParameters(projectionType,
                                                    stringHandler.stringToLongitude(_3ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Central Meridian"),
                                                    stringHandler.stringToLatitude(_3ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Standard Parallel"),
                                                    stringHandler.stringToDouble(_3ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Scale Factor"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
          case CoordinateType.MERCATOR_SF:
          {
              return new MercatorScaleFactorParameters(projectionType,
                                                    stringHandler.stringToLongitude(_4ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Central Meridian"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Scale Factor"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                    stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
          case CoordinateType.NEYS:
          {
              double std_par_1 = 71.0;

              if (neys71RadioButton.isSelected() == true)
                std_par_1 = 71.0;
              else if (neys74RadioButton.isSelected() == true)
                std_par_1 = 74.0;

              return new NeysParameters(projectionType,
                                          stringHandler.stringToLongitude(centralMeridianNeysParamsTextField.getText().trim(), "Invalid Central Meridian"),
                                          stringHandler.stringToLatitude(originLatitudeNeysParamsTextField.getText().trim(), "Invalid Origin Latitude"),
                                          std_par_1*Constants.PI_OVER_180,
                                          stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                          stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
          case CoordinateType.OMERC:
          {
              return new ObliqueMercatorParameters(projectionType,
                                                     stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Origin Latitude"),
                                                     stringHandler.stringToLongitude(_4ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Longitude 1"),
                                                     stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldD.getText().trim(), "Invalid Latitude 1"),
                                                     stringHandler.stringToLongitude(_4ParamFieldsRow2TextFieldA.getText().trim(), "Invalid Longitude 2"),
                                                     stringHandler.stringToLatitude(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid Latitude 2"),
                                                     stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Easting"),
                                                     stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldD.getText().trim(), "Invalid False Northing"),
                                                     stringHandler.stringToDouble(_4ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Scale Factor"));
          }
          case CoordinateType.POLARSTEREO_SP:
          {
              return new PolarStereographicStandardParallelParameters(projectionType,
                                                        stringHandler.stringToLongitude(_4ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Central Meridian"),
                                                        stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Standard Parallel"),
                                                        stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                        stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
          case CoordinateType.POLARSTEREO_SF:
          {
              char hemisphere = 'N';

              if (row1NHemiRadioButton.isSelected() == true)
                hemisphere = 'N';
              else if (row1SHemiRadioButton.isSelected() == true)
                hemisphere = 'S';
              return new PolarStereographicScaleFactorParameters(projectionType,
                                                        stringHandler.stringToLongitude(_3ParamFieldsRow1PS_SFTextFieldA.getText().trim(), "Invalid Central Meridian"),
                                                        stringHandler.stringToDouble(_3ParamFieldsRow1PS_SFTextFieldB.getText().trim(), "Invalid Scale Factor"),
                                                        hemisphere,
                                                        stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting"),
                                                        stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing"));
          }
          case CoordinateType.UTM:
          {
              UTMParameters parameters;
              if(zoneRadioButton.isSelected())
              {
                  long zone = stringHandler.stringToInt(zoneTextField.getText().trim(), "Invalid zone");
                  if ((zone < 1) || (zone > 60))
                      throw new CoordinateConversionException("Zone out of range (1-60)");
                  else
                      return new UTMParameters(projectionType, stringHandler.stringToInt(zoneTextField.getText().trim(), "Invalid zone"), override);
              }
              else
                  return new UTMParameters(projectionType, 0, override);
          }
          case CoordinateType.BNG:
            return new CoordinateSystemParameters(CoordinateType.BNG);
          case CoordinateType.GARS:
            return new CoordinateSystemParameters(CoordinateType.GARS);
          case CoordinateType.GEOCENTRIC:
            return new CoordinateSystemParameters(CoordinateType.GEOCENTRIC);
          case CoordinateType.GEOREF:
            return new CoordinateSystemParameters(CoordinateType.GEOREF);
          case CoordinateType.F16GRS:
          case CoordinateType.MGRS:
            return new CoordinateSystemParameters(CoordinateType.MGRS);
          case CoordinateType.NZMG:
             return new CoordinateSystemParameters(CoordinateType.NZMG);
          case CoordinateType.UPS:
            return new CoordinateSystemParameters(CoordinateType.UPS);
          case CoordinateType.USNG:
            return new CoordinateSystemParameters(CoordinateType.USNG);
          case CoordinateType.WEBMERCATOR:
             return new CoordinateSystemParameters(CoordinateType.WEBMERCATOR);
          case CoordinateType.SPHERICAL:
            return new CoordinateSystemParameters(CoordinateType.SPHERICAL);
          default:
              throw new CoordinateConversionException("Invalid coordinate type");
      }
    }


  /*
   *  The function initTargetCoordinates creates a new instance of the
   *  target coordinates.
   *
   *  @param    projType    : Type of target coordinates to create
   */
  public CoordinateTuple initTargetCoordinates() throws CoordinateConversionException
  {
    return coordPanel.initTargetCoordinates(projectionType);
  }


  public void setCoordinates(CoordinateTuple _coordinates)
  {
    if(coordPanel.setCoordinates(state, direction, projectionType, _coordinates))
    {
      if(projectionType == CoordinateType.MERCATOR_SP)
      {
        try
        {
          MercatorStandardParallelParameters parameters = (MercatorStandardParallelParameters) jniCoordinateConversionService.getCoordinateSystem(direction);

          stringHandler.setNumberFormat(_3ParamFieldsRow1TextFieldC, parameters.getScaleFactor(), 5);

        }
        catch(CoordinateConversionException e)
        {
          stringHandler.displayErrorMsg(this, e.getMessage());
        }
      }
      else
      {
        if(projectionType == CoordinateType.UTM)
        {
          zoneTextField.setText(String.valueOf(coordPanel.getZone()));
          if(coordPanel.getHemisphere() == 'N')
          {
            nHemiRadioButton.setSelected(true);
          }
          else
          {
            sHemiRadioButton.setSelected(true);
          }
        }
        else
        {
          if(projectionType == CoordinateType.UPS)
          {
            if(coordPanel.getHemisphere() == 'N')
            {
              nHemiRadioButton.setSelected(true);
            }
            else
            {
              sHemiRadioButton.setSelected(true);
            }
          }
        }
      }
    }
  }


  public CoordinateTuple getCoordinates() throws CoordinateConversionException
  {
    if(state != ConversionState.FILE)
    {
      if(projectionType == CoordinateType.UTM)
      {
        coordPanel.setZone((int) stringHandler.stringToInt(zoneTextField.getText().trim(), "Invalid zone"));

        if(nHemiRadioButton.isSelected())
        {
          coordPanel.setHemisphere('N');
        }
        else
        {
          coordPanel.setHemisphere('S');
        }
      }
      else
      {
        if(projectionType == CoordinateType.UPS)
        {
          if(nHemiRadioButton.isSelected())
          {
            coordPanel.setHemisphere('N');
          }
          else
          {
            coordPanel.setHemisphere('S');
          }
        }
      }

      return coordPanel.getCoordinates(state, direction, projectionType);
    }

    throw new CoordinateConversionException("getCoordinates: Unable to get coordinates: Conversion state is file processing, not interactive");
  }


  public Accuracy getAccuracy()
  {
    return coordPanel.getAccuracy();
  }


  public void setCoordinateConversionService(JNICoordinateConversionService _jniCoordinateConversionService) throws CoordinateConversionException
  {
    try
    {
      jniCoordinateConversionService = _jniCoordinateConversionService;
      jniDatumLibrary = new JNIDatumLibrary(_jniCoordinateConversionService.getDatumLibrary());
      jniEllipsoidLibrary = new JNIEllipsoidLibrary(_jniCoordinateConversionService.getEllipsoidLibrary());
    }
    catch(CoordinateConversionException e)
    {
      throw new CoordinateConversionException("setCoordinateConversionService: " + e.getMessage());
    }
  }


    private void createButtonGroups()
    {
        javax.swing.ButtonGroup row1HemiGroup = new javax.swing.ButtonGroup();
        javax.swing.ButtonGroup hemiGroup = new javax.swing.ButtonGroup();
        javax.swing.ButtonGroup neysGroup = new javax.swing.ButtonGroup();
        javax.swing.ButtonGroup geodeticCoordinateOrderGroup = new javax.swing.ButtonGroup();

        row1HemiGroup.add(row1NHemiRadioButton);
        row1HemiGroup.add(row1SHemiRadioButton);

        hemiGroup.add(nHemiRadioButton);
        hemiGroup.add(sHemiRadioButton);

        neysGroup.add(neys71RadioButton);
        neysGroup.add(neys74RadioButton);

        geodeticCoordinateOrderGroup.add(latitudeLongitudeRadioButton);
        geodeticCoordinateOrderGroup.add(longitudeLatitudeRadioButton);
    }


    private void hideParams()
    {
        paramFieldsRow1LayeredPane.setMinimumSize(new java.awt.Dimension(460, 55));
        paramFieldsRow1LayeredPane.setMaximumSize(new java.awt.Dimension(460, 55));
        paramFieldsRow1LayeredPane.setPreferredSize(new java.awt.Dimension(460, 55));

        paramFieldsRow2LayeredPane.setMinimumSize(new java.awt.Dimension(460, 55));
        paramFieldsRow2LayeredPane.setMaximumSize(new java.awt.Dimension(460, 55));
        paramFieldsRow2LayeredPane.setPreferredSize(new java.awt.Dimension(460, 55));

        _3ParamFieldsRow1Panel.setBounds(0, 0, 462, 38);
        _3ParamFieldsRow1PS_SFPanel.setBounds(0, 0, 462, 38);
        _3ParamFieldsRow2Panel.setBounds(0, 0, 462, 38);

        _3ParamFieldsRow1Panel.setVisible(false);
        _3ParamFieldsRow1PS_SFPanel.setVisible(false);
        _4ParamFieldsRow1Panel.setVisible(false);
        _3ParamFieldsRow2Panel.setVisible(false);
        _4ParamFieldsRow2Panel.setVisible(false);
        neysParamsRow1Panel.setVisible(false);
        zoneHemiPanel.setVisible(false);
        heightPanel.setVisible(false);
        heightLabelPanel.setVisible(false);
        geodeticCoordinateOrderPanel.setVisible(false);
        row1HemiBoxPanel.setVisible(false);
    }

    private void setHeight()
    {
      hideParams();
      heightPanel.setVisible(true);
      paramFieldsRow2LayeredPane.moveToFront(heightPanel);

      if((state == ConversionState.FILE) && (!createHeader))
      {
        if((inputFileType != CoordinateType.GEODETIC) &&
           (inputFileType != CoordinateType.GEOCENTRIC) &&
           (inputFileType != CoordinateType.LOCCART) &&
           (inputFileType != CoordinateType.LOCSPHER) &&
           (inputFileType != CoordinateType.SPHERICAL))
        {
          heightChanged = true;
          selectNoHeightButton();
          enableHeightComboBox(false);
        }
      }
      else
      {
        //Change the index from C++ index to the Java index.
        if (heightType == 7)
        {
         heightType = 2;
        }
        else if ((heightType < 7) && ( heightType >= 2))
        {
         heightType++;
        }

        heightComboBox.setSelectedIndex(heightType);
        enableHeightComboBox(true);
      }
    }

    private void setZone_Hemi()
    {
        hideParams();
        zoneHemiPanel.setVisible(true);
        paramFieldsRow2LayeredPane.moveToFront(zoneHemiPanel);
        /* initialize hemisphere*/
        nHemiRadioButton.setSelected(true);
        /* initialize zone override*/
        zoneTextField.setText( "0" );
        zoneRadioButton.setSelected(false);
        override = 0;
    }

    private void setThreeParams()
    {
        setFormat();
        hideParams();
        _3ParamFieldsRow1Panel.setVisible(true);
        _4ParamFieldsRow2Panel.setVisible(true);

        paramFieldsRow1LayeredPane.setMinimumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow1LayeredPane.setMaximumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow1LayeredPane.setPreferredSize(new java.awt.Dimension(439, 55));

        _3ParamFieldsRow1Panel.setBounds(2, 2, 439, 37);

           // Hide Extra Fields
        _3ParamFieldsRow1LabelA.setVisible(false);
        _3ParamFieldsRow1LabelC.setVisible(false);
        _4ParamFieldsRow2LabelA.setVisible(false);
        _4ParamFieldsRow2LabelD.setVisible(false);
        _3ParamFieldsRow1TextFieldA.setVisible(false);
        _3ParamFieldsRow1TextFieldC.setVisible(false);
        _4ParamFieldsRow2TextFieldA.setVisible(false);
        _4ParamFieldsRow2TextFieldD.setVisible(false);

        _3ParamFieldsRow1LabelB.setText("Central Meridian:");
        _4ParamFieldsRow2LabelB.setText("False Easting (m):");
        _4ParamFieldsRow2LabelC.setText("False Northing (m):");
        try
        {
            _3ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(0, useNSEW, useMinutes, useSeconds));
        }
        catch(CoordinateConversionException e)
        {
            stringHandler.displayErrorMsg(this, e.getMessage());
        }
        _4ParamFieldsRow2TextFieldB.setText( "0" );
        _4ParamFieldsRow2TextFieldC.setText( "0" );
    } // end setThreeParams()

    private void setThreeParamsUnedit()
    {
        _3ParamFieldsRow1TextFieldB.setEditable(false);
        _4ParamFieldsRow2TextFieldB.setEditable(false);
        _4ParamFieldsRow2TextFieldC.setEditable(false);
    }

    private void setFourParams()
    {
        setFormat();
        hideParams();
        _4ParamFieldsRow1Panel.setVisible(true);
        _4ParamFieldsRow2Panel.setVisible(true);

        // Hide Extra Fields
        _4ParamFieldsRow1LabelA.setVisible(false);
        _4ParamFieldsRow1LabelD.setVisible(false);
        _4ParamFieldsRow2LabelA.setVisible(false);
        _4ParamFieldsRow2LabelD.setVisible(false);
        _4ParamFieldsRow1TextFieldA.setVisible(false);
        _4ParamFieldsRow1TextFieldD.setVisible(false);
        _4ParamFieldsRow2TextFieldA.setVisible(false);
        _4ParamFieldsRow2TextFieldD.setVisible(false);

        _4ParamFieldsRow1LabelB.setText("Central Meridian:");
        _4ParamFieldsRow1LabelC.setText("Origin Latitude:");
        _4ParamFieldsRow2LabelB.setText("False Easting (m):");
        _4ParamFieldsRow2LabelC.setText("False Northing (m):");
        try
        {
            _4ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(0, useNSEW, useMinutes, useSeconds));
            _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(0, useNSEW, useMinutes, useSeconds));
        }
        catch(CoordinateConversionException e)
        {
            stringHandler.displayErrorMsg(this, e.getMessage());
        }
        _4ParamFieldsRow2TextFieldB.setText( "0" );
        _4ParamFieldsRow2TextFieldC.setText( "0" );
    }  // end setFourParams()

    private void setFourParamsUnedit()
    {
        _4ParamFieldsRow1TextFieldB.setEditable(false);
        _4ParamFieldsRow1TextFieldC.setEditable(false);
        _4ParamFieldsRow2TextFieldB.setEditable(false);
        _4ParamFieldsRow2TextFieldC.setEditable(false);
    }

    private void setLCFourParams()
    {
        setFormat();
        hideParams();
        _3ParamFieldsRow1Panel.setVisible(true);
        _3ParamFieldsRow2Panel.setVisible(true);

        paramFieldsRow1LayeredPane.setMinimumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow1LayeredPane.setMaximumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow1LayeredPane.setPreferredSize(new java.awt.Dimension(439, 55));

        paramFieldsRow2LayeredPane.setMinimumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow2LayeredPane.setMaximumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow2LayeredPane.setPreferredSize(new java.awt.Dimension(439, 55));

        _3ParamFieldsRow1Panel.setBounds(2, 2, 439, 37);
        _3ParamFieldsRow2Panel.setBounds(2, 2, 439, 37);

           // Show Extra Fields
        _3ParamFieldsRow1LabelA.setVisible(true);
        _3ParamFieldsRow1LabelC.setVisible(true);
        _3ParamFieldsRow1TextFieldA.setVisible(true);
        _3ParamFieldsRow1TextFieldC.setVisible(true);
        // Hide Extra Fields
        _3ParamFieldsRow2LabelA.setVisible(false);
        _3ParamFieldsRow2LabelC.setVisible(false);
        _3ParamFieldsRow2TextFieldA.setVisible(false);
        _3ParamFieldsRow2TextFieldC.setVisible(false);

        _3ParamFieldsRow1LabelA.setText("Origin Longitude:");
        _3ParamFieldsRow1LabelB.setText("Origin Latitude:");
        _3ParamFieldsRow1LabelC.setText("Origin Height (m):");
        _3ParamFieldsRow2LabelB.setText("Orientation:");


        try
        {
            _3ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(0, useNSEW, useMinutes, useSeconds));
            _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(0, useNSEW, useMinutes, useSeconds));
            _3ParamFieldsRow1TextFieldC.setText( "0" );
            _3ParamFieldsRow2TextFieldB.setText(stringHandler.longitudeToString(0, useNSEW, useMinutes, useSeconds));
        }
        catch(CoordinateConversionException e)
        {
            stringHandler.displayErrorMsg(this, e.getMessage());
        }
    }

    private void setLCFourParamsUnedit()
    {
        _3ParamFieldsRow1TextFieldA.setEditable(false);
        _3ParamFieldsRow1TextFieldB.setEditable(false);
        _3ParamFieldsRow1TextFieldC.setEditable(false);
        _3ParamFieldsRow2TextFieldB.setEditable(false);
    }

    private void setPS_SFFourParams()
    {
        setFormat();
        hideParams();
        _3ParamFieldsRow1PS_SFPanel.setVisible(true);
        _4ParamFieldsRow2Panel.setVisible(true);

        paramFieldsRow1LayeredPane.setMinimumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow1LayeredPane.setMaximumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow1LayeredPane.setPreferredSize(new java.awt.Dimension(439, 55));

        _3ParamFieldsRow1PS_SFPanel.setBounds(2, 2, 439, 37);

           // Show Extra Fields
        _3ParamFieldsRow1PS_SFLabelA.setVisible(true);
        _3ParamFieldsRow1PS_SFTextFieldA.setVisible(true);
        // Hide Extra Fields
        _4ParamFieldsRow2LabelA.setVisible(false);
        _4ParamFieldsRow2LabelD.setVisible(false);
        _4ParamFieldsRow2TextFieldA.setVisible(false);
        _4ParamFieldsRow2TextFieldD.setVisible(false);

        _3ParamFieldsRow1PS_SFLabelA.setText("Central Meridian:");
        _3ParamFieldsRow1PS_SFLabelB.setText("Scale Factor:");
        _4ParamFieldsRow2LabelB.setText("False Easting (m):");
        _4ParamFieldsRow2LabelC.setText("False Northing (m):");
        try
        {
            _3ParamFieldsRow1PS_SFTextFieldA.setText(stringHandler.longitudeToString(0, useNSEW, useMinutes, useSeconds));
            _3ParamFieldsRow1PS_SFTextFieldB.setText("1.00000");
        }
        catch(CoordinateConversionException e)
        {
            stringHandler.displayErrorMsg(this, e.getMessage());
        }
        _4ParamFieldsRow2TextFieldB.setText( "0" );
        _4ParamFieldsRow2TextFieldC.setText( "0" );

        row1HemiBoxPanel.setVisible(true);
        paramFieldsRow2LayeredPane.moveToFront(row1HemiBoxPanel);
        /* initialize hemisphere*/
        row1NHemiRadioButton.setSelected(true);
    }

    private void setPS_SFFourParamsUnedit()
    {
        _3ParamFieldsRow1PS_SFTextFieldA.setEditable(false);
        _3ParamFieldsRow1PS_SFTextFieldB.setEditable(false);
        _4ParamFieldsRow2TextFieldB.setEditable(false);
        _4ParamFieldsRow2TextFieldC.setEditable(false);
        row1NHemiRadioButton.setEnabled(false);
        row1SHemiRadioButton.setEnabled(false);
    }

    private void setFiveParams()
    {
        setFormat();
        hideParams();
        _3ParamFieldsRow1Panel.setVisible(true);
        _4ParamFieldsRow2Panel.setVisible(true);

        paramFieldsRow1LayeredPane.setMinimumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow1LayeredPane.setMaximumSize(new java.awt.Dimension(439, 55));
        paramFieldsRow1LayeredPane.setPreferredSize(new java.awt.Dimension(439, 55));

        _3ParamFieldsRow1Panel.setBounds(2, 2, 439, 37);

           // Show Extra Fields
        _3ParamFieldsRow1LabelA.setVisible(true);
        _3ParamFieldsRow1LabelC.setVisible(true);
        _3ParamFieldsRow1TextFieldA.setVisible(true);
        _3ParamFieldsRow1TextFieldC.setVisible(true);

        // Hide Extra Fields
        _4ParamFieldsRow2LabelA.setVisible(false);
        _4ParamFieldsRow2LabelD.setVisible(false);
        _4ParamFieldsRow2TextFieldA.setVisible(false);
        _4ParamFieldsRow2TextFieldD.setVisible(false);

        _3ParamFieldsRow1LabelA.setText("Central Meridian:");
        _3ParamFieldsRow1LabelB.setText("Origin Latitude:");
        _3ParamFieldsRow1LabelC.setText("Scale Factor:");
        _4ParamFieldsRow2LabelB.setText("False Easting (m):");
        _4ParamFieldsRow2LabelC.setText("False Northing (m):");
        try
        {
            _3ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(0, useNSEW, useMinutes, useSeconds));
            _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(0, useNSEW, useMinutes, useSeconds));
        }
        catch(CoordinateConversionException e)
        {
            stringHandler.displayErrorMsg(this, e.getMessage());
        }
        _3ParamFieldsRow1TextFieldC.setText( "1.00000" );
        _4ParamFieldsRow2TextFieldB.setText( "0" );
        _4ParamFieldsRow2TextFieldC.setText( "0" );
    } // end setFiveParams()

    private void setFiveParamsUnedit()
    {
        _3ParamFieldsRow1TextFieldA.setEditable(false);
        _3ParamFieldsRow1TextFieldB.setEditable(false);
        _3ParamFieldsRow1TextFieldC.setEditable(false);
        _4ParamFieldsRow2TextFieldB.setEditable(false);
        _4ParamFieldsRow2TextFieldC.setEditable(false);
    }

    private void setSixParams()
    {
        setFormat();
        hideParams();
        _4ParamFieldsRow1Panel.setVisible(true);
        _4ParamFieldsRow2Panel.setVisible(true);

        // Show/Hide Extra Fields
        _4ParamFieldsRow1LabelA.setVisible(true);
        _4ParamFieldsRow1LabelD.setVisible(true);
        _4ParamFieldsRow2LabelA.setVisible(false);
        _4ParamFieldsRow2LabelD.setVisible(false);
        _4ParamFieldsRow1TextFieldA.setVisible(true);
        _4ParamFieldsRow1TextFieldD.setVisible(true);
        _4ParamFieldsRow2TextFieldA.setVisible(false);
        _4ParamFieldsRow2TextFieldD.setVisible(false);

        _4ParamFieldsRow1LabelA.setText("Central Meridian:");
        _4ParamFieldsRow1LabelB.setText("Origin Latitude:");
        _4ParamFieldsRow1LabelC.setText("Std. Parallel 1:");
        _4ParamFieldsRow1LabelD.setText("Std. Parallel 2:");
        _4ParamFieldsRow2LabelB.setText("False Easting (m):");
        _4ParamFieldsRow2LabelC.setText("False Northing (m):");
        try
        {
            _4ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(0, useNSEW, useMinutes, useSeconds));
            _4ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(45, useNSEW, useMinutes, useSeconds));
            _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(40, useNSEW, useMinutes, useSeconds));
            _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(50, useNSEW, useMinutes, useSeconds));
        }
        catch(CoordinateConversionException e)
        {
            stringHandler.displayErrorMsg(this, e.getMessage());
        }
        _4ParamFieldsRow2TextFieldB.setText( "0" );
        _4ParamFieldsRow2TextFieldC.setText( "0" );
    } // end setSixParams()

    private void setSixParamsUnedit()
    {
        _4ParamFieldsRow1TextFieldA.setEditable(false);
        _4ParamFieldsRow1TextFieldB.setEditable(false);
        _4ParamFieldsRow1TextFieldC.setEditable(false);
        _4ParamFieldsRow1TextFieldD.setEditable(false);
        _4ParamFieldsRow2TextFieldB.setEditable(false);
        _4ParamFieldsRow2TextFieldC.setEditable(false);
    }

    private void setEightParams()
    {
        setFormat();
        hideParams();
        _4ParamFieldsRow1Panel.setVisible(true);
        _4ParamFieldsRow2Panel.setVisible(true);

        // Show/Hide Extra Fields
        _4ParamFieldsRow1LabelA.setVisible(true);
        _4ParamFieldsRow1LabelD.setVisible(true);
        _4ParamFieldsRow2LabelA.setVisible(true);
        _4ParamFieldsRow2LabelD.setVisible(true);
        _4ParamFieldsRow1TextFieldA.setVisible(true);
        _4ParamFieldsRow1TextFieldD.setVisible(true);
        _4ParamFieldsRow2TextFieldA.setVisible(true);
        _4ParamFieldsRow2TextFieldD.setVisible(true);

        _4ParamFieldsRow1LabelA.setText("Origin Latitude:");
        _4ParamFieldsRow1LabelB.setText("Scale Factor:");
        _4ParamFieldsRow1LabelC.setText("Longitude 1:");
        _4ParamFieldsRow1LabelD.setText("Latitude 1:");
        _4ParamFieldsRow2LabelA.setText("Longitude 2:");
        _4ParamFieldsRow2LabelB.setText("Latitude 2:");
        _4ParamFieldsRow2LabelC.setText("False Easting (m):");
        _4ParamFieldsRow2LabelD.setText("False Northing (m):");
        try
        {
            _4ParamFieldsRow1TextFieldA.setText(stringHandler.latitudeToString(45, useNSEW, useMinutes, useSeconds));
            _4ParamFieldsRow1TextFieldB.setText("1.00000");
            _4ParamFieldsRow1TextFieldC.setText(stringHandler.longitudeToString(-5, useNSEW, useMinutes, useSeconds));
            _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(40, useNSEW, useMinutes, useSeconds));
            _4ParamFieldsRow2TextFieldA.setText(stringHandler.longitudeToString(5, useNSEW, useMinutes, useSeconds));
            _4ParamFieldsRow2TextFieldB.setText(stringHandler.latitudeToString(50, useNSEW, useMinutes, useSeconds));
        }
        catch(CoordinateConversionException e)
        {
            stringHandler.displayErrorMsg(this, e.getMessage());
        }
        _4ParamFieldsRow2TextFieldC.setText("0");
        _4ParamFieldsRow2TextFieldD.setText("0");
    } // end setEightParams()


    private void setEightParamsUnedit()
    {
        _4ParamFieldsRow1TextFieldA.setEditable(false);
        _4ParamFieldsRow1TextFieldB.setEditable(false);
        _4ParamFieldsRow1TextFieldC.setEditable(false);
        _4ParamFieldsRow1TextFieldD.setEditable(false);
        _4ParamFieldsRow2TextFieldA.setEditable(false);
        _4ParamFieldsRow2TextFieldB.setEditable(false);
        _4ParamFieldsRow2TextFieldC.setEditable(false);
        _4ParamFieldsRow2TextFieldD.setEditable(false);
    }


    private void fillHeightList()
    {
         heightComboBox.addItem("No Height");
         heightComboBox.addItem("Ellipsoid Height");

         //"MSL-EGM2008-2.5M-BCS Height" is third in the list (index 2) on the
         //GUI but is still index 7 in the HeightType enum.
         heightComboBox.addItem("MSL-EGM2008-2.5M-BCS Height");

         heightComboBox.addItem("MSL-EGM96-15M-BL Height");
         heightComboBox.addItem("MSL-EGM96-VG-NS Height");
         heightComboBox.addItem("MSL-EGM84-10D-BL Height");
         heightComboBox.addItem("MSL-EGM84-10D-NS Height");
         heightComboBox.addItem("MSL-EGM84-30M-BL Height");
   }


    // Fill the projection combo box
    private void fillProjectionList()
    {
        projectionComboBox.addItem(CoordinateType.GEODETIC_STR);
        projectionComboBox.addItem(CoordinateType.GEOREF_STR);
        projectionComboBox.addItem(CoordinateType.GARS_STR);
        projectionComboBox.addItem(CoordinateType.GEOCENTRIC_STR);
        projectionComboBox.addItem(CoordinateType.SPHERICAL_STR);
        projectionComboBox.addItem(CoordinateType.LOCCART_STR);
        projectionComboBox.addItem(CoordinateType.LOCSPHER_STR);
        projectionComboBox.addItem(CoordinateType.MGRS_STR);
        projectionComboBox.addItem(CoordinateType.USNG_STR);
        projectionComboBox.addItem(CoordinateType.UTM_STR);
        projectionComboBox.addItem(CoordinateType.UPS_STR);
        projectionComboBox.addItem(CoordinateType.ALBERS_STR);
        projectionComboBox.addItem(CoordinateType.AZIMUTHAL_STR);
        projectionComboBox.addItem(CoordinateType.BNG_STR);
        projectionComboBox.addItem(CoordinateType.BONNE_STR);
        projectionComboBox.addItem(CoordinateType.CASSINI_STR);
        projectionComboBox.addItem(CoordinateType.CYLEQA_STR);
        projectionComboBox.addItem(CoordinateType.ECKERT4_STR);
        projectionComboBox.addItem(CoordinateType.ECKERT6_STR);
        projectionComboBox.addItem(CoordinateType.EQDCYL_STR);
        projectionComboBox.addItem(CoordinateType.GNOMONIC_STR);
        projectionComboBox.addItem(CoordinateType.LAMBERT_1_STR);
        projectionComboBox.addItem(CoordinateType.LAMBERT_2_STR);
        projectionComboBox.addItem(CoordinateType.MERCATOR_SP_STR);
        projectionComboBox.addItem(CoordinateType.MERCATOR_SF_STR);
        projectionComboBox.addItem(CoordinateType.MILLER_STR);
        projectionComboBox.addItem(CoordinateType.MOLLWEIDE_STR);
        projectionComboBox.addItem(CoordinateType.NEYS_STR);
        projectionComboBox.addItem(CoordinateType.NZMG_STR);
        projectionComboBox.addItem(CoordinateType.OMERC_STR);
        projectionComboBox.addItem(CoordinateType.ORTHOGRAPHIC_STR);
        projectionComboBox.addItem(CoordinateType.POLARSTEREO_SP_STR);
        projectionComboBox.addItem(CoordinateType.POLARSTEREO_SF_STR);
        projectionComboBox.addItem(CoordinateType.POLYCONIC_STR);
        projectionComboBox.addItem(CoordinateType.SINUSOIDAL_STR);
        projectionComboBox.addItem(CoordinateType.STEREOGRAPHIC_STR);
        projectionComboBox.addItem(CoordinateType.TRCYLEQA_STR);
        projectionComboBox.addItem(CoordinateType.TRANMERC_STR);
        projectionComboBox.addItem(CoordinateType.GRINTEN_STR);
        projectionComboBox.addItem(CoordinateType.WEBMERCATOR_STR);
        if (state != ConversionState.FILE)
            projectionComboBox.addItem(CoordinateType.F16GRS_STR);
        projectionComboBox.updateUI();
    }


    private void setFormat()
    {
        useNSEW = formatOptions.getUseNSEW();
        useMinutes = formatOptions.getUseMinutes();
        useSeconds = formatOptions.getUseSeconds();
    }


/**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
   //     jOptions joptions = new jOptions();
   //     JNIFiomeths jniFiomeths = new JNIFiomeths();
   //     new FileDlg (new javax.swing.JFrame (), true, null, joptions, jniFiomeths).show ();
    }



    // Add new datum to combo box
    public void addDatumToList()
    {
      long currentListIndex = datumIndex;// - 1;
      datumList = new FillList(jniDatumLibrary, jniEllipsoidLibrary, datumComboBox, ListType.DATUM);
  //       datumList.addDatum(datumComboBox);
      datumComboBox.setSelectedIndex((int)currentListIndex);
    }


    // Update the index of the current datum in case it is the one being deleted
    // before deleting the datum from the list to prevent checkValidConversion
    // from using the index of the deleted datum
    public void updateCurrentDatumIndex(int indexOfDeletedDatum)
    {
      if(indexOfDeletedDatum <= datumIndex)
        datumIndex = datumIndex-1;
    }


    // Remove datum from combo box
    public void deleteDatumFromList()
    {
      // FillList sets the current datum to WGS84, so keep track
      //  of the current datum before it is reset
      long currentListIndex = datumIndex;
      datumList = new FillList(jniDatumLibrary, jniEllipsoidLibrary, datumComboBox, ListType.DATUM);
      datumComboBox.setSelectedIndex((int)currentListIndex);
    }

    public void showErrors(java.awt.Component parent)
    {
        stringHandler.displayErrorMsg(parent, direction, projectionType);
    }

    public boolean getError()
    {
        return stringHandler.getError();
    }


    public void setHeightFieldEditable(boolean editable)
    {
        coordPanel.setHeightFieldEditable(editable);
    }

    public void setHeightText(String height)
    {
        coordPanel.setHeightText(height);
    }

    public void selectNoHeightButton()
    {
        heightType = HeightType.NO_HEIGHT;
        if (isEpsgCodeActive()) {
            heightChanged = true;
        } 
        heightComboBox.setSelectedIndex(heightType);
    }

    public void selectEllipsoidHeightButton()
    {
        heightType = HeightType.ELLIPSOID_HEIGHT;
        if (isEpsgCodeActive()) {
            heightChanged = true;           
        } 
        heightComboBox.setSelectedIndex(heightType);
    }

    public void enableHeightComboBox(boolean enable)
    {
        heightComboBox.setEnabled(enable);
    }

    public int getProjectionType()
    {
        return projectionType;
    }

    public String getProjectionCode()
    {
        return CoordinateType.code(projectionType);
    }

    public long getDatumIndex()
    {
        return datumIndex;
    }

    public String getDatumCode()
    {
      try
      {
        return jniDatumLibrary.getDatumInfo(datumIndex).getCode();
      }
      catch(CoordinateConversionException e)
      {
        stringHandler.setErrorMessage(true, e.getMessage());
        return "";
      }
    }

    public boolean getCoordinateOrder()
    {
      if(latitudeLongitudeRadioButton.isSelected())
        return true;
      else
        return false;
    }

    public void setAccuracy(Accuracy accuracy)
    {
       boolean applyPrecision = true;
       switch(projectionType)
       {
          case CoordinateType.BNG:
          case CoordinateType.GARS:
          case CoordinateType.GEOREF:
          case CoordinateType.F16GRS:
          case CoordinateType.MGRS:
          case CoordinateType.USNG:
          {
             applyPrecision = false;
          }
       }

       coordPanel.setAccuracy(accuracy, applyPrecision);
    }

    public void resetAccuracy()
    {
        coordPanel.resetOutputErrors();
    }

    public void updateSrcErrors(boolean _3dConversion)
    {
       boolean applyPrecision = true;
       switch(projectionType)
       {
          case CoordinateType.BNG:
          case CoordinateType.GARS:
          case CoordinateType.GEOREF:
          case CoordinateType.F16GRS:
          case CoordinateType.MGRS:
          case CoordinateType.USNG:
          {
             applyPrecision = false;
          }
       }
       coordPanel.updateSrcErrors(_3dConversion, applyPrecision );
    }

    // MSP CCS parent class
    public void setParent(MSP_GEOTRANS3 par)
    {
        parent = par;
    }

    public void setLookAndFeel(String lookAndFeel)
    {
        currLookAndFeel = lookAndFeel;
        if (!Platform.isJavaV1_3)
        {
            switch (projectionType)
            {
                case CoordinateType.UPS:
                case CoordinateType.UTM:
                {
                    if (Platform.isUnix)
                    {
                        if (currLookAndFeel.equals("Java"))
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                               javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10), java.awt.Color.black));
                        else
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                               javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), java.awt.Color.black));
                    }
                    else
                    {
                        if (currLookAndFeel.equals("Java"))
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                                   javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));
                        else
                            hemiBoxPanel.setBorder(new javax.swing.border.TitledBorder(null, "Hemisphere:", javax.swing.border.TitledBorder.CENTER,
                                                   javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12)));
                    }
                    break;
                }
                case CoordinateType.NEYS:
                {
                    if (Platform.isUnix)
                    {
                        if (currLookAndFeel.equals("Java"))
                            stdParallel1NeysParamsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Std. Parallel 1:", javax.swing.border.TitledBorder.CENTER,
                                                      javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10), java.awt.Color.black));
                        else
                            stdParallel1NeysParamsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Std. Parallel 1:", javax.swing.border.TitledBorder.CENTER,
                                                      javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), java.awt.Color.black));
                    }
                    else
                    {
                        if (currLookAndFeel.equals("Java"))
                            stdParallel1NeysParamsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Std. Parallel 1:", javax.swing.border.TitledBorder.CENTER,
                                                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));
                        else
                            stdParallel1NeysParamsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Std. Parallel 1:", javax.swing.border.TitledBorder.CENTER,
                                                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12)));
                    }
                }
            }
        }
    }


  // Get Parameters

  public int getGeodeticHeight()
  {
      return heightType;
  }


  public long getZone()
  {
    if(zoneRadioButton.isSelected())
    {
        long zone = stringHandler.stringToInt(zoneTextField.getText().trim(), "Invalid zone");
        if ((zone < 1) || (zone > 60))
            return 0;
        else
            return zone;
    }
    else
        return 0;
  }


  public long getOverride()
  {
    return override;
  }


  public double get3ParamCentralMeridian()
  {
    return stringHandler.stringToLongitude(_3ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Central Meridian") * Constants._180_OVER_PI;
  }


  public double getFalseEasting()
  {
    return stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid False Easting");
  }


  public double getFalseNorthing()
  {
    return stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Northing");
  }


  public double get4ParamCentralMeridian()
  {
    return stringHandler.stringToLongitude(_4ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Central Meridian") * Constants._180_OVER_PI;
  }


  public double get4ParamOriginLatitude()
  {
    return stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Origin Latitude") * Constants._180_OVER_PI;
  }


  public double getPS_SFCentralMeridian()
  {
    return stringHandler.stringToLongitude(_3ParamFieldsRow1PS_SFTextFieldA.getText().trim(), "Invalid Central Meridian") * Constants._180_OVER_PI;
  }


  public double getPS_SFScaleFactor()
  {
    return Double.parseDouble(_3ParamFieldsRow1PS_SFTextFieldB.getText().trim());
  }


  public char getRow1Hemisphere()
  {
    if (row1NHemiRadioButton.isSelected() == true)
      return 'N';
    else
      return 'S';
  }


  public double get5ParamCentralMeridian()
  {
    return stringHandler.stringToLongitude(_3ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Central Meridian") * Constants._180_OVER_PI;
  }


  public double get5ParamOriginLatitude()
  {
    return stringHandler.stringToLatitude(_3ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Origin Latitude") * Constants._180_OVER_PI;
  }


  public double get5ParamScaleFactor()
  {
    return Double.parseDouble(_3ParamFieldsRow1TextFieldC.getText().trim());
  }


  public double get6ParamCentralMeridian()
  {
    return stringHandler.stringToLongitude(_4ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Central Meridian") * Constants._180_OVER_PI;
  }


  public double get6ParamOriginLatitude()
  {
    return stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Origin Latitude") * Constants._180_OVER_PI;
  }


  public double get6ParamStandardParallel1()
  {
    return stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldC.getText().trim(), "Invalid 1st Standard Parallel") * Constants._180_OVER_PI;
  }


  public double get6ParamStandardParallel2()
  {
    return stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldD.getText().trim(), "Invalid 2nd Standard Parallel") * Constants._180_OVER_PI;
  }


  public double getOriginLongitude()
  {
    return stringHandler.stringToLongitude(_3ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Origin Longitude") * Constants._180_OVER_PI;
  }


  public double getOriginLatitude()
  {
    return stringHandler.stringToLatitude(_3ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Origin Latitude") * Constants._180_OVER_PI;
  }


  public double getOriginHeight()
  {
    return stringHandler.stringToDouble(_3ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Origin Height");
  }


  public double getOrientation()
  {
    return stringHandler.stringToLongitude(_3ParamFieldsRow2TextFieldB.getText().trim(), "Invalid Orientation") * Constants._180_OVER_PI;
  }


  public double getNeysCentralMeridian()
  {
    return stringHandler.stringToLongitude(centralMeridianNeysParamsTextField.getText().trim(), "Invalid Central Meridian") * Constants._180_OVER_PI;
  }


  public double getNeysOriginLatitude()
  {
    return stringHandler.stringToLatitude(originLatitudeNeysParamsTextField.getText().trim(), "Invalid Origin Latitude") * Constants._180_OVER_PI;
  }


  public double getNeysStandardParallel1()
  {
    double std_par_1 = 71.0;

    if (neys74RadioButton.isSelected() == true)
      std_par_1 = 74.0;

    return std_par_1;
  }


  public double getOmercOriginLatitude()
  {
    return stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldA.getText().trim(), "Invalid Origin Latitude") * Constants._180_OVER_PI;
  }


  public double getOmercLongitude1()
  {
    return stringHandler.stringToLongitude(_4ParamFieldsRow1TextFieldC.getText().trim(), "Invalid Longitude 1") * Constants._180_OVER_PI;
  }


  public double getOmercLatitude1()
  {
    return stringHandler.stringToLatitude(_4ParamFieldsRow1TextFieldD.getText().trim(), "Invalid Latitude 1") * Constants._180_OVER_PI;
  }


  public double getOmercLongitude2()
  {
    return stringHandler.stringToLongitude(_4ParamFieldsRow2TextFieldA.getText().trim(), "Invalid Longitude 2") * Constants._180_OVER_PI;
  }


  public double getOmercLatitude2()
  {
    return stringHandler.stringToLatitude(_4ParamFieldsRow2TextFieldB.getText().trim(), "Invalid Latitude 2") * Constants._180_OVER_PI;
  }


  public double getOmercFalseEasting()
  {
    return stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldC.getText().trim(), "Invalid False Easting");
  }


  public double getOmercFalseNorthing()
  {
    return stringHandler.stringToDouble(_4ParamFieldsRow2TextFieldD.getText().trim(), "Invalid False Northing");
  }


  public double getOmercScaleFactor()
  {
    return stringHandler.stringToDouble(_4ParamFieldsRow1TextFieldB.getText().trim(), "Invalid Scale Factor");
  }


  public void setDefaults(int direction, JNICoordinateConversionService _jniCoordinateConversionService, FormatOptions _formatOptions, StringHandler _stringHandler)////int _datumIndex, String _projectionTypeCode, CoordinateSystemParameters _parameters)
  {
    try
    {
      jniCoordinateConversionService = _jniCoordinateConversionService;
      jniDatumLibrary = new JNIDatumLibrary(jniCoordinateConversionService.getDatumLibrary());
      jniEllipsoidLibrary = new JNIEllipsoidLibrary(jniCoordinateConversionService.getEllipsoidLibrary());
      formatOptions = _formatOptions;
      stringHandler = _stringHandler;

      coordPanel.setDefaults(_formatOptions, stringHandler);

      datumIndex = jniDatumLibrary.getDatumIndex(jniCoordinateConversionService.getDatum(direction));
      CoordinateSystemParameters _parameters = jniCoordinateConversionService.getCoordinateSystem(direction);
      projectionType = _parameters.getCoordinateType();

      datumComboBox.setSelectedIndex((int)datumIndex);
      projectionComboBox.setSelectedItem(CoordinateType.name(projectionType));

      createMasterPanel();

      switch(projectionType)
      {
        case CoordinateType.ECKERT4:
        case CoordinateType.ECKERT6:
        case CoordinateType.MILLER:
        case CoordinateType.MOLLWEIDE:
        case CoordinateType.SINUSOIDAL:
        case CoordinateType.GRINTEN:
        {
          MapProjection3Parameters parameters = (MapProjection3Parameters)_parameters;
          _3ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
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
          MapProjection4Parameters parameters = (MapProjection4Parameters)_parameters;
          _4ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(parameters.getOriginLatitude() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.EQDCYL:
        {
          EquidistantCylindricalParameters parameters = (EquidistantCylindricalParameters)_parameters;
          _4ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(parameters.getStandardParallel() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.LAMBERT_1:
        case CoordinateType.TRCYLEQA:
        case CoordinateType.TRANMERC:
        {
          MapProjection5Parameters parameters = (MapProjection5Parameters)_parameters;
          _3ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(parameters.getOriginLatitude() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));

          stringHandler.setNumberFormat(_3ParamFieldsRow1TextFieldC, parameters.getScaleFactor(), 5);

          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.MERCATOR_SP:
        {
          MercatorStandardParallelParameters parameters = (MercatorStandardParallelParameters)_parameters;
          _3ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(parameters.getStandardParallel() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));

          stringHandler.setNumberFormat(_3ParamFieldsRow1TextFieldC, parameters.getScaleFactor(), 5);

          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.MERCATOR_SF:
        {
          MercatorScaleFactorParameters parameters = (MercatorScaleFactorParameters)_parameters;
          _4ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));

          stringHandler.setNumberFormat(_4ParamFieldsRow1TextFieldC, parameters.getScaleFactor(), 5);

          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.ALBERS:
        case CoordinateType.LAMBERT_2:
        {
          MapProjection6Parameters parameters = (MapProjection6Parameters)_parameters;
          _4ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(parameters.getOriginLatitude() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(parameters.getStandardParallel1() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(parameters.getStandardParallel2() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.LOCCART:
        case CoordinateType.LOCSPHER:
        {
          LocalCartesianParameters parameters = (LocalCartesianParameters)_parameters;
          _3ParamFieldsRow1TextFieldA.setText(stringHandler.longitudeToString(parameters.getLongitude() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _3ParamFieldsRow1TextFieldB.setText(stringHandler.latitudeToString(parameters.getLatitude() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _3ParamFieldsRow1TextFieldC.setText(stringHandler.meterToString(parameters.getHeight()));
          _3ParamFieldsRow2TextFieldB.setText(stringHandler.longitudeToString(parameters.getOrientation() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          break;
        }
        case CoordinateType.NEYS:
        {
          NeysParameters parameters = (NeysParameters)_parameters;
          boolean northHemi = true;

          double olat = parameters.getOriginLatitude() * Constants._180_OVER_PI;
          if (olat < 0)
              northHemi = false;
          centralMeridianNeysParamsTextField.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          originLatitudeNeysParamsTextField.setText(stringHandler.latitudeToString(olat, useNSEW, useMinutes, useSeconds));

          double standardParallel1 = parameters.getStandardParallel1() * Constants._180_OVER_PI;
          if(standardParallel1 == 71.0)
          {
            neys71RadioButton.setSelected(true);
            neys74RadioButton.setSelected(false);
          }
          else
          {
            neys71RadioButton.setSelected(false);
            neys74RadioButton.setSelected(true);
          }
      //    if (northHemi)
      //        _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(parameters.getStandardParallel1(), useNSEW, useMinutes, useSeconds));
      //    else
      //        _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(parameters.getStandardParallel1(), useNSEW, useMinutes, useSeconds));
          /* std parallel 2 = 89 59 59.0 */
    //      if (northHemi){System.out.println("n");
              stdParallel2NeysParamsTextField.setText(stringHandler.latitudeToString(89.99944444444444, false, useMinutes, useSeconds));
    //      else {System.out.println("s");
    //          stdParallel2NeysParamsTextField.setText(stringHandler.latitudeToString(-89.99944444444444, useNSEW, useMinutes, useSeconds));}
          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.OMERC:
        {
          ObliqueMercatorParameters parameters = (ObliqueMercatorParameters)_parameters;
          _4ParamFieldsRow1TextFieldA.setText(stringHandler.latitudeToString(parameters.getOriginLatitude() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));

          stringHandler.setNumberFormat(_4ParamFieldsRow1TextFieldB, parameters.getScaleFactor(), 5);

          _4ParamFieldsRow1TextFieldC.setText(stringHandler.longitudeToString(parameters.getLongitude1() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow1TextFieldD.setText(stringHandler.latitudeToString(parameters.getLatitude1() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow2TextFieldA.setText(stringHandler.longitudeToString(parameters.getLongitude2() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow2TextFieldB.setText(stringHandler.latitudeToString(parameters.getLatitude2() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldD.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.POLARSTEREO_SP:
        {
          PolarStereographicStandardParallelParameters parameters = (PolarStereographicStandardParallelParameters)_parameters;

          _4ParamFieldsRow1TextFieldB.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow1TextFieldC.setText(stringHandler.latitudeToString(parameters.getStandardParallel() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.POLARSTEREO_SF:
        {
          PolarStereographicScaleFactorParameters parameters = (PolarStereographicScaleFactorParameters)_parameters;
          _3ParamFieldsRow1PS_SFTextFieldA.setText(stringHandler.longitudeToString(parameters.getCentralMeridian() * Constants._180_OVER_PI, useNSEW, useMinutes, useSeconds));
          stringHandler.setNumberFormat(_3ParamFieldsRow1PS_SFTextFieldB, parameters.getScaleFactor(), 5);
          if (parameters.getHemisphere() == 'N')
          {
            row1NHemiRadioButton.setSelected(true);
            row1SHemiRadioButton.setSelected(false);
          }
          else
          {
            row1NHemiRadioButton.setSelected(false);
            row1SHemiRadioButton.setSelected(true);
          }
          _4ParamFieldsRow2TextFieldB.setText(stringHandler.meterToString(parameters.getFalseEasting()));
          _4ParamFieldsRow2TextFieldC.setText(stringHandler.meterToString(parameters.getFalseNorthing()));
          break;
        }
        case CoordinateType.GEODETIC:
        {
          GeodeticParameters parameters = (GeodeticParameters)_parameters;
          heightComboBox.setSelectedIndex(parameters.getHeightType());
          break;
        }
        case CoordinateType.UTM:
        {

          UTMParameters parameters = (UTMParameters)_parameters;
          if(parameters.getOverride() != 0)
            zoneRadioButton.setSelected(true);
          else
            zoneRadioButton.setSelected(false);
          zoneTextField.setText(String.valueOf(parameters.getZone()));
          break;
        }
      }
    }
    catch(CoordinateConversionException e)
    {
      stringHandler.displayErrorMsg(this, e.getMessage());
    }
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JLabel _3ParamFieldsRow1LabelA;
  private JLabel _3ParamFieldsRow1LabelB;
  private JLabel _3ParamFieldsRow1LabelC;
  private JLabel _3ParamFieldsRow1PS_SFLabelA;
  private JLabel _3ParamFieldsRow1PS_SFLabelB;
  private JPanel _3ParamFieldsRow1PS_SFPanel;
  private JTextField _3ParamFieldsRow1PS_SFTextFieldA;
  private JTextField _3ParamFieldsRow1PS_SFTextFieldB;
  private JPanel _3ParamFieldsRow1Panel;
  private JTextField _3ParamFieldsRow1TextFieldA;
  private JTextField _3ParamFieldsRow1TextFieldB;
  private JTextField _3ParamFieldsRow1TextFieldC;
  private JLabel _3ParamFieldsRow2LabelA;
  private JLabel _3ParamFieldsRow2LabelB;
  private JLabel _3ParamFieldsRow2LabelC;
  private JPanel _3ParamFieldsRow2Panel;
  private JTextField _3ParamFieldsRow2TextFieldA;
  private JTextField _3ParamFieldsRow2TextFieldB;
  private JTextField _3ParamFieldsRow2TextFieldC;
  private JLabel _4ParamFieldsRow1LabelA;
  private JLabel _4ParamFieldsRow1LabelB;
  private JLabel _4ParamFieldsRow1LabelC;
  private JLabel _4ParamFieldsRow1LabelD;
  private JPanel _4ParamFieldsRow1Panel;
  private JTextField _4ParamFieldsRow1TextFieldA;
  private JTextField _4ParamFieldsRow1TextFieldB;
  private JTextField _4ParamFieldsRow1TextFieldC;
  private JTextField _4ParamFieldsRow1TextFieldD;
  private JLabel _4ParamFieldsRow2LabelA;
  private JLabel _4ParamFieldsRow2LabelB;
  private JLabel _4ParamFieldsRow2LabelC;
  private JLabel _4ParamFieldsRow2LabelD;
  private JPanel _4ParamFieldsRow2Panel;
  private JTextField _4ParamFieldsRow2TextFieldA;
  private JTextField _4ParamFieldsRow2TextFieldB;
  private JTextField _4ParamFieldsRow2TextFieldC;
  private JTextField _4ParamFieldsRow2TextFieldD;
  private JPanel _geodeticCoordinateOrderPanel;
  private JLabel centralMeridianNeysParamsLabel;
  private JPanel centralMeridianNeysParamsPanel;
  private JTextField centralMeridianNeysParamsTextField;
  private javax.swing.JComboBox datumComboBox;
  private JLabel datumLabel;
  private JPanel datumPanel;
  private javax.swing.JLayeredPane datumSelectLayeredPane;
  private JTextField datumTextField;
  private JLabel ellipsoidLabel;
  private JTextField ellipsoidTextField;
  private JPanel geodeticCoordinateOrderPanel;
  private javax.swing.JComboBox heightComboBox;
  private JLabel heightLabel;
  private JPanel heightLabelPanel;
  private JPanel heightPanel;
  private JPanel hemiBoxPanel;
  private JLabel inputProjectionLabel;
  private JRadioButton latitudeLongitudeRadioButton;
  private JRadioButton longitudeLatitudeRadioButton;
  private JRadioButton nHemiRadioButton;
  private JRadioButton neys71RadioButton;
  private JRadioButton neys74RadioButton;
  private JPanel neysParamsRow1Panel;
  private JLabel originLatitudeNeysParamsLabel;
  private JPanel originLatitudeNeysParamsPanel;
  private JTextField originLatitudeNeysParamsTextField;
  private javax.swing.JLayeredPane paramFieldsRow1LayeredPane;
  private javax.swing.JLayeredPane paramFieldsRow2LayeredPane;
  private javax.swing.JComboBox projectionComboBox;
  private JPanel projectionPanel;
  private JPanel row1HemiBoxPanel;
  private JRadioButton row1NHemiRadioButton;
  private JRadioButton row1SHemiRadioButton;
  private JRadioButton sHemiRadioButton;
  private JPanel stdParallel1NeysParamsPanel;
  private JLabel stdParallel2NeysParamsLabel;
  private JPanel stdParallel2NeysParamsPanel;
  private JTextField stdParallel2NeysParamsTextField;
  private JLabel tempZoneBoxLabel;
  private JPanel zoneBoxPanel;
  private JPanel zoneHemiPanel;
  private JLabel zoneLabel;
  private JRadioButton zoneRadioButton;
  private JLabel zoneRangeLabel;
  private JTextField zoneTextField;

  private AutocompleteCombobox epsgCombobox;
  private JButton epsgApplyBtn;
  private JPanel epsgPanel;
  private List<String> epsgCodes;
}

// CLASSIFICATION: UNCLASSIFIED
