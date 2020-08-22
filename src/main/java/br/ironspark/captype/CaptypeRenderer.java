package br.ironspark.captype;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Scans the entities annotated with {@link CaptureEntity} and creates metadata
 * json file
 * 
 * @author filipemendonca
 *
 */
public class CaptypeRenderer {

	private static String getEntityCaptureName(Class<?> entity) {
		String value = entity.getAnnotation(CaptureEntity.class).name();
		return value.isEmpty() ? entity.getSimpleName() : value;
	}

	private static String getFieldCaptureDisplayName(Field field, DisplayNameEstrategy estrategy) {
		String value = "";

		if (field.isAnnotationPresent(CaptureField.class)) {
			value = field.getAnnotation(CaptureField.class).displayName();
		}

		if (value.isEmpty()) {
			switch (estrategy) {
			case PHRASAL: {
				String fieldName = field.getName();
				value = StringUtils.capitalize(StringUtils
						.join(StringUtils.splitByCharacterTypeCamelCase(fieldName), StringUtils.SPACE).toLowerCase());
				break;
			}

			default: {
				value = field.getName();
				break;
			}

			}
		}

		return value;
	}

	private static String getFieldCaptureName(Field field) {
		String value = "";
		if (field.isAnnotationPresent(CaptureField.class)) {
			value = field.getAnnotation(CaptureField.class).name();
		}

		if (value.isEmpty()) {
			value = field.getName();
		}

		return value;
	}

	private static int getFieldCaptureDisplayOrder(Field field) {
		int value;
		if (field.isAnnotationPresent(CaptureField.class)) {
			value = field.getAnnotation(CaptureField.class).displayOrder();
		} else {
			value = 0;
		}
		return value;
	}

	/**
	 * Scans the entities in the scanPath and creates a JSON file at filePath
	 * 
	 * @param scanPath
	 * @param filePath
	 * @throws Exception
	 */
	public static void render(String scanPath, String filePath) throws Exception {
		Map<String, Map<String, CaptureType>> result = new HashMap<>();
		Set<Class<?>> entities = new Reflections(scanPath).getTypesAnnotatedWith(CaptureEntity.class);

		for (Class<?> entity : entities) {
			CaptureEntity captureEntityAnnotation = ((CaptureEntity) entity.getAnnotation(CaptureEntity.class));
			String entityName = getEntityCaptureName(entity);
			Map<String, CaptureType> entityResult = new HashMap<>();

			Field[] fields = entity.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(JsonIgnore.class) || field.isAnnotationPresent(CaptureIgnore.class)) {
					continue;
				}

				String fieldName = getFieldCaptureName(field);
				String fieldDisplayName = getFieldCaptureDisplayName(field,
						captureEntityAnnotation.displayNameEstrategy());
				List<Annotation> fieldAnnotations = Arrays.asList(field.getDeclaredAnnotations());

				if (!entityResult.containsKey(fieldName)) {
					entityResult.put(fieldName, new CaptureType());
				} else {
					throw new Exception("Multiple fields declared with same name");
				}

				CaptureType fieldCapture = entityResult.get(fieldName);
				Map<String, Object> fieldResult = new HashMap<>();
				for (Annotation annotation : fieldAnnotations) {

					if (annotation.annotationType().equals(NotNull.class)) {
						fieldResult.put("required", true);
					}

					else if (annotation.annotationType().equals(Size.class)) {
						Map<String, Object> sizeMap = new HashMap<>();
						sizeMap.put("min", ((Size) annotation).min());
						sizeMap.put("max", ((Size) annotation).max());
						fieldResult.put("size", sizeMap);
					}

					else if (annotation.annotationType().equals(Max.class)) {
						fieldResult.put("max", ((Max) annotation).value());
					}

					else if (annotation.annotationType().equals(Min.class)) {
						fieldResult.put("min", ((Min) annotation).value());
					}

					else if (annotation.annotationType().equals(AssertTrue.class)) {
						fieldResult.put("assertion", true);
					}

					else if (annotation.annotationType().equals(AssertFalse.class)) {
						fieldResult.put("assertion", false);
					}

					else if (annotation.annotationType().equals(Pattern.class)) {
						fieldResult.put("regex", ((Pattern) annotation).regexp());
					}

				}

				// fills the field metadata
				fieldCapture.setDisplayName(fieldDisplayName);
				fieldCapture.setDisplayOrder(getFieldCaptureDisplayOrder(field));
				fieldCapture.setValidators(fieldResult);
				// stores
				entityResult.put(fieldName, fieldCapture);
			}

			result.put(entityName, entityResult);
		}

		try {
			writeToFile(filePath, result);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Writes the entites metadata to a JSON file. If no filePath is provided,
	 * creates the file in the default directory and gives it a default name. Also
	 * adds the json extension in case the filePath does not contain it
	 * 
	 * @param filePath
	 * @param result
	 * @throws IOException
	 */
	private static void writeToFile(String filePath, Map<String, Map<String, CaptureType>> result) throws IOException {

		if (filePath == null || filePath.isEmpty()) {
//			DateTimeFormatter format = DateTimeFormatter.ofPattern ("dd-MM-yyyy");
//			filePath = "captype-" + OffsetDateTime.now().format(format) + ".json";
			filePath = "captype.json";
		} else if (!filePath.endsWith(".json")) {
			filePath.concat(".json");
		}

		FileWriter fw = new FileWriter(filePath);
		ObjectMapper mapper = new ObjectMapper();
		String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		fw.write(jsonResult);
		fw.flush();
		fw.close();
	}
}
