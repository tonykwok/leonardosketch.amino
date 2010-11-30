package org.joshy.gfx.util.localization;

import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import com.joshondesign.xml.XMLParser;
import org.joshy.gfx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 19, 2010
 * Time: 2:15:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Localization {
    private static String masterLocaleName;
    private static HashMap<String, DynamicString> translations;

    public static void init(URL translationStore, String localeName) throws Exception {
        masterLocaleName = localeName;
        translations = new HashMap<String,DynamicString>();
        Doc doc = XMLParser.parse(translationStore.openStream());
        for(Elem set : doc.xpath("//set")) {
            String prefix = set.attr("name");
            for(Elem key : set.xpath("key")) {
                String keyName = key.attr("name");
                for(Elem value : key.xpath("value")) {
                    String language = value.attr("language");
                    String translationKey = prefix+"."+ keyName+"."+language;
                    String translationValue = value.text();
                    if(language.length() <= 0) {
                        translationKey = prefix+"."+keyName;
                    }
                    translations.put(translationKey,new DynamicString(prefix,keyName,language,translationValue));
                }
            }
        }
        
    }

    public static CharSequence getString(String key) {
        if(translations.containsKey(key+"."+masterLocaleName)) {
            return translations.get(key+"."+masterLocaleName);
        }
        DynamicString s = translations.get(key);
        return s;
    }


    public static void launchEditor() {
        Stage stage = Stage.createStage();
        stage.setContent(new TranslationEditor());
        stage.setWidth(700);
        stage.setHeight(400);
    }

    public static Set<String> getAllKeys() {
        return translations.keySet();
    }

    public static DynamicString getDynamicString(String key) {
        return translations.get(key);
    }

    public static DynamicString createDynamicString(String prefix, String key, String lang, String value) {
        DynamicString ds = new DynamicString(prefix, key, lang, value);
        translations.put(prefix+"."+key+"."+lang,ds);
        return ds;
    }

    public static class DynamicString implements CharSequence {
        private String key;
        private String prefix;
        private String locale;
        private String value;

        public DynamicString(String prefix, String key, String locale, String value) {
            this.prefix = prefix;
            this.key = key;
            this.locale = locale;
            this.value = value;
        }

        public int length() {
            return getValue().length();
        }

        public char charAt(int i) {
            return getValue().charAt(i);
        }

        public CharSequence subSequence(int i, int i1) {
            return getValue().subSequence(i,i1);
        }

        @Override
        public String toString() {
            return getValue();
        }


        public String getValue() {
            return value;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getKey() {
            return key;
        }

        public String getLang() {
            return this.locale;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
