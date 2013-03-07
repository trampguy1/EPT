/*
 Jazzy - a Java library for Spell Checking
 Copyright (C) 2001 Mindaugas Idzelis
 Full text of license can be found in LICENSE.txt

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package mil.af.rl.jcat.util;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.SpellDictionaryCachedDichoDisk;
import com.swabunga.spell.event.DocumentWordTokenizer;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.swing.JSpellDialog;
import com.swabunga.spell.swing.autospell.AutoSpellEditorKit;

import javax.swing.*;
import javax.swing.text.*;

import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.List;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import com.swabunga.spell.swing.autospell.*;


public class JTextComponentSpellChecker implements SpellCheckListener
{

	// private static final String COMPLETED="COMPLETED";
	private String dialogTitle = null;
	private SpellChecker spellCheck = null;
	private JSpellDialog dlg = null;
	private JTextComponent textComp = null;
	private ResourceBundle messages;
	private SpellDictionary mainDict = null;
	private AutoSpellCheckHandler markHandler;
	private static Logger logger = Logger.getLogger(JTextComponentSpellChecker.class);

	// Constructor
	public JTextComponentSpellChecker(SpellDictionary dict)
	{
		this(dict, null, null);
	}

	// Convinient Constructors, for those lazy guys.
	public JTextComponentSpellChecker(String dictFile) throws IOException
	{
		this(dictFile, null);
	}

	public JTextComponentSpellChecker(String dictFile, String title) throws IOException
	{
		this(new SpellDictionaryHashMap(new File(dictFile)), null, title);
	}

	public JTextComponentSpellChecker(String dictFile, String phoneticFile, String title) throws IOException
	{
		this(new SpellDictionaryHashMap(new File(dictFile), new File(phoneticFile)), null, title);
	}

	public JTextComponentSpellChecker(SpellDictionary dict, SpellDictionary userDict, String title)
	{
		spellCheck = new SpellChecker(dict);
		mainDict = dict;
		spellCheck.setCache();
		if(userDict != null)
			spellCheck.setUserDictionary(userDict);
		spellCheck.addSpellCheckListener(this);
		dialogTitle = title;
		messages = ResourceBundle.getBundle("com.swabunga.spell.swing.messages", Locale.getDefault());
		markHandler = new AutoSpellCheckHandler(spellCheck, messages);
	}

	public JTextComponentSpellChecker(List<SpellDictionary> dicts, SpellDictionary userDict, String title)
	{
		spellCheck = new SpellChecker();
		mainDict = dicts.get(0);
		Iterator<SpellDictionary> dictIt = dicts.iterator();
		while(dictIt.hasNext())
			spellCheck.addDictionary(dictIt.next());

		spellCheck.setCache();
		if(userDict != null)
			spellCheck.setUserDictionary(userDict);
		spellCheck.addSpellCheckListener(this);
		dialogTitle = title;
		messages = ResourceBundle.getBundle("com.swabunga.spell.swing.messages", Locale.getDefault());
		markHandler = new AutoSpellCheckHandler(spellCheck, messages);
	}

	/**
	 * Set user dictionary (used when a word is added)
	 */
	public void setUserDictionary(SpellDictionary dictionary)
	{
		if(spellCheck != null)
			spellCheck.setUserDictionary(dictionary);
	}

	private void setupDialog(JTextComponent textComp, Component parent)
	{

		Component comp = null;
		if(parent != null)
			comp = parent;
		else
			comp = SwingUtilities.getRoot(textComp);

		// Probably the most common situation efter the first time.
		if(dlg != null && dlg.getOwner() == comp)
			return;

		if(comp != null && comp instanceof Window)
		{
			if(comp instanceof Frame)
				dlg = new JSpellDialog((Frame) comp, dialogTitle, true);
			if(comp instanceof Dialog)
				dlg = new JSpellDialog((Dialog) comp, dialogTitle, true);
			// Put the dialog in the middle of it's parent.
			if(dlg != null)
			{
				Window win = (Window) comp;
				int x = (int) (win.getLocation().getX() + win.getWidth() / 2 - dlg.getWidth() / 2);
				int y = (int) (win.getLocation().getY() + win.getHeight() / 2 - dlg.getHeight() / 2);
				dlg.setLocation(x, y);
			}
		}
		else
		{
			dlg = new JSpellDialog((Frame) null, dialogTitle, true);
		}
	}

	/**
	 * This method is called to check the spelling of a JTextComponent.
	 * 
	 * @param textComp The JTextComponent to spellcheck.
	 * @param parent The parent to center the component.
	 * @return Either SpellChecker.SPELLCHECK_OK,
	 *         SpellChecker.SPELLCHECK_CANCEL or the number of errors found.
	 *         The number of errors are those that are found BEFORE any
	 *         corrections are made.
	 */
	public synchronized int spellCheck(JTextComponent textComp, Component parent)
	{
		setupDialog(textComp, parent);
		this.textComp = textComp;

		DocumentWordTokenizer tokenizer = new DocumentWordTokenizer(textComp.getDocument());
		int exitStatus = spellCheck.checkSpelling(tokenizer);

		textComp.requestFocus();
		textComp.setCaretPosition(0);
		this.textComp = null;
		try
		{
			if(mainDict instanceof SpellDictionaryCachedDichoDisk)
				((SpellDictionaryCachedDichoDisk) mainDict).saveCache();
		}catch(IOException ex)
		{
			logger.error("spellCheck - IOExc error saving cache:  " + ex.getMessage());
		}
		return exitStatus;
	}

	/**
	 * @param pane
	 */
	public void startAutoSpellCheck(JEditorPane pane)
	{
		Document doc = pane.getDocument();
		pane.setEditorKit(new AutoSpellEditorKit((StyledEditorKit) pane.getEditorKit()));
		pane.setDocument(doc);
		markHandler.addJEditorPane(pane);
	}

	/**
	 * @param pane
	 */
	public void stopAutoSpellCheck(JEditorPane pane)
	{
		EditorKit kit;
		Document doc;
		if(pane.getEditorKit() instanceof com.swabunga.spell.swing.autospell.AutoSpellEditorKit)
		{
			doc = pane.getDocument();
			kit = ((com.swabunga.spell.swing.autospell.AutoSpellEditorKit) pane.getEditorKit()).getStyledEditorKit();
			pane.setEditorKit(kit);
			pane.setDocument(doc);
		}
		markHandler.removeJEditorPane(pane);
	}

	/**
	 * 
	 */
	public void spellingError(SpellCheckEvent event)
	{

		// java.util.List suggestions = event.getSuggestions();
		event.getSuggestions();
		int start = event.getWordContextPosition();
		int end = start + event.getInvalidWord().length();

		// Mark the invalid word in TextComponent
		textComp.requestFocus();
		textComp.setCaretPosition(0);
		textComp.setCaretPosition(start);
		textComp.moveCaretPosition(end);

		dlg.show(event);
	}
}
