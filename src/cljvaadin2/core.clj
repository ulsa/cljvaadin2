(ns cljvaadin2.core
  (:require [clojure.string :as str])
  (:import [com.vaadin Application]
	   [com.vaadin.data Property$ValueChangeListener]
	   [com.vaadin.data.util IndexedContainer]
	   [com.vaadin.ui Window Label Form Table TextField
	    Button Button$ClickListener
	    SplitPanel HorizontalLayout VerticalLayout]))

(def contact-list (Table.))
(def contact-editor (Form.))
(def bottom-left-corner (HorizontalLayout.))
(def fields ["First Name" "Last Name" "Company" "Mobile Phone" "Work Phone"
	     "Home Phone" "Work Email" "Home Email" "Street" "Zip" "City" "State"
	     "Country"])
(def visible-cols ["Last Name" "First Name" "Company"])

(defn create-dummy-data []
  (let [fnames ["Peter" "Alice" "Joshua" "Mike" "Olivia" "Nina" "Alex" "Rita"
		"Dan" "Umberto" "Henrik" "Rene" "Lisa" "Marge"]
	lnames ["Smith" "Gordon" "Simpson" "Brown" "Clavel" "Simons" "Verne"
		"Scott" "Allison" "Gates" "Rowling" "Barks" "Ross" "Schneider" "Tate"]
	ic (IndexedContainer.)]

    (doseq [p fields]
      (.addContainerProperty ic p String ""))

    (doseq [i (range 1000)]
      (let [id (.addItem ic)
	    fn (count fnames)
	    ln (count lnames)]
	(.. ic
	    (getContainerProperty id "First Name")
	    (setValue (fnames (rand-int fn))))
	(.. ic
	    (getContainerProperty id "Last Name")
	    (setValue (lnames (rand-int ln))))))
    ic))

(def address-book-data (create-dummy-data))

(defn init-layout [app]
  (let [left (doto (VerticalLayout.)
	       .setSizeFull
	       (.addComponent contact-list)
	       (.setExpandRatio contact-list 1))
	split-panel (doto (SplitPanel. SplitPanel/ORIENTATION_HORIZONTAL)
		      (.addComponent left)
		      (.addComponent contact-editor))]
    (.setSizeFull contact-list)
    (.setMainWindow app (Window. "Address Book" split-panel))
    (.setSizeFull contact-editor)
    (.. contact-editor getLayout (setMargin true))
    (.setImmediate contact-editor true)
    (.setWidth bottom-left-corner "100%")
    (.addComponent left bottom-left-corner)))

(defn init-contact-add-remove-buttons [_]
  (doto bottom-left-corner
    (.addComponent
     (Button. "+" 
	      (proxy [Button$ClickListener] []
		(buttonClick [event]
			     (.setValue contact-list (.addItem contact-list)))))))
  (def contact-removal-button
     (Button. "-"
	      (proxy [Button$ClickListener] []
		(buttonClick [event]
			     (.removeItem contact-list (.getValue contact-list))
			     (.select contact-list nil)))))
  (.setVisible contact-removal-button false)
  (.addComponent bottom-left-corner contact-removal-button))

(defn init-address-list [_]
  (.setContainerDataSource contact-list address-book-data)
  (.setVisibleColumns contact-list (into-array visible-cols))
  (.setSelectable contact-list true)
  (.setImmediate contact-list true)
  (.addListener contact-list
		(proxy [Property$ValueChangeListener] []
		  (valueChange [event]
			       (let [id (.getValue contact-list)]
				 (.setItemDataSource contact-editor
						     (if id (.getItem contact-list id)))
				 (.setVisible contact-removal-button (if id true false))))))
  visible-cols)

(defn init-filtering-controls [app]
  (doseq [pn visible-cols]
    (let [sf (TextField.)]
      (.addComponent bottom-left-corner sf)
      (.setWidth sf "100%")
      (.setInputPrompt sf pn)
      (.setImmediate sf true)
      (.setExpandRatio bottom-left-corner sf 1)
      (.addListener sf (proxy [Property$ValueChangeListener] []
			 (valueChange [event]
				      (.removeContainerFilters address-book-data pn)
				      (when (and (not (str/blank? (.toString sf)))
						 (not= pn sf))
					(.addContainerFilter address-book-data pn (.toString sf)
							     true false))
				      (.. app
					  getMainWindow
					  (showNotification (str (.size address-book-data)
								 " matches found")))))))))
  
(defn main []
  (proxy [Application] []
    (init []
	  (init-layout this)
	  (init-contact-add-remove-buttons this)
	  (init-address-list this)
	  (init-filtering-controls this))))
