package controllers;

import javafx.beans.property.SimpleStringProperty;

public class StepAndView
{
   private SimpleStringProperty step;

   private SimpleStringProperty view;

   public StepAndView(String step, String view)
   {
      this.step = new SimpleStringProperty(step);
      this.view = new SimpleStringProperty(view);
   }

   public String getStep()
   {
      return step.get();
   }

   public void setStep(SimpleStringProperty step)
   {
      this.step = step;
   }

   public String getView()
   {
      return view.get();
   }

   public void setView(SimpleStringProperty view)
   {
      this.view = view;
   }
}
