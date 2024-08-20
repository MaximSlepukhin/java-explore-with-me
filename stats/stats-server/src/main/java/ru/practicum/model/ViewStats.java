package ru.practicum.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class ViewStats {

    public String app;

    public String uri;

    public Long hits;

    public ViewStats(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
