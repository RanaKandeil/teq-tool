package com.advansys.dto.generator;

/**
 * Copyright (c) Dematic GmbH 2010. All rights reserved. Confidential.
 */

import javax.persistence.Entity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.dematic.apache.bcel.classfile.AnnotationEntry;
import com.dematic.apache.bcel.classfile.ClassParser;
import com.dematic.apache.bcel.classfile.ElementValuePair;
import com.dematic.apache.bcel.classfile.JavaClass;
import com.dematic.apache.bcel.classfile.Method;
import com.dematic.wms.app.base.entity.AbstractEntityWithoutVersion;
import com.dematic.wms.app.base.ui.server.dto.annotation.GeneratedDtoMapper;
import com.dematic.wms.app.base.ui.server.dto.annotation.ManualDtoMapper;
import com.dematic.wms.app.base.ui.server.dto.annotation.SubstituteDto;
import com.dematic.wms.app.base.ui.shared.dto.GenerateDto;

import controllers.DynamicPage;

public class SingleClassDtoMapperGenerator {

	/**
	* 
	*/
	public static final String META_DATA_CONSTANT_SUFFIX = "_META_DATA";

	static class Mapper {
		public boolean needsDownCast;
		public String dtoClassName;
		public String mapperClassName;
	}

	static Map<String, Mapper> existingMappings = new HashMap<String, Mapper>();

	static Map<String, List<JavaClass>> existingFactories = new HashMap<String, List<JavaClass>>();

	static Set<String> existingProviders = new HashSet<String>();

	static String srcDirNameGenerated;

	static String targetDirPath;
	static String srcDirNameJava;

	static ZipFile sqlKeyWordZipFile;

	static ZipEntry sqlKeyWordZipEntry;

	static URLClassLoader classLoader = null;

	public static void generateDtoForSpecificClass(String dtoClassName, String targetDir,
			String generatedSrcFilesDirectory, String javaSrcFilesDirectory) throws Exception {

		srcDirNameGenerated = generatedSrcFilesDirectory;
		srcDirNameJava = javaSrcFilesDirectory;

		targetDirPath = targetDir;
		determineAllDtoEntityTuples(dtoClassName);

		System.out.println("Evaluating directory (" + targetDirPath + "/classes) for DTOs");
		analyseClassDirectory(targetDirPath + "/classes", dtoClassName);
		Files.walkFileTree(FileSystems.getDefault().getPath(targetDirPath, "classes"), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				String classFileName = file.toString();

				if (classFileName.endsWith(".class") && classFileName.contains(dtoClassName)) {

					System.out.println("DtoMapperGenerator::main -> processing class (" + classFileName + ")");
					ClassParser parser = new ClassParser(classFileName);

					try {
						processClass(classFileName, parser);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void main(String[] args) throws Exception {

		

		String targetDirName = "C:\\Users\\asharafs\\workspace\\carrefour_merge\\sources\\fallavier\\target";

		SingleClassDtoMapperGenerator.generateDtoForSpecificClass("OrderSQFDto", targetDirName,"C://Users//asharafs//workspace//carrefour_merge//sources//fallavier//src//main//generated","C://Users//asharafs//workspace//carrefour_merge//sources//fallavier//src//main//java");
		 
	}

	protected static void processClass(String classFileName, ClassParser parser)
			throws IOException, FileNotFoundException, Exception, ClassNotFoundException {

		System.out.println("DtoMapperGenerator::processClass - classFileName(" + classFileName + ")");

		JavaClass classDescription = parser.parse();

		for (AnnotationEntry annotationEntry : classDescription.getAnnotationEntries()) {

			System.out.println("   Checking if the DTO class has the annotation (" + GenerateDto.class.getSimpleName()
					+ ";" + ")");
			if (annotationEntry.getAnnotationType().endsWith(GenerateDto.class.getSimpleName() + ";")) {
				System.out.println("   Yes - it does");
				String dtoClassName = classDescription.getClassName();
				String dtoClassFileName = srcDirNameJava + "/" + dtoClassName.replace('.', '/') + ".java";
				String dtoMetaDataFileName = srcDirNameJava + "/" + dtoClassName.replace('.', '/') + "MetaData.java";

				System.out.println("   Processing DTOClassName(" + dtoClassFileName + ")");
				System.out.println("   Processing dtoClassFileName(" + dtoMetaDataFileName + ")");
				System.out.println("   Processing dtoMetaDataFileName(" + dtoMetaDataFileName + ")");

				File dtoSourceFile = new File(dtoClassFileName);
				File path = dtoSourceFile.getParentFile();
				path.mkdirs();
				dtoSourceFile.delete();

				PrintStream dtoSourceStream = new PrintStream(new FileOutputStream(dtoSourceFile));
				PrintStream dtoMetaDataStream = new PrintStream(new FileOutputStream(new File(dtoMetaDataFileName)));

				new BaseDtoMapperGenerator().generateClassesWithoutMapper(dtoClassName, dtoSourceStream,
						dtoMetaDataStream);

			} else if (annotationEntry.getAnnotationType().endsWith(GeneratedDtoMapper.class.getSimpleName() + ";")) {
				System.out.println("   No - it does not");
				System.out.println("   The class has the annotation(" + GeneratedDtoMapper.class.getSimpleName()
						+ ";) which is the second DTO version");

				System.out.println("   Class (" + classFileName + ") is being generated now");
				Class<?> dtoClass = null;
				Class<?> entityClass = null;

				// Load the class dynamically. The forName() method causes the
				// class to be initialized and returns
				// the class object associated with the class.
				Class<?> currentMapperClass = null;
				try {
					System.out.println(
							"   Getting currentMapperClass for class(" + classDescription.getClassName() + ")");
					System.out.println("   The class path is: " + System.getProperty("java.class.path"));
					currentMapperClass = Class.forName(classDescription.getClassName(), true, classLoader);
				} catch (ClassNotFoundException e) {
					System.out.println("ClassNotFoundException encountered when trying to load class("
							+ classDescription.getClassName() + ")");
				} catch (Exception e) {
					System.out.println(
							"Exception encountered when trying to load class(" + classDescription.getClassName() + ")");
				}

				while (!(currentMapperClass.getGenericSuperclass() instanceof ParameterizedType)) {
					System.out.println("   Getting super class");
					currentMapperClass = currentMapperClass.getSuperclass();
				}

				System.out.println("   Getting entity class");
				entityClass = (Class<?>) ((ParameterizedType) currentMapperClass.getGenericSuperclass())
						.getActualTypeArguments()[0];
				System.out.println("   Getting DTO class");
				dtoClass = (Class<?>) ((ParameterizedType) currentMapperClass.getGenericSuperclass())
						.getActualTypeArguments()[1];

				if (dtoClass == null || entityClass == null) {
					System.err.println("'" + classFileName
							+ "' does not contain a compatible createEmptyDto() and createEmptyEntity() method.\n"
							+ "Check the naming convention : mapper class name = <Dto class name>Mapper");
				} else {

					System.out.println("   DTO = " + dtoClass.getName());
					System.out.println("   ENTITY = " + entityClass.getName());

					generateDtoSource(entityClass.getName(), dtoClass.getName(), classDescription.getClassName());
				}
			} else {
				System.out.println("The class does not end with a DTO convention so it will be ignored");
			}
		}
	}

	static private void determineAllDtoEntityTuples(String dtoClassName) throws IOException {
		DynamicPage dp = new DynamicPage();
		String mavenRoot = "C:\\Program Files\\apache-maven\\repository";
		File[] files = new File(mavenRoot).listFiles();
		String rcpsuffixString = "eventservice";
		String wmsVersionString = "DiQ2.3.0-HF2";
		String appCoreVersionString = "5.1.8";
		ArrayList<URL> getAllJars = new ArrayList<>();
		getAllJars = dp.getAllJarsToGenerateClasses2(files, rcpsuffixString, appCoreVersionString, wmsVersionString,
				mavenRoot);
		getAllJars.add(FileSystems.getDefault().getPath(targetDirPath, "classes").toUri().toURL());
		classLoader = new URLClassLoader(getAllJars.toArray(new URL[0]));
		// URLClassLoader classLoader = (URLClassLoader)
		// SingleClassDtoMapperGenerator.class.getClassLoader();
		URL[] classPath = classLoader.getURLs();

		for (URL path : classPath) {

			String path2 = path.getPath();

			System.out.println("Evaluating path(" + path2 + ") for classes to load");

			if (new File(path2).isFile()) {
				analyseJarFile(path2);
			} else if (new File(path2).isDirectory()) {
				analyseClassDirectory(path2, dtoClassName);
			}
		}
	}

	/**
	 * @param path
	 * @throws IOException
	 */
	private static void analyseJarFile(String path) throws IOException {

		try (JarFile jarFile = new JarFile(path)) {

			Enumeration<JarEntry> entries = jarFile.entries();

			while (entries.hasMoreElements()) {

				JarEntry entry = entries.nextElement();

				if (!entry.getName().endsWith(".class"))
					continue;

				ClassParser parser = new ClassParser(path, entry.getName());

				JavaClass classDescription = parser.parse();

				analyseClass(classDescription);
			}
		} catch (IOException e) {
			System.out.println("could not analyze JAR file " + path);
			throw e;
		}
	}

	private static void analyseClassDirectory(String path, String dtoClassName) throws IOException {

		File[] entries = new File(path).listFiles();

		for (File entry : entries) {

			String name = entry.getAbsolutePath();

			if (entry.isFile() && name.contains(dtoClassName)) {

				if (!name.endsWith(".class"))
					continue;

				ClassParser parser = new ClassParser(name);

				JavaClass classDescription = parser.parse();

				analyseClass(classDescription);

			} else if (entry.isDirectory() && !entry.isHidden()) {
				analyseClassDirectory(name, dtoClassName);
			}
		}
	}

	/**
	 * @param entry
	 * @param classDescription
	 */
	private static void analyseClass(JavaClass classDescription) {

		String className = classDescription.getClassName();

		if (className.endsWith("Factory")) {

			String packageName = getPackageName(className);

			List<JavaClass> classes = existingFactories.get(packageName);

			if (classes == null) {
				classes = new ArrayList<JavaClass>();
				existingFactories.put(packageName, classes);
			}

			classes.add(classDescription);
		}

		if (className.endsWith("Provider")) {
			existingProviders.add(className);
		}

		if (classDescription.isAbstract())
			return;

		for (AnnotationEntry annotatioEntry : classDescription.getAnnotationEntries()) {

			if (annotatioEntry.getAnnotationType().endsWith(GeneratedDtoMapper.class.getSimpleName() + ";")
					|| annotatioEntry.getAnnotationType().endsWith(ManualDtoMapper.class.getSimpleName() + ";")) {

				System.out.println(className);

				for (Method method : classDescription.getMethods()) {

					if (method.getName().equals("fillDtoFromEntity")) {

						String dtoClassName = method.getArgumentTypes()[0].toString();
						String entityClassName = method.getArgumentTypes()[1].toString();

						Mapper mapper = new Mapper();

						mapper.dtoClassName = dtoClassName;
						mapper.mapperClassName = className;

						existingMappings.put(entityClassName, mapper);
					}
				}

				// add mapper configuration for the substituted entity as well,
				// which could be used in
				// project specific entities instead of the base entity
				for (AnnotationEntry annotationEntry : classDescription.getAnnotationEntries()) {

					if (annotationEntry.getAnnotationType().endsWith(SubstituteDto.class.getSimpleName() + ";")) {

						String dtoClassName = null;
						String entityClassName = null;

						for (ElementValuePair elementValuePair : annotationEntry.getElementValuePairs()) {

							if (elementValuePair.getNameString().equals("withDto")) {
								dtoClassName = convertTypeSignature2ClassName(
										elementValuePair.getValue().stringifyValue());
							} else if (elementValuePair.getNameString().equals("withEntity")) {
								entityClassName = convertTypeSignature2ClassName(
										elementValuePair.getValue().stringifyValue());
							}
						}

						Mapper mapper = new Mapper();

						// if some one uses the specific entity in project
						// specific entites the DTO mapper only returns the
						// base entity and base DTO. Therefore an additional
						// downcast is necessary for the setters of the
						// project specific DTO/entity
						mapper.needsDownCast = true;
						mapper.dtoClassName = dtoClassName;
						mapper.mapperClassName = className;
						existingMappings.put(entityClassName, mapper);
					}
				}
			}
		}
	}

	// convert something like "Lcom/dematic/SomeClass;" to
	// "com.dematic.SomeClass"
	static String convertTypeSignature2ClassName(String typeSignature) {

		String result = typeSignature.substring(1);
		result = result.substring(0, result.length() - 1);
		result = result.replace('/', '.');

		return result;
	}

	/**
	 * @param entityClassName
	 * @param dtoClassName
	 * @param mapperClassName
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	static void generateDtoSource(String entityClassName, String dtoClassName, String mapperClassName)
			throws Exception {

		try {

			Annotation[] annotations = new Annotation[] {};
			String dtoClassNameForFile = dtoClassName;

			try {

				Class<?> mapperClass = Class.forName(mapperClassName,true,classLoader);
				annotations = mapperClass.getAnnotations();
				SubstituteDto substituteDto = mapperClass.getAnnotation(SubstituteDto.class);

				if (substituteDto != null) {
					dtoClassNameForFile = substituteDto.withDto().getName();
				}
			} catch (ClassNotFoundException notYetCreated) {
				notYetCreated.printStackTrace();
			}

			String dtoClassFileName = srcDirNameGenerated + "/" + dtoClassNameForFile.replace('.', '/') + ".java";
			String dtoMetaDataFileName = srcDirNameGenerated + "/" + dtoClassNameForFile.replace('.', '/')
					+ "MetaData.java";
			String mapperFileName = srcDirNameGenerated + "/" + mapperClassName.replace('.', '/') + ".java";

			System.out.println("create files for " + entityClassName + ":");
			System.out.println(dtoClassFileName);
			System.out.println(dtoMetaDataFileName);
			System.out.println(mapperFileName);

			Mapper newMapper = new Mapper();
			newMapper.dtoClassName = dtoClassName;
			newMapper.mapperClassName = mapperClassName;

			existingMappings.put(entityClassName, newMapper);
			File dtoSourceFile = new File(dtoClassFileName);
			File dtoMapperSourceFile = new File(mapperFileName);
			File path = dtoSourceFile.getParentFile();
			path.mkdirs();
			path = dtoMapperSourceFile.getParentFile();
			path.mkdirs();
			dtoSourceFile.delete();

			PrintStream dtoSourceStream = new PrintStream(new FileOutputStream(dtoSourceFile));
			PrintStream dtoMetaDataStream = new PrintStream(new FileOutputStream(new File(dtoMetaDataFileName)));
			PrintStream dtoMapperSourceStream = new PrintStream(new FileOutputStream(dtoMapperSourceFile));
			Class<?> entityClass = Class.forName(entityClassName, true, classLoader);

			BaseDtoMapperGenerator generator;

			generator = createPlattformSpecificGenerator(entityClass);

			generator.generateClasses(entityClassName, dtoClassName, mapperClassName, dtoSourceStream,
					dtoMetaDataStream, dtoMapperSourceStream, entityClass, annotations);
		} catch (Exception e) {
			System.out.println("generation for class '" + entityClassName + "' failed. Please create a manual mapping");
			throw e;
		}
	}

	private static BaseDtoMapperGenerator createPlattformSpecificGenerator(Class<?> entityClass) {

		BaseDtoMapperGenerator generator;

		if (AbstractEntityWithoutVersion.class.isAssignableFrom(entityClass)
				|| entityClass.getAnnotation(Entity.class) != null) {
			generator = new EntityDtoMapperGenerator();
		} else {
			generator = new BaseDtoMapperGenerator();
		}

		return generator;
	}

	/**
	 * @param name
	 * @return
	 */
	public static String convertCamelCase2Capital(String name) {

		StringBuilder result = new StringBuilder();

		for (int i = 0; i < name.length(); i++) {

			char currentChar = name.charAt(i);

			if (Character.isUpperCase(currentChar)) {
				result.append("_");
			}

			result.append(Character.toUpperCase(currentChar));
		}

		return result.toString();
	}

	private static String getPackageName(String className) {
		return className.substring(0, className.lastIndexOf('.'));
	}
}
