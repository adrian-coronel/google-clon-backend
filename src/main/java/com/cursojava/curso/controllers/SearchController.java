package com.cursojava.curso.controllers;

import com.cursojava.curso.entities.WebPage;
import com.cursojava.curso.services.SearchService;
import com.cursojava.curso.services.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST que maneja las solicitudes relacionadas con la búsqueda y el rastreo de páginas web.
 */
@RestController
public class SearchController {

    @Autowired
    private SearchService service;

    @Autowired
    private SpiderService spiderService;
    @Autowired
    private SearchService searchService;

    /**
     * Maneja las solicitudes GET para realizar búsquedas.
     *
     * @param params Parámetros de la solicitud, incluyendo el término de búsqueda.
     * @return Una lista de páginas web que coinciden con el término de búsqueda.
     */
    @RequestMapping(value = "api/search", method = RequestMethod.GET)
    public List<WebPage> search(@RequestParam Map<String, String> params) {
        String query = params.get("query");
        return service.search(query);
    }

    /**
     * Maneja las solicitudes GET para iniciar el proceso de indexación de páginas web.
     * Llama al método indexWebPages() de SpiderService.
     */
    @RequestMapping(value = "api/scrapperUrls", method = RequestMethod.GET)
    public List<String> scrapperUrl() {
        //spiderService.indexWebPages();
        return spiderService.indexWebPages2();
    }

    @RequestMapping(value = "api/getUrls", method = RequestMethod.GET)
    public List<WebPage> getUrls() {
        return searchService.getOnlyUrls();
    }
    @RequestMapping(value = "api/inhabilited/{id}", method = RequestMethod.GET)
    public void disabled(@PathVariable Long id) {
        searchService.disabled(id);
    }
    @RequestMapping(value = "api/disabled/{id}", method = RequestMethod.DELETE)
    public void disabledPage(@PathVariable Long id) {
        searchService.disabled(id);
    }


    @RequestMapping(value = "api/all", method = RequestMethod.GET)
    public List<WebPage> getAll() {
        return searchService.getAll();
    }

    @RequestMapping(value = "api/save", method = RequestMethod.POST)
    public WebPage save(@RequestBody WebPage webPage) {
        searchService.save(webPage);
        return searchService.getByUrl(webPage.getUrl());
    }

}

