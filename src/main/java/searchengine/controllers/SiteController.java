package searchengine.controllers;

import lombok.Getter;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.site.DTOPage;
import searchengine.model.Page;
import searchengine.model.PageRepository;
import searchengine.model.Site;
import searchengine.model.SiteRepository;
import searchengine.config.SitesList;
import searchengine.services.SiteIndexService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@RestController
public class SiteController {

    private final SitesList sites;
    private boolean indexing = false;


    public  SiteController(SiteRepository siteRepository, PageRepository pageRepository, SitesList sites) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.sites = sites;
    }

    @Autowired
    @Getter
    private  final SiteRepository siteRepository;
    @Autowired
    @Getter
    private final PageRepository pageRepository;
    private SiteIndexService siteIndexService = new SiteIndexService();

    @GetMapping("/api/startIndexing")
    public HashMap<String, Object> startIndexing() {

        HashMap<String, Object> response = new HashMap<>();
        if (indexing){
            response.put("result",false);
            response.put("error", "Индексация уже запущена");
            return response;
        }

        HashMap<Site, List<Page>> map = new HashMap<>();

        for (int i = 0; i < sites.getSites().size(); i++) {
            try {
                map.putAll(siteIndexService.siteIndex(sites.getSites().get(i).getUrl()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            map.keySet().forEach(siteRepository::save);
            map.values().forEach(pages -> pages.forEach(page -> {
                System.out.println(page.getPath());
                pageRepository.save(page);
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.put("result", true);
        indexing = true;
        return  response;
    }
    @GetMapping("/api/stopIndexing")
    public HashMap<String, Object> stopIndexing() {
        HashMap<String, Object> response = new HashMap<>();
        if (!indexing){
            response.put("result",false);
            response.put("error", "Индексация не запущена");
            return response;
        }
        for (int i = 0; i < sites.getSites().size(); i++) {

            if (SiteIndexService.getSites().get(i).getStatus().equals(Site.Status.INDEXING)) {
                siteRepository.delete(SiteIndexService.getSites().get(i));
            }
        }
        response.put("result", true);
        indexing = false;
        return  response;
    }
    @GetMapping("/api/search")
    public HashMap<String, Object> search(@RequestParam String query) {
        String query1 = query;
        int offset = 0;
        int limit = 20;
        if (query.contains(", ")){
          String[]  s = query.split(", ");
            query1 = s[0];
            limit = Integer.parseInt(s[2]);
            offset = Integer.parseInt(s[1]);


        }
        HashMap<String, Object> response = new HashMap<>();
        int count = 0;
        AtomicReference<String> massage = new AtomicReference<>("");
        AtomicBoolean error = new AtomicBoolean(false);
        List<DTOPage> data = new ArrayList<>();
        if (query != null && !query.isBlank() && !query.isEmpty()){
        try {
            String finalQuery = query1;
            pageRepository.findAll().forEach(page -> {
                System.out.println(" ");
                if (page.getContent().contains(finalQuery)) {

                    DTOPage dtoPage = new DTOPage();
                    dtoPage.setSite("");
                    for (Site site : siteRepository.findAll()){
                        if (site.getId() == page.getSiteId())
                        {dtoPage.setSiteName(site.getName());}
                    }
                    dtoPage.setSiteName("skillbox");
                    dtoPage.setTittle(Jsoup.parse(page.getContent()).title());
                    dtoPage.setSnippet(getContext(finalQuery, page.getContent()));
                    dtoPage.setUri(page.getPath());
                    dtoPage.setRelevance(0);
                    System.out.println(dtoPage);
                    data.add(dtoPage);

                }


            });

            if (!data.isEmpty()) {
                for (int i = offset;offset == -1 || i==0 ;i--){
                    data.remove(i);
                }
                if (limit <=data.size()){
                    int i = limit;
                    while (i>data.size()-1) {
                        data.remove(i);
                        i++;
                    }
                }

                response.put("result", true);
                response.put("count", count);
                response.put("data", data);
            }
            else{
                error.set(true);
                massage.set("Ничего не найдено):");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }}
        else {
            error.set(true);
            massage.set("пустой запрос");
        }
        if(error.get()){
            response.put("result", false);
            response.put("error", massage);
        }

        return response;
    }
    private String getContext(String query,String content){
        String substring = content.substring(content.indexOf(query)-10);
        String substring1 = substring.substring(0,query.length()+10);
        return "<b>" + substring1 + "</b>";
    }
    @PostMapping("/api/indexPage")
    public HashMap<String, Object> indexPage(@RequestParam String url){
     Iterable<Site>  siteRepositoryAll = siteRepository.findAll();
        HashMap<String, Object> response = new HashMap<>();
        boolean result = false;
      int siteId = 0;
        for (Site site : siteRepositoryAll){
            if (url.contains(site.getUrl().replaceAll("www.",""))){
                siteId = site.getId();
                result = true;
            }
        }
        if (!result){
            response.put("result", false);
            response.put("error", "Данная страница находится за пределами сайтов, \n" +
                    "указанных в конфигурационном файле");
            return response;
        }
        try {
            pageRepository.save(new SiteIndexService().pageIndex(url, siteId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.put("result", result);
        return response;
    }
    public List<Page> getPages(int siteId){
        List<Page> pages = new ArrayList<>();
        for (Page page : pageRepository.findAll()){
            if (page.getSiteId() == siteId){
                pages.add(page);
            }
        }
        return pages;
    }

}
