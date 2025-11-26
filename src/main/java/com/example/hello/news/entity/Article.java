package com.example.hello.news.entity;

import com.example.hello.news.dto.ArticleDTO;
import com.example.hello.news.dto.SourceDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="article")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "source_id", foreignKey = @ForeignKey(name = "article_ibfk_1"))
    private Source source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "category_id", foreignKey = @ForeignKey(name = "article_ibfk_2"))
    private Category category;

    @Column(length = 255)
    private String author;
    @Column(length = 255)
    private String title;
    @Column(columnDefinition = "Text")
    private String description;
    @Column(length = 500)
    private String url;
    @Column(name = "url_to_image", length = 500)
    private String urlToImage;
    @Column(name = "published_at", length = 100)
    private String publishedAt;
    @Column(columnDefinition = "Text")
    private String content;

    @Column(name="created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", insertable = false)
    private LocalDateTime updatedAt;


    /**
     * ArticleDTO를 이용하여 Article(Entity) 객체를 생성하여 반환하는 함수.
     *
     * <p>DTO에 담긴 기사 정보를 기반으로 새로운 Article 객체를 만들고,
     * 지정된 Source와 Category를 설정한다.</p>
     *
     * @param dto 변환에 사용할 ArticleDTO 객체
     * @param src 설정할 뉴스 소스(Source) 객체
     * @param cat 설정할 카테고리(Category) 객체
     * @return ArticleDTO 정보를 반영한 새로운 Article(Entity) 객체
     */
    // ArticleDTO를 이용하여 Article(Entity)를 생상하여 반환하는 함수
    public static Article fromDTO(ArticleDTO dto, Source src, Category cat) {
        Article article = new Article();
        article.setSource(src);
        article.setCategory(cat);

        article.setAuthor(dto.getAuthor());
        article.setTitle(dto.getTitle());
        article.setDescription(dto.getDescription());
        article.setUrl(dto.getUrl());
        article.setUrlToImage(dto.getUrlToImage());
        article.setPublishedAt(dto.getPublishedAt());
        article.setContent(dto.getContent());

        return article;
    }

    /**
     * Article(Entity) 객체를 ArticleDTO로 변환하여 반환하는 함수.
     *
     * <p>Article 객체의 필드 값을 DTO로 복사하고,
     * 관련된 Source 객체도 SourceDTO로 변환하여 설정한다.</p>
     *
     * @param article 변환할 Article(Entity) 객체
     * @return Article 정보를 반영한 새로운 ArticleDTO 객체
     */
    public static ArticleDTO toDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setAuthor(article.getAuthor());
        dto.setTitle(article.getTitle());
        dto.setUrl(article.getUrl());
        dto.setDescription(article.getDescription());
        dto.setUrlToImage(article.getUrlToImage());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setContent(article.getContent());

        SourceDTO srcDTO = Source.toDTO(article.getSource());
        dto.setSource(srcDTO);

        return dto;

    }
}

