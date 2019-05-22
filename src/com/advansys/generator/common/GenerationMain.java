package com.advansys.generator.common;

import com.advansys.generator.createnewpushevent.ParseAndCreateEventMapperWPN;
import com.advansys.generator.createnewpushevent.ParseAndCreateEventWPN;
import com.advansys.generator.createnewpushevent.ParseAndCreatePushEventWPN;
import com.advansys.generator.createnewstepbystepdialog.eventbutton.GenerationEventButton;
import com.advansys.generator.createnewstepbystepdialog.operationdialog.GenerationOperationDialog;
import com.advansys.generator.createnewstepbystepdialog.services.GenerationServiceAsync;
import com.advansys.generator.createnewstepbystepdialog.services.GenerationServiceFactory;
import com.advansys.generator.createnewstepbystepdialog.services.GenerationServiceImpl;
import com.advansys.generator.createnewstepbystepdialog.services.GenerationServiceInterface;
import com.advansys.generator.createnewstepbystepdialog.steps.GenerationStep;
import com.advansys.generator.createnewstepbystepdialog.views.GenerationView;
import com.advansys.validation.Validation;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

/**
 * Created by elbaklna on 6/19/2017.
 */
public class GenerationMain implements GenerationInterface {
    // project path (ex. C:\Projects\My Project)
    private static String projectPath;

    // (ex. LaRedoute)
    private static String projectName;

    // (ex. LoadUnitGuide)
    private static String dialogName;

    // base Event package name (ex. com\\dematic\\laredoute)
    private String baseEventName;

    // event package name (ex. C:\\Projects\\My
    // Project\\src\\main\\java\\com\\dematic\\laredoute
    private String eventDirectory;

    // base package name (ex. com\\dematic\\laredoute\\ui)
    private String basePackageName;

    // base package directory (ex. C:\\Projects\\MyProject\\src\\main\\java\\com\\dematic\\projectName\\ui)
    private String basePackageDirectory;

    // (ex. com.dematic.laredoute.ui.client.loadunitguide.LoadUnitGuideOperationDialog)
    private String dialogClassName;

    // (ex. com.dematic.laredoute.ui.client.loadunitguide.LoadUnitGuideServiceFactory)
    private String serviceFactoryClassName;

    // (ex. com.dematic.laredoute.ui.server.loadunitguide.service.LoadUnitGuideServiceImpl)
    private String serviceImplClassName;

    // (ex. com.dematic.laredoute.ui.client.loadunitguide.LoadUnitGuideService)
    private String serviceClassName;

    // (ex. com.dematic.laredoute.ui.client.loadunitguide.LoadUnitGuideServiceAsync)
    private String serviceAsyncClassName;

    // (ex. com.dematic.laredoute.ui.client.loadunitguide.steps.XStep)
    private String stepClassName;

    // (ex. com.dematic.laredoute.ui.client.loadunitguide.views.XView)
    private String viewClassName;

    // (ex. com.dematic.laredoute.ui.client.loadunitguide.SwitchToOtherStepEvent) //waheed
    // (ex. com.dematic.laredoute.loadunitguide.boundary.events)
    private String eventClassName;

    private boolean isGenerateEvent;

    // (ex. com.dematic.laredoute.ui.client.loadunitguide.service.event)
    private String pushEventClassName;

    // (ex. com.dematic.laredoute.ui.server.loadunitguide.mappers)
    private String EventMapperClassName;

    // a map holds step lists with its views
    private static LinkedHashMap<String, String> stepViewMap;

    private static LinkedHashMap<String, String> attributes;

    private static String userName;

    private static PrintWriter generatedFileWriter;

    private static File generatedFile;

    private static File operationFile;

    private static final Logger LOGGER = Logger.getLogger(GenerationMain.class.getName());

    private String eventMapperClassName;

    private GenerationType generationType;

    private String eventClassNameWPN, pushEventClassNameWPN, eventMapperClassNameWPN;

    private String eventNameWithoutTags, eventNameWithTags;

    Validation validation = new Validation();

    @Override
    public void createNewPushEvent(String projectPath, String projectName, String userName, String dialogName, String stepClassName, String eventClassName, LinkedHashMap<String, String> attributes, GenerationType generationType)
    {
        try {
            this.projectPath = projectPath;
            this.projectName = validation.validateName(projectName);
            this.userName = validation.validateUserName(userName);
            this.setStepClassName(stepClassName);
            this.dialogName = validation.validateDialogName(dialogName);
            this.setEventNameWithoutTags(eventClassName);
            this.setEventClassName(validation.validateeventClassName(eventClassName));
            this.setPushEventClassName(validation.validatePushEventClassName(eventClassName));
            this.setEventMapperClassName(validation.validateMapperClassName(eventClassName));
            this.setGenerationType(generationType);
            this.setEventNameWithoutTags(eventClassName);
            this.setEventNameWithTags(validation.validateeventClassName(eventClassName));
            getLOGGER().info("create New PushEvent");
            setAttributes(attributes);
            getAttributes().put("terminalId", "String");

            generatedFileWriter = GenerationHelper.createLogFile(getProjectPath(), "generated_output.txt");
            generatedFileWriter = createServerPushEvent(generatedFileWriter);
        }
        catch (JSONException e){
            GenerationMain.getLOGGER().warning(dialogName + "'s " + "JSON file is not found" + e.getMessage());
        }
    }

    public PrintWriter createServerPushEvent(PrintWriter generatedFileWriter)
    {
        try {
            StringBuilder log = createPackagesPushEvent(new StringBuilder());

            ParseAndCreateEventWPN eventWPN = new ParseAndCreateEventWPN();
            eventWPN.createEvent(getEventClassName(), getAttributes(), log, getEventDirectory(), getProjectName(), getUserName(), getEventNameWithoutTags(), getDialogName(), getEventClassNameWPN());

            ParseAndCreatePushEventWPN pushEventWPN = new ParseAndCreatePushEventWPN();
            pushEventWPN.createPushEvent(getPushEventClassName(), getAttributes(), log, getBasePackageDirectory(), getProjectName(), getUserName(), getEventNameWithoutTags(), getDialogName(), getPushEventClassNameWPN());

            ParseAndCreateEventMapperWPN eventMapperWPN = new ParseAndCreateEventMapperWPN();
            eventMapperWPN.createEventMapper(getEventMapperClassName(), getAttributes(), log, getBasePackageDirectory(), getProjectName(), getUserName(), getDialogName(), getEventMapperClassNameWPN(), getEventNameWithTags(), getPushEventClassName(), getEventNameWithoutTags());

            GenerationOperationDialog generationOperationDialog = new GenerationOperationDialog(getOperationFile(), getPushEventClassName(), getPushEventClassNameWPN());
            generationOperationDialog.modifyStepByStepDialogClass(getPushEventClassName(), log);

            GenerationStep generationStep = new GenerationStep(getOperationFile(), getPushEventClassName(), getPushEventClassNameWPN());
            generationStep.modifyStepClass(getStepClassName(), log);

            GenerationHelper.printLog(log.toString(), generatedFileWriter);
            return generatedFileWriter;
        }
        catch (FileNotFoundException e){
            GenerationMain.getLOGGER().warning(dialogName + "file was not found" + e.getMessage());
            return null;
        }
        catch (JSONException e){
            GenerationMain.getLOGGER().warning(dialogName + "'s " + "JSON file is not found" + e.getMessage());
            return null;
        }
    }

    // create directories for packages (folders in file system)
    public StringBuilder createPackages(StringBuilder createPackages, GenerationType type) {
        String fileSeparator = System.getProperty("file.separator");
        String srcDirectory = "src" + fileSeparator + "main" + fileSeparator
                + "java" + fileSeparator;
        setBasePackageName("com" + fileSeparator + "dematic" + fileSeparator
                + getProjectName().toLowerCase() + fileSeparator + "ui");
        setBasePackageDirectory(getProjectPath() + fileSeparator + srcDirectory
                + getBasePackageName());
        setBaseEventName("com" + fileSeparator + "dematic" + fileSeparator
                + getProjectName().toLowerCase());
        setEventDirectory(getProjectPath() + fileSeparator + srcDirectory
                + getBaseEventName());
        switch (type) {
            case STEP_BY_STEP:
                new File(getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase())
                        .mkdirs();
                GenerationHelper.printLog("package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + " created");
                createPackages.append("package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + " created");
                getLOGGER().info("package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + " created");

                new File(getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase()
                        + "/steps").mkdirs();
                GenerationHelper.printLog("package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/steps" + " created");
                createPackages.append(" \n package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/steps"
                        + " created");
                getLOGGER().info(" \n package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/steps"
                        + " created");

                new File(getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase()
                        + "/views").mkdirs();
                GenerationHelper.printLog("package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/views" + " created");
                createPackages.append(" \n package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/views"
                        + " created");
                getLOGGER().info(" package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/views"
                        + " created");

                new File(getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase()
                        + "/service").mkdirs();
                GenerationHelper.printLog("package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/service" + " created");
                createPackages.append(" \n package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/service"
                        + " created");
                getLOGGER().info(" package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/service"
                        + " created");

                new File(getBasePackageDirectory() + "/server/" + getDialogName().toLowerCase()
                        + "/service").mkdirs();
                GenerationHelper.printLog("package " + getBasePackageDirectory() + "/server/" + getDialogName().toLowerCase() + "/service" + " created");
                createPackages.append(" \n package " + getBasePackageDirectory() + "/server/" + getDialogName().toLowerCase() + "/service"
                        + " created");
                getLOGGER().info(" package " + getBasePackageDirectory() + "/server/" + getDialogName().toLowerCase() + "/service"
                        + " created");

                break;
        }
        // if the user choose to generate an event button
        if (isGenerateEvent) {
            new File(getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/event").mkdirs();
            GenerationHelper.printLog("package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/event" + " created");
            createPackages.append(" \n package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/event"
                    + " created");
            getLOGGER().info(" package " + getBasePackageDirectory() + "/client/" + getDialogName().toLowerCase() + "/event"
                    + " created");
        }
        getLOGGER().info("Packages are created successfuly ");
        return createPackages;
    }

    public StringBuilder createPackagesPushEvent(StringBuilder createPackages) {
        String fileSeparator = System.getProperty("file.separator");
        String srcDirectory = "src" + fileSeparator + "main" + fileSeparator
                + "java" + fileSeparator;
        setBasePackageName("com" + fileSeparator + "dematic" + fileSeparator
                + getProjectName().toLowerCase() + fileSeparator + "ui");
        setBasePackageDirectory(getProjectPath() + fileSeparator + srcDirectory
                + getBasePackageName());
        setBaseEventName("com" + fileSeparator + "dematic" + fileSeparator
                + getProjectName().toLowerCase());
        setEventDirectory(getProjectPath() + fileSeparator + srcDirectory
                + getBaseEventName());
        preparePackageNamesPushEvent();
        new File(getEventDirectory() + "/" + getDialogName().toLowerCase()
                + "/boundary/" + "events").mkdirs();
        createPackages.append("package " + getEventDirectory() + "/"
                + getDialogName().toLowerCase() + "/boundary" + "events"
                + " created");
        getLOGGER().info("package " + getEventDirectory() + "/"
                + getDialogName().toLowerCase() + "/boundary" + "events"
                + " created");

        new File(getBasePackageDirectory() + "/client/"
                + getDialogName().toLowerCase() + "/service" + "/event")
                .mkdirs();
        createPackages.append("package " + getBasePackageDirectory() + "/client/"
                + getDialogName().toLowerCase() + "/service" + "event"
                + " created");
        getLOGGER().info("package " + getBasePackageDirectory() + "/client/"
                + getDialogName().toLowerCase() + "/service" + "event"
                + " created");

        new File(basePackageDirectory + "/server/"
                + dialogName.toLowerCase() + "/mappers").mkdirs();
        createPackages.append("package " + basePackageDirectory + "/server/"
                + dialogName.toLowerCase() + "/mappers" + " created");
        getLOGGER().info("package " + basePackageDirectory + "/server/"
                + dialogName.toLowerCase() + "/mappers" + " created");

        return createPackages;
    }

    // create the package name with the class name (ex. com.advansys.generator.common.GenerationMain)
    public void preparePackageNames(GenerationType type) {
        LOGGER.info("preparing package Names");
        switch (type) {
            case STEP_BY_STEP:
                setDialogClassName(getBasePackageName().replace("\\", ".") + ".client."
                        + getDialogName().toLowerCase() + "." + getDialogName()
                        + "OperationDialog");
                getLOGGER().info("Package name " + getDialogClassName() + " is prepared.");

                setServiceClassName(getBasePackageName().replace("\\", ".") + ".client."
                        + getDialogName().toLowerCase() + ".service." + getDialogName()
                        + "Service");
                getLOGGER().info("Package name " + getServiceClassName() + " is prepared.");

                setServiceAsyncClassName(getBasePackageName().replace("\\", ".") + ".client."
                        + getDialogName().toLowerCase() + ".service." + getDialogName()
                        + "ServiceAsync");
                getLOGGER().info("Package name " + getServiceAsyncClassName() + " is prepared.");

                setServiceFactoryClassName(getBasePackageName().replace("\\", ".")
                        + ".client." + getDialogName().toLowerCase() + ".service."
                        + getDialogName() + "ServiceFactory");
                getLOGGER().info("Package name " + getServiceFactoryClassName() + " is prepared.");

                setServiceImplClassName(getBasePackageName().replace("\\", ".") + ".server."
                        + getDialogName().toLowerCase() + ".service." + getDialogName()
                        + "ServiceImpl");
                getLOGGER().info("Package name " + getServiceImplClassName() + " is prepared.");

                setStepClassName(getBasePackageName().replace("\\", ".") + ".client."
                        + getDialogName().toLowerCase() + ".steps.");
                getLOGGER().info("Package name " + getStepClassName() + " is prepared.");

                setViewClassName(getBasePackageName().replace("\\", ".") + ".client."
                        + getDialogName().toLowerCase() + ".views.");
                getLOGGER().info("Package name " + getViewClassName() + " is prepared.");
                break;
            case PUSH_EVENT:
                setEventClassName(getBaseEventName().replace("\\", ".") + "."
                        + getDialogName().toLowerCase() + ".boundary" + ".events."
                        + getDialogName() + "Event");
                getLOGGER().info("Package name " + getEventClassName() + " is prepared.");

                setPushEventClassName(getBasePackageName().replace("\\", ".")
                        + ".client." + getDialogName().toLowerCase() + ".service"
                        + ".event." + getDialogName() + "PushEvent");
                getLOGGER().info("Package name " + getPushEventClassName() + " is prepared.");

                setEventMapperClassName(getBasePackageName().replace("\\", ".")
                        + ".server." + getDialogName().toLowerCase() + ".mappers."
                        + getDialogName() + "EventMapper");
                getLOGGER().info("Package name " + getEventMapperClassName() + " is prepared.");

                break;
        }
        // if the user choose to generate an event button
        if (isGenerateEvent) {
            setEventClassName(getBasePackageName().replace("\\", ".") + ".client."
                    + getDialogName().toLowerCase() + ".event.SwitchToOtherStepEvent");
            getLOGGER().info("Package name " + getEventClassName() + " is prepared.");
        }
        getLOGGER().info("Package Names are prepared");
    }

    public void preparePackageNamesPushEvent() {
        setEventClassNameWPN(getBaseEventName().replace("\\", ".") + "."
                + getDialogName().toLowerCase() + ".boundary" + ".events."
                + getEventClassName());
        getLOGGER().info("Package name " + getEventClassNameWPN() + " is prepared.");

        setPushEventClassNameWPN(getBasePackageName().replace("\\", ".")
                + ".client." + getDialogName().toLowerCase() + ".service"
                + ".event." + getPushEventClassName());
        getLOGGER().info("Package name " + getPushEventClassNameWPN() + " is prepared.");

        setEventMapperClassNameWPN(getBasePackageName().replace("\\", ".")
                + ".server." + getDialogName().toLowerCase() + ".mappers."
                + getEventMapperClassName());
        getLOGGER().info("Package name " + getEventMapperClassNameWPN() + " is prepared.");
    }

    /**
     * create new step by step dialog.
     *
     * @param projectPath the ProjectPath to set
     * @param projectName the Project Name to set
     * @param dialogName  the Dialog Name to set
     * @param stepViewMap
     */
    @Override
    public StringBuilder createNewStepByStepDialog(String projectPath, String projectName, String dialogName, LinkedHashMap<String, String> stepViewMap, String userName, boolean isGeneratedEvent, GenerationType generationType)
    {
        try {
//Validation validation = new Validation();
            getLOGGER().info("create New StepByStepDialog");
            setProjectName(validation.validateName(projectName));
            setProjectPath(projectPath);
            setDialogName(validation.validateDialogName(dialogName));
            setStepViewMap(validation.validateStepViewNames(stepViewMap));
            setUserName(validation.validateUserName(userName));
            setGenerateEvent(isGeneratedEvent);

            try {
                generatedFile = new File(projectPath + "/" + "generated_output.txt");
                generatedFileWriter = new PrintWriter(generatedFile, "UTF-8");
            } catch (IOException e) {
                LOGGER.warning("canot generate output file" + e.getMessage());
                throw new RuntimeException(e);
            }

            StringBuilder sb = createPackages(new StringBuilder(), generationType);

            preparePackageNames(generationType);

            GenerationOperationDialog generationOperationDialog = new GenerationOperationDialog(basePackageDirectory, isGeneratedEvent, dialogClassName);
            sb = generationOperationDialog.createStepByStepDialog(sb);

            GenerationStep generationStep = new GenerationStep(basePackageDirectory, isGeneratedEvent, stepClassName);
            sb = generationStep.createStepClasses(sb);

            GenerationView generationView = new GenerationView(basePackageDirectory, isGeneratedEvent, viewClassName);
            sb = generationView.createViewClasses(sb);

            GenerationServiceInterface generationServiceInterface = new GenerationServiceInterface(basePackageDirectory, isGeneratedEvent, serviceClassName);
            sb = generationServiceInterface.createServiceInterface(sb);

            GenerationServiceAsync generationServiceAsync = new GenerationServiceAsync(basePackageDirectory, isGeneratedEvent, serviceAsyncClassName);
            sb = generationServiceAsync.createServiceAsyncInterface(sb);

            GenerationServiceFactory generationServiceFactory = new GenerationServiceFactory(basePackageDirectory, isGeneratedEvent, serviceFactoryClassName);
            sb = generationServiceFactory.createServiceFactory(sb);

            GenerationServiceImpl generationServiceImpl = new GenerationServiceImpl(basePackageDirectory, isGeneratedEvent, serviceImplClassName);
            sb = generationServiceImpl.createServiceImpl(sb);

            if (isGenerateEvent) {
                GenerationEventButton generationEventButton = new GenerationEventButton(basePackageDirectory, isGeneratedEvent, eventClassName);
                sb = generationEventButton.createEventButtonClass(sb);
            }

            generatedFileWriter.close();
            return sb;
        }
        catch (JSONException e){
            GenerationMain.getLOGGER().warning(serviceAsyncClassName + "'s " + "JSON file is not found" + e.getMessage());
            return null;
        }
        catch (RuntimeException e){
            GenerationMain.getLOGGER().warning(serviceAsyncClassName + " file was not generated." + e.getMessage());
            return null;
        }


    }

    //////////GETTERS AND SEtTERS//////////////////////
    public static String getProjectPath() {
        return projectPath;
    }

    public static void setProjectPath(String projectPath) {
        GenerationMain.projectPath = projectPath;
    }

    public static String getProjectName() {
        return projectName;
    }

    public static void setProjectName(String projectName) {
        GenerationMain.projectName = projectName;
    }

    public static String getDialogName() {
        return dialogName;
    }

    public static void setDialogName(String dialogName) {
        GenerationMain.dialogName = dialogName;
    }

    public String getBaseEventName() {
        return baseEventName;
    }

    public void setBaseEventName(String baseEventName) {
        this.baseEventName = baseEventName;
    }

    public String getEventDirectory() {
        return eventDirectory;
    }

    public void setEventDirectory(String eventDirectory) {
        this.eventDirectory = eventDirectory;
    }

    public String getBasePackageName() {
        return basePackageName;
    }

    public void setBasePackageName(String basePackageName) {
        this.basePackageName = basePackageName;
    }

    public String getBasePackageDirectory() {
        return basePackageDirectory;
    }

    public void setBasePackageDirectory(String basePackageDirectory) {
        this.basePackageDirectory = basePackageDirectory;
    }

    public String getDialogClassName() {
        return dialogClassName;
    }

    public void setDialogClassName(String dialogClassName) {
        this.dialogClassName = dialogClassName;
    }

    public String getServiceFactoryClassName() {
        return serviceFactoryClassName;
    }

    public void setServiceFactoryClassName(String serviceFactoryClassName) {
        this.serviceFactoryClassName = serviceFactoryClassName;
    }

    public String getServiceImplClassName() {
        return serviceImplClassName;
    }

    public void setServiceImplClassName(String serviceImplClassName) {
        this.serviceImplClassName = serviceImplClassName;
    }

    public String getServiceClassName() {
        return serviceClassName;
    }

    public void setServiceClassName(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    public String getServiceAsyncClassName() {
        return serviceAsyncClassName;
    }

    public void setServiceAsyncClassName(String serviceAsyncClassName) {
        this.serviceAsyncClassName = serviceAsyncClassName;
    }

    public String getStepClassName() {
        return stepClassName;
    }

    public void setStepClassName(String stepClassName) {
        this.stepClassName = stepClassName;
    }

    public String getViewClassName() {
        return viewClassName;
    }

    public void setViewClassName(String viewClassName) {
        this.viewClassName = viewClassName;
    }

    public String getEventClassName() {
        return eventClassName;
    }

    public void setEventClassName(String eventClassName) {
        this.eventClassName = eventClassName;
    }

    public boolean isGenerateEvent() {
        return isGenerateEvent;
    }

    public void setGenerateEvent(boolean generateEvent) {
        isGenerateEvent = generateEvent;
    }

    public String getPushEventClassName() {
        return pushEventClassName;
    }

    public void setPushEventClassName(String pushEventClassName) {
        this.pushEventClassName = pushEventClassName;
    }

    public String getEventMapperClassName() {
        return EventMapperClassName;
    }

    public void setEventMapperClassName(String eventMapperClassName) {
        EventMapperClassName = eventMapperClassName;
    }

    public static LinkedHashMap<String, String> getStepViewMap() {
        return stepViewMap;
    }

    public static void setStepViewMap(LinkedHashMap<String, String> stepViewMap) {
        GenerationMain.stepViewMap = stepViewMap;
    }

    public static LinkedHashMap<String, String> getAttributes() {
        return attributes;
    }

    public static void setAttributes(LinkedHashMap<String, String> attributes) {
        GenerationMain.attributes = attributes;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        GenerationMain.userName = userName;
    }

    public static PrintWriter getGeneratedFileWriter() {
        return generatedFileWriter;
    }

    public static void setGeneratedFileWriter(PrintWriter generatedFileWriter) {
        GenerationMain.generatedFileWriter = generatedFileWriter;
    }

    public static File getGeneratedFile() {
        return generatedFile;
    }

    public static void setGeneratedFile(File generatedFile) {
        GenerationMain.generatedFile = generatedFile;
    }

    public static File getOperationFile() {
        return operationFile;
    }

    public static void setOperationFile(File operationFile) {
        GenerationMain.operationFile = operationFile;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    /*public GenerationType getGenerationType() {
        return generationType;
    }
 */
    public void setGenerationType(GenerationType generationType) {
        this.generationType = generationType;
    }

    public String getEventClassNameWPN() {
        return eventClassNameWPN;
    }

    public void setEventClassNameWPN(String eventClassNameWPN) {
        this.eventClassNameWPN = eventClassNameWPN;
    }

    public String getPushEventClassNameWPN() {
        return pushEventClassNameWPN;
    }

    public void setPushEventClassNameWPN(String pushEventClassNameWPN) {
        this.pushEventClassNameWPN = pushEventClassNameWPN;
    }

    public String getEventMapperClassNameWPN() {
        return eventMapperClassNameWPN;
    }

    public void setEventMapperClassNameWPN(String eventMapperClassNameWPN) {
        this.eventMapperClassNameWPN = eventMapperClassNameWPN;
    }

   /* public Validation getValidation() {
        return validation;
    }*/

    /*public void setValidation(Validation validation) {
        this.validation = validation;
    }*/

    public String getEventNameWithoutTags() {
        return eventNameWithoutTags;
    }

    public void setEventNameWithoutTags(String eventNameWithoutTags) {
        this.eventNameWithoutTags = eventNameWithoutTags;
    }

    public String getEventNameWithTags() {
        return eventNameWithTags;
    }

    public void setEventNameWithTags(String eventNameWithTags) {
        this.eventNameWithTags = eventNameWithTags;
    }
}