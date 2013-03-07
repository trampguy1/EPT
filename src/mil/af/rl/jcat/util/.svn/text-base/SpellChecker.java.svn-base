package mil.af.rl.jcat.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import mil.af.rl.jcat.exceptions.MissingRequiredFileException;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
//import com.swabunga.spell.swing.JTextComponentSpellChecker;

/*
 * Created on Oct 5, 2005
 */

public class SpellChecker //extends JFrame
{
	private static JTextComponentSpellChecker checker;
	//private static String phoneticFileName = this.getClass().getClassLoader().getResource("dict/phonet.en");
	private static boolean dictsLoaded = false;
	//private static URI userDicLoc = null;
	private static JTextComponent text = null;
	private static Logger logger = Logger.getLogger(SpellChecker.class);
	private static boolean initialized;
	

	public static String check(String text, java.awt.Component parent) throws MissingRequiredFileException
	{
		JTextArea temp = new JTextArea(text);
		if(!dictsLoaded)
			init();
		
		if(!initialized)
			throw new MissingRequiredFileException("Spell Checker was not initialized properly");
		int result = checker.spellCheck(temp, parent);
		
		if(result == com.swabunga.spell.event.SpellChecker.SPELLCHECK_CANCEL)
			return null;
		else
			return temp.getText();
	}
	
	public static void check(JTextComponent inTextArea) throws MissingRequiredFileException
	{
		text = inTextArea;
		if(!dictsLoaded)
			init();
		
		checker.spellCheck(text, null);
	}
	
	public static void preInit() throws MissingRequiredFileException
	{
		init();
	}
	
	private static void init() throws MissingRequiredFileException
	{
		Vector<SpellDictionary> dictionaries = new Vector<SpellDictionary>();
		InputStream dicLoc = null;
		String userDicLoc = null;
		
		try{
			try{
//				dicLoc = new File("resources/config/eng_com.dic").getPath();
//				dictionaries.add(new SpellDictionaryHashMap(new File(dicLoc), null));
//				dicLoc = new File("resources/config/center.dic").getPath();
//				dictionaries.add(new SpellDictionaryHashMap(new File(dicLoc), null));
//				dicLoc = new File("resources/config/color.dic").getPath();
//				dictionaries.add(new SpellDictionaryHashMap(new File(dicLoc), null));
//				dicLoc = new File("resources/config/ize.dic").getPath();
//				dictionaries.add(new SpellDictionaryHashMap(new File(dicLoc), null));
//				dicLoc = new File("resources/config/labeled.dic").getPath();
//				dictionaries.add(new SpellDictionaryHashMap(new File(dicLoc), null));
//				dicLoc = new File("resources/config/yze.dic").getPath();
//				dictionaries.add(new SpellDictionaryHashMap(new File(dicLoc), null));
				
				// This new way of loading resources worx with a common resource jar for Web Start
				dicLoc = SpellChecker.class.getClassLoader().getResourceAsStream("eng_com.dic");
				dictionaries.add(new SpellDictionaryHashMap(new InputStreamReader(dicLoc), null));
				dicLoc = SpellChecker.class.getClassLoader().getResourceAsStream("center.dic");
				dictionaries.add(new SpellDictionaryHashMap(new InputStreamReader(dicLoc), null));
				dicLoc = SpellChecker.class.getClassLoader().getResourceAsStream("color.dic");
				dictionaries.add(new SpellDictionaryHashMap(new InputStreamReader(dicLoc), null));
				dicLoc = SpellChecker.class.getClassLoader().getResourceAsStream("ize.dic");
				dictionaries.add(new SpellDictionaryHashMap(new InputStreamReader(dicLoc), null));
				dicLoc = SpellChecker.class.getClassLoader().getResourceAsStream("labeled.dic");
				dictionaries.add(new SpellDictionaryHashMap(new InputStreamReader(dicLoc), null));
				dicLoc = SpellChecker.class.getClassLoader().getResourceAsStream("yze.dic");
				dictionaries.add(new SpellDictionaryHashMap(new InputStreamReader(dicLoc), null));
				
				
				dictsLoaded = true;
			}catch(NullPointerException exc){
				//this is unexceptable, spell checker cannot continue
				throw new MissingRequiredFileException("Spell Checker dictionary file could not be found.");
			}catch(IOException exc){
				throw new MissingRequiredFileException("Spell Checker dictionary file could not be found.");
			}
			
//			try{
//				userDicLoc = this.getClass().getClassLoader().getResource("userdict.dic").toURI();
//			}catch(NullPointerException exc){
				//user dictionary is missing, just make a new empty one
				//changed the location of this without moving old userdic (nobody will care at this point) -MikeD
				//String dicLocStr = dicLoc.toString();
				userDicLoc = (EnvUtils.getJCATSettingsHome()+"/userdict.dic");
//				logger.info("init - NullPointerExc, no user-dictionary found:  "+exc.getMessage());
//			}
//		}catch(URISyntaxException exc){
//			logger.error("init - URISyntaxExc, error loading spell check dictionaries"+dicLoc+":  "+exc.getMessage(), exc);
//			throw new MissingRequiredFileException("Spell Checker dictionary file could not be found.", exc);
		}
		catch(Exception exc){
			logger.error("init - Exception, error loading spell check dictionaries"+dicLoc+":  "+exc.getMessage(), exc);
			throw new MissingRequiredFileException("An error occured while loading spell check dictionaries.", exc);
		}

		try{
			File userDictFile = new File(userDicLoc);
			
			if(!userDictFile.exists())
				userDictFile.createNewFile();
			
			SpellDictionary userDict = new SpellDictionaryHashMap(userDictFile, null);
			
			checker = new JTextComponentSpellChecker(dictionaries, userDict, "Spell Check");
			initialized = true;
			
		}catch(Exception exc){
			initialized = false;
			logger.error("init - error initializing spellchecker - "+exc.getMessage());
		}
	}
}
