package controllers;

/**
 * Created by galalme on 5/4/2017.
 */
public class MethodParam
{
   String type;

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   String name;

   @Override
   public String toString()
   {
      return this.type + " " + this.name;
   }
}
