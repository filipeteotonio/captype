package br.ironspark.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.ironspark.captype.CaptureEntity;
import br.ironspark.captype.CaptureField;
import br.ironspark.captype.CaptureIgnore;
import lombok.Data;

@Data
@Entity
@CaptureEntity(name = "Project")
public class Project {

	@CaptureIgnore
	@Id
	private Long id;

	@JsonIgnore
	@NotNull
	@Size(max = 60)
	private String name;

	@CaptureField(displayName = "short")
	@NotNull
	@Size(max = 60)
	private String shortDescription;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String longDescription;

	private String base;

}
