package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.DB.Status;
import searchengine.config.SitesList;
import searchengine.services.Index;

import java.io.IOException;
@RequiredArgsConstructor
@RestController
public class SiteController {
    private final SitesList sites;
    @GetMapping("/api/startIndexing")
    public void startIndexing(){

        for (int i = 0; i<sites.getSites().size(); i++) {
            try {
                Index.indexOrUpdate(sites.getSites().get(i).getUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @GetMapping("/api/stopIndexing")
    public void stopIndexing(){
        for (int i = 0; i<sites.getSites().size();i++){
            if (Index.getSites().get(i).getStatus().equals(Status.INDEXING)){
                Index.sites.remove(Index.getSites().get(i));
            }
        }
    }
}
