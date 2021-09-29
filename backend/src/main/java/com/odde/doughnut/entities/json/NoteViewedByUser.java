package com.odde.doughnut.entities.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class NoteViewedByUser {
    @Setter
    @Getter
    private NoteBreadcrumbViewedByUser noteBreadcrumbViewedByUser;
    @Getter
    @Setter
    private Integer id;
    @Getter
    @Setter
    private Note note;

    @Getter
    @Setter
    private Map<Link.LinkType, LinkViewed> links;

    @Getter
    @Setter
    @JsonIgnoreProperties({"noteContent"})
    private List<Note> children;
}
