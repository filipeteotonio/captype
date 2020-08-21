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

import org.reflections.Reflections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CaptypeRenderer {

	private static String getEntityCaptureName(Class<?> entity) {
		String value = entity.getAnnotation(CaptureEntity.class).name();
		return value.isEmpty() ? entity.getSimpleName() : value;
	}

	private static String getFieldCaptureDisplayName(Field field) {
		String value = field.getAnnotation(CaptureField.class).displayName();
		return value.isEmpty() ? field.getName() : value;
	}

	private static String getFieldCaptureName(Field field) {
		String value = field.getAnnotation(CaptureField.class).name();
		return value.isEmpty() ? field.getName() : value;
	}

	private static int getFieldCaptureDisplayOrder(Field field) {
		int value = field.getAnnotation(CaptureField.class).displayOrder();
		return value;
	}

	public static void render(String scanPath, String filePath) throws JsonProcessingException {
		Map<String, Map<String, CaptureType>> result = new HashMap<>();
		Set<Class<?>> entities = new Reflections(scanPath).getTypesAnnotatedWith(CaptureEntity.class);

		for (Class<?> entity : entities) {
			String entityName = getEntityCaptureName(entity);
			Field[] fields = entity.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(CaptureField.class)) {
					String fieldName = getFieldCaptureName(field);
					List<Annotation> fieldAnnotations = Arrays.asList(field.getDeclaredAnnotations());
					if (!result.containsKey(entityName)) {
						result.put(entityName, new HashMap<String, CaptureType>());
					}
					Map<String, CaptureType> entityResult = result.get(entityName);
					if (!entityResult.containsKey(field.getName())) {
						entityResult.put(fieldName, new CaptureType());
					}
					CaptureType fieldCapture = entityResult.get(fieldName);
					Map<String, Object> fieldResult = new HashMap<>();

					// required validation
					boolean isRequired = false;
					for (Annotation annotation : fieldAnnotations) {

						if (annotation.annotationType().equals(NotNull.class)) {
							isRequired = true;
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

					// adds the required validation if any
					fieldResult.put("required", isRequired);
					// fills the field metadata
					fieldCapture.setDisplayName(getFieldCaptureDisplayName(field));
					fieldCapture.setDisplayOrder(getFieldCaptureDisplayOrder(field));
					fieldCapture.setValidators(fieldResult);
					// stores
					entityResult.put(fieldName, fieldCapture);
					result.put(entityName, entityResult);
				}
			}
		}

		try {
			writeToFile(filePath, result);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	private static void writeToFile(String filePath, Map<String, Map<String, CaptureType>> result) throws IOException {

		if (filePath == null || filePath.isEmpty()) {
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
