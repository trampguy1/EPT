package mil.af.rl.jcat.siamtransform.io;

/*
 * File FileOperations.java Copywrite (c) 2005 - All rights reserved Developed
 * by NGI Systems Engineer Simon Vogel Created on Sept , 2005
 */

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FileOperations {

    public static final int OPEN = 1;

    public static final int SAVE = 2;

    //Using a filter
    /*public static String fileSelect(int type, String extension,
            String description) {
        JFileChooser fc = new JFileChooser();
        JFrame fileFrame = new JFrame();
        MultiFileFilter filter = new MultiFileFilter(extension, description);

        fc.setFileFilter(filter);
        int ret = 0;

        if (type == OPEN)
            ret = fc.showOpenDialog(fileFrame);
        else if (type == SAVE)
            fc.showSaveDialog(fileFrame);
        else
            return null;
        if( ret == JFileChooser.CANCEL_OPTION)
            return null;
        File selFile = fc.getSelectedFile();
        if (selFile == null)
            return null;
        File fPath = fc.getCurrentDirectory();
        String sPath = fPath.getAbsolutePath();
        sPath += "\\";
        sPath += fc.getName(selFile);

        return sPath;
    }*/

    public static File fileSelect(int type, String extension,
        String description, Component parent) {
    JFileChooser fc = new JFileChooser();
    MultiFileFilter filter = new MultiFileFilter(extension, description);

    fc.setFileFilter(filter);
    int ret = 0;

    if (type == OPEN)
        ret = fc.showOpenDialog(parent);
    else if (type == SAVE)
        fc.showSaveDialog(parent);
    else
        return null;
    if( ret == JFileChooser.CANCEL_OPTION)
        return null;
    File selFile = fc.getSelectedFile();
    /*if (selFile == null)
        return null;
    File fPath = fc.getCurrentDirectory();
    String sPath = fPath.getAbsolutePath();
    sPath += "\\";
    sPath += fc.getName(selFile);*/

    return selFile;
}


    //Sans filter
    public static String fileSelect(int type) {
        JFileChooser fc = new JFileChooser();
        JFrame fileFrame = new JFrame();

        if (type == OPEN)
            fc.showOpenDialog(fileFrame);
        else if (type == SAVE)
            fc.showSaveDialog(fileFrame);
        else
            return null;

        File selFile = fc.getSelectedFile();
        if (selFile == null)
            return null;
        File fPath = fc.getCurrentDirectory();
        String sPath = fPath.getAbsolutePath();
        sPath += "\\";
        sPath += fc.getName(selFile);

        return sPath;
    }

}
