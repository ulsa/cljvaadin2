;; Copied from https://github.com/hsenid-mobile/clj-vaadin/blob/master/src/vaadin/core.clj
(ns cljvaadin2.vaadin
  (:import [com.vaadin Application]
	   [com.vaadin.data Property$ValueChangeListener]
	   [com.vaadin.ui
	    AbstractComponentContainer
	    Button Button$ClickEvent Button$ClickListener
	    Component
	    GridLayout
	    Label
	    Window Window$CloseListener Window$Notification]))

(defn show-message [window caption description]
  (.showNotification window caption description Window$Notification/TYPE_HUMANIZED_MESSAGE))
(defn show-warning [window caption description]
  (.showNotification window caption description Window$Notification/TYPE_WARNING_MESSAGE))
(defn show-error [window caption description]
  (.showNotification window caption description Window$Notification/TYPE_ERROR_MESSAGE))
(defn show-tray-notification [window caption description]
  (.showNotification window caption description Window$Notification/TYPE_TRAY_NOTIFICATION))

(defn add-components [container & components]
  (doseq [component components]
    (.addComponent container component))
  container) ; added by ulsa

(defn- app-close-listener [app]
  (reify Window$CloseListener
	 (windowClose [this event]
		      (.close app))))

(defn main-window [app title layout]
  (let [window (Window. title layout)]
    ;; removed by ulsa
    ;;(.setSizeUndefined layout)
    (.setMainWindow app window)
    (.addListener window (app-close-listener app))
    window))

(defn button-click-listener [listener]
  (reify Button$ClickListener
	 (buttonClick [this event]
		      (listener event))))

(defn property-change-listener [listener]
  (reify Property$ValueChangeListener
	 (valueChange [this event]
		      (listener event))))