/*
 * Copyright (c) Dematic GmbH 2017. All rights reserved. Confidential.
 */
package com.advansys.validation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author waheedmm
 */
public class Validation
{
   /**
    * be sure that the dialog name is not ending with "OperationDialog"
    *
    * @param dialogName
    */
   public String validateDialogName(String dialogName)
   {
      if (dialogName != null)
      {
         dialogName = dialogName.trim();
         dialogName = dialogName.substring(0, 1).toUpperCase() + dialogName.substring(1);
         if (dialogName.toLowerCase().endsWith("operationdialog"))
         {
            dialogName = dialogName.substring(0, dialogName.toLowerCase().lastIndexOf("operationdialog"));
         }
      }
      else
      {
         throw new RuntimeException("No dialog name found");
      }

      return dialogName;
   }

   /**
    * to trim the passed string and be sure that the first char in upper case
    *
    * @param name
    */
   public String validateName(String name)
   {
      if (name != null)
      {
         name = name.trim();
         name = name.substring(0, 1).toUpperCase() + name.substring(1);
      }
      else
      {
         throw new RuntimeException("No project name found");
      }
      return name;
   }

   public String validateUserName(String userName)
   {
      if (userName != null)
      {
         userName = userName.trim();
      }
      else
      {
         userName = "userName";
      }
      return userName;
   }

   /**
    * be sure that the steps and the views names are following the naming convention
    *
    * @param stepViewMap
    */
   public LinkedHashMap<String, String> validateStepViewNames(LinkedHashMap<String, String> stepViewMap)
   {
      LinkedHashMap<String, String> newStepViewMap = new LinkedHashMap<>();
      Iterator<Map.Entry<String, String>> it = stepViewMap.entrySet().iterator();
      while (it.hasNext())
      {
         Map.Entry<String, String> entry = it.next();
         String stepName = entry.getKey();
         String viewName = entry.getValue();

         stepName = validateStepName(stepName);
         viewName = validateViewName(viewName);
         it.remove();
         newStepViewMap.put(stepName, viewName);
      }
      return newStepViewMap;
   }

   private String validateViewName(String viewName)
   {
      viewName = viewName.trim();
      viewName = viewName.substring(0, 1).toUpperCase() + viewName.substring(1);
      if (viewName.endsWith("view"))
      {
         viewName = viewName.replace("view", "View");
      }
      else if (!viewName.endsWith("View"))
      {
         viewName = viewName + "View";
      }
      return viewName;
   }

   public String validatePushEventClassName(String pushEventClassName)
   {
      pushEventClassName = pushEventClassName.trim();
      //pushEventClassName = pushEventClassName.substring(0, 1).toUpperCase() + pushEventClassName.substring(1);
      if(pushEventClassName.endsWith("pushevent") || pushEventClassName.endsWith("pushEvent") || pushEventClassName.endsWith("PushEvent") || pushEventClassName.endsWith("Pushevent")){
         pushEventClassName = pushEventClassName.toLowerCase();
         pushEventClassName = pushEventClassName.substring(0, 1).toUpperCase() + pushEventClassName.substring(1);
         pushEventClassName = pushEventClassName.replace("pushevent", "PushEvent");
      }
      else if (pushEventClassName.endsWith("event") || pushEventClassName.endsWith("Event"))
      {
         pushEventClassName = pushEventClassName.toLowerCase();
         pushEventClassName = pushEventClassName.substring(0, 1).toUpperCase() + pushEventClassName.substring(1);
         pushEventClassName = pushEventClassName.replace("event", "PushEvent");
      }
      else if(pushEventClassName.endsWith("eventmapper") || pushEventClassName.endsWith("eventMapper") || pushEventClassName.endsWith("EventMapper") || pushEventClassName.endsWith("Eventmapper")){
         pushEventClassName = pushEventClassName.toLowerCase();
         pushEventClassName = pushEventClassName.substring(0, 1).toUpperCase() + pushEventClassName.substring(1);
         pushEventClassName = pushEventClassName.replace("eventmapper", "PushEvent");
      }
      else if (!pushEventClassName.endsWith("PushEvent"))
      {
         pushEventClassName = pushEventClassName + "PushEvent";
      }
      return pushEventClassName;
   }

   public String validateeventClassName(String eventClassName)
   {
      eventClassName = eventClassName.trim();
      //eventClassName = eventClassName.substring(0, 1).toUpperCase() + eventClassName.substring(1);
      if(eventClassName.endsWith("pushevent") || eventClassName.endsWith("pushEvent") || eventClassName.endsWith("PushEvent") || eventClassName.endsWith("Pushevent"))
      {
         eventClassName = eventClassName.toLowerCase();
         eventClassName = eventClassName.substring(0, 1).toUpperCase() + eventClassName.substring(1);
         eventClassName = eventClassName.replace("pushevent", "Event");
      }
      else if (eventClassName.endsWith("event") || eventClassName.endsWith("Event"))
      {
         eventClassName = eventClassName.substring(0, 1).toUpperCase() + eventClassName.substring(1);
         eventClassName = eventClassName.replace("event", "Event");
      }
      else if(eventClassName.endsWith("eventmapper") || eventClassName.endsWith("eventMapper") || eventClassName.endsWith("EventMapper") || eventClassName.endsWith("Eventmapper")){
         eventClassName = eventClassName.toLowerCase();
         eventClassName = eventClassName.substring(0, 1).toUpperCase() + eventClassName.substring(1);
         eventClassName = eventClassName.replace("eventmapper", "Event");
      }
      else if (!eventClassName.endsWith("Event"))
      {
         eventClassName = eventClassName + "Event";
      }
      return eventClassName;
   }

   private String validateStepName(String stepName)
   {
      stepName = stepName.trim();
      stepName = stepName.substring(0, 1).toUpperCase() + stepName.substring(1);
      if (stepName.endsWith("step"))
      {
         stepName = stepName.replace("step", "Step");
      }
      else if (!stepName.endsWith("Step"))
      {
         stepName = stepName + "Step";
      }
      return stepName;
   }

   public String validateMapperClassName(String mapperClassName)
   {
      mapperClassName = mapperClassName.trim();
      //mapperClassName = mapperClassName.substring(0, 1).toUpperCase() + mapperClassName.substring(1);

      if(mapperClassName.endsWith("pushevent") || mapperClassName.endsWith("pushEvent") || mapperClassName.endsWith("PushEvent") || mapperClassName.endsWith("Pushevent")){
         mapperClassName = mapperClassName.toLowerCase();
         mapperClassName = mapperClassName.substring(0, 1).toUpperCase() + mapperClassName.substring(1);
         mapperClassName = mapperClassName.replace("pushevent", "EventMapper");
      }
      else if (mapperClassName.endsWith("event") || mapperClassName.endsWith("Event"))
      {
         mapperClassName = mapperClassName.toLowerCase();
         mapperClassName = mapperClassName.substring(0, 1).toUpperCase() + mapperClassName.substring(1);
         mapperClassName = mapperClassName.replace("event", "EventMapper");
      }
      else if(mapperClassName.endsWith("eventmapper") || mapperClassName.endsWith("eventMapper") || mapperClassName.endsWith("EventMapper") || mapperClassName.endsWith("Eventmapper")){
         mapperClassName = mapperClassName.toLowerCase();
         mapperClassName = mapperClassName.substring(0, 1).toUpperCase() + mapperClassName.substring(1);
         mapperClassName = mapperClassName.replace("eventmapper", "EventMapper");
      }
      else if (!mapperClassName.endsWith("EventMapper"))
      {
         mapperClassName = mapperClassName + "EventMapper";
      }
      return mapperClassName;
   }
}
