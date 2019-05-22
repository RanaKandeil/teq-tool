package controllers;

import javafx.beans.property.SimpleStringProperty;

public class KeyAndValue
{
   private SimpleStringProperty key;

   private SimpleStringProperty value;

   public KeyAndValue(String key, String value)
   {
      this.key = new SimpleStringProperty(key);
      this.value = new SimpleStringProperty(value);
   }

   public String getKey()
   {
      return key.get();
   }

   public void setKey(SimpleStringProperty key)
   {
      this.key = key;
   }

   public String getValue()
   {
      return value.get();
   }

   public void setValue(SimpleStringProperty value)
   {
      this.value = value;
   }
}
