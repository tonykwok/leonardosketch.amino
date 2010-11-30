package org.joshy.gfx.util.localization;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SelectionEvent;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.util.ArrayListModel;
import org.joshy.gfx.util.control.StandardDialogs;
import org.joshy.gfx.util.u;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TranslationEditor extends VFlexBox {
    public TranslationEditor() {

        final Textbox editBox = new Textbox();
        editBox.setPrefWidth(200);

        final Set<String> keys = Localization.getAllKeys();

        final Map<String,Prefix> prefixes = new HashMap<String, Prefix>();
        for(String dsKey : keys) {
            Localization.DynamicString ds = Localization.getDynamicString(dsKey);
            if(!prefixes.containsKey(ds.getPrefix())) {
                prefixes.put(ds.getPrefix(),new Prefix(ds.getPrefix()));
            }
            Prefix pf = prefixes.get(ds.getPrefix());
            if(!pf.keyMap.containsKey(ds.getKey())) {
                Key k = new Key(ds.getKey());
                pf.keyMap.put(ds.getKey(),k);
                pf.keys.add(k);
            }
            Key key = pf.keyMap.get(ds.getKey());
            String lang = ds.getLang();
            u.p("lang = " + lang);
            if(lang == null || "".equals(lang.trim())) {
                lang = "DEFAULT";
            }
            Localization.DynamicString value = ds;
            if(!key.langMap.containsKey(lang)) {
                key.langMap.put(lang,value);
                key.langs.add(lang);
            }

        }

        
        final ArrayListModel<Prefix> prefixList = new ArrayListModel<Prefix>();
        prefixList.addAll(prefixes.values());
        
        final ListView<Prefix> prefixView = new ListView<Prefix>();
        prefixView.setModel(prefixList);
        final ListView<Key> keyView = new ListView<Key>();
        final ListView<String> langView = new ListView<String>();


        EventBus.getSystem().addListener(SelectionEvent.Changed, new Callback<SelectionEvent>(){
            public void call(SelectionEvent selectionEvent) throws Exception {
                u.p("changed: " + selectionEvent.getSource());

                if(selectionEvent.getView() instanceof ListView) {
                    if(selectionEvent.getView() == prefixView) {
                        Prefix pf = prefixList.get(selectionEvent.getView().getSelectedIndex());
                        u.p("updating the model");
                        u.p("pf = " + pf);
                        keyView.setModel(pf.keys);
                    }
                    if(selectionEvent.getView() == keyView) {
                        Prefix prefix = prefixList.get(prefixView.getSelectedIndex());
                        Key key = prefix.keys.get(keyView.getSelectedIndex());
                        langView.setModel(key.langs);
                    }
                    if(selectionEvent.getView() == langView) {
                        Prefix prefix = prefixList.get(prefixView.getSelectedIndex());
                        Key key = prefix.keys.get(keyView.getSelectedIndex());
                        String lang = key.langs.get(langView.getSelectedIndex());
                        Localization.DynamicString ds = key.langMap.get(lang);
                        editBox.setText(ds.toString());
                    }
                }
            }
        });
        Callback<ActionEvent> setString = new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                Key key = keyView.getModel().get(keyView.getSelectedIndex());
                String lang = langView.getModel().get(langView.getSelectedIndex());
                String value = editBox.getText();
                key.langMap.get(lang).setValue(value);
                Core.getShared().reloadSkins();
            }
        };
        this.setBoxAlign(Align.Stretch);
        Callback<ActionEvent> addLangAction = new Callback<ActionEvent>(){
            @Override
            public void call(ActionEvent event) throws Exception {
                Prefix prefix = prefixView.getModel().get(prefixView.getSelectedIndex());
                String newLang = StandardDialogs.showEditText("New Locale","en-US");
                Key key = keyView.getModel().get(keyView.getSelectedIndex());
                Localization.DynamicString ds = Localization.createDynamicString(
                        prefix.prefix, key.key, newLang, "---"
                );
                key.langMap.put(newLang,ds);
                key.langs.add(newLang);
            }
        };
        this.add(
                new HFlexBox()
                        .setBoxAlign(Align.Stretch)
                        .add(new ScrollPane(prefixView)
                                .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                                .setPrefWidth(150))
                        .add(new ScrollPane(keyView)
                                .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                                .setPrefWidth(150))
                        .add(new VFlexBox()
                            .add(new ScrollPane(langView)
                                    .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                                    .setPrefWidth(100)
                                    .setPrefHeight(100),1)
                            .add(new Button("Add Lang").onClicked(addLangAction))
                            .add(new Button("Del Lang"))
                        )
                        .add(editBox)
                        .add(new Button("Set").onClicked(setString))
        ,1);

        Button applyButton = new Button("Apply");
        Button exportButton = new Button("export");

        this.add(new HFlexBox()
            .add(applyButton)
            .add(exportButton));
    }

    class Prefix {
        ArrayListModel<Key> keys = new ArrayListModel<Key>();
        private String prefix;
        public Map<String,Key> keyMap = new HashMap<String,Key>();

        public Prefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String toString() {
            return "prefix: " + prefix;
        }
    }

    class Key {
        private String key;
        ArrayListModel<String> langs = new ArrayListModel<String>();
        private Map<String, Localization.DynamicString> langMap = new HashMap<String, Localization.DynamicString>();

        public Key(String key) {
            this.key = key;
        }
        public String toString() {
            return "key: " + key;
        }
    }
}
