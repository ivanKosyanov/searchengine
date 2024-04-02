package searchengine.services;

import lombok.Getter;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.model.Page;
import searchengine.model.PageRepository;
import searchengine.model.Site;
import searchengine.model.SiteRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SiteIndexService {

    @Getter
    protected static List<Site> sites = new ArrayList<>();
    @Getter
    private static HashMap<Site, List<Page>> mapSites = new HashMap<>();
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");



    public HashMap<Site, List<Page>> siteIndex(String url) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Site site = new Site();
        site.setName(Jsoup.connect(url).get().title());
        site.setUrl(url);
        site.setStatus(Site.Status.INDEXING);

        site.setStatusTime(formatter.format(now));
        sites.add(site);

        List<Page> pages = new ArrayList<>();



        Jsoup.connect(url).get().select("a").forEach(element -> {
            try {
                if ((element.attr("href").contains("https") || element.attr("href").contains("http")) && element.attr("href").contains(url.replaceAll("www.", "")) && !element.attr("href").contains(".pdf") ) {

                   pages.add( pageIndex(element.attr("href"), site.getId()));
                    System.out.println(element.attr("href"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        HashMap<Site, List<Page>> map = new HashMap<>();
        site.setStatus(Site.Status.INDEXED);
        map.put(site, pages);
        mapSites.put(site, pages);
        return map;
    }

    public Page pageIndex(String url, int siteId) throws Exception {
        Page page = new Page();
        page.setPath(url);
        page.setSiteId(siteId);
        page.setCode(200);
        String content = Jsoup.connect(url).get().toString();
        page.setContent(content);

        return page;
    }


}
