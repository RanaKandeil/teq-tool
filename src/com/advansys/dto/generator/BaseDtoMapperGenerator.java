package com.advansys.dto.generator;

/**
 * Copyright (c) Dematic GmbH 2010. All rights reserved. Confidential.
 */


import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.dematic.wms.app.base.ui.shared.data.common.SqlDateProperty;
import com.dematic.wms.app.base.ui.shared.data.common.TimeProperty;
import org.hibernate.cfg.ImprovedNamingStrategy;

import com.dematic.wms.app.base.entity.AbstractDomainValue;
import com.dematic.wms.app.base.entity.DomainValue;
import com.dematic.wms.app.base.entity.DomainValueReference;
import com.dematic.wms.app.base.entity.LongDomainValue;
import com.dematic.wms.app.base.entity.Purgeable;
import com.dematic.wms.app.base.entity.SimpleType;
import com.dematic.wms.app.base.entity.annotation.LogicalKeyPart;
import com.dematic.wms.app.base.ui.shared.dto.DtoCreator;
import com.dematic.wms.app.base.ui.shared.common.OverrideDefaultImplementation;
import com.dematic.wms.app.base.ui.server.dto.AbstractDtoMapper;
import com.dematic.wms.app.base.ui.server.dto.AbstractObjectDtoMapper;
import com.dematic.wms.app.base.ui.server.dto.annotation.ColumnNameMapping;
import com.dematic.wms.app.base.ui.server.dto.annotation.GeneratedDtoMapper;
import com.dematic.wms.app.base.ui.server.dto.annotation.IgnoreAttributes;
import com.dematic.wms.app.base.ui.server.dto.annotation.LengthMapping;
import com.dematic.wms.app.base.ui.server.dto.annotation.MandatoryMapping;
import com.dematic.wms.app.base.ui.server.dto.annotation.ManualDatabaseColumnNameMetaData;
import com.dematic.wms.app.base.ui.server.dto.annotation.ManualDtoMapper;
import com.dematic.wms.app.base.ui.server.dto.annotation.ManualDtoTypeMetaData;
import com.dematic.wms.app.base.ui.server.dto.annotation.ManualIsMandatoryMetaData;
import com.dematic.wms.app.base.ui.server.dto.annotation.ManualLengthMetaData;
import com.dematic.wms.app.base.ui.server.dto.annotation.ManualLogicalIdMetaData;
import com.dematic.wms.app.base.ui.server.dto.annotation.ManualReferencedEntityClass;
import com.dematic.wms.app.base.ui.server.dto.annotation.ReferenceMapping;
import com.dematic.wms.app.base.ui.server.dto.annotation.SubstituteDto;
import com.dematic.wms.app.base.ui.server.dto.annotation.TypeMapping;
import com.dematic.wms.app.base.ui.shared.data.CollectionProperty;
import com.dematic.wms.app.base.ui.shared.data.DtoProperty;
import com.dematic.wms.app.base.ui.shared.data.common.BigDecimalProperty;
import com.dematic.wms.app.base.ui.shared.data.common.BooleanProperty;
import com.dematic.wms.app.base.ui.shared.data.common.ByteProperty;
import com.dematic.wms.app.base.ui.shared.data.common.DateProperty;
import com.dematic.wms.app.base.ui.shared.data.common.DoubleProperty;
import com.dematic.wms.app.base.ui.shared.data.common.FloatProperty;
import com.dematic.wms.app.base.ui.shared.data.common.IntegerProperty;
import com.dematic.wms.app.base.ui.shared.data.common.LongProperty;
import com.dematic.wms.app.base.ui.shared.data.common.Property;
import com.dematic.wms.app.base.ui.shared.data.common.StringProperty;
import com.dematic.wms.app.base.ui.shared.data.common.TimestampProperty;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDto;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.DtoMetaDataCreator;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.MetaData;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.MetaData.CollectionAccess;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.MetaData.EmbeddedMetaData;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.MetaData.RangeInfo;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.MetaData.Type;
import com.dematic.wms.app.base.util.jpa.JpaUtils;
import com.google.gwt.core.shared.GWT;

/**
 * Generator for standard code to maintain DTOs their meta data and mapper classes
 * All classes in a minor subsystem which implement the 
 * DtoMapperGeneratorMarker interface are generated
 * Annotations at the mapper classes are preserved by the generator.
 * To run the generator, the minor subsystem has to be compilable
 * The "tool.dtogenerator" ANT Target updates the source files
 * The generator can be started within Eclipse via the external tools menu, 
 * when the Eclipse project is selected in the navigator or package explorer.  
 * For bootstrapping a new mapper class has to be created in the server package. 
 * It can have a empty implementation. An empty DTO class should be generated in the client package
 * In case of JCoffee entities, the entity navigation is used 
 * to generate dependent mapper and DTO classes as well
 *  
 * @author reichhol
 */
public class BaseDtoMapperGenerator
{
   /**
    * @param out
    * @param entityClass
    * @param dtoClassName
    */
   static class AttributeDescription
   {
      String name;

      String entityAttributeClassName;

      String dtoAttributeClassName;

      int size;

      boolean isNumber;

      boolean isMandatory;

      String[] enumValues;

      String domainValueClassName;

      String referencedValueObjectClassName;
      
      String mappedByAttribute;

      String embeddedSimpleTypeClassName;

      String enumClassName;

      String manualSetDatabaseColumnName;

      int logicalIdIndex = -1;

      AbstractDtoMetaData.MetaData.Type manualSetType;

      Map<String, EmbeddedMetaData> embeddedColumnNames;

      CollectionAccess collectionAccess;

      RangeInfo rangeInfo;
      
      TemporalType temporalType;

      /* (non-Javadoc)
       * @see java.lang.Object#toString()
       */
      @Override
      public String toString()
      {
         return name + ", entityType = " + entityAttributeClassName + ", dtoType = " + dtoAttributeClassName;
      }
   }

   protected static class MyDomainValue
   {
      private String name;

      private String key;

      private boolean isLong;

      /* (non-Javadoc)
       * @see java.lang.Object#hashCode()
       */
      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (isLong ? 1231 : 1237);
         result = prime * result + ((key == null) ? 0 : key.hashCode());
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         return result;
      }

      /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         MyDomainValue other = (MyDomainValue) obj;
         if (isLong != other.isLong)
            return false;
         if (key == null) {
            if (other.key != null)
               return false;
         } else if (!key.equals(other.key))
            return false;
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         return true;
      }
   }

   /**
    * 
    */
   public static final String META_DATA_CONSTANT_SUFFIX = "_META_DATA";

   protected SubstituteDto substituteDto;

   protected List<AttributeDescription> fieldDescriptions;

   protected Class<?> dtoClass;

   protected Class<?> dtoBaseClass;

   protected String dtoClassName;

   protected String dtoOverridenClassName;

   protected String interfaceRelevantDtoClassName;

   protected String interfaceRelevantEntityClassName;

   protected String entityClassName;

   protected String mapperClassName;

   protected Class<?> entityClass;

   protected Annotation[] annotations;

   protected String tableName;

   protected String auditTrailTableName;

   private String dtoPackageName;

   private String mapperPackageName;

   public void generateClassesWithoutMapper(String argDtoClassName,
            PrintStream dtoSourceStream,
            PrintStream dtoMetaDataStream) throws Exception
   {
      Class<?> dtoClazz = Class.forName(argDtoClassName);
      this.dtoClassName = argDtoClassName;
      this.entityClass = Class.forName(argDtoClassName);
      this.entityClassName = argDtoClassName;
      this.annotations = (dtoClazz.getAnnotations() != null) ? dtoClazz.getAnnotations() : new Annotation[]{};
      this.mapperClassName = "no.mapper.available"; //"." are necessary to make the following init() method happy
      init();
      fieldDescriptions = getFieldDescriptions(entityClass);
      generateDto(dtoSourceStream);
      generateDtoMetaData(dtoMetaDataStream);
   }

   /**
    * @param argEntityClassName
    * @param argDtoClassName
    * @param argMapperClassName
    * @param dtoSourceStream
    * @param dtoMetaDataStream
    * @param dtoMapperSourceStream
    * @param argEntityClass
    * @param argAnnotations
    * @throws Exception
    */
   public void generateClasses(String argEntityClassName,
            String argDtoClassName,
            String argMapperClassName,
            PrintStream dtoSourceStream,
            PrintStream dtoMetaDataStream,
            PrintStream dtoMapperSourceStream,
            Class<?> argEntityClass,
            Annotation[] argAnnotations) throws Exception
   {
      this.entityClass = argEntityClass;
      this.entityClassName = argEntityClassName;
      this.dtoClassName = argDtoClassName;
      this.mapperClassName = argMapperClassName;
      this.annotations = argAnnotations;
      interfaceRelevantDtoClassName = argDtoClassName;
      interfaceRelevantEntityClassName = argEntityClassName;
      tableName = getTableName(argEntityClass, argAnnotations);
      auditTrailTableName = getAuditTrailTableName(argEntityClass);
      init();
      fieldDescriptions = getFieldDescriptions(argEntityClass);
      substituteDto = getSubstituteAnnotation(argAnnotations);

      if (substituteDto != null)
      {
         List<AttributeDescription> derivedDtoFieldDescriptions = getFieldDescriptions(substituteDto.withEntity());

         for (AttributeDescription description : fieldDescriptions)
         {
            for (AttributeDescription derivedDescription : derivedDtoFieldDescriptions)
            {
               if (derivedDescription.name.equals(description.name))
               {
                  derivedDtoFieldDescriptions.remove(derivedDescription);
                  break;
               }
            }
         }

         fieldDescriptions = derivedDtoFieldDescriptions;
         this.entityClass = substituteDto.withEntity();
         this.entityClassName = this.entityClass.getName();
         this.dtoOverridenClassName = this.dtoClassName;
         this.dtoClassName = substituteDto.withDto().getName();
      }

      init();
      applyMetaDataAnnotations();
      generateDto(dtoSourceStream);
      generateDtoMapper(dtoMapperSourceStream);
      generateDtoMetaData(dtoMetaDataStream);
   }

   protected void evaluateSingleAttribute(Class<?> entityAttributeType, AttributeDescription description,
            Class<?> argEntityClass, Annotation[] mapperAnnotations, Field field) throws Exception {

      if (handlePrimitiveAttributes(entityAttributeType, description))
      {
         // is already done in handlePrimitiveAttributes
      }
      else if (Enum.class.isAssignableFrom(entityAttributeType))
      {
         Enum<?>[] enumConstants = (Enum[]) entityAttributeType.getEnumConstants();
         description.enumValues = new String[enumConstants.length];

         for (int i = 0; i < enumConstants.length; i++)
         {
            description.enumValues[i] = enumConstants[i].name();
         }

         description.enumClassName = entityAttributeType.getName();
         description.dtoAttributeClassName = String.class.getName();
      }
      else if (SimpleType.class.isAssignableFrom(entityAttributeType))
      {
         Class<?> simpleTypeSubClass = entityAttributeType;

         while (!simpleTypeSubClass.getSuperclass().equals(SimpleType.class))
         {
            simpleTypeSubClass = simpleTypeSubClass.getSuperclass();
         }

         ParameterizedType parameterizedType = (ParameterizedType) simpleTypeSubClass.getGenericSuperclass();
         Class<?> simpleType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

         if (!handlePrimitiveAttributes(simpleType, description))
         {
            throw new RuntimeException();
         }

         description.embeddedSimpleTypeClassName = entityAttributeType.getName();
         description.dtoAttributeClassName = simpleType.getName();
      }
      else if (DomainValue.class.isAssignableFrom(entityAttributeType))
      {
         description.domainValueClassName = entityAttributeType.getName();
         description.dtoAttributeClassName = String.class.getName();
      }
      else if (LongDomainValue.class.isAssignableFrom(entityAttributeType))
      {
         description.domainValueClassName = entityAttributeType.getName();
         description.dtoAttributeClassName = Long.class.getName();
      }
      else if (Collection.class.isAssignableFrom(entityAttributeType)
               || Map.class.isAssignableFrom(entityAttributeType))
      {
         description.referencedValueObjectClassName = getReferencedEntity(mapperAnnotations,
                  field,
                  description.name);

         String referencedDtoClassName = handleReference(description, argEntityClass);
         description.dtoAttributeClassName = ArrayList.class.getName() + "<" + referencedDtoClassName + ">";

         determineCollectionAccess(field, description);
      }
      else if (entityAttributeType.getName().startsWith("com.dematic"))
      {
         description.referencedValueObjectClassName = entityAttributeType.getName();
         description.dtoAttributeClassName = handleReference(description, argEntityClass);
      }

      LogicalKeyPart logicalIdAnnotation = field.getAnnotation(LogicalKeyPart.class);

      if (logicalIdAnnotation != null)
      {
         description.logicalIdIndex = logicalIdAnnotation.index();
      }
   }

   /**
    * @param field
    * @param description
    */
   protected void determineCollectionAccess(Field field, AttributeDescription description) {
   }

   /**
    * @param entityAttributeType
    * @param description
    */
   protected boolean handlePrimitiveAttributes(Class<?> entityAttributeType, AttributeDescription description) {

      boolean result = false;

      if (Integer.class.isAssignableFrom(entityAttributeType)
               || int.class.equals(entityAttributeType))
      {
         description.size = 11;
         result = true;
      }
      else if (Long.class.isAssignableFrom(entityAttributeType)
               || long.class.equals(entityAttributeType))
      {
         description.size = 20;
         result = true;
      }
      else if (Double.class.isAssignableFrom(entityAttributeType)
               || double.class.equals(entityAttributeType))
      {
         description.size = 20;
         result = true;
      }
      else if (Float.class.isAssignableFrom(entityAttributeType)
               || float.class.equals(entityAttributeType))
      {
         description.size = 11;
         result = true;
      }
      else if (BigDecimal.class.isAssignableFrom(entityAttributeType))
      {
         description.size = 20;
         result = true;
      }
      else if (String.class.isAssignableFrom(entityAttributeType))
      {
         description.size = 255;
         result = true;
      }

      return result;
   }

   protected String getDefaultInheritBaseMetaData() {
      return "";
   }

   protected boolean isBaseClassReached(Class<?> entity) {
      return entity.equals(Object.class) || entity.equals(AbstractDto.class);
   }

   private void init() throws Exception
   {
      dtoPackageName = getPackageName(dtoClassName);
      mapperPackageName = getPackageName(mapperClassName);

      try
      {
         dtoClass = Class.forName(dtoClassName);
         dtoBaseClass = dtoClass.getSuperclass();
      } catch (Exception e)
      {
         dtoBaseClass = getDefaultDtoBaseClass();
      }
   }

   /**
    * @return
    */
   protected Class<?> getDefaultDtoBaseClass()
   {
      return AbstractDto.class;
   }

   /**
    * @param mapperClassName
    * @param argAnnotations
    * @return
    */
   protected SubstituteDto getSubstituteAnnotation(Annotation[] argAnnotations)
   {
      SubstituteDto result = null;

      for (Annotation annotation : argAnnotations)
      {
         if (annotation instanceof SubstituteDto)
         {
            result = (SubstituteDto) annotation;
            break;
         }
      }

      return result;
   }

   /**
    * @param argEntityClass 
    * @param argAnnotations 
    * @return
    */
   protected String getTableName(Class<?> argEntityClass, Annotation[] argAnnotations) throws Exception
   {
      return null;
   }

   /**
    * @param argEntityClass 
    * @param argAnnotations 
    * @return
    */
   protected String getAuditTrailTableName(Class<?> argEntityClass) throws Exception
   {
      return null;
   }

   /**
    * @param fieldDescriptions
    * @param annotations
    * @param mapperClassName 
    */
   protected void applyMetaDataAnnotations()
   {
      for (Annotation annotation : annotations)
      {
         if (annotation instanceof ManualDtoTypeMetaData)
         {
            ManualDtoTypeMetaData manualMetaData = (ManualDtoTypeMetaData) annotation;

            for (TypeMapping typeMapping : manualMetaData.mappings())
            {
               String attributeName = typeMapping.attributeName();
               AttributeDescription field = getFieldDescription(fieldDescriptions, attributeName);

               field.manualSetType = typeMapping.value();
            }
         }
         else if (annotation instanceof ManualLengthMetaData)
         {
            ManualLengthMetaData manualMetaData = (ManualLengthMetaData) annotation;

            for (LengthMapping lengthMapping : manualMetaData.mappings())
            {
               String attributeName = lengthMapping.attributeName();
               AttributeDescription field = getFieldDescription(fieldDescriptions, attributeName);

               field.size = lengthMapping.value();
            }
         }
         else if (annotation instanceof ManualIsMandatoryMetaData)
         {
            ManualIsMandatoryMetaData manualMetaData = (ManualIsMandatoryMetaData) annotation;

            for (MandatoryMapping mandatoryMapping : manualMetaData.mappings())
            {
               String attributeName = mandatoryMapping.attributeName();
               AttributeDescription field = getFieldDescription(fieldDescriptions, attributeName);

               field.isMandatory = mandatoryMapping.value();
            }
         }
         else if (annotation instanceof ManualDatabaseColumnNameMetaData)
         {
            ManualDatabaseColumnNameMetaData manualMetaData = (ManualDatabaseColumnNameMetaData) annotation;

            for (ColumnNameMapping columnNameMapping : manualMetaData.mappings())
            {
               String attributeName = columnNameMapping.attributeName();
               AttributeDescription field = getFieldDescription(fieldDescriptions, attributeName);

               field.manualSetDatabaseColumnName = columnNameMapping.value();
            }
         }
         else if (annotation instanceof ManualLogicalIdMetaData)
         {
            ManualLogicalIdMetaData manualMetaData = (ManualLogicalIdMetaData) annotation;
            String[] attributeNames = manualMetaData.attributeNames();

            for (int i = 0; i < attributeNames.length; i++)
            {
               String attributeName = attributeNames[i];
               AttributeDescription field = getFieldDescription(fieldDescriptions, attributeName);

               field.logicalIdIndex = i + 1;
            }
         }
      }
   }

   protected boolean ignoreAttribute(String attributeName,
            Annotation[] argAnnotation)
   {
      boolean result = false;

      for (Annotation annotation : argAnnotation)
      {
         if (annotation instanceof IgnoreAttributes)
         {
            IgnoreAttributes manualMetaData = (IgnoreAttributes) annotation;
            String[] attributeNamesToBeIgnored = manualMetaData.attributeNames();

            for (int i = 0; i < attributeNamesToBeIgnored.length; i++)
            {
               String attributeNameToBeIgnored = attributeNamesToBeIgnored[i];

               if (attributeNameToBeIgnored.equals(attributeName))
               {
                  result = true;
               }
            }
         }
      }

      return result;
   }

   /**
    * @param argFieldDescriptions
    * @param attributeName
    * @return
    */
   protected AttributeDescription getFieldDescription(List<AttributeDescription> argFieldDescriptions,
            String attributeName)
   {
      AttributeDescription field = null;

      for (AttributeDescription candidate : argFieldDescriptions)
      {
         if (candidate.name.equals(attributeName))
         {
            field = candidate;
            break;
         }
      }

      return field;
   }

   /**
    * @param field
    * @return
    */
   protected MetaData.Type getType(AttributeDescription field)
   {
      MetaData.Type result = null;
      String[] enumValues = field.enumValues;

      if (field.manualSetType != null)
      {
         result = field.manualSetType;
      }
      else if (String.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.STRING;

         if ((enumValues != null)
                  && (enumValues.length > 0))
         {
            result = Type.ENUM;
         }
         else if (field.domainValueClassName != null)
         {
            result = Type.DOMAINVALUE;
         }
      }
      else if (Boolean.class.getName().equals(field.dtoAttributeClassName)
               || boolean.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.BOOLEAN;
      }
      else if (Double.class.getName().equals(field.dtoAttributeClassName)
               || double.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.DOUBLE;
      }
      else if (Float.class.getName().equals(field.dtoAttributeClassName)
               || float.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.FLOAT;
      }
      else if (Long.class.getName().equals(field.dtoAttributeClassName)
               || long.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.LONG;

         if (field.domainValueClassName != null)
         {
            result = Type.LONGDOMAINVALUE;
         }
      }
      else if (Integer.class.getName().equals(field.dtoAttributeClassName)
               || int.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.INTEGER;
      }
      else if (Byte.class.getName().equals(field.dtoAttributeClassName)
               || byte.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.BYTE;
      }
      else if (Date.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.DATETIME;
      }
      else if (java.sql.Date.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.DATE;
      }
      else if (java.sql.Time.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.TIME;
      }
      else if (Timestamp.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.TIMESTAMP;
      }
      else if (BigDecimal.class.getName().equals(field.dtoAttributeClassName))
      {
         result = Type.BIGDECIMAL;
      }
      else if (field.dtoAttributeClassName.startsWith(ArrayList.class.getName()))
      {
         result = Type.COLLECTION;
      }
      else if (field.embeddedColumnNames != null)
      {
         result = Type.EMBEDDED;
      }
      else
      {
         result = Type.REFERENCE;
      }

      return result;
   }

   /**
    * @param name
    * @return
    */
   public static String convertCamelCase2Capital(String name)
   {
      StringBuilder result = new StringBuilder();

      for (int i = 0; i < name.length(); i++)
      {
         char currentChar = name.charAt(i);

         if (Character.isUpperCase(currentChar)
                  && result.length() > 0)
         {
            result.append("_");
         }

         result.append(Character.toUpperCase(currentChar));
      }

      return result.toString();
   }

   /**
    * @param string
    * @return
    */
   protected String toCamelCase(String name)
   {
      StringBuilder result = new StringBuilder();

      for (int i = 0; i < name.length(); i++)
      {
         char currentChar = name.charAt(i);

         if (currentChar == '_')
         {
            result.append(name.charAt(++i));
         }
         else
         {
            result.append(Character.toLowerCase(currentChar));
         }
      }

      return result.toString();
   }

   protected void printFileCopyrightComment(PrintStream out)
   {
      int year = Calendar.getInstance().get(Calendar.YEAR);
      out.println("/**");
      out.println(" * Copyright (c) Dematic GmbH " + year + ". All rights reserved. Confidential.");
      out.println(" */");
   }

   protected void printClassComment(PrintStream out,
            String purpose)
   {
      out.println("/**");
      out.println(" * " + purpose);
      out.println(" * This is generated code.");
      out.println(" * Do not modify unless you replace the " + GeneratedDtoMapper.class.getSimpleName() + " annotation ");
      out.println(" * with the " + ManualDtoMapper.class.getSimpleName() + " annotation");
      out.println(" * @author " + SingleClassDtoMapperGenerator.class.getName());
      out.println(" */");
   }

   protected void printImportsExistingAnnotations(PrintStream out,
            Annotation[] argAnnotations) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
   {
      HashSet<String> annotationClassNames = new HashSet<String>();

      for (Annotation annotation : argAnnotations)
      {
         annotationClassNames.add(annotation.annotationType().getName());
      }

      getEmbeddedAnnotationClassNames(argAnnotations, annotationClassNames);

      String[] classNamesArray = annotationClassNames.toArray(new String[annotationClassNames.size()]);
      Arrays.sort(classNamesArray);

      if (classNamesArray.length == 0)
      {
         classNamesArray = new String[]{GeneratedDtoMapper.class.getName()};
      }

      for (String className : classNamesArray)
      {
         out.println("import " + className + ";");
      }
   }

   protected void getEmbeddedAnnotationClassNames(Annotation[] argAnnotations, HashSet<String> result)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
   {
      for (Annotation annotation : argAnnotations)
      {
         java.lang.reflect.Method[] declaredMethods = annotation.annotationType().getDeclaredMethods();

         for (int i = 0; i < declaredMethods.length; i++)
         {
            java.lang.reflect.Method method = declaredMethods[i];

            Object value = method.invoke(annotation, new Object[]{});

            if (value.getClass().isArray())
            {
               if ((Array.getLength(value) >= 0)
                        && (Array.get(value, 0) instanceof Annotation))
               {
                  result.add(((Annotation) Array.get(value, 0)).annotationType().getName());
               }
            }
         }
      }
   }

   /**
    * @param out
    * @param argAnnotations
    * @throws InvocationTargetException 
    * @throws IllegalAccessException 
    * @throws IllegalArgumentException 
    */
   protected void printExistingAnnotations(PrintStream out,
            Annotation... argAnnotations) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
   {
      Arrays.sort(argAnnotations, new Comparator<Annotation>()
      {
         @Override
         public int compare(Annotation o1, Annotation o2)
         {
            return o1.annotationType().getName().compareTo(o2.annotationType().getName());
         }
      });

      if (argAnnotations.length == 0)
      {
         out.println("@" + GeneratedDtoMapper.class.getSimpleName());
      }

      for (Annotation annotation : argAnnotations)
      {
         out.print("@" + annotation.annotationType().getSimpleName() + "(");

         java.lang.reflect.Method[] declaredMethods = annotation.annotationType().getDeclaredMethods();
         Arrays.sort(declaredMethods, new Comparator<java.lang.reflect.Method>()
         {
            @Override
            public int compare(java.lang.reflect.Method o1, java.lang.reflect.Method o2)
            {
               return o1.getName().compareTo(o2.getName());
            }
         });

         for (int i = 0; i < declaredMethods.length; i++)
         {
            java.lang.reflect.Method method = declaredMethods[i];

            out.print(method.getName() + "=");

            Object value = method.invoke(annotation, new Object[]{});

            if (value instanceof Class)
            {
               out.print(((Class<?>) value).getCanonicalName() + ".class");

            }
            else if (value instanceof Enum)
            {
               Enum<?> enumValue = (Enum<?>) value;
               out.print(enumValue.getDeclaringClass().getCanonicalName() + "." + enumValue.toString());
            }
            else if (value instanceof String)
            {
               out.print("\"" + value + "\"");
            }
            else if (value.getClass().isArray())
            {
               out.print("{");

               for (int j = 0; j < Array.getLength(value); j++)
               {
                  Object singleValue = Array.get(value, j);

                  if (singleValue instanceof Annotation)
                  {
                     printExistingAnnotations(out, (Annotation) singleValue);
                  }
                  else if (singleValue instanceof String)
                  {
                     out.print("\"" + singleValue + "\"");
                  }
                  else if (singleValue instanceof Enum)
                  {
                     Enum<?> enumValue = (Enum<?>) singleValue;
                     out.print(enumValue.getDeclaringClass().getCanonicalName() + "." + enumValue.toString());
                  }
                  else if (singleValue instanceof Class)
                  {
                     out.print(((Class<?>) singleValue).getCanonicalName() + ".class");
                  }
                  else
                  {
                     out.print(singleValue);
                  }

                  if (j + 1 < Array.getLength(value))
                  {
                     out.print(",");
                  }
               }

               out.print("}");
            }
            else
            {
               out.print(value);
            }

            if (i + 1 < declaredMethods.length)
            {
               out.print(",");
            }
         }

         out.println(")");
      }
   }

   protected List<AttributeDescription> getFieldDescriptions(Class<?> entity) throws Exception
   {
      List<AttributeDescription> result = new ArrayList<AttributeDescription>();

      while (!isBaseClassReached(entity))
      {
         getFieldDescriptionsOfSingleInterface(entity, result, annotations);
         entity = entity.getSuperclass();
      }

      return result;
   }

   /**
    * @param argEntityClass
    * @param argFieldDescriptions
    * @param mapperClassName 
    * @param dtoClassName 
    * @throws Exception 
    */
   protected void getFieldDescriptionsOfSingleInterface(Class<?> argEntityClass,
            List<AttributeDescription> argFieldDescriptions,
            Annotation[] mapperAnnotations) throws Exception
   {
      for (java.lang.reflect.Field field : argEntityClass.getDeclaredFields())
      {
         int modifiers = field.getModifiers();

         if (Modifier.isTransient(modifiers) 
                  || Modifier.isStatic(modifiers) 
                  || (field.getAnnotation(Transient.class) != null)
                  || Purgeable.END_OF_LIFE_FIELD_NAME.equals(field.getName())) {
            continue;
         }

         Class<?> entityAttributeType = field.getType();

         AttributeDescription description = new AttributeDescription();

         description.dtoAttributeClassName = entityAttributeType.getName();
         description.name = field.getName();
         description.entityAttributeClassName = entityAttributeType.getCanonicalName();

         if (ignoreAttribute(description.name, mapperAnnotations))
            continue;

         if (Object.class.getName().equals(description.entityAttributeClassName))
         {
            // members of type java.lang.Object are not supported by GWT (not serializable)
            continue;
         }

         evaluateSingleAttribute(entityAttributeType, description, argEntityClass, mapperAnnotations, field);

         System.out.println(description);
         argFieldDescriptions.add(description);
      }
   }

   protected String handleReference(AttributeDescription description,
            Class<?> argEntityClass) throws Exception
   {
      resolveReferences(description.referencedValueObjectClassName);

      SingleClassDtoMapperGenerator.Mapper referencedType = SingleClassDtoMapperGenerator.existingMappings
               .get(description.referencedValueObjectClassName);

      if (referencedType == null)
      {
         System.err.println("Could not resolve referenced type of attribute '" + description.name + "' in entity '"
                  + argEntityClass.getName() + "'");
         System.err.println("Please add a @ManualReferencedEntityClass annotation to your DtoMapper class");
      }

      return (referencedType != null) ? referencedType.dtoClassName : dtoBaseClass.getName();
   }

   protected void resolveReferences(String referencedValueObjectClassName) throws Exception
   {
      if (referencedValueObjectClassName != null
               && SingleClassDtoMapperGenerator.existingMappings.get(referencedValueObjectClassName) == null)
      {
         String referencedObjectSimpleName = getSimpleClassName(referencedValueObjectClassName);

         String subDtoPackageName = dtoPackageName;

         if (substituteDto != null)
         {
            subDtoPackageName = getPackageName(substituteDto.withDto().getName());
         }

         SingleClassDtoMapperGenerator.generateDtoSource(referencedValueObjectClassName,
                  subDtoPackageName + "." + referencedObjectSimpleName + "Dto",
                  mapperPackageName + "." + referencedObjectSimpleName + "DtoMapper");
      }
   }

   /**
    * @param mapperAnnotations
    * @param method
    * @param defaultMessage
    */
   protected String getReferencedEntity(Annotation[] mapperAnnotations,
            java.lang.reflect.Field field,
            String attributeName)
   {
      String result = field.getType().getName();

      if (Collection.class.isAssignableFrom(field.getType())
               && (field.getGenericType() instanceof ParameterizedType))
      {
         ParameterizedType type = (ParameterizedType) field.getGenericType();
         java.lang.reflect.Type actualType = type.getActualTypeArguments()[0];

         if (actualType instanceof Class)
         {
            result = ((Class<?>) actualType).getName();
         }
      }
      else if (Map.class.isAssignableFrom(field.getType())
               && (field.getGenericType() instanceof ParameterizedType))
      {
         ParameterizedType type = (ParameterizedType) field.getGenericType();
         java.lang.reflect.Type actualType = type.getActualTypeArguments()[1];

         if (actualType instanceof Class)
         {
            result = ((Class<?>) actualType).getName();
         }
      }

      for (Annotation annotation : field.getAnnotations())
      {
         java.lang.reflect.Method annotationMethod = null;

         try
         {
            annotationMethod = annotation.annotationType().getMethod("targetEntity");
         } catch (Exception ignore)
         {
         }

         if (annotationMethod != null)
         {
            try
            {
               Class<?> referencedClass = (Class<?>) annotationMethod.invoke(annotation);

               if (!referencedClass.equals(void.class))
               {
                  result = referencedClass.getName();
               }

               break;
            } catch (Exception ignore)
            {
            }
         }
      }

      String annotatedReferencedEntity = getAnnotatedReferencedEntity(mapperAnnotations,
               attributeName);

      if (result != null
               && annotatedReferencedEntity != null)
      {
         assert annotatedReferencedEntity.equals(result) : ManualReferencedEntityClass.class.getSimpleName() + " for "
                  + mapperClassName
                  + " tries to override the referenced class for attribute " + attributeName
                  + " with " + annotatedReferencedEntity + ", but data model has " + result;
      }

      if (annotatedReferencedEntity != null)
      {
         result = annotatedReferencedEntity;
      }

      return result;
   }

   /**
    * @param mapperAnnotations
    * @param defaultMessage
    * @return
    */
   protected String getAnnotatedReferencedEntity(Annotation[] mapperAnnotations,
            String attributeName)
   {
      String result = null;

      for (Annotation annotation : mapperAnnotations)
      {
         if (annotation instanceof ManualReferencedEntityClass)
         {
            ManualReferencedEntityClass manualMetaData = (ManualReferencedEntityClass) annotation;
            ReferenceMapping[] mappings = manualMetaData.mappings();

            for (ReferenceMapping referenceMapping : mappings)
            {
               if (attributeName.equals(referenceMapping.attributeName()))
               {
                  result = referenceMapping.value().getName();
                  break;
               }
            }
         }
      }

      return result;
   }

   protected void addGenericDataAccessors(PrintStream out,
            List<AttributeDescription> argFieldDescriptions,
            String dtoClassSimpleName)
   {
      out.println("   @Override");
      out.println("   protected " + Property.class.getName() + " getAttribute(String  attributeName) {");
      out.println("   ");
      out.println("      " + Property.class.getName() + " result = null;");
      out.println();
      for (int i = 0; i < argFieldDescriptions.size(); i++)
      {
         AttributeDescription field = argFieldDescriptions.get(i);

         if (!isAbstractDtoAttribute(field.name))
         {
            String methodNameFragment = getAccessMethodNameFragment(field.name);
            String propertyClassName = mapDtoAttributeClass2PropertyClass(field.dtoAttributeClassName);

            out.print("      ");

            if (i > 0)
            {
               out.print("else ");
            }

            out.println("if (" + dtoClassSimpleName + "MetaData." + convertCamelCase2Capital(field.name)
                     + ".equals(attributeName)) {");
            out.println("      ");
            out.println("         " + propertyClassName + " property = new " + propertyClassName + "();");
            out.println("         property.setValue(get" + methodNameFragment + "());");
            out.println("         result = property;");
            out.println("      }");
         }
      }

      boolean callBaseClass = callBaseClassInGenericDataAccessors();

      if (callBaseClass)
      {
         out.println("      else {");
         out.println("         result = super.getAttribute(attributeName);");
         out.println("      }");
      }

      out.println();
      out.println("      return result;");
      out.println("   }");
      out.println();

      out.println("   @Override");
      out.println("   public void setAttribute(String  attributeName, " + Property.class.getName() + " value) {");
      out.println("   ");
      for (int i = 0; i < argFieldDescriptions.size(); i++)
      {
         AttributeDescription field = argFieldDescriptions.get(i);

         if (!isAbstractDtoAttribute(field.name))
         {
            String methodNameFragment = getAccessMethodNameFragment(field.name);

            out.print("      ");

            if (i > 0)
            {
               out.print("else ");
            }

            out.println("if (" + dtoClassSimpleName + "MetaData." + convertCamelCase2Capital(field.name)
                     + ".equals(attributeName)) {");
            out.println("         set" + methodNameFragment + "((("
                     + mapDtoAttributeClass2PropertyClass(field.dtoAttributeClassName) + ")value).getValue());");
            out.println("      }");
         }
      }

      if (callBaseClass)
      {
         out.println("      else {");
         out.println("         super.setAttribute(attributeName, value);");
         out.println("      }");
      }

      out.println("   }");
      out.println();
   }

   /**
    * @return
    */
   protected boolean callBaseClassInGenericDataAccessors()
   {
      return substituteDto != null;
   }

   /**
    * @param name
    * @return
    */
   protected boolean isAbstractDtoAttribute(String name)
   {
      return false;
   }

   protected String mapDtoAttributeClass2PropertyClass(String attributeClass)
   {
      String result;

      if (attributeClass.equals(String.class.getName()))
      {
         result = StringProperty.class.getName();
      }
      else if (attributeClass.equals(Double.class.getName())
               || attributeClass.equals(double.class.getName()))
      {
         result = DoubleProperty.class.getName();
      }
      else if (attributeClass.equals(Float.class.getName())
               || attributeClass.equals(float.class.getName()))
      {
         result = FloatProperty.class.getName();
      }
      else if (attributeClass.equals(Long.class.getName())
               || attributeClass.equals(long.class.getName()))
      {
         result = LongProperty.class.getName();
      }
      else if (attributeClass.equals(Integer.class.getName())
               || attributeClass.equals(int.class.getName()))
      {
         result = IntegerProperty.class.getName();
      }
      else if (attributeClass.equals(Byte.class.getName())
               || attributeClass.equals(byte.class.getName()))
      {
         result = ByteProperty.class.getName();
      }
      else if (attributeClass.equals(BigDecimal.class.getName()))
      {
         result = BigDecimalProperty.class.getName();
      }
      else if (attributeClass.equals(Time.class.getName()))
      {
         result = TimeProperty.class.getName();
      }
      else if (attributeClass.equals(Date.class.getName()))
      {
         result = DateProperty.class.getName();
      }
      else if (attributeClass.equals(java.sql.Date.class.getName()))
      {
         result = SqlDateProperty.class.getName();
      }
      else if (attributeClass.equals(Timestamp.class.getName()))
      {
         result = TimestampProperty.class.getName();
      }
      else if (attributeClass.equals(Boolean.class.getName())
               || attributeClass.equals(boolean.class.getName()))
      {
         result = BooleanProperty.class.getName();
      }
      else if (attributeClass.startsWith(ArrayList.class.getName()))
      {
         result = CollectionProperty.class.getName() + attributeClass.substring(attributeClass.indexOf("<"));
      }
      else
      {
         result = DtoProperty.class.getName() + "<" + attributeClass + ">";
      }

      return result;
   }

   /**
    * @param out
    * @param field
    */
   protected void setDtoAttributeFromEntity(PrintStream out,
            AttributeDescription field,
            String dtoVariableName,
            String entityVariableName)
   {
      // Collections without meta data have to implemented by hand. Therefore do nothing here
      Type type = getDtoMetaDataType(field);

      if (type == Type.COLLECTION
               && field.referencedValueObjectClassName == null)
      {
         return;
      }

      // Currently we do not handle collections of references
      // as soon as we know how to handle them best generically, the relevant code goes in here
      if (type == Type.COLLECTION)
      {
         return;
      }

      String attributeName = field.name;

      String methodNameFragment = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
      String getterPrefix = (type == Type.BOOLEAN) ? ".is" : ".get";

      if (type == Type.REFERENCE
               || type == Type.EMBEDDED)
      {

         SingleClassDtoMapperGenerator.Mapper referenceMapper = SingleClassDtoMapperGenerator.existingMappings.get(field.referencedValueObjectClassName);

         if (referenceMapper != null)
         {
            String mapperParameters = "("
                     + (field.entityAttributeClassName.equals(field.referencedValueObjectClassName)
                              ? ""
                              : "(" + field.referencedValueObjectClassName + ")")
                     + entityVariableName + getterPrefix + methodNameFragment + "()));";

            if (type == Type.REFERENCE) {

               out.print((field.logicalIdIndex < 0) ? "      if (processReferences) " : "      ");
               
               String castExpression = referenceMapper.needsDownCast ? ("(" + referenceMapper.dtoClassName + ")"): "";

               out.println(dtoVariableName + ".set" + methodNameFragment
                           + "(" + castExpression + "dtoMapperFactory.create(" + referenceMapper.mapperClassName + ".class)."
                           + "mapEntity2DtoWithoutReferences"
                           + mapperParameters);

            } else /*type == Type.EMBEDDED*/{

               if (field.logicalIdIndex >= 0) {

                  out.println("      " + dtoVariableName + ".set" + methodNameFragment
                           + "(dtoMapperFactory.create(" + referenceMapper.mapperClassName + ".class)."
                           + "mapEntity2Dto"
                           + mapperParameters);

               } else /* no logical id */{

                  out.println("      if (processReferences) {");

                  out.println("         " + dtoVariableName + ".set" + methodNameFragment
                           + "(dtoMapperFactory.create(" + referenceMapper.mapperClassName + ".class)."
                           + "mapEntity2Dto" + mapperParameters);
                  out.println("      } else {");
                  out.println("         " + dtoVariableName + ".set" + methodNameFragment
                           + "(dtoMapperFactory.create(" + referenceMapper.mapperClassName + ".class)."
                           + "mapEntity2DtoWithoutReferences" + mapperParameters);
                  out.println("      }");
               }
            }
         }
      }
      else
      {
         out.println("      " + dtoVariableName + ".set" + methodNameFragment + "((" + type.getClassName()
                  + ")convertEntityData2Dto(" + entityVariableName + getterPrefix + methodNameFragment
                  + "(), dto.getMetaData().getMetaData(\"" + attributeName + "\")));");
      }
   }

   /**
    * @param out
    * @param field
    */
   protected void setEntityAttributeFromDto(PrintStream out,
            AttributeDescription field,
            String dtoVariableName,
            String entityVariableName)
   {
      Type type = getDtoMetaDataType(field);

      // Collections without meta data have to implemented by hand. Therefore do nothing here
      if (type == Type.COLLECTION
               && field.referencedValueObjectClassName == null)
      {
         return;
      }

      // Currently we do not handle collections of references
      // as soon as we know to handle them best generically, the relevant code goes in here
      if (type == Type.COLLECTION)
      {
         return;
      }

      String methodNameFragment = getAccessMethodNameFragment(field.name);

      if (type == Type.REFERENCE
               || type == Type.EMBEDDED)
      {
         SingleClassDtoMapperGenerator.Mapper referenceMapping = SingleClassDtoMapperGenerator.existingMappings.get(field.referencedValueObjectClassName);

         if (referenceMapping != null)
         {
            String castExpression = referenceMapping.needsDownCast ? ("(" + field.referencedValueObjectClassName + ")"): "";
            out.println("      " + entityVariableName + ".set" + methodNameFragment 
                        + "(" + castExpression  + "dtoMapperFactory.create(" + referenceMapping.mapperClassName + ".class).mapDto2Entity(" 
                        + dtoVariableName + ".get" + methodNameFragment + "()));");
         }
      }
      else
      {
         out.println("      " + entityVariableName + ".set" + methodNameFragment + "((" + getEntityClassNameForCast(field)
                  + ")convertDto2EntityData(" + dtoVariableName + ".get" + methodNameFragment
                  + "(), dto.getMetaData().getMetaData(\"" + field.name + "\")));");
      }
   }

   /**
    * @param field
    * @return
    */
   protected String getAccessMethodNameFragment(String fieldName) {
      return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
   }

   /**
    * @param field
    * @return
    */
   protected String getEntityClassNameForCast(AttributeDescription field)
   {
      String result = field.entityAttributeClassName;

      boolean isPrimitive = !result.contains(".");

      if (isPrimitive)
      {
         result = getDtoMetaDataType(field).getClassName();
      }

      return result;
   }

   /**
    * @param className
    * @return
    */
   protected String getSimpleClassName(String className)
   {
      return className.substring(className.lastIndexOf('.') + 1);
   }

   protected String getPackageName(String className)
   {
      return className.substring(0, className.lastIndexOf('.'));
   }

   protected void generateDto(PrintStream out) throws ClassNotFoundException
   {
      String dtoPackage = dtoClassName.substring(0, dtoClassName.lastIndexOf('.'));
      String dtoClassSimpleName = getSimpleClassName(dtoClassName);

      printFileCopyrightComment(out);
      out.println("package " + dtoPackage + ";");
      out.println();
      printClassComment(out, "This class is the DTO for the class " + entityClassName);

      if (substituteDto != null)
      {
         out.println("@" + OverrideDefaultImplementation.class.getName() + "(defaultClass=" + dtoOverridenClassName + ".class)");
      }
      out.println("public class " + dtoClassSimpleName + " extends " + getDtoBaseClassNameForDtoSource() + " {");
      out.println("   private static final long serialVersionUID = 1L;");
      out.println();

      out.println("   protected " + dtoClassSimpleName + "() {");
      out.println("      super();");
      out.println("   }");
      out.println();

      for (AttributeDescription field : fieldDescriptions)
      {
         if (!isAbstractDtoAttribute(field.name))
         {
            String methodNameFragment = getAccessMethodNameFragment(field.name);
            String propertyClassName = mapDtoAttributeClass2PropertyClass(field.dtoAttributeClassName);
            String mapAccessKey = dtoClassSimpleName + "MetaData." + convertCamelCase2Capital(field.name);

            out.println("   public void set" + methodNameFragment + "(" + field.dtoAttributeClassName + " " + field.name + ") {");
            out.println("      " + propertyClassName + " property = new " + propertyClassName + "();");
            out.println("      property.setValue(" + field.name + ");");
            out.println("      setAttribute(" + mapAccessKey + ", property);");
            out.println("   }");
            out.println();
            out.println("   public " + field.dtoAttributeClassName + " get" + methodNameFragment + "() {");
            out.println("      return ((" + propertyClassName + ") getAttribute(" + mapAccessKey + ")).getValue();");
            out.println("   }");
            out.println();
         }
      }

      out.println();
      out.println("   @Override");
      out.println("   protected " + AbstractDtoMetaData.class.getName() + " createMetaData() {");
      out.println("      return new " + dtoClassSimpleName + "MetaData();");
      out.println("   }");
      out.println();

      out.println();
      out.println("   static public " + dtoClassSimpleName + " createInstance() {");
      out.println("      " + DtoCreator.class.getName() + "<" + dtoClassSimpleName + "> creator = " + GWT.class.getName()
               + ".create(" + dtoClassSimpleName + ".class);");
      out.println("      return creator.create();");
      out.println("   }");
      out.println();
      out.println("   static public " + AbstractDtoMetaData.class.getName() + " metaData() {");
      out.println("      return createInstance().getMetaData();");
      out.println("   }");
      out.println();

      generatePlatformSpecificDtoSource(out,
               fieldDescriptions,
               dtoClassName,
               entityClassName,
               mapperClassName);

      out.println("}");
   }

   /**
    * @return
    * @throws ClassNotFoundException 
    */
   protected String getDtoBaseClassNameForDtoSource() throws ClassNotFoundException
   {
      return dtoBaseClass.getName();
   }

   /**
    * @param out
    * @param argFieldDescriptions
    * @param argDtoClassName
    * @param argEntityClassName
    * @param argMapperClassName
    */
   protected void generatePlatformSpecificDtoSource(PrintStream out,
            List<AttributeDescription> argFieldDescriptions,
            String argDtoClassName, String argEntityClassName,
            String argMapperClassName)
   {
   }

   protected void generateDtoMapper(PrintStream out) throws Exception
   {
      String mapperClassSimpleName = getSimpleClassName(mapperClassName);
      String genericsAppendix = "<" + entityClassName + "," + dtoClassName + ">";

      printFileCopyrightComment(out);
      out.println("package " + mapperPackageName + ";");
      out.println();
      printImportsExistingAnnotations(out, annotations);
      out.println();
      printClassComment(out, "This class offers basic services to convert " + entityClassName + " objects into " + dtoClassName
               + " DTO objects and vice versa");

      out.println();
      printExistingAnnotations(out, annotations);

      Class<?> alreadySpecifiedMapperBaseClass = null;
      String mapperBaseClass;

      try
      {
         Class<?> mapperClass = Class.forName(mapperClassName);

         alreadySpecifiedMapperBaseClass = mapperClass.getSuperclass();

         mapperBaseClass = alreadySpecifiedMapperBaseClass.getName();

         if ((mapperClass.getGenericSuperclass() instanceof ParameterizedType)) {

            mapperBaseClass += genericsAppendix;
         }
      } catch (Exception e)
      {
         mapperBaseClass = getDefaultDtoMapperBaseClassName() + genericsAppendix;
      }

      String entityVariableName = "entity";
      String dtoVariableName = "dto";

      if (substituteDto != null)
      {
         if (alreadySpecifiedMapperBaseClass != null)
         {
            mapperBaseClass = alreadySpecifiedMapperBaseClass.getName();
         }

         entityVariableName = "specificEntity";
         dtoVariableName = "specificDto";
      }

      out.println("public class " + mapperClassSimpleName + " extends " + mapperBaseClass + " {");
      out.println("");
      out.println("   @Override");
      out.println("   public void fillEntityFromDto(" + interfaceRelevantEntityClassName + " entity, "
               + interfaceRelevantDtoClassName + " dto) {");
      out.println("   ");

      if (substituteDto != null)
      {
         out.println("      " + dtoClassName + " " + dtoVariableName + " = (" + dtoClassName + ")dto;");
         out.println("      " + entityClassName + " " + entityVariableName + " = " + JpaUtils.class.getName() + ".cast(entity);");
      }

      for (AttributeDescription field : fieldDescriptions)
      {
         if (!isAbstractDtoAttribute(field.name))
         {
            setEntityAttributeFromDto(out, field, dtoVariableName, entityVariableName);
         }
      }

      out.println("      super.fillEntityFromDto(entity, dto);");
      out.println("   }");
      out.println();
      //      out.println("   @SuppressWarnings({\"unchecked\", \"cast\"})");
      out.println("   @Override");
      out.println("   public void fillDtoFromEntity(" + interfaceRelevantDtoClassName + " dto, " + interfaceRelevantEntityClassName
               + " entity, boolean processReferences) {");
      out.println("   ");

      if (substituteDto != null)
      {
         out.println("      " + dtoClassName + " " + dtoVariableName + " = (" + dtoClassName + ")dto;");
         out.println("      " + entityClassName + " " + entityVariableName + " = " + JpaUtils.class.getName() + ".cast(entity);");
      }

      for (AttributeDescription field : fieldDescriptions)
      {
         if (!isAbstractDtoAttribute(field.name))
         {
            setDtoAttributeFromEntity(out, field, dtoVariableName, entityVariableName);
         }
      }

      out.println("      super.fillDtoFromEntity(dto, entity, processReferences);");
      out.println("   }");

      String resultType = dtoClassName;

      if (substituteDto != null)
      {
         resultType = interfaceRelevantDtoClassName;
      }

      out.println();
      out.println("   @Override");
      out.println("   protected Class<? extends " + resultType + "> getSpecificDtoClass() {");
      out.println("      return " + dtoClassName + ".class;");
      out.println("   }");
      out.println();

      generateSpecificMapperCode(out);

      out.println("}");
   }

   /**
    * @return
    */
   protected String getDefaultDtoMapperBaseClassName()
   {
      return AbstractObjectDtoMapper.class.getName();
   }

   /**
    * @param out
    */
   protected void generateSpecificMapperCode(PrintStream out)
   {
      Class<?> mapperClass;
      
      try {
         
         mapperClass = Class.forName(mapperClassName);
         
         if (AbstractDtoMapper.class.isAssignableFrom(mapperClass)) {
            
            out.println();
            out.println("   @Override");
            out.println("   public " + entityClassName + " createEmptyEntity() {");
            out.println("      return new " + entityClassName + "();");
            out.println("   }");
         }
      } catch (ClassNotFoundException e) {
         // ignore
      }
   }

   protected void generateDtoMetaData(PrintStream out) throws Exception
   {
      String dtoPackage = dtoClassName.substring(0, dtoClassName.lastIndexOf('.'));
      String dtoClassSimpleName = getSimpleClassName(dtoClassName);

      printFileCopyrightComment(out);
      out.println("package " + dtoPackage + ";");
      out.println();

      printClassComment(out, "This class offers meta data for the DTO class " + dtoClassName);

      String baseClassName = getDefaultMetaDataClass().getName();
      String inheritBaseMetaData = getDefaultInheritBaseMetaData();
      String dataMemberName = createMetaDataMapMemberName(dtoClassSimpleName + "MetaData");

      if (substituteDto != null) {
         baseClassName = dtoOverridenClassName + "MetaData";
         inheritBaseMetaData = createMetaDataMapMemberName(getSimpleClassName(baseClassName));
      }

      try
      {
         Class<?> alreadySpecifiedMetaDataBaseClass = Class.forName(dtoClassName + "MetaData").getSuperclass();

         if (!alreadySpecifiedMetaDataBaseClass.getName().equals(baseClassName))
         {
            baseClassName = alreadySpecifiedMetaDataBaseClass.getName();

            if (AbstractDtoMetaData.class.getName().equals(baseClassName)) {
               inheritBaseMetaData = "";
            } else {
               inheritBaseMetaData = createMetaDataMapMemberName(getSimpleClassName(baseClassName));
            }
         }
      } catch (Exception metaClassDoesNotExistYet) {
      }

      out.println("public class " + dtoClassSimpleName + "MetaData extends " + baseClassName + "{");
      out.println();
      String metaDataClassName = AbstractDtoMetaData.MetaData.class.getCanonicalName();
      String hashMapClassName = HashMap.class.getName() + "<String, " + metaDataClassName + ">";
      out.println("   protected static final " + hashMapClassName + " " + dataMemberName + ";");
      for (AttributeDescription field : fieldDescriptions)
      {
         if (!isAbstractDtoAttribute(field.name))
         {
            out.println("   public final static String " + convertCamelCase2Capital(field.name) + " = \"" + field.name + "\";");
         }
      }
      out.println();
      for (AttributeDescription field : fieldDescriptions)
      {
         out.println("   public final static " + metaDataClassName + " " + convertCamelCase2Capital(field.name)
                  + META_DATA_CONSTANT_SUFFIX + ";");
      }
      out.println();
      generateEnumMetaData(out);
      out.println("   static {");
      out.println("      " + dataMemberName + " = new " + hashMapClassName + "(" + inheritBaseMetaData + ");");
      out.println("      " + metaDataClassName + " metaData;");

      for (AttributeDescription field : fieldDescriptions)
      {
         out.println();
         out.println("      metaData = new " + metaDataClassName + "();");
         setMetaDataValues(out, field);
         out.println("      " + dataMemberName + ".put(" + convertCamelCase2Capital(field.name) + ", metaData);");
         out.println("      " + convertCamelCase2Capital(field.name) + META_DATA_CONSTANT_SUFFIX + " = metaData;");
      }

      out.println("   }");
      out.println();
      out.println("   protected " + dtoClassSimpleName + "MetaData() {");
      out.println("   }");
      out.println();
      out.println("   @Override");
      out.println("   protected " + hashMapClassName + " getMetaData() {");
      out.println("      return " + dataMemberName + ";");
      out.println("   }");
      out.println();
      out.println("   @Override");
      out.println("   public String getDtoClassName() {");
      out.println("      return \"" + dtoClassName + "\";");
      out.println("   }");
      out.println();

      if (dtoOverridenClassName != null)
      {
         out.println("   @Override");
         out.println("   public String getOverridenDtoClassName() {");
         out.println("      return \"" + dtoOverridenClassName + "\";");
         out.println("   }");
         out.println();
      }

      out.println("   @Override");
      out.println("   protected " + AbstractDto.class.getName() + " createNewDtoOnClient() {");
      out.println("      return " + dtoClassSimpleName + ".createInstance();");
      out.println("   }");
      out.println();
      out.println("   @Override");
      out.println("   public String getMapperClassName() {");
      out.println("      return \"" + mapperClassName + "\";");
      out.println("   }");
      out.println();
      generateTableNameInMetaData(out);
      out.println("}");
   }

   /**
    * @param dtoMetaDataClassSimpleName
    * @return
    */
   protected String createMetaDataMapMemberName(String dtoMetaDataClassSimpleName)
   {
      return  Character.toLowerCase(dtoMetaDataClassSimpleName.charAt(0)) + dtoMetaDataClassSimpleName.substring(1) + "Map";
   }

   /**
    * @param out
    */
   protected void generateEnumMetaData(PrintStream out)
   {
      for (AttributeDescription field : fieldDescriptions)
      {
         if (field.domainValueClassName != null)
         {
            List<MyDomainValue> enums = getDomainValues(field.domainValueClassName);

            for (MyDomainValue enumValue : enums)
            {
               if (enumValue.isLong)
               {
                  out.println("   public final static long ENUM_" + convertCamelCase2Capital(field.name) + "_" + enumValue.name
                           + " = " + enumValue.key + ";");
               }
               else
               {
                  out.println("   public final static String ENUM_" + convertCamelCase2Capital(field.name) + "_" + enumValue.name
                           + " = \"" + enumValue.key + "\";");
               }
            }

            out.println();
         }
         else if (field.enumClassName != null)
         {
            for (String enumValue : field.enumValues)
            {
               out.println("   public final static String ENUM_" + convertCamelCase2Capital(field.name) + "_" + enumValue
                        + " = \"" + enumValue + "\";");
            }

            out.println();
         }
      }
   }

   /**
    * @param domainValueClassName
    * @return
    */
   protected List<MyDomainValue> getDomainValues(String domainValueClassName)
   {
      List<MyDomainValue> result = new ArrayList<MyDomainValue>();

      try
      {
         Class<?> domainValueClass = Class.forName(domainValueClassName);

         Field[] fields = domainValueClass.getFields();

         for (Field field : fields)
         {
            if (Modifier.isStatic(field.getModifiers())
                     && DomainValueReference.class.isAssignableFrom(field.getType()))
            {
               MyDomainValue newEntry = new MyDomainValue();

               newEntry.name = field.getName();
               AbstractDomainValue<?> abstractDomainValue = ((DomainValueReference<AbstractDomainValue<?>>) field.get(null)).get();
               newEntry.key = abstractDomainValue instanceof DomainValue
                        ? ((DomainValue) abstractDomainValue).getValue()
                        : "" + ((LongDomainValue) abstractDomainValue).getValue();

               if (!result.contains(newEntry))
               {
                  result.add(newEntry);
               }
            }
         }
      } catch (Throwable e)
      {
         result.clear();
      }

      return result;
   }

   /**
    * @param out
    */
   protected void generateTableNameInMetaData(PrintStream out)
   {

   }

   /**
    * @return
    */
   protected Class<? extends AbstractDtoMetaData> getDefaultMetaDataClass()
   {
      return AbstractDtoMetaData.class;
   }

   protected void setMetaDataValues(PrintStream out, AttributeDescription field)
   {
      out.println("      metaData.setType(" + AbstractDtoMetaData.MetaData.Type.class.getCanonicalName() + "." + getDtoMetaDataType(field)
               + ");");
      out.println("      metaData.setLength(" + field.size + ");");
      out.println("      metaData.setMandatory(" + field.isMandatory + ");");
      out.println("      metaData.setLogicalId(" + field.logicalIdIndex + ");");

      if (field.domainValueClassName != null)
      {
         out.println("      metaData.setDomainValueClassName(\"" + field.domainValueClassName + "\");");
      }

      if (field.embeddedSimpleTypeClassName != null)
      {
         out.println("      metaData.setSimpleTypeClassName(\"" + field.embeddedSimpleTypeClassName + "\");");
      }

      if (field.enumClassName != null)
      {
         out.println("      metaData.setEnumClassName(\"" + field.enumClassName + "\");");

         if (field.enumValues != null && field.enumValues.length > 0)
         {
            out.print("      metaData.setAllowedValues(new String[] {\"" + field.enumValues[0] + "\"");

            for (int i = 1; i < field.enumValues.length; i++)
            {
               out.println(",");
               out.print("                                              \"" + field.enumValues[i] + "\"");
            }

            out.println("});");
         }
      }

      if (field.referencedValueObjectClassName != null)
      {
         out.println("      metaData.setReferencedDtoMetaDataCreator(new " + DtoMetaDataCreator.class.getCanonicalName() + "() {");
         out.println("      ");
         out.println("         @Override");
         out.println("         public " + AbstractDtoMetaData.class.getName() + " createDtoMetaData() {");
         out.println("            return new "
                  + SingleClassDtoMapperGenerator.existingMappings.get(field.referencedValueObjectClassName).dtoClassName + "MetaData(){};");
         out.println("         }");
         out.println("      });");
      }

      if (field.rangeInfo != null)
      {
         out.println("      metaData.setRangeInfo(new " + RangeInfo.class.getCanonicalName() + "("
                  + (field.rangeInfo.getMinValue() != null ? "\"" + field.rangeInfo.getMinValue() + "\"" : "null") + ", "
                  + (field.rangeInfo.getMaxValue() != null ? "\"" + field.rangeInfo.getMaxValue() + "\"" : "null") + "));");
      }
   }

   /**
    * @param field
    * @return
    */
   protected Type getDtoMetaDataType(AttributeDescription field) {
      
      Type result = getType(field);
      
      if (field.temporalType != null) {
         
         switch (field.temporalType) {
            
            case DATE: 
               result = Type.DATE;
               break;
               
            case TIME: 
               result = Type.TIME;
               break;
               
            case TIMESTAMP : 
               result = Type.TIMESTAMP;
         }
      }
      
      return result;
   }

   /**
    * @param field
    * @return
    */
   protected String getColumnName(AttributeDescription field)
   {
      String result = field.name;

      if (field.manualSetDatabaseColumnName != null)
      {
         result = field.manualSetDatabaseColumnName;
      }
      else
      {
         try
         {
            String name = field.name;

            name = ImprovedNamingStrategy.INSTANCE.propertyToColumnName(name);

            result = name.toUpperCase();
         } catch (Exception e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      return result;
   }
}
