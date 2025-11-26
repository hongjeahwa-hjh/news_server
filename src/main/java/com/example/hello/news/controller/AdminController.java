package com.example.hello.news.controller;

import com.example.hello.news.dto.*;
import com.example.hello.news.entity.Category;
import com.example.hello.news.service.ArticleService;
import com.example.hello.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")   // localhost:8090/admin -> 해당 라우터 경로 아래로는 이 클래스에서 처리 담당한다
public class AdminController {
    private final NewsService newsService;
    private final ArticleService articleService;

    /**
     * /admin/category request를 처리하기 위한 함수
     * newsService로부터 카테고리 데이터 전체를 가져와서 model
    * @param model : 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
    * @return 카테고리 템플릿 페이지
    * */
    @GetMapping("/category")
    public String categories(Model model) {
        // 데이터베이스로부터 카테고리 정보를 가져와서 admin의 category페이지에 전달한다.
        List<CategoryDTO> categories = newsService.getCategories();
        model.addAttribute("category", categories);
        return "category";      // templates directory아래에 있는 category.html을 랜더링해라
    }

    /**
     * /inputCategory 요청을 처리하기 위한 함수.
     * 전달된 category_name 값을 검증한 후, 유효한 경우 Category 엔티티를 생성하여
     * newsService를 통해 데이터베이스에 저장한다.
     * 저장 중 오류가 발생하면 오류 메시지와 함께 카테고리 목록을 다시 조회하여
     * category 템플릿으로 반환한다.
     *
     * @param categoryName 클라이언트로부터 전달된 카테고리명 (category_name)
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @return 성공 시 /admin/category로 리다이렉트, 실패 시 category 템플릿 페이지
     */
    // category_name으로부터 전달된 데이터를 데이터베이스에 저장하라는 request
    @PostMapping("/inputCategory")
    public String inputCategory(@RequestParam("category_name") String categoryName, Model model){
        if(categoryName != null && !categoryName.trim().isEmpty()){
            // 카테고리명 데이터가 정상적으로 전달되었음

            // Category Entity 인스턴스를 생성
            Category category = new Category();
            category.setName(categoryName);     // name field를 categoryName값으로 설정
            String msg = newsService.inputCategory(category);
            if (msg != null && msg.startsWith("ERROR")){
                // 저장하다가 에러가 발생한 경우
                model.addAttribute("ERROR", msg);
                List<CategoryDTO> categories = newsService.getCategories();
                model.addAttribute("category", categories);
                // templates 폴더 아래에 있는 category.html을 렌더링해라
                // Server Side Rendering(SSR)
                return "category";
            }
        }
        // request를 다시 만들어서 해당 request를 요청
        return "redirect:/admin/category";
    }

    /**
     * /updateCategory/{id} 요청을 처리하기 위한 함수.
     * 전달된 카테고리 ID, 카테고리명, 메모를 기반으로 해당 카테고리 정보를 수정한다.
     * 수정 작업은 newsService를 통해 수행되며, 완료 후 카테고리 목록 페이지로 리다이렉트한다.
     *
     * @param categoryId 수정할 카테고리의 고유 ID
     * @param categoryName 수정할 카테고리명
     * @param categoryMemo 수정할 메모 내용
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @return 수정 완료 후 /admin/category 페이지로 리다이렉트
     */
    @PostMapping("/updateCategory/{id}")
    public String updateCategory(@PathVariable("id")String categoryId,
                                 @RequestParam("name")String categoryName,
                                 @RequestParam("memo")String categoryMemo,
                                 Model model){
        newsService.updateCategory(categoryId, categoryName, categoryMemo);
        return "redirect:/admin/category";
    }

    /**
     * /deleteCategory/{id} 요청을 처리하기 위한 함수.
     * 전달된 카테고리 ID를 기준으로 해당 카테고리를 삭제한다.
     * 삭제 과정에서 예외가 발생하면 오류 메시지를 모델에 담아
     * category 템플릿 페이지를 다시 렌더링한다.
     *
     * @param id 삭제할 카테고리의 고유 ID
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @return 삭제 성공 시 /admin/category로 리다이렉트, 실패 시 category 템플릿 페이지
     */
    @PostMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable String id, Model model) {
        try {
            newsService.deleteCategory(id);
        } catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "category";
        }
        return "redirect:/admin/category";
    }

    /**
     * /source 요청을 처리하기 위한 함수.
     * 전달된 Pageable 정보를 기반으로 뉴스 소스(Source) 데이터를 페이징하여 조회하고,
     * 조회된 데이터를 모델에 담아 source 템플릿으로 전달한다.
     *
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @param pageable 페이징 처리를 위한 페이지 정보(org.springframework.data.domain.Pageable)
     * @return source 템플릿 페이지
     */
    @GetMapping("/source")
    public String getSource(Model model, Pageable pageable) {
        Page<SourceDTO> sources = newsService.getSources(pageable);
        model.addAttribute("sources", sources);


        return "source";
    }

    /**
     * /inputSources 요청을 처리하기 위한 함수.
     * 뉴스 소스 데이터를 외부 API 등에서 가져와 데이터베이스에 저장하는 작업을 수행한다.
     * 처리 과정에서 발생할 수 있는 예외(URISyntaxException, IOException, InterruptedException 등)를
     * 처리하여 오류 메시지를 모델에 담고 source 템플릿 페이지를 반환한다.
     *
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @return 성공 시 /admin/source 로 리다이렉트, 실패 시 source 템플릿 페이지
     */
    @GetMapping("/inputSources")
    public String inputSources(Model model) {
        try {
            newsService.inputSources();
        }catch (URISyntaxException | IOException | InterruptedException | RuntimeException e) {
            e.getStackTrace();
            model.addAttribute("error", e.getMessage());
            return "source";
        }
        return "redirect:/admin/source";
    }

    /**
     * /article 요청을 처리하기 위한 함수.
     * 뉴스 기사와 관련된 통계 정보를 조회하여 템플릿에 전달한다.
     *
     * <p>조회하는 정보:
     * <ul>
     *   <li>전체 기사 개수</li>
     *   <li>카테고리별 기사 개수</li>
     *   <li>전체 카테고리 목록</li>
     *   <li>상위 10개 뉴스 소스별 기사 개수</li>
     *   <li>상위 10개를 제외한 기타 기사 개수</li>
     * </ul>
     * </p>
     *
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @return article 템플릿 페이지
     */
    @GetMapping("/article")
    public String article(Model model){
        // 카테고리 목록
        List<CategoryDTO> categories = newsService.getCategories();
        // 전체 기사 개수
        Long articleCount = articleService.getTotalArticleCount();
        // 기사가 있는 카테고리 개수
        List<CountArticleByCategory> countByCategories = articleService.countArticleByCategories();

        List<SourceByArticleDTO> sourceByArticles = articleService.getArticleCountBySource();
        Long top10sum = sourceByArticles.stream().mapToLong(SourceByArticleDTO :: getCount).sum();  // 상위 10개 기사 개수  "count : ??"
        Long etcCount = articleCount - top10sum; // 기타 개수
        // 소스별 기사들의 개수

        // 상위 10개의 정보들만 별도로 취함하고, 나머지 개수들을 별도로 구함

        // 위에서 구한 데이터들을 템플릿에 전달
        model.addAttribute("articleCount", articleCount);
        model.addAttribute("countByCategory", countByCategories);
        model.addAttribute("categories", categories);
        model.addAttribute("sourceByArticles",sourceByArticles);
        model.addAttribute("etcCount", etcCount);

        return "article";
    }

    /**
     * /inputArticles 요청을 처리하기 위한 함수.
     * 클라이언트로부터 전달된 카테고리 이름을 기준으로 기사 데이터를 외부 API 등에서 가져와
     * 데이터베이스에 저장하는 작업을 수행한다.
     *
     * <p>처리 과정에서 URISyntaxException, IOException, InterruptedException 등이 발생할 수 있으며,
     * 예외 발생 시 오류 메시지를 모델에 담아 article 템플릿 페이지를 반환한다.</p>
     *
     * @param category 클라이언트로부터 전달된 카테고리명 (categoryName)
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @return 성공 시 /admin/article로 리다이렉트, 실패 시 article 템플릿 페이지
     */
    @PostMapping("/inputArticles")
    public String inputArticles(@RequestParam("categoryName")String category, Model model){
        try {
            articleService.inputArticles(category);
        } catch (URISyntaxException| IOException| InterruptedException e) {
            e.getStackTrace();
            model.addAttribute("error",e.getMessage());
            return "article";
        }

        return "redirect:/admin/article";
    }

    /**
     * /dashboard 요청을 처리하기 위한 함수.
     * 뉴스 관련 주요 통계 정보를 조회하여 템플릿에 전달한다.
     *
     * <p>조회하는 정보:
     * <ul>
     *   <li>뉴스, 카테고리, 기사 등 주요 레코드의 개수</li>
     * </ul>
     * </p>
     *
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @return dashboard 템플릿 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model){
        HashMap<String, Long> counts = newsService.getRecordCount();
        model.addAttribute("counts", counts);
        return  "dashboard";
    }

    /**
     * 루트("/") 요청을 처리하기 위한 함수.
     * 요청이 들어오면 자동으로 /admin/dashboard 경로로 리다이렉트한다.
     *
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model) — (현재는 사용하지 않음)
     * @return /admin/dashboard로 리다이렉트
     */
    @GetMapping("/")
    public String index(Model model){
        return "redirect:/admin/dashboard";
    }

    /**
     * /search 요청을 처리하기 위한 함수.
     * 전달된 검색어(query)와 검색 타입(searchType)을 기반으로 뉴스 기사를 검색하고,
     * 검색 결과를 템플릿에 전달한다.
     *
     * <p>검색어가 없는 경우에는 빈 페이지를 전달하며,
     * 전체 카테고리 목록은 항상 템플릿에 포함된다.</p>
     *
     * @param model 템플릿에 전달할 데이터 세트(org.springframework.ui.Model)
     * @param pageable 페이징 처리를 위한 페이지 정보(org.springframework.data.domain.Pageable)
     * @param query 검색어 (선택적, null 또는 빈 문자열이면 전체 결과 없음)
     * @param searchType 검색 유형 ("title" 기본값)
     * @return news 템플릿 페이지
     */
    @GetMapping("/search")
    public String searchArticles(Model model, Pageable pageable, @RequestParam(value = "query", required = false) String query, @RequestParam(value = "searchType", defaultValue = "title") String searchType) {
        if (query != null && !query.trim().isEmpty()) {
            Page<ArticleDTO> searchResults = articleService.searchArticles(query, searchType, pageable);
            model.addAttribute("articles", searchResults);
            model.addAttribute("query", query);
            model.addAttribute("searchType", searchType);
        } else {
            model.addAttribute("articles", Page.empty(pageable));
        }
        List<CategoryDTO> categories = newsService.getCategories();
        model.addAttribute("categories", categories);
        return "news";
    }


}
