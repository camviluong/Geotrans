// CLASSIFICATION: UNCLASSIFIED

/********************************************************************************
* Filename: AutocompleteCombobox.java
*
* Copyright BAE Systems Inc. 2020 ALL RIGHTS RESERVED
*
* MODIFICATION HISTORY
*
* DATE      NAME        DR#          DESCRIPTION
*
* 04/06/20  E. Poon     GTR-53       Move AutocompleteCombobox out of 
                                     MasterPanel and add support of EPSG codes
********************************************************************************/

package geotrans3.gui;



import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.FocusListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

import geotrans3.misc.StringHandler;
import geotrans3.misc.StringSearchable;


/**
 * JComboBox with an autocomplete drop down menu. This class is hard-coded for String objects, but can be
 * altered into a generic form to allow for any searchable item. 
 *
 */
public class AutocompleteCombobox extends JComboBox {
    static final long serialVersionUID = 4321421L;
    
    private static final int MAX_CODE_VALUE = 32767;
    private static final int MIN_CODE_VALUE = 1024;
    private static final int NUM_INITIAL_OPTIONS = 15;
    
    private static StringSearchable searchable;
    private static List<String> firstNItems;
    
    private boolean itemSelected = false;
    private boolean populatingValues = false;
    
    private static StringHandler stringHandler;
    
    private void s(final String str) {
        // Uncomment for debugging
        // System.out.println(Instant.now().toString() + " - AutocompleteCombobox: " + str);
    }

    /**
     * Constructs a new object based upon the parameter searchable
     * @param s
     */
    public AutocompleteCombobox(final StringSearchable itemList) {
        super();
        s("AutocompleteCombobox");
        
        this.searchable = itemList;
        this.firstNItems = itemList.getFirstNItems(NUM_INITIAL_OPTIONS, true);
        
        init();
    }
    
    private void init() {
        s("init");
        
        populateInitialValues();
        setEditable(true);
        
        final Component editorComponent = getEditor().getEditorComponent();
        
        if (!(editorComponent instanceof JTextComponent)) {
            throw new IllegalStateException("Cannot create AutocompleteCombobox: Editing component is not a JTextComponent");
        }
        
        addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                // s("itemStateChanged: " + e);
                
                if (e.getStateChange() == ItemEvent.SELECTED && searchable.contains((String)e.getItem())) {
                    s("itemStateChanged(): New EPSG code selected: " + e.paramString());
                    itemSelected = true;
                    
                } else {
                    s("itemStateChanged(): No EPSG code selected");
                    itemSelected = false;
                }
            }
        });

        final JTextComponent textbox = (JTextComponent)editorComponent;
        final PlainDocument doc = (PlainDocument)textbox.getDocument();
        
        // Only accept integer values
        doc.setDocumentFilter(new DocumentFilter() {
            private String getFullText(final FilterBypass fb, final int offset, final String newText) throws BadLocationException {
                final Document doc = fb.getDocument();
                final StringBuilder sb = new StringBuilder();
                
                // If newText is multi-character, this is a replace,
                // rather than a keyboard typed value, and should be accepted.
                final int newTextLength = newText.length();
                
                if (newTextLength > 1) {
                    return newText;
                }
                
                sb.append(doc.getText(0, doc.getLength()));
                sb.replace(offset, offset + newTextLength, newText);
                
                return sb.toString();
            }
            
            @Override
            public void insertString(final FilterBypass fb, final int offset, final String str, final AttributeSet as) throws BadLocationException {
                if (isValidString(str)) {
                    s("DocumentFilter: insertStringstring is valid: '" + str + "'");
                    super.insertString(fb, offset, str, as);
                } else {
                
                    s("DocumentFilter: insertStringstring is NOT valid: '" + str + "'");
                }
            }
            
            @Override
            public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
                s("DocumentFilter: IN REMOVE");
                super.remove(fb, offset, length);
            }
            
            @Override
            public void replace(final FilterBypass fb, final int offset, final int length, final String str, final AttributeSet as) throws BadLocationException {
                if (populatingValues) {
                    return;
                }
                
                if (isValidString(getFullText(fb, offset, str))) {
                    s("DocumentFilter: replacestring is valid: '" + str + "'");
                    super.replace(fb, offset, length, str, as);
                } else {
                    s("DocumentFilter: replacestringstring is NOT valid: '" + str + "'");
                }
            }
            
            private boolean isValidString(final String text) {
                try {
                    final Integer codeInt = Integer.parseInt(text);
                    
                    if (codeInt >= 1 && codeInt <= MAX_CODE_VALUE) {
                        return true;
                    }
                } catch (final NumberFormatException e) {}
                
                return false;
            }
        });
        
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent arg0) {
                // s("changedUpdate(): DocumentEvent length: " + arg0.getLength());
                // s("changedUpdate(): DocumentEvent offset: " + arg0.getOffset());
                // s("changedUpdate(): DocumentEvent type: " + arg0.getType());
                
                try {
                    final Document doc = (Document)arg0.getDocument();
                    s("DocumentListener: changedUpdate(): DocumentEvent Document text: " + doc.getText(0, doc.getEndPosition().getOffset()));
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void insertUpdate(final DocumentEvent arg0) {
                // s("insertUpdate(): DocumentEvent length: " + arg0.getLength());
                // s("insertUpdate(): DocumentEvent offset: " + arg0.getOffset());
                // s("insertUpdate(): DocumentEvent type: " + arg0.getType());
                
                try {
                    final Document doc = (Document)arg0.getDocument();
                    final String text = doc.getText(arg0.getOffset(), arg0.getLength());
                    s("DocumentListener: insertUpdate(): DocumentEvent Document text: " + text);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                
                update();
            }

            @Override
            public void removeUpdate(final DocumentEvent arg0) {
                // s("removeUpdate(): DocumentEvent length: " + arg0.getLength());
                // s("removeUpdate(): DocumentEvent offset: " + arg0.getOffset());
                // s("removeUpdate(): DocumentEvent type: " + arg0.getType());
                
                try {
                    final Document doc = (Document)arg0.getDocument();
                    s("DocumentListener: removeUpdate(): DocumentEvent Document text: " + doc.getText(0, doc.getEndPosition().getOffset()));
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                
                itemSelected = false;
                update();
            }

            public void update() {
                // Perform separately, as listener conflicts between the editing component
                // and JComboBox will result in an IllegalStateException due to editing 
                // the component when it is locked. 
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (itemSelected) {
                            itemSelected = false;
                            return;
                        }
                        
                        final String enteredText = textbox.getText();
                        
                        s("update(): entered text: '" + enteredText + "'");
                        
                        if (enteredText.isEmpty()) {
                            populateInitialValues();
                            return;
                        }
                        
                        final List<String> founds = new ArrayList<>(searchable.search(enteredText));
                        final Set<String> foundSet = new HashSet<>(founds.size());
                        
                        for (final String s : founds) {
                            foundSet.add(s.toLowerCase());
                        }
                        
                        // Sort alphabetically
                        Collections.sort(founds);
                        setEditable(false);
                        removeAllItems();

                        // If founds contains the search text, then only add once.
                        if (foundSet.contains(enteredText.toLowerCase()) == false) {
                            addItem(enteredText);
                        }

                        for (final String s : founds) {
                            addItem(s);
                        }

                        setEditable(true);
                        setPopupVisible(true);
                        textbox.requestFocus();
                    }
                });
            }
        });

        // When the text component changes, focus is gained 
        // and the menu disappears. To account for this, whenever the focus
        // is gained by the JTextComponent and it has searchable values, we show the popup.
        textbox.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent arg0) {
                s("focusGained(): textbox focus gained");
                
                if (textbox.getText().length() > 0) {
                    s("focusGained(): text is not empty; setting popup visible");
                    setPopupVisible(true);
                }
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                s("focusLost(): textbox focus lost");
            }
        });
    }
    
    // Returns the selected item if it's in searchable;
    // returns null if not
    public String getSelectedItemString() {
        final Object selectedItem = this.getSelectedItem();
        
        if (null == selectedItem) {
            return null;
        }
        
        final String enteredString = selectedItem.toString();
        
        // Validate EPSG code value
        if (this.searchable.contains(enteredString)) {
            return enteredString;
        } else {
            return null;
        }
    }
    
    public void clearSelection() {
        s("clearSelection(): Clearing selection");
        
        s("clearSelection(): Resetting the selected index");
        setSelectedIndex(-1);
        s("clearSelection(): Selected index reset");
        
        // Dismiss the popup
        s("clearSelection(): Dismissing the popup");
        setPopupVisible(false);
        s("clearSelection(): Popup dismissed");
    }
    
    
    private void populateInitialValues() {
        s("populateInitialValues(): Populating initial values");
        populatingValues = true;
        
        removeAllItems();
        
        for (final String code : this.firstNItems) {
            addItem(code);
        }
        
        setSelectedIndex(-1);
        setPopupVisible(false);
        
        populatingValues = false;
    }
}

// CLASSIFICATION: UNCLASSIFIED
