package com.example.hello.news.controller;

import com.example.hello.news.dto.ArticleDTO;
import com.example.hello.news.dto.NewsResponse;
import com.example.hello.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequiredArgsConstructor
public class NewsController {

    // @Autowired  // 자동 주입
    private final NewsService newsService;

    /**
     * /news 요청을 처리하기 위한 함수.
     * 전체 뉴스 기사를 페이징 처리하여 템플릿에 전달한다.
     *
     * <p>뉴스 조회 중 예외가 발생하면 오류 메시지를 모델에 담아 news 템플릿 페이지를 반환한다.</p>
     *
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @param pageable 페이징 처리를 위한 페이지 정보(org.springframework.data.domain.Pageable)
     * @return news 템플릿 페이지
     */
    @RequestMapping("/news")
    public String newsHome(Model model, Pageable pageable) {
        try {
            Page<ArticleDTO> articles = newsService.getArticles(pageable);
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            // error 처리
            model.addAttribute("error", e.getMessage() );
        }
        return "news";
    }

    /**
     * 루트("/") 요청을 처리하기 위한 함수.
     * 요청이 들어오면 자동으로 /news 경로로 리다이렉트한다.
     *
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model) — (현재는 사용하지 않음)
     * @return /news로 리다이렉트
     */
    @RequestMapping("/")
    public String index(Model model){
        return "redirect:/news";
    }
}
