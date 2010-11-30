package org.joshy.gfx.util.localization;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SelectionEvent;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.util.ArrayListModel;
import org.joshy.gfx.util.control.StandardDialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TranslationEditor extends VFlexBox {
    public TranslationEditor() {

        final Textbox editBox = new Textbox();
        editBox.setPrefWidth(200);

        final Set<String> keys = Localization.getAllKeys();

        final Map<String,Prefix> prefixes = new HashMap<String, Prefix>();
        ArrayListModel<String> currentLocaleModel = new ArrayListModel<String>();
        for(String dsKey : keys) {
            Localization.KeyString ks = Localization.getKeyString(dsKey);
            if(!prefixes.containsKey(ks.getPrefix())) {
                prefixes.put(ks.getPrefix(),new Prefix(ks.getPrefix()));
            }
            Prefix pf = prefixes.get(ks.getPrefix());
            if(!pf.keyMap.containsKey(ks.getKeyname())) {
                Key k = new Key(ks.getKeyname());
                pf.keyMap.put(ks.getKeyname(),k);
                pf.keys.add(k);
            }
            Key key = pf.keyMap.get(ks.getKeyname());
            key.keyString = ks;
            for(String lang : key.keyString.translations.keySet()) {
                if(!currentLocaleModel.contains(lang)) {
                    currentLocaleModel.add(lang);
                }
            }
        }

        
        final ArrayListModel<Prefix> prefixList = new ArrayListModel<Prefix>();
        prefixList.addAll(prefixes.values());
        
        final ListView<Prefix> prefixView = new ListView<Prefix>();
        prefixView.setModel(prefixList);
        final ListView<Key> keyView = new ListView<Key>();
        keyView.setModel(new ArrayListModel<Key>());
        final ListView<String> langView = new ListView<String>();
        langView.setModel(new ArrayListModel<String>());


        final PopupMenuButton<String> currentLocalePopup = new PopupMenuButton<String>()
                .setModel(currentLocaleModel);

        EventBus.getSystem().addListener(SelectionEvent.Changed, new Callback<SelectionEvent>(){
            public void call(SelectionEvent selectionEvent) throws Exception {
                if(selectionEvent.getView() instanceof ListView) {
                    if(selectionEvent.getView() == prefixView) {
                        Prefix pf = prefixList.get(selectionEvent.getView().getSelectedIndex());
                        keyView.setModel(pf.keys);
                        keyView.setSelectedIndex(-1);
                        langView.setSelectedIndex(-1);
                        editBox.setText("");
                    }
                    if(selectionEvent.getView() == keyView) {
                        Prefix prefix = prefixList.get(prefixView.getSelectedIndex());
                        Key key = prefix.keys.get(keyView.getSelectedIndex());
                        ArrayListModel<String> m = new ArrayListModel<String>();
                        m.addAll(key.keyString.translations.keySet());
                        langView.setModel(m);
                        langView.setSelectedIndex(-1);
                        editBox.setText("");
                    }
                    if(selectionEvent.getView() == langView) {
                        Prefix prefix = prefixList.get(prefixView.getSelectedIndex());
                        Key key = prefix.keys.get(keyView.getSelectedIndex());
                        String lang = langView.getModel().get(langView.getSelectedIndex());
                        String val = key.keyString.translations.get(lang);
                        editBox.setText(val);
                    }
                }
                if(selectionEvent.getView() == currentLocalePopup) {
                    String locale = currentLocalePopup.getModel().get(currentLocalePopup.getSelectedIndex());
                    Localization.setCurrentLocale(locale);
                    Core.getShared().reloadSkins();
                }
            }
        });

        Callback<ActionEvent> setString = new Callback<ActionEvent>() {
            public void call(ActionEvent actionEvent) throws Exception {
                Key key = keyView.getModel().get(keyView.getSelectedIndex());
                String lang = langView.getModel().get(langView.getSelectedIndex());
                String value = editBox.getText();
                key.keyString.setTranslation(lang,value);
                Core.getShared().reloadSkins();
            }
        };


        Callback<ActionEvent> addLangAction = new Callback<ActionEvent>(){
            @Override
            public void call(ActionEvent event) throws Exception {
                String newLang = StandardDialogs.showEditText("New Locale","en-US");
                Key key = keyView.getModel().get(keyView.getSelectedIndex());
                key.keyString.addTranslation(newLang,"---");
                ArrayListModel<String> m = new ArrayListModel<String>();
                m.addAll(key.keyString.translations.keySet());
                langView.setModel(m);
            }
        };


        this.setBoxAlign(Align.Stretch);
        this.add(new HFlexBox()
                .add(new Label("Current Locale"))
                .add(currentLocalePopup)
            ,0);
        this.add(new HFlexBox()
                .setBoxAlign(Align.Stretch)
                .add(new VFlexBox()
                        .add(new Label("Category"),0)
                        .add(new ScrollPane(prefixView)
                                .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                                .setPrefWidth(150),1)
                )
                .add(new VFlexBox()
                        .add(new Label("Key"),0)
                        .add(new ScrollPane(keyView)
                                .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                                .setPrefWidth(150),1)
                )
                .add(new VFlexBox()
                    .add(new Label("Locale"))
                    .add(new ScrollPane(langView)
                            .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                            .setPrefWidth(100)
                            .setPrefHeight(100),1)
                    .add(new Button("Add Lang").onClicked(addLangAction))
                    .add(new Button("Del Lang"))
                )
                .add(new VFlexBox()
                    .add(new Label("Translation"))
                    .add(editBox)
                    .add(new Button("Set").onClicked(setString))
                )
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
            return prefix;
        }
    }

    class Key {
        private String key;
        public Localization.KeyString keyString;

        public Key(String key) {
            this.key = key;
        }
        public String toString() {
            return key;
        }
    }
}
