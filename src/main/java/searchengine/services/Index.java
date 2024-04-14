package searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.DB.DBConnection;
import searchengine.DB.Site;
import searchengine.DB.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Index {
 public static List<Site> sites = new ArrayList<>();

 public static List<Site> getSites() {
  return sites;
 }

 public static void indexOrUpdate(String url) throws IOException {

 Element element = Jsoup.connect(url).get().createElement("title");
  Site site = new Site();
  site.setName(element.text());
  site.setUrl(url);
  site.setStatus(Status.INDEXING);
  site.setStatusTime(new Date());
  sites.add(site);


  DBConnection.addSite(site);


 }
}
