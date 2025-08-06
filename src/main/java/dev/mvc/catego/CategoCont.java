package dev.mvc.catego;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.mvc.users.UsersProc;
import dev.mvc.users.UsersProcInter;
import dev.mvc.catego.CategoVOMenu;
import dev.mvc.contents.ContentsProcInter;
import dev.mvc.catego.CategoVO;
import dev.mvc.tool.Tool;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/catego")
public class CategoCont {
  @Autowired // Spring이 CateProcInter를 구현한 CateProc 클래스의 객체를 생성하여 할당
  @Qualifier("dev.mvc.catego.CategoProc")
  private CategoProcInter categoProc;
  
  @Autowired // Spring이 CateProcInter를 구현한 CateProc 클래스의 객체를 생성하여 할당
  @Qualifier("dev.mvc.users.UsersProc")
  private UsersProcInter usersProc;
  
  @Autowired
  @Qualifier("dev.mvc.contents.ContentsProc") // @Service("dev.mvc.users.usersProc")
  private ContentsProcInter contentsProc;
  
  /** 페이지당 출력할 레코드 갯수, nowPage는 1부터 시작 */
  public int record_per_page = 6;

  /** 블럭당 페이지 수, 하나의 블럭은 10개의 페이지로 구성됨 */
  public int page_per_block = 10;
  
  /** 페이징 목록 주소, @GetMapping(value="/list_search") */
  private String list_url = "/catego/list_search";
    
  public CategoCont() {
    System.out.println("-> CategoCont created.");    
  }
  
//  @GetMapping(value="/create") // http://localhost:9092/catego/create
//  @ResponseBody
//  public String create() {
//    System.out.println("-> http://localhost:9092/catego/create");
//    return "<h2>Create test</h2>";
  
  /**
   * 등록품
   * // http://localhost:9092/catego/create
   * // http://localhost:9092/catego/create/ X
   * @return
   */
  @GetMapping(value="/create")
  public String create(@ModelAttribute("categoVO") CategoVO categoVO) {
    categoVO.setName("");  
    categoVO.setDifficulty("★★★☆☆");
    
    return "catego/create"; // /templates/catego/create.html
  }
  
  /**
   * 등록 처리
   * Model model : controller -> html로 데이터 전송
   * @Valid: @NotEmpty, @Size, @NotNull, @Min, @Man, @Patten... 규칙 일반 검사 지원
   * CateVO cateVO: FORM 태그의 값 자동 저장, Integer.parseInt(request.getParameter("seqno")))<-이게 자동
   * BindingResult bindingResult: @Valid의 결과 저장
   * @param model
   * @return
   */
  @PostMapping(value="/create")
  public String create(Model model, 
                           @Valid CategoVO categoVO,
                           BindingResult bindingResult,
                           @RequestParam(name="word", defaultValue="") String word,
                           RedirectAttributes ra) {
    System.out.println("-> create post");
    if(bindingResult.hasErrors() == true) {
      return "catego/create"; // /templates/catego/create.html
    }
    
    // System.out.println("-> cateVO.getName() : " + cateVO.getName());
    // System.out.println("-> cateVO.getSeqno() : " + cateVO.getSeqno());
    
    int cnt = this.categoProc.create(categoVO);
    // System.out.println("-> cnt : " + cnt);
    
    if (cnt == 1) {
//      model.addAttribute("code", Tool.CREATE_SUCCESS);
//      model.addAttribute("name", categoVO.getName());
      ra.addAttribute("word", word);
      return "redirect:/catego/list_search";
    } else {
      model.addAttribute("code", Tool.CREATE_FAIL);
    }
    
    model.addAttribute("cnt", cnt);
    
    return "catego/msg"; // /templates/catego/msg.html
  }
  
//  /**
//   * 전체 목록
//   * http://localhost:9092/catego/list_all
//   * @param model
//   * @return
//   */
//  @GetMapping(value="/list_all")
//  public String list_all(Model model, @ModelAttribute("categoVO") CategoVO categoVO) {
//    categoVO.setName("");  
//    categoVO.setDifficulty("★★★☆☆");
//    
//    ArrayList<CategoVO> list = this.categoProc.list_all();
//    
//    
//    model.addAttribute("list", list);
//    
//    // 2단 메뉴
//    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
//    model.addAttribute("menu", menu);
//    
//    // 카테고리 그룹 목록
//    ArrayList<String> grpset = this.categoProc.grpset();
//    categoVO.setName(String.join("/",  grpset));
//    System.out.println("-> cateVO.getName(): " + categoVO.getName());
//    
//    return "catego/list_all"; // /templates/catego/list_all.html
//  }
  
  /**
   * 조회
   * http://localhost:9092/catego/read/1
   * @param model
   * @return
   */
  @GetMapping(value="/read/{categono}")
  public String list_all(Model model,
                           @PathVariable("categono") Integer categono,
                           @RequestParam(name="word", defaultValue="") String word,
                           @RequestParam(name="now_page", defaultValue="1") int now_page) {
    System.out.println("-> read categono : " + categono);
    
    CategoVO categoVO = this.categoProc.read(categono);
    model.addAttribute("categoVO", categoVO);
    
//    ArrayList<CategoVO> list = this.categoProc.list_all();
//    model.addAttribute("list", list);
    
    // 2단 메뉴
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);
    
    // 카테고리 그룹 목록
    ArrayList<String> grpset = this.categoProc.grpset();
    categoVO.setName(String.join("/",  grpset));
    System.out.println("-> cateVO.getName(): " + categoVO.getName());
    
    ArrayList<CategoVO> list = this.categoProc.list_search_paging(word, now_page, this.record_per_page);
    model.addAttribute("list", list);
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    // --------------------------------------------------------------------------------------
    // 페이지 번호 목록 생성
    // --------------------------------------------------------------------------------------
    int search_count = this.categoProc.list_search_count(word);
    String paging = this.categoProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    
    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * this.record_per_page);
    model.addAttribute("no", no);
    // --------------------------------------------------------------------------------------
    
    return "catego/read"; // /templates/catego/read.html
  }
  
  /**
   * 수정폼
   * http://localhost:9092/catgo/update/1
   * @param model
   * @return
   */
  @GetMapping(value="/update/{categono}")
  public String update(Model model, HttpSession session,
                             @PathVariable("categono") Integer categono,
                             @RequestParam(name="word", defaultValue="") String word,
                             @RequestParam(name="now_page", defaultValue="1") int now_page) {
    if (this.usersProc.isAdmin(session)) {

    System.out.println("-> update categono : " + categono);
    
    CategoVO categoVO = this.categoProc.read(categono);
    model.addAttribute("categoVO", categoVO);
    
    
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);
    
    ArrayList<CategoVO> list = this.categoProc.list_search_paging(word, now_page, this.record_per_page);
    model.addAttribute("list", list);
    model.addAttribute("word", word);
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    // --------------------------------------------------------------------------------------
    // 페이지 번호 목록 생성
    // --------------------------------------------------------------------------------------
    int search_count = this.categoProc.list_search_count(word);
    String paging = this.categoProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    
    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * this.record_per_page);
    model.addAttribute("no", no);
    // --------------------------------------------------------------------------------------
    
    return "catego/update"; // /templates/catego/update.html
    } else {
      return "redirect:/users/login_cookie_need?url=/catego/update/{categono}";
    }
  }
  
  
  /**
   * 수정 처리
   * Model model : controller -> html로 데이터 전송
   * @Valid: @NotEmpty, @Size, @NotNull, @Min, @Man, @Patten... 규칙 일반 검사 지원
   * CateVO cateVO: FORM 태그의 값 자동 저장, Integer.parseInt(request.getParameter("seqno")))<-이게 자동
   * BindingResult bindingResult: @Valid의 결과 저장
   * @param model
   * @return
   */
  @PostMapping(value="/update")
  public String update(Model model, 
                            @Valid CategoVO categoVO,
                            BindingResult bindingResult,
                            @RequestParam(name="word", defaultValue="") String word,
                            RedirectAttributes ra,
                            @RequestParam(name="now_page", defaultValue="1") int now_page) {
    // System.out.println("-> create post");
    if(bindingResult.hasErrors() == true) {
      return "catego/update"; // /templates/catego/create.html
    }
    
    // System.out.println("-> cateVO.getName() : " + cateVO.getName());
    // System.out.println("-> cateVO.getSeqno() : " + cateVO.getSeqno());
    
    int cnt = this.categoProc.update(categoVO);
    // System.out.println("-> cnt : " + cnt);
    
    if (cnt == 1) {
//      model.addAttribute("code", Tool.UPDATE_SUCCESS);
//      model.addAttribute("name", categoVO.getName());
      // return "redirect:/catego/update/" + categoVO.getCategono(); // 한글깨짐(X)
      ra.addAttribute("word", word);
      ra.addAttribute("now_page", now_page);
      
      return "redirect:/catego/update/" + categoVO.getCategono(); // redirect로 데이터 전송, 한글 깨짐 방지
    } else {
      model.addAttribute("code", Tool.UPDATE_FAIL);
    }
    
    model.addAttribute("cnt", cnt);
    model.addAttribute("word", word);
    
    // --------------------------------------------------------------------------------------
    // 페이지 번호 목록 생성
    // --------------------------------------------------------------------------------------
    int search_count = this.categoProc.list_search_count(word);
    String paging = this.categoProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    
    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * this.record_per_page);
    model.addAttribute("no", no);
    // --------------------------------------------------------------------------------------
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    return "catego/msg"; // /templates/catego/list_all.html
  }
  
  
  /**
   * 삭제폼
   * http://localhost:9092/catgoe/delete/1
   * @param model
   * @return
   */
  @GetMapping(value="/delete/{categono}")
  public String delete(Model model, HttpSession session,
                           @PathVariable("categono") Integer categono,
                           @RequestParam(name="word", defaultValue="") String word,
                           @RequestParam(name="now_page", defaultValue="1") int now_page) {
    // System.out.println("-> update cateno : " + categono);
    if (this.usersProc.isAdmin(session)) {
      

    CategoVO categoVO = this.categoProc.read(categono);
    model.addAttribute("categoVO", categoVO);
    
    // 자식 항목이 있는지 확인
    int childCount = this.contentsProc.count_by_categono(categono); // 자식 항목 수를 가져오는 메서드 필요
    model.addAttribute("childCount", childCount); // 자식 항목 수를 모델에 추가
    
    ArrayList<CategoVO> list = this.categoProc.list_search_paging(word, now_page, this.record_per_page);
    model.addAttribute("list", list);
    model.addAttribute("word", word);
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    // --------------------------------------------------------------------------------------
    // 페이지 번호 목록 생성
    // --------------------------------------------------------------------------------------
    int search_count = this.categoProc.list_search_count(word);
    String paging = this.categoProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    
    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * this.record_per_page);
    model.addAttribute("no", no);
    // --------------------------------------------------------------------------------------
    
    return "catego/delete"; // /templates/catego/delete.html
    } else {
      return "redirect:/uesrs/login_cookie_need?url=/catego/update/{categono}";
    }
  }
  
  
  /**
   * 삭제 처리
   * @param model
   * @return
   */
  @PostMapping(value="/delete/{categono}")
  public String delete_process(Model model, 
                                      @PathVariable("categono") Integer categono,
                                      @RequestParam(name="word", defaultValue="") String word,
                                      RedirectAttributes ra,
                                      @RequestParam(name="now_page", defaultValue="1") int now_page) {
    
    CategoVO categoVO = this.categoProc.read(categono); // 삭제 정보 출력용으로 사전에 읽음
    model.addAttribute("categoVO", categoVO);
    
    // ⭐ 해당 카테고리 안의 글 먼저 삭제
    int contents_cnt = this.contentsProc.delete_by_categono(categono);
    model.addAttribute("contents_cnt", contents_cnt);
    
    int cnt = this.categoProc.delete(categono);
    // System.out.println("-> cnt : " + cnt);
    
    if (cnt == 1) {
      
      // ----------------------------------------------------------------------------------------------------------
      // 마지막 페이지에서 모든 레코드가 삭제되면 페이지수를 1 감소 시켜야함.
      int search_cnt = this.categoProc.list_search_count(word);
      if (search_cnt % this.record_per_page == 0) {
        now_page = now_page - 1;
        if (now_page < 1) {
          now_page = 1; // 최소 시작 페이지
        }
       
      }
      // ----------------------------------------------------------------------------------------------------------
      
//      model.addAttribute("code", Tool.DELETE_SUCCESS);
      ra.addAttribute("word", word);
      ra.addAttribute("now_page", now_page);
      
      return "redirect:/catego/list_search"; // @GetMapping(value="/list_all")
 
    } else {
      model.addAttribute("code", Tool.DELETE_FAIL);
    }
    model.addAttribute("name", categoVO.getName());
    model.addAttribute("title", categoVO.getTitle());
    model.addAttribute("cnt", cnt);
    
    ArrayList<CategoVO> list = this.categoProc.list_search_paging(word, now_page, this.record_per_page);
    model.addAttribute("list", list);
    model.addAttribute("word", word);
    
 // --------------------------------------------------------------------------------------
    // 페이지 번호 목록 생성
    // --------------------------------------------------------------------------------------
    int search_count = this.categoProc.list_search_count(word);
    String paging = this.categoProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    
    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * this.record_per_page);
    model.addAttribute("no", no);
    // --------------------------------------------------------------------------------------
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    return "catego/msg"; // /templates/catego/list_all.html
  }
  
  /**
   *  우선 순위 높임 10등->1등
   *  http://localhost:9091/catego/update/1
   */
  @GetMapping(value="/update_seqno_forward/{categono}")
  public String update_seqno_forward(Model model, 
                                                @PathVariable("categono") Integer categono,
                                                @RequestParam(name="word", defaultValue="") String word,
                                                RedirectAttributes ra,
                                                @RequestParam(name="now_page", defaultValue="1") int now_page) {
    // System.out.println("-> create post");

    this.categoProc.update_seqno_forward(categono);
    
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    ra.addAttribute("word", word);
    ra.addAttribute("now_page", now_page);
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    return "redirect:/catego/list_search"; // /templates/cate/list_all.html
  }
  
  /**
   *  우선 순위 낮춤 1등->10등
   *  http://localhost:9091/catego/update/10
   */
  @GetMapping(value="/update_seqno_backward/{categono}")
  public String update_seqno_backward(Model model, 
                                                  @PathVariable("categono") Integer categono,
                                                  @RequestParam(name="word", defaultValue="") String word,
                                                  RedirectAttributes ra,
                                                  @RequestParam(name="now_page", defaultValue="1") int now_page) {
    // System.out.println("-> create post");

    this.categoProc.update_seqno_backward(categono);
    
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    ra.addAttribute("word", word);
    ra.addAttribute("now_page", now_page);
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    return "redirect:/catego/list_search"; // /templates/cate/list_all.html
  }

  
  /**
   *  카테고리 공개 설정
   *  http://localhost:9091/catego/update_visible_y/1
   */
  @GetMapping(value="/update_visible_y/{categono}")
  public String update_visible_y(Model model, HttpSession session,
                                       @PathVariable("categono") Integer categono,
                                       @RequestParam(name="word", defaultValue="") String word,
                                       RedirectAttributes ra,
                                       @RequestParam(name="now_page", defaultValue="1") int now_page) {
    // System.out.println("-> create post");
    if (this.usersProc.isAdmin(session)) {
      

    this.categoProc.update_visible_y(categono);
    
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    ra.addAttribute("word", word);
    ra.addAttribute("now_page", now_page);
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    return "redirect:/catego/list_search"; // @GetMapping(value="list_all")
    } else {
      return "redirect:/users/login_cookie_need?url=/catego/update/{categono}";
    }
  }
  
  /**
   *  카테고리 비공개 설정
   *  http://localhost:9091/catego/update_visible_n/1
   */
  @GetMapping(value="/update_visible_n/{categono}")
  public String update_visible_n(Model model, HttpSession session,
                                        @PathVariable("categono") Integer categono,
                                        @RequestParam(name="word", defaultValue="") String word,
                                        RedirectAttributes ra,
                                        @RequestParam(name="now_page", defaultValue="1") int now_page) {
//    System.out.println("-> update_visible_n" +  update_visible_n());
    
    if (this.usersProc.isAdmin(session)) {
      
      

    this.categoProc.update_visible_n(categono);
    
    model.addAttribute("word", word);
    ra.addAttribute("word", word); 
    ra.addAttribute("now_page", now_page);
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    
    return "redirect:/catego/list_search"; // @GetMapping(value="list_all")
    } else {
      return "redirect:/users/login_cookie_need?url=/catego/update/{categono}";
    }
  }
  
//  /**
//   * 검색 목록
//   * http://localhost:9092/catego/list_search
//   * @param model
//   * @return
//   */
//  @GetMapping(value="/list_search")
//  public String list_all(Model model,
//                           @ModelAttribute("categoVO") CategoVO categoVO,
//                           @RequestParam(name = "word", defaultValue = "") String word) {
//    categoVO.setName("");  
//    categoVO.setDifficulty("★★★☆☆");
//    
//    ArrayList<CategoVO> list = this.categoProc.list_search_paging(word, now_page, this.record_per_page);
//    model.addAttribute("list", list);
//    
//    // 2단 메뉴
//    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
//    model.addAttribute("menu", menu);
//    
//    // 카테고리 그룹 목록
//    ArrayList<String> grpset = this.categoProc.grpset();
//    categoVO.setName(String.join("/",  grpset));
//    System.out.println("-> cateVO.getGrp(): " + categoVO.getName());
//    
//    model.addAttribute("word", word);
//    
//    int list_search_count = this.categoProc.list_search_count(word);
//    model.addAttribute("list_search_count", list_search_count);
//    
//    return "catego/list_search"; // /templates/catego/list_search.html
//  }

  
 
  
  /**
   * 등록 폼 및 검색 목록 + 페이징
   * http://localhost:9091/cate/list_search
   * http://localhost:9091/cate/list_search?word=&now_page=
   * http://localhost:9091/cate/list_search?word=까페&now_page=1
   * @param model
   * @return
   */
  @GetMapping(value="/list_search") 
  public String list_search_paging(Model model, HttpSession session,
                                   @RequestParam(name="word", defaultValue = "") String word,
                                   @RequestParam(name="now_page", defaultValue="1") int now_page) {
    if (this.usersProc.isAdmin(session)) {

    CategoVO categoVO = new CategoVO();
    // cateVO.setGenre("분류");
    // cateVO.setName("카테고리 이름을 입력하세요."); // Form으로 초기값을 전달
    
    // 카테고리 그룹 목록
    ArrayList<String> list_grp = this.categoProc.grpset();
    categoVO.setName(String.join("/", list_grp));
    
    model.addAttribute("categoVO", categoVO); // 등폭폼 카테고리
    
    word = Tool.checkNull(word); // Null -> ""
    
    ArrayList<CategoVO> list = this.categoProc.list_search_paging(word, now_page, this.record_per_page);
    
    
    int totalCnt = 0;
    for (CategoVO vo : list) {
      int categono = vo.getCategono();
      this.categoProc.updateCntByCategono(categono);  // 중분류 cnt 갱신

      String name = vo.getName();
      this.categoProc.updateCntByName(name);          // 대분류 cnt 갱신

      totalCnt += vo.getCnt();  // 모든 카테고리의 cnt 값을 합산
  }
  model.addAttribute("totalCnt", totalCnt);
    model.addAttribute("list", list);
    
    
//    ArrayList<CateVO> menu = this.cateProc.list_all_categrp_y();
//    model.addAttribute("menu", menu);

    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);
    
    int search_cnt = list.size();
    model.addAttribute("search_cnt", search_cnt);    
    
    model.addAttribute("word", word); // 검색어
    
    int list_search_count = this.categoProc.list_search_count(word);
    model.addAttribute("list_search_count", list_search_count);
    
    // --------------------------------------------------------------------------------------
    // 페이지 번호 목록 생성
    // --------------------------------------------------------------------------------------
    int search_count = this.categoProc.list_search_count(word);
    String paging = this.categoProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    
    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * this.record_per_page);
    model.addAttribute("no", no);
    model.addAttribute("search_count", search_count);
    // -------------------------------------------------------------------------------------- 
    
    return "catego/list_search";  // /templates/cate/list_search.html
    } else {
      return "redirect:/users/login_cookie_need?url=/catego/list_search";
    }
  }
  
  
  
}
