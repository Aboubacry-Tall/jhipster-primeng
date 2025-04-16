package co.ats.wepapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Feed.
 */
@Entity
@Table(name = "feed")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "site_name")
    private String siteName;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "auto_discovered")
    private Boolean autoDiscovered;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "last_fetched_date")
    private Instant lastFetchedDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "feed")
    @JsonIgnoreProperties(value = { "feed" }, allowSetters = true)
    private Set<Article> articles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Feed id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Feed name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public Feed url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public Feed siteName(String siteName) {
        this.setSiteName(siteName);
        return this;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDescription() {
        return this.description;
    }

    public Feed description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public Feed category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getAutoDiscovered() {
        return this.autoDiscovered;
    }

    public Feed autoDiscovered(Boolean autoDiscovered) {
        this.setAutoDiscovered(autoDiscovered);
        return this;
    }

    public void setAutoDiscovered(Boolean autoDiscovered) {
        this.autoDiscovered = autoDiscovered;
    }

    public String getFaviconUrl() {
        return this.faviconUrl;
    }

    public Feed faviconUrl(String faviconUrl) {
        this.setFaviconUrl(faviconUrl);
        return this;
    }

    public void setFaviconUrl(String faviconUrl) {
        this.faviconUrl = faviconUrl;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Feed active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Feed createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastFetchedDate() {
        return this.lastFetchedDate;
    }

    public Feed lastFetchedDate(Instant lastFetchedDate) {
        this.setLastFetchedDate(lastFetchedDate);
        return this;
    }

    public void setLastFetchedDate(Instant lastFetchedDate) {
        this.lastFetchedDate = lastFetchedDate;
    }

    public Set<Article> getArticles() {
        return this.articles;
    }

    public void setArticles(Set<Article> articles) {
        if (this.articles != null) {
            this.articles.forEach(i -> i.setFeed(null));
        }
        if (articles != null) {
            articles.forEach(i -> i.setFeed(this));
        }
        this.articles = articles;
    }

    public Feed articles(Set<Article> articles) {
        this.setArticles(articles);
        return this;
    }

    public Feed addArticle(Article article) {
        this.articles.add(article);
        article.setFeed(this);
        return this;
    }

    public Feed removeArticle(Article article) {
        this.articles.remove(article);
        article.setFeed(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Feed)) {
            return false;
        }
        return getId() != null && getId().equals(((Feed) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Feed{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", url='" + getUrl() + "'" +
            ", siteName='" + getSiteName() + "'" +
            ", description='" + getDescription() + "'" +
            ", category='" + getCategory() + "'" +
            ", autoDiscovered='" + getAutoDiscovered() + "'" +
            ", faviconUrl='" + getFaviconUrl() + "'" +
            ", active='" + getActive() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastFetchedDate='" + getLastFetchedDate() + "'" +
            "}";
    }
}
