package com.cursojava.curso.repositories;

import com.cursojava.curso.entities.WebPage;

import java.util.List;

/**
 * Repositorio que define las operaciones de acceso a datos para las páginas web.
 */
public interface SearchRepository {

    /**
     * Obtiene una página web por su URL.
     *
     * @param url La URL de la página web a buscar.
     * @return La página web correspondiente a la URL especificada, o null si no existe.
     */
    WebPage getByUrl(String url);

    /**
     * Obtiene una lista de páginas web que deben ser indexadas (sin título ni descripción).
     *
     * @return Una lista de páginas web que deben ser indexadas.
     */
    List<WebPage> getLinksToIndex();

    WebPage getOnlyLink();

    /**
     * Realiza una búsqueda de páginas web por una cadena de texto en la descripción.
     *
     * @param textSearch El texto a buscar en las descripciones de las páginas web.
     * @return Una lista de páginas web que contienen el texto especificado en su descripción.
     */
    List<WebPage> search(String textSearch);

    /**
     * Guarda o actualiza una página web en la base de datos.
     *
     * @param webPage La página web a guardar o actualizar.
     */
    void save(WebPage webPage);

    /**
     * Verifica si una URL específica ya existe en la base de datos.
     *
     * @param link La URL que se desea verificar.
     * @return true si la URL existe en la base de datos, false si no.
     */
    boolean exist(String link);

    List<WebPage> getAll();

    WebPage getById(int Id);

    void changeEnabledByUrl(Long id, boolean b);
}
