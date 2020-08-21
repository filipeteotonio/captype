package br.ironspark.captype;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptureType {

	private String displayName;

	private int displayOrder;

	private Map<String, Object> validators;
}
