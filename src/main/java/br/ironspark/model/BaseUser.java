package br.ironspark.model;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.ironspark.captype.CaptureEntity;
import br.ironspark.captype.CaptureField;
import lombok.Data;

@CaptureEntity(name = "User")
@Entity
@Data
public class BaseUser {

	@CaptureField(displayName = "Name", displayOrder = 100)
	@NotNull
	@Size(max = 60, min = 5)
	private String name;

	@CaptureField
	@NotNull
	@Size(max = 100, min = 5)
	private String email;

	@CaptureField
	@Max(value = 105)
	private Long strikes;

}
