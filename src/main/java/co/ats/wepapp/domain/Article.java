package co.ats.wepapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Article.
 */
@Entity
@Table(name = "article")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "link", nullable = false)
    private String link;

    @Lob
    @Column(name = "content")
    private String content;

    @Lob
    @Column(name = "summary")
    private String summary;

    @Column(name = "author")
    private String author;

    @Column(name = "published_date")
    private Instant publishedDate;

    @Column(name = "jhi_read")
    private Boolean read;

    @Column(name = "favorited")
    private Boolean favorited;

    @Column(name = "tags")
    private String tags;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "views")
    private Integer views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "articles" }, allowSetters = true)
    private Feed feed;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Article id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Article title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return this.link;
    }

    public Article link(String link) {
        this.setLink(link);
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return this.content;
    }

    public Article content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return this.summary;
    }

    public Article summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAuthor() {
        return this.author;
    }

    public Article author(String author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Instant getPublishedDate() {
        return this.publishedDate;
    }

    public Article publishedDate(Instant publishedDate) {
        this.setPublishedDate(publishedDate);
        return this;
    }

    public void setPublishedDate(Instant publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Boolean getRead() {
        return this.read;
    }

    public Article read(Boolean read) {
        this.setRead(read);
        return this;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getFavorited() {
        return this.favorited;
    }

    public Article favorited(Boolean favorited) {
        this.setFavorited(favorited);
        return this;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public String getTags() {
        return this.tags;
    }

    public Article tags(String tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public Article coverImageUrl(String coverImageUrl) {
        this.setCoverImageUrl(coverImageUrl);
        return this;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getViews() {
        return this.views;
    }

    public Article views(Integer views) {
        this.setViews(views);
        return this;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Feed getFeed() {
        return this.feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Article feed(Feed feed) {
        this.setFeed(feed);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Article)) {
            return false;
        }
        return getId() != null && getId().equals(((Article) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Article{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", link='" + getLink() + "'" +
            ", content='" + getContent() + "'" +
            ", summary='" + getSummary() + "'" +
            ", author='" + getAuthor() + "'" +
            ", publishedDate='" + getPublishedDate() + "'" +
            ", read='" + getRead() + "'" +
            ", favorited='" + getFavorited() + "'" +
            ", tags='" + getTags() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", views=" + getViews() +
            "}";
    }
}
