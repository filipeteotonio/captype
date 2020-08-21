package br.ironspark.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.ironspark.captype.CaptureEntity;
import br.ironspark.captype.CaptureField;
import lombok.Data;

@CaptureEntity(name = "Backlog")
@Entity
@Data
public class Project {

	@CaptureField(name = "title", displayName = "Title", displayOrder = 10)
	@NotNull
	@Size(min = 5)
	private String name;

	@CaptureField(name = "shortDescription", displayName = "Short description")
	@NotNull
	@Size(max = 100)
	private String email;

}
