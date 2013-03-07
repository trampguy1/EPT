/*
 * File SIAMTransformDialog.java Copywrite (c) 2005 - All rights reserved
 * Developed by NGI Systems Engineer Simon Vogel Created on Feb 8, 2005 
 */
package mil.af.rl.jcat.siamtransform.gui;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.io.File;
import mil.af.rl.jcat.siamtransform.io.FileOperations;
import mil.af.rl.jcat.siamtransform.xml.SIAMTransform;

/**
 * @author Simon 
 */
public class SIAMTransformDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel _pnlMain = null;

    private JTextField _txtSiamPath = null;

    private JTextField _txtSavePath = null;

    private JButton _btnOK = null;

    private JButton _btnClose = null;

    private JLabel jLabel = null;

    private JLabel jLabel2 = null;

    private SIAMTransform _transform = null;

    /**
     * This is the default constructor
     */
    public SIAMTransformDialog() {
        super();
        initialize();
        _transform.setTransformPath("./resources/SiamToCatStyleSheet_v0_7.xsl");
    }

    public static void main(String[] args) {
        SIAMTransformDialog _transDlg = new SIAMTransformDialog();

        _transDlg.pack();
        _transDlg.setVisible(true);
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(456, 149);
        this.setTitle("SIAM to CAT Translator");

        this.setContentPane(get_pnlMain());
        _transform = new SIAMTransform();
        this.setResizable(false);
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel get_pnlMain() {
        if (_txtSavePath == null) {
            jLabel2 = new JLabel();
            jLabel = new JLabel();
            _pnlMain = new javax.swing.JPanel();
            _pnlMain.setLayout(null);
            jLabel.setBounds(21, 12, 103, 19);
            jLabel.setText("SIAM Path");
            jLabel2.setBounds(20, 37, 103, 19);
            jLabel2.setText("Result Path");
            _pnlMain.setLayout(null);
            _pnlMain.add(get_txtSiamPath(), null);
            _pnlMain.add(get_txtSavePath(), null);
            _pnlMain.add(get_btnOK(), null);
            _pnlMain.add(get_btnClose(), null);
            _pnlMain.add(jLabel, null);
            _pnlMain.add(jLabel2, null);
            _pnlMain.add(get_txtSiamPath(), null);
            _pnlMain.setPreferredSize(new Dimension(450, 115));
        }
        return _pnlMain;
    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField get_txtSiamPath() {
        if (_txtSiamPath == null) {
            _txtSiamPath = new JTextField();
            _txtSiamPath.setPreferredSize(new java.awt.Dimension(150, 20));
            _txtSiamPath.setLocation(131, 11);
            _txtSiamPath.setSize(298, 20);
            _txtSiamPath.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    //String path = FileOperations.fileSelect(
                    File path = FileOperations.fileSelect(
                            FileOperations.OPEN, "xml",
                            "Path to the SIAM xml file.", new JFrame());
                    _transform.setSIAMPath(path);
                    _txtSiamPath.setText(path.getName());
                }
            });
        }
        return _txtSiamPath;
    }

    /**
     * This method initializes jTextField2
     *
     * @return javax.swing.JTextField
     */
    private JTextField get_txtSavePath() {
        if (_txtSavePath == null) {
            _txtSavePath = new JTextField();
            _txtSavePath.setBounds(132, 36, 298, 20);
            _txtSavePath.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    String path = FileOperations
                            .fileSelect(FileOperations.SAVE);
                    _transform.setResultPath(path);
                    _txtSavePath.setText(path);
                }
            });
        }
        return _txtSavePath;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton get_btnOK() {
        if (_btnOK == null) {
            _btnOK = new JButton();
            _btnOK.setBounds(119, 70, 66, 26);
            _btnOK.setText("OK");
            _btnOK.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (_transform.getResultPath() != null
                            && _transform.getTransformPath() != null
                            && _transform.getSIAMPath() != null) {

                        _transform.performTransform();
                    } else {

                    }
                }
            });
        }
        return _btnOK;
    }

    /**
     * This method initializes jButton1
     *
     * @return javax.swing.JButton
     */
    private JButton get_btnClose() {
        if (_btnClose == null) {
            _btnClose = new JButton();
            _btnClose.setBounds(252, 70, 66, 26);
            _btnClose.setText("Close");
            _btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    System.exit(0);
                }
            });
        }
        return _btnClose;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
