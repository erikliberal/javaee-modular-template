package io.github.erikliberal;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import io.github.erikliberal.RepositoryManager;

@Stateless
public class RepositoryManagerImpl implements RepositoryManager {

	public void executaParaString(String teste){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, teste, teste));
	}
	
}
