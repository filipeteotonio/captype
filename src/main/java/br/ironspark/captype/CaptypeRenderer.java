package br.ironspark.captype;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;
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

	public static void render(String path) throws JsonProcessingException {
		Map<String, Map<String, CaptureType>> result = new HashMap<>();
		Set<Class<?>> entities = new Reflections(path).getTypesAnnotatedWith(CaptureEntity.class);

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
					boolean isRequired = fieldAnnotations.stream()
							.anyMatch(annotation -> annotation.annotationType().equals(NotNull.class));
					fieldResult.put("required", isRequired);

					// size validation
					Annotation annotation = fieldAnnotations.stream().filter(a -> a.annotationType().equals(Size.class))
							.findFirst().orElse(null);
					if (annotation != null) {
						Map<String, Object> sizeMap = new HashMap<>();
						sizeMap.put("min", ((Size) annotation).min());
						sizeMap.put("max", ((Size) annotation).max());
						fieldResult.put("size", sizeMap);
					}

					fieldCapture.setDisplayName(getFieldCaptureDisplayName(field));
					fieldCapture.setDisplayOrder(getFieldCaptureDisplayOrder(field));
					fieldCapture.setValidators(fieldResult);
					entityResult.put(fieldName, fieldCapture);
					result.put(entityName, entityResult);
				}
			}
		}

		ObjectMapper mapper = new ObjectMapper();
		String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);

		System.out.println(jsonResult);

	}
}
