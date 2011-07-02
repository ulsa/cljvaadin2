(ns cljvaadin2.VaadinServlet
  (:use [cljvaadin2.core :only (main)])
  (:gen-class
   :extends com.vaadin.terminal.gwt.server.AbstractApplicationServlet))

(defn -getApplicationClass [_] (class com.vaadin.Application))

(defn -getNewApplication [_ _] (main))