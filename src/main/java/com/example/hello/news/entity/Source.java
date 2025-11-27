package com.example.hello.news.entity;

import com.example.hello.news.dto.SourceDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="source")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String sid;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String url;

    @Column(length = 50)
    private String category;

    @Column(length = 10)
    private String language;

    @Column(length = 10)
    private String country;

    @Column(name="created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", insertable = false)
    private LocalDateTime updatedAt;

    /**
     * Source 엔티티 객체를 SourceDTO로 변환하는 정적 메서드.
     *
     * <p>DB에서 조회한 {@link Source} 엔티티를 클라이언트나 서비스 계층에서
     * 활용하기 위한 {@link SourceDTO} 객체로 매핑한다.
     * 엔티티의 주요 필드(id, name, description, url, category, language, country)를
     * 동일하게 DTO에 복사하여 반환한다.</p>
     *
     * @param source 변환할 Source 엔티티 객체
     * @return SourceDTO 변환 결과 DTO 객체
     */
    public static SourceDTO toDTO(Source source) {
        SourceDTO dto = new SourceDTO();
        dto.setId( source.getSid() );
        dto.setName(source.getName());
        dto.setDescription(source.getDescription());
        dto.setUrl(source.getUrl());
        dto.setCategory(source.getCategory());
        dto.setLanguage(source.getLanguage());
        dto.setCountry(source.getCountry());

        return dto;
    }
}
