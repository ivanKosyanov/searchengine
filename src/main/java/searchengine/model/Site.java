package searchengine.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity

public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private Status status;
    @Column(name = "status_time")
    private String statusTime;
    @Column(name = "last_error")
    private String lastError;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(String statusTime) {
        this.statusTime = statusTime;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String url;
    private String name;



    public enum Status {
        INDEXING,INDEXED,FAILED
    }
}

