package com.cursojava.curso.services;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cursojava.curso.entities.WebPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.hibernate.internal.util.StringHelper.isBlank;

/**
 * Servicio encargado de indexar páginas web, extrayendo contenido, metadatos y gestionando enlaces.
 */
@Service
public class SpiderService {

    @Autowired
    private SearchService searchService;

    /**
     * Indexa todas las páginas web obtenidas desde el servicio de búsqueda.
     * Cada página se procesa de manera paralela.
     */
    public void indexWebPages() {
        List<WebPage> linksToIndex = searchService.getLinksToIndex();
        linksToIndex.stream().parallel().forEach(webPage -> {
            try {
                System.out.println("Indexando");
                indexWebPage(webPage);
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    /**
     * Indexa una página web específica, extrayendo su contenido y gestionando los enlaces.
     *
     * @param webPage La página web que se va a indexar.
     * @throws Exception Si ocurre un error durante la indexación.
     */
    private void indexWebPage(WebPage webPage) throws Exception {
        String url = webPage.getUrl();
        System.out.println(url);
        String content = getWebContent(url);
        if (isBlank(content)) {
            return;
        }

        indexAndSaveWebPage(webPage, content);

        System.out.println("Dominio: " + getDomain(url));
        saveLinks(getDomain(url), content);
    }

    /**
     * Obtiene el dominio de una URL dada.
     *
     * @param url La URL de la cual se extraerá el dominio.
     * @return El dominio de la URL.
     */
    private String getDomain(String url) {
        String[] aux = url.split("/");
        return aux[0] + "//" + aux[2];
    }

    /**
     * Guarda los enlaces válidos encontrados en el contenido de una página web.
     *
     * @param domain  El dominio de la página web.
     * @param content El contenido de la página web.
     */
    private void saveLinks(String domain, String content) {
        System.out.println("Guardando enlaces");
        List<String> links = getLinks(domain, content);
        System.out.println("Enlaces: " + links);
        links.stream().filter(link -> !searchService.exist(link.split("#")[0]))
                .map(link -> new WebPage(link))
                .forEach(webPage -> searchService.save(webPage));
    }

    /**
     * Obtiene todos los enlaces válidos presentes en el contenido de una página web.
     *
     * @param domain  El dominio de la página web.
     * @param content El contenido de la página web.
     * @return Una lista de enlaces válidos y limpios.
     */
    public List<String> getLinks(String domain, String content) {
        List<String> links = new ArrayList<>();

        String[] splitHref = content.split("href=\"");
        List<String> listHref = Arrays.asList(splitHref);

        listHref.forEach(strHref -> {
            String[] aux = strHref.split("\"");
            links.add(aux[0]);
        });
        return cleanLinks(domain, links);
    }

    /**
     * Filtra y limpia los enlaces basados en el dominio y extensiones excluidas.
     *
     * @param domain El dominio de la página web.
     * @param links  La lista de enlaces a limpiar.
     * @return Una lista de enlaces limpios y únicos.
     */
    private List<String> cleanLinks(String domain, List<String> links) {
        String[] excludedExtensions = new String[]{"css","js","json","jpg","png","woff2"};

        List<String> resultLinks = links.stream()
                .filter(link -> Arrays.stream(excludedExtensions).noneMatch(link::endsWith))
                .map(link -> link.startsWith("/") ? domain + link : link)
                .filter(link -> link.startsWith("http"))
                .collect(Collectors.toList());

        List<String> uniqueLinks = new ArrayList<>();
        uniqueLinks.addAll(new HashSet<>(resultLinks));

        return uniqueLinks;
    }

    /**
     * Indexa y guarda una página web junto con su contenido y metadatos.
     *
     * @param webPage La página web a indexar y guardar.
     * @param content El contenido de la página web.
     */
    private void indexAndSaveWebPage(WebPage webPage, String content) {
        String title = getTitle(content);
        String description = getDescription(content);

        webPage.setDescription(description);
        webPage.setTitle(title);

        System.out.println("Guardando: " + webPage);
        searchService.save(webPage);
    }

    /**
     * Obtiene el título de una página web a partir de su contenido HTML.
     *
     * @param content El contenido HTML de la página web.
     * @return El título de la página web.
     */
    public String getTitle(String content) {
        String[] aux = content.split("<title>");
        String[] aux2 = aux[1].split("</title>");
        return aux2[0];
    }

    /**
     * Obtiene la descripción de una página web a partir de su contenido HTML.
     *
     * @param content El contenido HTML de la página web.
     * @return La descripción de la página web.
     */
    public String getDescription(String content) {
        try {
            String[] aux = content.split("<meta name=\"description\" content=\""); // Corta y parte en 2 por <meta name="description" content="
            String[] aux2 = aux[1].split("\"\\s?/?\\s?>"); // Cortamos por el primer "/> o " />
            return aux2[0];
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return "";
    }

    /**
     * Obtiene el contenido web de una URL específica.
     *
     * @param link La URL de la cual obtener el contenido web.
     * @return El contenido de la página web como una cadena de caracteres.
     */
    private String getWebContent(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String encoding = conn.getContentEncoding();

            InputStream input = conn.getInputStream();

            Stream<String> lines = new BufferedReader(new InputStreamReader(input))
                    .lines();

            return lines.collect(Collectors.joining());
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }
        return "";
    }

    public List<String> indexWebPages2() {
        try {
            // SCRAPEAR LINKS
            WebPage webPage = searchService.getOnlyLink();
            return indexWebPage2(webPage);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }


    private List<String> indexWebPage2(WebPage webPage) {
        String url = webPage.getUrl();
        System.out.println(url);
        // Obtenemos el contenido DOCTYPE....
        String content = getWebContent(url);
        if (isBlank(content)) {
            return null;
        }

        //
        indexAndSaveWebPage(webPage, content);

        System.out.println("Dominio: " + getDomain(url));
        return saveLinks2(getDomain(url), content);
    }

    private List<String> saveLinks2(String domain, String content) {
        System.out.println("Guardando enlaces");
        List<String> links = getLinks(domain, content);
        System.out.println("Enlaces: " + links);
        links.stream().limit(50).filter(link -> !searchService.exist(link.split("#")[0]))
                .map(link -> new WebPage(link))
                .forEach(webPage -> {
                    try {
                        String contenido = getWebContent(webPage.getUrl());

                        if (isBlank(contenido)) return;

                        indexAndSaveWebPage(webPage, contenido);
                    }
                    catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                });
        return links.stream().limit(50).collect(Collectors.toList());
    }
}
