package com.cursojava.curso.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "webpage")
@Getter @Setter
@ToString @EqualsAndHashCode
public class WebPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "description",length = 2000)
    private String description;

    @Column(name = "enabled", length = 1)
    private boolean enabled;

    public WebPage() {
    }
    public WebPage(String url) {
        this.url = url;
    }
}
