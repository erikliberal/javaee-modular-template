package io.github.erikliberal;

import static java.text.MessageFormat.format;
import static java.util.Optional.ofNullable;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EntityListeners(TestEntityRepository.class)
@Entity @Getter @Setter @NoArgsConstructor @EqualsAndHashCode(of="id")
public class TestEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	private Long id;
	@Column(unique=true, nullable=false)
	private String code;
	@Column(unique=false, nullable=false)
	private String name;
	@Column(unique=true, nullable=false)
	private String path;
	@ManyToOne(optional=true)
	@JoinColumn
	private TestEntity parent;
	@OneToMany(mappedBy="id", fetch=FetchType.LAZY, orphanRemoval=true)
	private List<TestEntity> children;

	public String getResolvedPath(){
		return ofNullable(getParent()).map(prt -> format("{0}{1}/", prt.getPath(), prt.getId())).orElse("/");
	}
	
}
