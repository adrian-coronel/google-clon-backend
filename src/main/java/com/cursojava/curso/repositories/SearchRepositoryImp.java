package com.cursojava.curso.repositories;

import com.cursojava.curso.entities.WebPage;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Implementación de repositorio que gestiona operaciones de base de datos para páginas web.
 */
@Repository
public class SearchRepositoryImp implements SearchRepository {

    @PersistenceContext
    EntityManager entityManager;



    /**
     * Obtiene una página web por su URL.
     *
     * @param url La URL de la página web a buscar.
     * @return La página web correspondiente a la URL especificada, o null si no existe.
     */
    @Override
    public WebPage getByUrl(String url) {
        String query = "FROM WebPage WHERE url = :url AND enabled = true";
        List<WebPage> list = entityManager.createQuery(query)
                .setParameter("url", url)
                .getResultList();
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * Obtiene una lista de páginas web que deben ser indexadas (sin título ni descripción).
     *
     * @return Una lista de hasta 100 páginas web que deben ser indexadas.
     */
    @Override
    public List<WebPage> getLinksToIndex() {
        String query = "FROM WebPage WHERE title is null AND description is null AND enabled = true";
        return entityManager.createQuery(query)
                .setMaxResults(30)
                .getResultList();
    }

    @Transactional
    @Override
    public WebPage getOnlyLink() {
        String query = "FROM WebPage WHERE title is null AND description is null AND enabled = true";
        List<WebPage> listPage = entityManager.createQuery(query, WebPage.class).getResultList();

        if (listPage.isEmpty()) {
            return null; // O lanzar una excepción si prefieres
        }

        WebPage selectedPage = listPage.get(0);

        entityManager.createQuery("UPDATE WebPage SET enabled = :enabled WHERE title is null AND description is null AND enabled = true AND id <> :id")
                .setParameter("enabled", false)
                .setParameter("id", selectedPage.getId()) // Utiliza el ID del objeto WebPage
                .executeUpdate();

        return selectedPage;
    }


    /**
     * Realiza una búsqueda de páginas web por una cadena de texto en la descripción.
     *
     * @param textSearch El texto a buscar en las descripciones de las páginas web.
     * @return Una lista de páginas web que contienen el texto especificado en su descripción.
     */
    @Transactional
    @Override
    public List<WebPage> search(String textSearch) {
        String query = "FROM WebPage WHERE LOWER(description) like LOWER(:textSearch) and enabled = true ";
        return entityManager.createQuery(query)
                .setParameter("textSearch", "%" + textSearch + "%")
                .getResultList();
    }

    /**
     * Guarda o actualiza una página web en la base de datos.
     *
     * @param webPage La página web a guardar o actualizar.
     */
    @Transactional
    @Override
    public void save(WebPage webPage) {
        entityManager.merge(webPage);
    }

    /**
     * Verifica si una URL específica ya existe en la base de datos.
     *
     * @param url La URL que se desea verificar.
     * @return true si la URL existe en la base de datos, false si no.
     */
    @Override
    public boolean exist(String url) {
        String query = "FROM WebPage WHERE LOWER(url) = LOWER(:url) AND enabled <> false";
        List<WebPage> list = entityManager.createQuery(query)
                .setParameter("url", url)
                .getResultList();
        return list.size() != 0;
    }

    @Override
    public List<WebPage> getAll() {
        return entityManager.createQuery("FROM WebPage WHERE enabled = true", WebPage.class)
                .getResultList();
    }

    @Override
    @Transactional
    public WebPage getById(int Id) {
        List<WebPage> list = entityManager.createQuery("FROM WebPage WHERE id = :id AND enabled = true")
                .setParameter("id", Id)
                .getResultList();
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * Cambia el estado enabled de una página web basado en su URL.
     *
     * @param enabled true para habilitar la página, false para deshabilitarla.
     */
    @Override
    @Transactional
    public void changeEnabledByUrl(Long id, boolean enabled) {
        String query = "UPDATE WebPage SET enabled = :enabled WHERE id = :id";
        int updatedCount = entityManager.createQuery(query)
                .setParameter("enabled", enabled)
                .setParameter("id", id)
                .executeUpdate();

        // Opcional: Log o verificar el número de entidades afectadas
        if (updatedCount == 0) {
            // No se actualizó ninguna entidad, podrías manejar esto si es necesario
            System.out.println("No entity was updated");
        }
    }



}