package com.advansys.dto.generator;



/**
 * Copyright (c) Dematic GmbH 2010. All rights reserved. Confidential.
 */
 
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dematic.wms.app.base.entity.AbstractDomainValue;
import com.dematic.wms.app.base.entity.AbstractEntity;
import com.dematic.wms.app.base.entity.AbstractEntityWithoutVersion;
import com.dematic.wms.app.base.entity.AbstractOptimisticLockEntity;
import com.dematic.wms.app.base.entity.AbstractTechnicalEntity;
import com.dematic.wms.app.base.entity.SimpleType;
import com.dematic.wms.app.base.entity.annotation.LogicalKeyPart;
import com.dematic.wms.app.base.ui.server.dto.AbstractEntityDtoMapper;
import com.dematic.wms.app.base.ui.server.dto.annotation.ManualDatabaseTableNameMetaData;
import com.dematic.wms.app.base.ui.shared.common.JdbcEscapeSequences;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.MetaData.CollectionAccess;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.MetaData.EmbeddedMetaData;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.MetaData.RangeInfo;
import com.dematic.wms.app.base.ui.shared.dto.AbstractDtoMetaData.MetaData.Type;
import com.dematic.wms.app.base.ui.shared.dto.AbstractEntityDto;
import com.dematic.wms.app.base.ui.shared.dto.AbstractEntityDtoMetaData;
import com.dematic.wms.app.base.ui.shared.dto.AbstractEntityWithoutVersionDto;
import com.dematic.wms.app.base.ui.shared.dto.AbstractEntityWithoutVersionDtoMetaData;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

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
class EntityDtoMapperGenerator extends BaseDtoMapperGenerator {

   private boolean embeddableMapCreated;

   private boolean collectionAccessCreated;

   /**
    * @param argEntityClassName
    * @param argDtoClassName
    * @param argMapperClassName
    * @param argDtoSourceStream
    * @param argDtoMetaDataStream
    * @param argDtoMapperSourceStream
    * @param argEntityClass
    * @param argAnnotations
    * @throws Exception
    */
   @Override
   public void generateClasses(String argEntityClassName,
                               String argDtoClassName,
                               String argMapperClassName,
                               PrintStream argDtoSourceStream,
                               PrintStream argDtoMetaDataStream,
                               PrintStream argDtoMapperSourceStream,
                               Class<?> argEntityClass,
                               Annotation[] argAnnotations) throws Exception {

      super.generateClasses(argEntityClassName,
                            argDtoClassName,
                            argMapperClassName,
                            argDtoSourceStream,
                            argDtoMetaDataStream,
                            argDtoMapperSourceStream,
                            argEntityClass,
                            argAnnotations);
   }

   @Override
   protected String getTableName(Class<?> argEntityClass, Annotation[] argAnnotations) throws Exception {

      String result = null;

      for (Annotation candidate : argAnnotations) {

         if (candidate instanceof ManualDatabaseTableNameMetaData) {
            result = ((ManualDatabaseTableNameMetaData) candidate).name();
            break;
         }
      }

      if (result == null) {

         result = evaluateTableAnnotation(argEntityClass);
      }

      return result;
   }

   protected String evaluateTableAnnotation(Class<?> argEntityClass) {

      String result;
      Table tableInfo = null;

      for (Class<?> currentClass = argEntityClass;
           !currentClass.equals(Object.class) && !currentClass.equals(AbstractEntityWithoutVersion.class) && (tableInfo == null);
           currentClass = currentClass.getSuperclass()) {

         tableInfo = currentClass.getAnnotation(Table.class);
      }

      if (tableInfo == null) {
         throw new RuntimeException("entity class " + argEntityClass.getName()
                  + " has no @Table annotation which defines the table name");
      }

      String schema = tableInfo.schema();

      result = ((schema != null && !schema.isEmpty()) ? (schema + ".")
                                                      : "") + tableInfo.name();

      if (result == null) {
         throw new RuntimeException("entity class " + argEntityClass.getName()
                  + " defines no table name in its @Table annotation");
      }

      return result;
   }

   protected String getDiscriminatorColumnCondition(Class<?> argEntityClass) {

      String result = null;

      for (Class<?> currentClass = argEntityClass;
           !currentClass.equals(Object.class) && !currentClass.equals(AbstractEntityWithoutVersion.class);
           currentClass = currentClass.getSuperclass()) {

         Inheritance inheritance = currentClass.getAnnotation(Inheritance.class);

         if (inheritance != null && inheritance.strategy() == InheritanceType.SINGLE_TABLE) {

            String discriminatorColumnName = "DTYPE";
            DiscriminatorType discriminatorType = DiscriminatorType.STRING;
            DiscriminatorColumn discriminatorColumn = currentClass.getAnnotation(DiscriminatorColumn.class);

            if (discriminatorColumn != null) {

               discriminatorColumnName = discriminatorColumn.name();
               discriminatorType = discriminatorColumn.discriminatorType();
            }

            String discriminatorValue = argEntityClass.getSimpleName();
            DiscriminatorValue discriminatorValueAnnotation = argEntityClass.getAnnotation(DiscriminatorValue.class);

            if (discriminatorValueAnnotation != null) {

               discriminatorValue = discriminatorValueAnnotation.value();
            }

            result = discriminatorColumnName + " = "
                     + ((discriminatorType != DiscriminatorType.INTEGER) ? "'" : "")
                     + JdbcEscapeSequences.esc(discriminatorValue)
                     + ((discriminatorType != DiscriminatorType.INTEGER) ? "'" : "");

            break;
         }
      }

      return result;
   }

   /* (non-Javadoc)
    * @see com.dematic.wcs.app.gwt.gui.server.dto.BaseDtoMapperGenerator#getAuditTrailTableName(java.lang.Class)
    */
   @Override
   protected String getAuditTrailTableName(Class<?> argEntityClass) throws Exception {

      String result = null;

      if (argEntityClass.getAnnotation(Audited.class) != null) {

         result = tableName + "_AUD";

         AuditTable auditTable = argEntityClass.getAnnotation(AuditTable.class);

         if (auditTable != null) {
            result = auditTable.value();
         }
      }

      return result;
   }

   /**
    * @param field
    * @param description
    */
   @Override
   protected void determineCollectionAccess(Field field, AttributeDescription description) {

      JoinTable joinTable = field.getAnnotation(JoinTable.class);

      if (joinTable != null) {


         String joinTableName = joinTable.name().trim();

         description.collectionAccess = new CollectionAccess();
         description.collectionAccess.setJoinTableName(joinTableName);

         // If no join column is set for my own PK, then my table name is used
         String myPrimaryKeyColumnName = tableName;

         if (joinTable.joinColumns().length == 1) {
            myPrimaryKeyColumnName = joinTable.joinColumns()[0].name().trim();
         }

         description.collectionAccess.setMyPrimaryKeyColumnName(myPrimaryKeyColumnName);

         // If no explicit column name is set, than the column name is derived from the field name 
         String collectionPrimaryKeyColumnName = convertCamelCase2Capital(field.getName());

         if (joinTable.inverseJoinColumns().length == 1) {
            collectionPrimaryKeyColumnName = joinTable.inverseJoinColumns()[0].name();
         }

         description.collectionAccess.setCollectionPrimaryKeyColumnName(collectionPrimaryKeyColumnName);

      } else {

         JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

         if (joinColumn != null) {
            description.collectionAccess = new CollectionAccess();
            description.collectionAccess.setCollectionPrimaryKeyColumnName(joinColumn.name());

         } else {

            OneToMany oneToMany = field.getAnnotation(OneToMany.class);

            if (oneToMany != null) {

               if (oneToMany.mappedBy().trim().length() > 0) {

                  description.collectionAccess = new CollectionAccess();
                  description.collectionAccess.setMappedByAttributeName(oneToMany.mappedBy().trim());

               } else {

                  java.lang.reflect.Type genericType = field.getGenericType();

                  if (genericType instanceof ParameterizedType) {

                     Class<?> referencedType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];

                     for (Field backReferenceCandidate : referencedType.getDeclaredFields()) {

                        if (backReferenceCandidate.getType().equals(field.getDeclaringClass())
                                 && backReferenceCandidate.getAnnotation(ManyToOne.class) != null) {

                           description.collectionAccess = new CollectionAccess();
                           description.collectionAccess.setMappedByAttributeName(backReferenceCandidate.getName());
                           break;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   /* (non-Javadoc)
    * @see com.dematic.wcs.app.gwt.gui.server.dto.BaseDtoMapperGenerator#getDefaultDtoBaseClass()
    */
   @Override
   protected Class<?> getDefaultDtoBaseClass() {
      return AbstractOptimisticLockEntity.class.isAssignableFrom(entityClass)
               ? AbstractEntityDto.class : AbstractEntityWithoutVersionDto.class;
   }

   @Override
   protected boolean isAbstractDtoAttribute(String attributeName) {

      boolean result = false;

      if (AbstractEntityDto.class.isAssignableFrom(dtoBaseClass)) {

         for (String keyAttribute : AbstractEntityDtoMetaData.KEY_ATTRIBUTES) {

            if (keyAttribute.equals(attributeName)) {
               result = true;
               break;
            }
         }
      }

      return result;
   }

   @Override
   protected void evaluateSingleAttribute(Class<?> entityAttributeType,
                                          AttributeDescription description,
                                          Class<?> argEntityClass,
                                          Annotation[] mapperAnnotations,
                                          Field field) throws Exception {

      boolean isEmbeddable = entityAttributeType.getAnnotation(Embeddable.class) != null
               && !SimpleType.class.isAssignableFrom(entityAttributeType)
               && !AbstractDomainValue.class.isAssignableFrom(entityAttributeType);

      if (AbstractEntityWithoutVersion.class.isAssignableFrom(entityAttributeType)
               || isEmbeddable) {

         description.referencedValueObjectClassName = entityAttributeType.getName();
         description.dtoAttributeClassName = handleReference(description,
                  argEntityClass);

         if (isEmbeddable) {

            description.embeddedColumnNames = retrieveEmbeddedColumnNames(field,
                                                                          getColumnnameMap(field));
         }

         LogicalKeyPart logicalIdAnnotation = field.getAnnotation(LogicalKeyPart.class);

         if (logicalIdAnnotation != null) {
            description.logicalIdIndex = logicalIdAnnotation.index();
         }

      } else {
         super.evaluateSingleAttribute(entityAttributeType, description, argEntityClass, mapperAnnotations, field);
      }

      if (entityAttributeType.isPrimitive()) {
         description.isMandatory = true;
      }

      Column columnDescription = field.getAnnotation(Column.class);

      if (columnDescription == null) {

         final AttributeOverride attributeOverride = field.getAnnotation(AttributeOverride.class);

         if (attributeOverride != null) {
            columnDescription = attributeOverride.column();
         }
      }

      if (columnDescription != null) {
         final String columnName = columnDescription.name();

         if (columnName != null && columnName.trim().length() > 0) {
            description.manualSetDatabaseColumnName = columnName;
         }

         if (!columnDescription.nullable()) {
            description.isMandatory = true;
         }
      }

      JoinColumn joinColumnDescription = field.getAnnotation(JoinColumn.class);

      if (joinColumnDescription != null) {

         final String columnName = joinColumnDescription.name();

         if (columnName != null && columnName.trim().length() > 0) {
            description.manualSetDatabaseColumnName = columnName;
         }
      }

      NotNull notNullAnnotation = field.getAnnotation(NotNull.class);

      if (notNullAnnotation != null && notNullAnnotation.groups().length == 0) {
         description.isMandatory = true;
      }

      NotEmpty notEmptyAnnotation = field.getAnnotation(NotEmpty.class);

      if (notEmptyAnnotation != null && notEmptyAnnotation.groups().length == 0) {
         description.isMandatory = true;
      }

      if (field.getAnnotation(Size.class) != null) {
         Size size = field.getAnnotation(Size.class);
         description.size = size.max();
      }

      String maxRangeValue = null;
      String minRangeValue = null;

      Max maxAnnotation = field.getAnnotation(Max.class);

      if ((maxAnnotation != null)
               && (maxAnnotation.groups().length == 0)) {
         maxRangeValue = "" + maxAnnotation.value();
      }

      DecimalMax decimalMaxAnnotation = field.getAnnotation(DecimalMax.class);

      if ((decimalMaxAnnotation != null)
               && (decimalMaxAnnotation.groups().length == 0)) {
         maxRangeValue = decimalMaxAnnotation.value();
      }

      Min minAnnotation = field.getAnnotation(Min.class);

      if ((minAnnotation != null)
               && (minAnnotation.groups().length == 0)) {
         minRangeValue = "" + minAnnotation.value();
      }

      DecimalMin decimalMinAnnotation = field.getAnnotation(DecimalMin.class);

      if ((decimalMinAnnotation != null)
               && (decimalMinAnnotation.groups().length == 0)) {
         minRangeValue = decimalMinAnnotation.value();
      }

      if ((maxRangeValue != null)
               || (minRangeValue != null)) {

         description.rangeInfo = new RangeInfo(minRangeValue, maxRangeValue);
      }
      
      Temporal temporal  = field.getAnnotation(Temporal.class);
      
      if (temporal != null) {

         description.temporalType = temporal.value();

         switch (description.temporalType) {

            case DATE:
               description.dtoAttributeClassName = java.sql.Date.class.getName();
               break;

            case TIMESTAMP:
               description.dtoAttributeClassName = Timestamp.class.getName();
               break;

            case TIME:
               description.dtoAttributeClassName = Time.class.getName();
               break;
         }
      }
      
      OneToOne onToOne = field.getAnnotation(OneToOne.class);
      
      if (onToOne != null && !onToOne.mappedBy().isEmpty()) {
         description.mappedByAttribute = onToOne.mappedBy();
      }
   }

   /**
    * @param annotatedMappings 
    * @param embeddedField
    * @return
    */
   private Map<String, EmbeddedMetaData> retrieveEmbeddedColumnNames(Field embeddedField, Map<String, String> annotatedMappings) {

      Map<String, EmbeddedMetaData> result = new HashMap<String, EmbeddedMetaData>();

      LogicalKeyPart logicalKeyPartAnnotation = embeddedField.getAnnotation(LogicalKeyPart.class);
      List<String> logicalKeyPart = null;
      int logicalIdIndex = 0;

      if (logicalKeyPartAnnotation != null) {
         logicalKeyPart = Arrays.asList(logicalKeyPartAnnotation.embeddedKeys());
      }

      for (java.lang.reflect.Field field : embeddedField.getType().getDeclaredFields()) {

         int modifiers = field.getModifiers();

         if (Modifier.isTransient(modifiers)
                  || Modifier.isStatic(modifiers)
                  || field.getAnnotation(Transient.class) != null)
            continue;

         Class<?> fieldType = field.getType();

         if (Object.class.getName().equals(fieldType.getCanonicalName())) {
            // members of type java.lang.Object are not supported by GWT (not serializable)
            continue;
         }

         String fieldName = field.getName();

         if (AbstractDomainValue.class.isAssignableFrom(fieldType)
                  || SimpleType.class.isAssignableFrom(fieldType)
                  || field.getAnnotation(Embedded.class) == null) {

            String annotatedMappingsKey;

            if (AbstractDomainValue.class.isAssignableFrom(fieldType)
                     || SimpleType.class.isAssignableFrom(fieldType)) {

               annotatedMappingsKey = fieldName + ".value";

            } else {
               annotatedMappingsKey = fieldName;
            }

            String columnName = annotatedMappings.get(annotatedMappingsKey);

            if (columnName == null) {
               columnName = convertCamelCase2Capital(fieldName);
            }

            EmbeddedMetaData value = new EmbeddedMetaData(columnName,
                     (logicalKeyPart != null
                     && logicalKeyPart.contains(fieldName))
                              ? logicalIdIndex++
                              : -1);
            result.put(fieldName, value);

         } else if (field.getAnnotation(Embedded.class) != null) {

            Map<String, EmbeddedMetaData> subFields = retrieveEmbeddedColumnNames(field, annotatedMappings);

            for (Map.Entry<String, EmbeddedMetaData> entry : subFields.entrySet()) {
               result.put(fieldName + "." + entry.getKey(), entry.getValue());
            }
         }
      }

      return result;
   }

   /**
    * @param embeddedField
    */
   protected Map<String, String> getColumnnameMap(Field embeddedField) {

      Map<String, String> result = getDefaultMappings(embeddedField);
      AttributeOverride[] overrides = new AttributeOverride[0];
      AttributeOverrides attributes = embeddedField.getAnnotation(AttributeOverrides.class);      

      if (attributes != null) {
         overrides = attributes.value();

      } else {

         AttributeOverride attribute = embeddedField.getAnnotation(AttributeOverride.class);

         if (attribute != null) {
            overrides = new AttributeOverride[1];
            overrides[0] = attribute;
         }
      }

      for (AttributeOverride attributeOverride : overrides) {

         Column column = attributeOverride.column();

         if (column != null) {
            result.put(attributeOverride.name(), column.name());
         }
      }

      AssociationOverride[] associationOverrides = new AssociationOverride[0];
      AssociationOverrides associationAttributes = embeddedField.getAnnotation(AssociationOverrides.class);

      if (associationAttributes != null) {
         associationOverrides = associationAttributes.value();

      } else {

         AssociationOverride attribute = embeddedField.getAnnotation(AssociationOverride.class);

         if (attribute != null) {
            associationOverrides = new AssociationOverride[1];
            associationOverrides[0] = attribute;
         }
      }

      for (AssociationOverride associationOverride : associationOverrides) {

         JoinColumn[] column = associationOverride.joinColumns();

         if (column != null && column.length == 1 && column[0] != null) {
            result.put(associationOverride.name(), column[0].name());
         }
      }

      return result;
   }

   /**
    * @param embeddedField 
    * @return
    */
   protected HashMap<String, String> getDefaultMappings(Field embeddedField) {
      
      HashMap<String, String> result = new HashMap<>();
      Class<?> embeddable = embeddedField.getType();
      
      for (Field embeddableField : embeddable.getDeclaredFields()) {
         
         Column columnDescription = embeddableField.getAnnotation(Column.class);
         
         if (columnDescription != null) {
            
            String name = columnDescription.name();
            
            if (!name.isEmpty()) {
               result.put(embeddableField.getName(), name);
            }
         }
      }
      
      return result;
   }

   @Override
   protected String getDefaultInheritBaseMetaData() {

      return AbstractOptimisticLockEntity.class.isAssignableFrom(entityClass)
               ? "abstractEntityDtoMetaDataMap"
               : AbstractEntityWithoutVersion.class.isAssignableFrom(entityClass) ? "abstractEntityWithoutVersionDtoMetaDataMap"
                                                                                  : "";
   }

   @Override
   protected boolean isBaseClassReached(Class<?> entity) {

      return entity.equals(AbstractEntity.class)
               || entity.equals(AbstractTechnicalEntity.class)
               || entity.equals(AbstractOptimisticLockEntity.class)
               || entity.equals(AbstractEntityWithoutVersion.class)
               || super.isBaseClassReached(entity);
   }

   /* (non-Javadoc)
    * @see com.dematic.wcs.app.gwt.gui.server.dto.BaseDtoMapperGenerator#callBaseClassInGenericDataAccessors()
    */
   @Override
   protected boolean callBaseClassInGenericDataAccessors() {
      return true;
   }

   /* (non-Javadoc)
    * @see com.dematic.wcs.app.gwt.gui.server.dto.BaseDtoMapperGenerator#getDefaultMetDataClass()
    */
   @Override
   protected Class<? extends AbstractDtoMetaData> getDefaultMetaDataClass() {

      return AbstractOptimisticLockEntity.class.isAssignableFrom(entityClass)
               ? AbstractEntityDtoMetaData.class : AbstractEntityWithoutVersionDtoMetaData.class;
   }

   @Override
   protected void generateTableNameInMetaData(PrintStream out) {

      if (substituteDto == null) {

         out.println("   @Override");
         out.println("   public String getTableName() {");
         out.println("      return \"" + tableName + "\";");
         out.println("   }");
         out.println();

         if (auditTrailTableName != null) {

            out.println("   @Override");
            out.println("   public String getAuditTrailTableName() {");
            out.println("      return \"" + auditTrailTableName + "\";");
            out.println("   }");
            out.println();
         }
      }

      String discriminatorColumnCondition = getDiscriminatorColumnCondition(entityClass);

      if (discriminatorColumnCondition != null) {

         out.println("   @Override");
         out.println("   public String getDiscriminatorColumnCondition() {");
         out.println("      return \"" + discriminatorColumnCondition + "\";");
         out.println("   }");
         out.println();
      }
   }

   @Override
   protected void setMetaDataValues(PrintStream out, AttributeDescription field) {

      super.setMetaDataValues(out, field);

      CollectionAccess collectionAccess = field.collectionAccess;

      if (getType(field) == Type.COLLECTION && collectionAccess != null) {

         if (!collectionAccessCreated) {
            collectionAccessCreated = true;
            out.println("      " + CollectionAccess.class.getCanonicalName() + " collectionAccess;");
         }

         out.println("      collectionAccess = new " + CollectionAccess.class.getCanonicalName() + "();");

         if (collectionAccess.getJoinTableName() != null) {
            out.println("      collectionAccess.setJoinTableName(\"" + collectionAccess.getJoinTableName() + "\");");
         }

         if (collectionAccess.getMyPrimaryKeyColumnName() != null) {
            out.println("      collectionAccess.setMyPrimaryKeyColumnName(\"" + collectionAccess.getMyPrimaryKeyColumnName()
                     + "\");");
         }

         if (collectionAccess.getCollectionPrimaryKeyColumnName() != null) {
            out.println("      collectionAccess.setCollectionPrimaryKeyColumnName(\""
                     + collectionAccess.getCollectionPrimaryKeyColumnName() + "\");");
         }

         if (collectionAccess.getMappedByAttributeName() != null) {
            out.println("      collectionAccess.setMappedByAttributeName(\"" + collectionAccess.getMappedByAttributeName() + "\");");
         }

         out.println("      metaData.setCollectionAccess(collectionAccess);");

      } else if (getType(field) == Type.EMBEDDED) {

         if (!embeddableMapCreated) {
            embeddableMapCreated = true;
            out.println("      " + Map.class.getName() + "<String, " + EmbeddedMetaData.class.getCanonicalName()
                     + "> embeddableColumnNameMap;");
         }

         out.println("      embeddableColumnNameMap = new " + HashMap.class.getName() + "<String, "
                  + EmbeddedMetaData.class.getCanonicalName() + ">();");

         for (Map.Entry<String, EmbeddedMetaData> entry : field.embeddedColumnNames.entrySet()) {
            EmbeddedMetaData value = entry.getValue();
            out.println("      embeddableColumnNameMap.put(\"" + entry.getKey() + "\", new "
                     + EmbeddedMetaData.class.getCanonicalName() + "(\"" + value.getDatabaseColumnName() + "\", "
                     + value.getLogicalIdIndex() + "));");
         }

         out.println("      metaData.setEmbeddedObjectDatabaseColumnNames(embeddableColumnNameMap);");

      } else if ((getType(field) == Type.REFERENCE) 
                    && (field.mappedByAttribute != null)) {
         
         out.println("      metaData.setMappedByAttribute(\"" + field.mappedByAttribute + "\");");            
         
      } else {
         out.println("      metaData.setDatabaseColumnName(\"" + getColumnName(field) + "\");");
      }
   }

   /* (non-Javadoc)
    * @see com.dematic.wcs.app.gwt.gui.server.dto.BaseDtoMapperGenerator#getDefaultDtoMapperClass()
    */
   @Override
   protected String getDefaultDtoMapperBaseClassName() {
      return AbstractEntityDtoMapper.class.getName();
   }

   /* (non-Javadoc)
    * @see com.dematic.wcs.app.gwt.gui.server.dto.BaseDtoMapperGenerator#generateSpecificMapperCode(java.io.PrintStream)
    */
   @Override
   protected void generateSpecificMapperCode(PrintStream out) {

      Class<?> mapperClass;

      try {

         mapperClass = Class.forName(mapperClassName);

         if (!AbstractEntityDtoMapper.class.isAssignableFrom(mapperClass)) {

            super.generateSpecificMapperCode(out);
         }
      } catch (ClassNotFoundException e) {
         // ignore
      }
   }
}
