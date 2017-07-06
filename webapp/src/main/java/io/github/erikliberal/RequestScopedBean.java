package io.github.erikliberal;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class RequestScopedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private RepositoryManager repositoryManager;
	@EJB(lookup="java:module/TestEntityRepository")
	private Repository<TestEntity> testEntityRepository;
	
	@Getter @Setter
	private String texto;

	@Getter
	private List<SelectItem> parents;
	@Getter
	private Collection<? extends TestEntity> values;
	@Getter @Setter
	private Long id;
	@Getter @Setter
	private String code;
	@Getter @Setter
	private String name;
	@Getter @Setter
	private Long parentId;
	
	@PostConstruct
	public void init(){
		this.values = testEntityRepository.retrieve();
		this.parents = testEntityRepository.retrieve().stream()
				.map(entity->new SelectItem(entity.getId(), entity.getName()))
				.collect(Collectors.toList());
		id = null;
		name = null;
		code = null;
		parentId = null;
		
		if (parents.isEmpty())
			cargaInicial();
	}
	
	private void cargaInicial() {
		for (int i = 0; i < 20; i++)
			entidade("CD"+i, "Código "+i);
	}

	private void entidade(String code, String name) {
		entidade(code, name, null);
	}
	private void entidade(String code, String name, Long parentId) {
		this.code=code;
		this.name=name;
		this.parentId=parentId;
		save();
	}

	void edit(TestEntity entity){
		id = entity.getId();
		name = entity.getName();
		code = entity.getCode();
		parentId = Optional.ofNullable(entity.getParent()).map(p->p.getId()).orElse(null);
	}
	
	public void edit(Long id){
		Optional.ofNullable(id).map(testEntityRepository::retrieve).ifPresent(this::edit);
	}

	public void save(){
		TestEntity ent = Optional.ofNullable(id).map(testEntityRepository::retrieve)
				.orElseGet(TestEntity::new);
		
		ent.setCode(code);
		ent.setName(name);
		if (!Objects.equals(id, parentId))
			ent.setParent(Optional.ofNullable(parentId).map(testEntityRepository::retrieve).orElse(null));
		
		testEntityRepository.add(ent);
		
		init();
	}
	
	public void execute(){
		repositoryManager.executaParaString(getTexto());
	}

}
