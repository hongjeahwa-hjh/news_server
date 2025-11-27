package com.example.hello.news.repository;

import com.example.hello.news.dto.CountArticleByCategory;
import com.example.hello.news.dto.SourceByArticleDTO;
import com.example.hello.news.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByUrl(String url);

    /**
     * /admin/category 요청을 처리하는 함수.
     *
     * <p>카테고리별 기사 개수를 조회하기 위해 NewsService를 호출하고,
     * 조회된 데이터를 모델(Model)에 담아 템플릿으로 전달한다.</p>
     *
     * @return 카테고리별 기사 수 정보를 담은 DTO 리스트
     *         (com.example.hello.news.dto.CountArticleByCategory)
     */
    // 카테고리별 기사 개수를 구하는 함수
    @Query("select new com.example.hello.news.dto.CountArticleByCategory(a.category.name, COUNT(a.id)) " +
            "from Article a " +
            "group by a.category.name " +
            "order by COUNT(a.id) desc")
    List<CountArticleByCategory> countArticleByCategory();

    /**
     * 소스별(article source) 기사 수를 조회하는 함수.
     *
     * <p>Article 엔티티를 기반으로 소스 이름과 소스 URL별로 그룹화하여
     * 해당 소스에 속한 기사 개수를 집계한다.
     * 결과는 {@link com.example.hello.news.dto.SourceByArticleDTO} 형태로 반환되며,
     * 기사 수가 많은 순으로 정렬된다.</p>
     *
     * <p>페이징 기능(Pageable)을 지원하여 필요한 범위만 조회할 수 있다.</p>
     *
     * @param pageable 조회할 데이터 범위 및 정렬 조건을 포함한 Pageable 객체
     * @return 소스 이름, 소스 URL, 해당 소스의 기사 수가 담긴 DTO 리스트
     */
    // 소스별 기사 수
    @Query("select new com.example.hello.news.dto.SourceByArticleDTO(a.source.name, a.source.url, COUNT(a.id)) " +
            "from Article a " + "group by a.source.name, a.source.url " + "order by COUNT(a.id) desc")
    List<SourceByArticleDTO> countArticleBySource(Pageable pageable);

    Page<Article> findByTitleContaining(String query, Pageable pageable);

    Page<Article> findByDescriptionContaining(String query, Pageable pageable);

    Page<Article> findByAuthorContaining(String query, Pageable pageable);
}
