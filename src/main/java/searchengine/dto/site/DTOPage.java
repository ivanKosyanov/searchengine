package searchengine.dto.site;

import lombok.Data;

@Data
public class DTOPage {
    private String site;
    private String siteName;
    private String uri;
    private String tittle;
    private String snippet;
    private float relevance;
}

