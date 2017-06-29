package io.github.erikliberal;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import io.github.erikliberal.RepositoryManager;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class RequestScopedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private RepositoryManager repositoryManager;
	@Getter @Setter
	private String texto;
	
	public void execute(){
		repositoryManager.executaParaString(getTexto());
	}

}
