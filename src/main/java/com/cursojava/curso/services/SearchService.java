package com.cursojava.curso.services;

import com.cursojava.curso.entities.WebPage;
import com.cursojava.curso.repositories.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Servicio que maneja la lógica de negocio para la búsqueda y gestión de páginas web.
 */
@Service
public class SearchService {

    @Autowired
    private SearchRepository repository;

    public List<WebPage> getAll() {
        return repository.getAll();
    }

    public List<WebPage> getOnlyUrls() {
        return repository.getLinksToIndex();
    }

    public WebPage getByUrl(String url) {
        return repository.getByUrl(url);
    }
    /**
     * Realiza una búsqueda de páginas web que contienen un texto específico en su descripción.
     *
     * @param textSearch El texto a buscar en las descripciones de las páginas web.
     * @return Una lista de páginas web que contienen el texto especificado en su descripción.
     */
    public List<WebPage> search(String textSearch) {
        return repository.search(textSearch);
    }

    /**
     * Guarda o actualiza una página web en la base de datos.
     *
     * @param webPage La página web a guardar o actualizar.
     */
    public void save(WebPage webPage) {
        webPage.setEnabled(true);
        repository.save(webPage);
    }

    /**
     * Verifica si una URL específica ya existe en la base de datos.
     *
     * @param link La URL que se desea verificar.
     * @return true si la URL existe en la base de datos, false si no.
     */
    public boolean exist(String link) {
        return repository.exist(link);
    }

    /**
     * Obtiene una lista de páginas web que deben ser indexadas (sin título ni descripción).
     *
     * @return Una lista de páginas web que deben ser indexadas.
     */
    public List<WebPage> getLinksToIndex() {
        return repository.getLinksToIndex();
    }

    @Transactional
    public void disabled(Long id) {
        repository.changeEnabledByUrl(id, false);
    }

    @Transactional
    public WebPage getOnlyLink() {
        return repository.getOnlyLink();
    }
}
