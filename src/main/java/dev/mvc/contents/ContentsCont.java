package dev.mvc.contents;

import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.mvc.catego.CategoProcInter;
import dev.mvc.catego.CategoVO;
import dev.mvc.catego.CategoVOMenu;
import dev.mvc.contentsgood.ContentsgoodProcInter;
import dev.mvc.contentsgood.ContentsgoodVO;
import dev.mvc.users.UsersProcInter;
import dev.mvc.tool.Tool;
import dev.mvc.tool.Upload;

@RequestMapping(value = "/contents")
@Controller
public class ContentsCont {
  @Autowired
  @Qualifier("dev.mvc.users.UsersProc") // @Service("dev.mvc.users.usersProc")
  private UsersProcInter usersProc;

  @Autowired
  @Qualifier("dev.mvc.catego.CategoProc") // @Component("dev.mvc.catego.categoProc")
  private CategoProcInter categoProc;

  @Autowired
  @Qualifier("dev.mvc.contentsgood.ContentsgoodProc") // @Component("dev.mvc.contentsgood.ContentsgoodProc")
  private ContentsgoodProcInter contentsgoodProc;
  
  @Autowired
  @Qualifier("dev.mvc.contents.ContentsProc") // @Component("dev.mvc.contents.ContentsProc")
  private ContentsProcInter contentsProc;

  public ContentsCont() {
    System.out.println("-> ContentsCont created.");
  }

  /**
   * POST 요청시 새로고침 방지, POST 요청 처리 완료 → redirect → url → GET → forward -> html 데이터
   * 전송
   * 
   * @return
   */
  @GetMapping(value = "/post2get")
  public String post2get(Model model, 
                               @RequestParam(name="url", defaultValue="") String url) {
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);

    return url; // forward, /templates/...
  }

  // 등록 폼, contents 테이블은 FK로 categono를 사용함.
  // http://localhost:9091/contents/create X
  // http://localhost:9091/contents/create?categono=1 // categono 변수값을 보내는 목적
  // http://localhost:9091/contents/create?categono=2
  // http://localhost:9091/contents/create?categono=5
  @GetMapping(value = "/create")
  public String create(Model model, 
      @ModelAttribute("contentsVO") ContentsVO contentsVO, 
      @RequestParam(name="categono", defaultValue="0") int categono) {
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);

    CategoVO categoVO = this.categoProc.read(categono); // 카테고리 정보를 출력하기위한 목적
    model.addAttribute("categoVO", categoVO);

    return "contents/create"; // /templates/contents/create.html
  }

  /**
   * 등록 처리 http://localhost:9091/contents/create
   * 
   * @return
   */
  @PostMapping(value = "/create")
  public String create_(HttpServletRequest request, 
      HttpSession session, 
      Model model, 
      @ModelAttribute("contentsVO") ContentsVO contentsVO,
      RedirectAttributes ra) {

    if (usersProc.isAdmin(session)) { // 관리자로 로그인한경우
      // ------------------------------------------------------------------------------
      // 파일 전송 코드 시작
      // ------------------------------------------------------------------------------
      String file1 = ""; // 원본 파일명 image
      String file1saved = ""; // 저장된 파일명, image
      String thumb1 = ""; // preview image

      String upDir = Contents.getUploadDir(); // 파일을 업로드할 폴더 준비
      // upDir = upDir + "/" + 한글을 제외한 카테고리 이름
      System.out.println("-> upDir: " + upDir);

      // 전송 파일이 없어도 file1MF 객체가 생성됨.
      // <input type='file' class="form-control" name='file1MF' id='file1MF'
      // value='' placeholder="파일 선택">
      MultipartFile mf = contentsVO.getFile1MF();

      file1 = mf.getOriginalFilename(); // 원본 파일명 산출, 01.jpg
      System.out.println("-> 원본 파일명 산출 file1: " + file1);

      long size1 = mf.getSize(); // 파일 크기
      if (size1 > 0) { // 파일 크기 체크, 파일을 올리는 경우
        if (Tool.checkUploadFile(file1) == true) { // 업로드 가능한 파일인지 검사
          // 파일 저장 후 업로드된 파일명이 리턴됨, spring.jsp, spring_1.jpg, spring_2.jpg...
          file1saved = Upload.saveFileSpring(mf, upDir);

          if (Tool.isImage(file1saved)) { // 이미지인지 검사
            // thumb 이미지 생성후 파일명 리턴됨, width: 200, height: 150
            thumb1 = Tool.preview(upDir, file1saved, 200, 150);
          }

          contentsVO.setFile1(file1); // 순수 원본 파일명
          contentsVO.setFile1saved(file1saved); // 저장된 파일명(파일명 중복 처리)
          contentsVO.setThumb1(thumb1); // 원본이미지 축소판
          contentsVO.setSize1(size1); // 파일 크기


        } else { // 전송 못하는 파일 형식
          ra.addFlashAttribute("code", "check_upload_file_fail"); // 업로드 할 수 없는 파일
          ra.addFlashAttribute("cnt", 0); // 업로드 실패
          ra.addFlashAttribute("url", "/contents/msg"); // msg.html, redirect parameter 적용
          return "redirect:/contents/msg"; // Post -> Get - param...
        }
      } else { // 글만 등록하는 경우
        System.out.println("-> 글만 등록");
      }

      // ------------------------------------------------------------------------------
      // 파일 전송 코드 종료
      // ------------------------------------------------------------------------------

      // Call By Reference: 메모리 공유, Hashcode 전달
      int userno = (int) session.getAttribute("userno"); // userno FK
      contentsVO.setUserno(userno);
      // ------------------------------------------------------------------------------
      // FastAPI Langchain 호출 처리
      // ------------------------------------------------------------------------------
      
      // ------------------------------------------------------------------------------
      int cnt = this.contentsProc.create(contentsVO);

      // ------------------------------------------------------------------------------
      // PK의 return
      // ------------------------------------------------------------------------------
      // System.out.println("--> contentsno: " + contentsVO.getContentsno());
      // mav.addObject("contentsno", contentsVO.getContentsno()); // redirect
      // parameter 적용
      // ------------------------------------------------------------------------------

      if (cnt == 1) {
        // 등록 성공한 경우, cnt 갱신 로직 추가
        int categono = contentsVO.getCategono();
        this.categoProc.updateCntByCategono(categono); // 중분류 cnt 갱신

        String name = this.categoProc.read(categono).getName();
        this.categoProc.updateCntByName(name);         // 대분류 cnt 갱신

        ra.addAttribute("categono", contentsVO.getCategono());
        return "redirect:/contents/list_by_categono";
      } else {
        ra.addFlashAttribute("code", Tool.CREATE_FAIL); // DBMS 등록 실패
        ra.addFlashAttribute("cnt", 0); // 업로드 실패
        ra.addFlashAttribute("url", "/contents/msg"); // msg.html, redirect parameter 적용
        return "redirect:/contents/msg"; // Post -> Get - param...
      }
    } else { // 로그인 실패 한 경우
      // /users/login_cookie_need.html
      return "redirect:/users/login_cookie_need?url=/contents/create?categono=" + contentsVO.getCategono();
    }
  }

  /**
   * 전체 목록, 관리자만 사용 가능 http://localhost:9091/contents/list_all
   * 
   * @return
   */
  @GetMapping(value = "/list_all")
  public String list_all(HttpSession session, Model model) {
    // System.out.println("-> list_all");
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);

    if (this.usersProc.isAdmin(session)) { // 관리자만 조회 가능
      ArrayList<ContentsVO> list = this.contentsProc.list_all(); // 모든 목록
      
      

      // Thymeleaf는 CSRF(크로스사이트) 스크립팅 해킹 방지 자동 지원
      // for문을 사용하여 객체를 추출, Call By Reference 기반의 원본 객체 값 변경
//      for (ContentsVO contentsVO : list) {
//        String title = contentsVO.getTitle();
//        String content = contentsVO.getContent();
//        
//        title = Tool.convertChar(title);  // 특수 문자 처리
//        content = Tool.convertChar(content); 
//        
//        contentsVO.setTitle(title);
//        contentsVO.setContent(content);  
//
//      }

      model.addAttribute("list", list);
      return "contents/list_all";

    } else {
      return "redirect:/users/login_cookie_need";

    }

  }

//  /**
//   * 유형 1
//   * 카테고리별 목록
//   * http://localhost:9091/contents/list_by_categono?categono=5
//   * http://localhost:9091/contents/list_by_categono?categono=6 
//   * @return
//   */
//  @GetMapping(value="/list_by_categono")
//  public String list_by_categono(HttpSession session, Model model, 
//                                     @RequestParam(name="categono", defaultValue="") int categono) {
//    
//    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
//    model.addAttribute("menu", menu);
//    
//     CategoVO categoVO = this.categoProc.read(categono);
//     model.addAttribute("categoVO", categoVO);
//    
//    ArrayList<ContentsVO> list = this.contentsProc.list_by_categono(categono);
//    model.addAttribute("list", list);
//    
//    // System.out.println("-> size: " + list.size());
//
//    return "contents/list_by_categono"; // /templates/contents/list_by_categono
//  }

//  /**
//   * 유형 2
//   * 카테고리별 목록 + 검색
//   * http://localhost:9091/contents/list_by_categono?categono=5
//   * http://localhost:9091/contents/list_by_categono?categono=6 
//   * @return
//   */
//  @GetMapping(value="/list_by_categono")
//  public String list_by_categono_search(HttpSession session, Model model, 
//                                                    int categono, @RequestParam(name="word", defaultValue = "") String word) {
//    ArrayList<categoVOMenu> menu = this.categoProc.menu();
//    model.addAttribute("menu", menu);
//    
//     categoVO categoVO = this.categoProc.read(categono);
//     model.addAttribute("categoVO", categoVO);
//    
//     word = Tool.checkNull(word).trim();
//     
//     HashMap<String, Object> map = new HashMap<>();
//     map.put("categono", categono);
//     map.put("word", word);
//     
//    ArrayList<ContentsVO> list = this.contentsProc.list_by_categono_search(map);
//    model.addAttribute("list", list);
//    
//    // System.out.println("-> size: " + list.size());
//    model.addAttribute("word", word);
//    
//    int search_count = this.contentsProc.list_by_categono_search_count(map);
//    model.addAttribute("search_count", search_count);
//    
//    return "contents/list_by_categono_search"; // /templates/contents/list_by_categono_search.html
//  }

  /**
   * 유형 3
   * 카테고리별 목록 + 검색 + 페이징 http://localhost:9091/contents/list_by_categono?categono=5
   * http://localhost:9091/contents/list_by_categono?categono=6
   * 
   * @return
   */
  @GetMapping(value = "/list_by_categono")
  public String list_by_categono_search_paging(HttpSession session, Model model,
                                                          @RequestParam(name = "categono", defaultValue = "0") int categono,
                                                          @RequestParam(name = "word", defaultValue = "") String word,
                                                          @RequestParam(name = "now_page", defaultValue = "1") int now_page) {

    // System.out.println("-> categono: " + categono);

    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);

    CategoVO categoVO = this.categoProc.read(categono);
    model.addAttribute("categoVO", categoVO);
    System.out.println("categoVO" + categoVO);

    word = Tool.checkNull(word).trim();
    HashMap<String, Object> map = new HashMap<>();
    map.put("categono", categono);
    map.put("word", word);
    map.put("now_page", now_page);

    ArrayList<ContentsVO> list = this.contentsProc.list_by_categono_search_paging(map);
    model.addAttribute("list", list);

    // System.out.println("-> size: " + list.size());
    model.addAttribute("word", word);

    int search_count = this.contentsProc.list_by_categono_search_count(map);
    String paging = this.contentsProc.pagingBox(categono, now_page, word, "/contents/list_by_categono", search_count,
        Contents.RECORD_PER_PAGE, Contents.PAGE_PER_BLOCK);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);

    model.addAttribute("search_count", search_count);

    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * Contents.RECORD_PER_PAGE);
    model.addAttribute("no", no);

    return "contents/list_by_categono_search_paging"; // /templates/contents/list_by_categono_search_paging.html
  }

  /**
   * 카테고리별 목록 + 검색 + 페이징 + Grid
   * http://localhost:9091/contents/list_by_categono?categono=5
   * http://localhost:9091/contents/list_by_categono?categono=6
   * 
   * @return
   */
  @GetMapping(value = "/list_by_categono_grid")
  public String list_by_categono_search_paging_grid(HttpSession session, Model model,
                                                                @RequestParam(name = "categono", defaultValue = "0") int categono,
                                                                @RequestParam(name = "word", defaultValue = "") String word,
                                                                @RequestParam(name = "now_page", defaultValue = "1") int now_page) {

    // System.out.println("-> categono: " + categono);

    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);

    CategoVO categoVO = this.categoProc.read(categono);
    model.addAttribute("categoVO", categoVO);

    word = Tool.checkNull(word).trim();

    HashMap<String, Object> map = new HashMap<>();
    map.put("categono", categono);
    map.put("word", word);
    map.put("now_page", now_page);

    ArrayList<ContentsVO> list = this.contentsProc.list_by_categono_search_paging(map);
    model.addAttribute("list", list);
    

    // System.out.println("-> size: " + list.size());
    model.addAttribute("word", word);

    int search_count = this.contentsProc.list_by_categono_search_count(map);
    String paging = this.contentsProc.pagingBox(categono, now_page, word, "/contents/list_by_categono_grid", search_count,
        Contents.RECORD_PER_PAGE, Contents.PAGE_PER_BLOCK);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);

    model.addAttribute("search_count", search_count);

    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * Contents.RECORD_PER_PAGE);
    model.addAttribute("no", no);

    // /templates/contents/list_by_categono_search_paging_grid.html
    return "contents/list_by_categono_search_paging_grid";
  }

//  /**
//   * 조회 http://localhost:9091/contents/read?contentsno=17
//   * 
//   * @return
//   */
//  @GetMapping(value = "/read")
//  public String read(Model model, 
//                         @RequestParam(name="contentsno", defaultValue="0")int contentsno,
//                         @RequestParam(name="word", defaultValue="") String word, 
//                         @RequestParam(name="now_page", defaultValue="0")int now_page) { // int categono =
//                                                                               // (int)request.getParameter("categono");
//    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
//    model.addAttribute("menu", menu);
//
//    ContentsVO contentsVO = this.contentsProc.read(contentsno);
//
////    String title = contentsVO.getTitle();
////    String content = contentsVO.getContent();
////    
////    title = Tool.convertChar(title);  // 특수 문자 처리
////    content = Tool.convertChar(content); 
////    
////    contentsVO.setTitle(title);
////    contentsVO.setContent(content);  
//
//    long size1 = contentsVO.getSize1();
//    String size1_label = Tool.unit(size1);
//    contentsVO.setSize1_label(size1_label);
//
//    model.addAttribute("contentsVO", contentsVO);
//
//    CategoVO categoVO = this.categoProc.read(contentsVO.getCategono());
//    model.addAttribute("categoVO", categoVO);
//
//    // 조회에서 화면 하단에 출력
//    // ArrayList<ReplyVO> reply_list = this.replyProc.list_contents(contentsno);
//    // mav.addObject("reply_list", reply_list);
//
//    model.addAttribute("word", word);
//    model.addAttribute("now_page", now_page);
//
//    return "contents/read";
//  }
  
  /**
   * 조회 http://localhost:9091/contents/read?contentsno=17
   * 
   * @return
   */
  @GetMapping(value = "/read")
  public String read(HttpSession session, Model model, 
      @RequestParam(name="contentsno", defaultValue = "0") int contentsno, 
      @RequestParam(name="word", defaultValue = "") String word, 
      @RequestParam(name="now_page", defaultValue = "1") int now_page) {
    
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);

    ContentsVO contentsVO = this.contentsProc.read(contentsno);

//    String title = contentsVO.getTitle();
//    String content = contentsVO.getContent();
//    
//    title = Tool.convertChar(title);  // 특수 문자 처리
//    content = Tool.convertChar(content); 
//    
//    contentsVO.setTitle(title);
//    contentsVO.setContent(content);  

    long size1 = contentsVO.getSize1();
    String size1_label = Tool.unit(size1);
    contentsVO.setSize1_label(size1_label);

    model.addAttribute("contentsVO", contentsVO);

    CategoVO categoVO = this.categoProc.read(contentsVO.getCategono());
    model.addAttribute("categoVO", categoVO);

    // 조회에서 화면 하단에 출력
    // ArrayList<ReplyVO> reply_list = this.replyProc.list_contents(contentsno);
    // mav.addObject("reply_list", reply_list);

    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    
    // -------------------------------------------------------------------------------------------
    // 추천 관련
    // -------------------------------------------------------------------------------------------
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("contentsno", contentsno);
    
    int hartCnt = 0; // 로그인하지 않음 or 비회원 or 추천하지 않음
    if (session.getAttribute("userno") != null ) { // 회원인 경우만 카운트 처리
      int userno = (int)session.getAttribute("userno");
      map.put("userno", userno);
      
      hartCnt = this.contentsgoodProc.hartCnt(map);
    } 
    
    model.addAttribute("hartCnt", hartCnt);
    // -------------------------------------------------------------------------------------------
    
    return "contents/read";
  }

  /**
   * 맵 등록/수정/삭제 폼 http://localhost:9091/contents/map?contentsno=1
   * 
   * @return
   */
  @GetMapping(value = "/map")
  public String map(Model model, 
                         @RequestParam(name="contentsno", defaultValue="0") int contentsno) {
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);

    ContentsVO contentsVO = this.contentsProc.read(contentsno); // map 정보 읽어 오기
    model.addAttribute("contentsVO", contentsVO); // request.setAttribute("contentsVO", contentsVO);

    CategoVO categoVO = this.categoProc.read(contentsVO.getCategono()); // 그룹 정보 읽기
    model.addAttribute("categoVO", categoVO);

    return "contents/map";
  }

  /**
   * MAP 등록/수정/삭제 처리 http://localhost:9091/contents/map
   * 
   * @param contentsVO
   * @return
   */
  @PostMapping(value = "/map")
  public String map_update(Model model, 
                                   @RequestParam(name="contentsno", defaultValue="0") int contentsno, 
                                   @RequestParam(name="map", defaultValue="") String map) {
    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    hashMap.put("contentsno", contentsno);
    hashMap.put("map", map);

    this.contentsProc.map(hashMap);

    return "redirect:/contents/read?contentsno=" + contentsno;
  }

  /**
   * Youtube 등록/수정/삭제 폼 http://localhost:9091/contents/youtube?contentsno=1
   * 
   * @return
   */
  @GetMapping(value = "/youtube")
  public String youtube(Model model, 
                              @RequestParam(name="contentsno", defaultValue="0") int contentsno, 
                              @RequestParam(name="word", defaultValue="") String word, 
                              @RequestParam(name="now_page", defaultValue="0") int now_page) {
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);

    ContentsVO contentsVO = this.contentsProc.read(contentsno); // map 정보 읽어 오기
    model.addAttribute("contentsVO", contentsVO); // request.setAttribute("contentsVO", contentsVO);

    CategoVO categoVO = this.categoProc.read(contentsVO.getCategono()); // 그룹 정보 읽기
    model.addAttribute("categoVO", categoVO);

    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    
    return "contents/youtube";  // forward
  }

  /**
   * Youtube 등록/수정/삭제 처리 http://localhost:9091/contents/youtube
   * 
   * @param contentsVO
   * @return
   */
  @PostMapping(value = "/youtube")
  public String youtube_update(Model model, 
                                             RedirectAttributes ra,
                                             @RequestParam(name="contentsno", defaultValue="0") int contentsno, 
                                             @RequestParam(name="youtube", defaultValue="") String youtube, 
                                             @RequestParam(name="word", defaultValue="") String word, 
                                             @RequestParam(name="now_page", defaultValue="0") int now_page) {

    if (youtube.trim().length() > 0) { // 삭제 중인지 확인, 삭제가 아니면 youtube 크기 변경
      youtube = Tool.youtubeResize(youtube, 640); // youtube 영상의 크기를 width 기준 640 px로 변경
    }

    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    hashMap.put("contentsno", contentsno);
    hashMap.put("youtube", youtube);

    this.contentsProc.youtube(hashMap);
    
    ra.addAttribute("contentsno", contentsno);
    ra.addAttribute("word", word);
    ra.addAttribute("now_page", now_page);

    // return "redirect:/contents/read?contentsno=" + contentsno;
    return "redirect:/contents/read";
  }

  /**
   * 수정 폼 http:// localhost:9091/contents/update_text?contentsno=1
   *
   */
  @GetMapping(value = "/update_text")
  public String update_text(HttpSession session, Model model, 
                                  RedirectAttributes ra,
                                  @RequestParam(name="contentsno", defaultValue="0") int contentsno,
                                  @RequestParam(name="word", defaultValue="") String word,
                                  @RequestParam(name="now_page", defaultValue="0") int now_page) {
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);

    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);

    if (this.usersProc.isAdmin(session)) { // 관리자로 로그인한경우
      ContentsVO contentsVO = this.contentsProc.read(contentsno);
      model.addAttribute("contentsVO", contentsVO);

      CategoVO categoVO = this.categoProc.read(contentsVO.getCategono());
      model.addAttribute("categoVO", categoVO);

      return "contents/update_text"; // /templates/contents/update_text.html
      // String content = "장소:\n인원:\n준비물:\n비용:\n기타:\n";
      // model.addAttribute("content", content);

    } else {
      // ra.addAttribute("url", "/users/login_cookie_need"); // /templates/users/login_cookie_need.html
      return "redirect:/users/login_cookie_need?url=/contents/update_text?contentsno=" + contentsno;
    }

  }

  /**
   * 수정 처리 http://localhost:9091/contents/update_text?contentsno=1
   * 
   * @return
   */
  @PostMapping(value = "/update_text")
  public String update_text_proc(HttpSession session, Model model,ContentsVO contentsVO, 
                                  RedirectAttributes ra,
                                  @RequestParam(name="search_word", defaultValue="") String search_word, // contentsVO.word와 구분 필요
                                  @RequestParam(name="now_page", defaultValue="0") int now_page) {
    ra.addAttribute("word", search_word);
    ra.addAttribute("now_page", now_page);

    if (this.usersProc.isAdmin(session)) { // 관리자 로그인 확인
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("contentsno", contentsVO.getContentsno());
      map.put("passwd", contentsVO.getPasswd());

      if (this.contentsProc.password_check(map) == 1) { // 패스워드 일치
        this.contentsProc.update_text(contentsVO); // 글수정

        // mav 객체 이용
        ra.addAttribute("contentsno", contentsVO.getContentsno());
        ra.addAttribute("categono", contentsVO.getCategono());
        return "redirect:/contents/read"; // @GetMapping(value = "/read")

      } else { // 패스워드 불일치
        ra.addFlashAttribute("code", Tool.PASSWORD_FAIL); // redirect -> forward -> html
        ra.addFlashAttribute("cnt", 0);
        ra.addAttribute("url", "/contents/msg"); // msg.html, redirect parameter 적용

        return "redirect:/contents/post2get"; // @GetMapping(value = "/msg")
      }
    } else { // 정상적인 로그인이 아닌 경우 로그인 유도
      // 로그인 안함 -> // 로그인 안함 -> http://localhost:9092/contents/update_text?contentsno=32&now_page=1&word=
      return "redirect:/users/login_cookie_need?url=/contents/update_text?contentsno=" + contentsVO.getContentsno();
    }

  }

  /**
   * 파일 수정 폼 http://localhost:9091/contents/update_file?contentsno=1
   * 
   * @return
   */
  @GetMapping(value = "/update_file")
  public String update_file(HttpSession session, Model model, 
                                 @RequestParam(name="contentsno", defaultValue="0") int contentsno,
                                 @RequestParam(name="word", defaultValue="") String word, 
                                 @RequestParam(name="now_page", defaultValue="0") int now_page) {
    if (this.usersProc.isAdmin(session)) { // 관리자로 로그인한 경우
    ArrayList<CategoVOMenu> menu = this.categoProc.menu();
    model.addAttribute("menu", menu);
    
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);
    
    ContentsVO contentsVO = this.contentsProc.read(contentsno);
    model.addAttribute("contentsVO", contentsVO);

    CategoVO categoVO = this.categoProc.read(contentsVO.getCategono());
    model.addAttribute("categoVO", categoVO);

    return "contents/update_file";
    } else {
      // 로그인 후 파일 수정폼이 자동으로 열림
      return "redirect:/users/login_cookie_need?url=/contents/update_text?contentsno=" + contentsno;
    }

  }

  /**
   * 파일 수정 처리 http://localhost:9091/contents/update_file
   * 
   * @return
   */
  @PostMapping(value = "/update_file")
  public String update_file(HttpSession session, Model model, RedirectAttributes ra,
                                      ContentsVO contentsVO,
                                      @RequestParam(name="word", defaultValue="") String word, 
                                      @RequestParam(name="now_page", defaultValue="0") int now_page) {

    if (this.usersProc.isAdmin(session)) {
      // 삭제할 파일 정보를 읽어옴, 기존에 등록된 레코드 저장용
      ContentsVO contentsVO_old = contentsProc.read(contentsVO.getContentsno());

      // -------------------------------------------------------------------
      // 파일 삭제 시작
      // -------------------------------------------------------------------
      String file1saved = contentsVO_old.getFile1saved(); // 실제 저장된 파일명
      String thumb1 = contentsVO_old.getThumb1(); // 실제 저장된 preview 이미지 파일명
      long size1 = 0;

      String upDir = Contents.getUploadDir(); // C:/kd/deploy/resort_v4sbm3c/contents/storage/

      Tool.deleteFile(upDir, file1saved); // 실제 저장된 파일삭제
      Tool.deleteFile(upDir, thumb1); // preview 이미지 삭제
      // -------------------------------------------------------------------
      // 파일 삭제 종료
      // -------------------------------------------------------------------

      // -------------------------------------------------------------------
      // 파일 전송 시작
      // -------------------------------------------------------------------
      String file1 = ""; // 원본 파일명 image

      // 전송 파일이 없어도 file1MF 객체가 생성됨.
      // <input type='file' class="form-control" name='file1MF' id='file1MF'
      // value='' placeholder="파일 선택">
      MultipartFile mf = contentsVO.getFile1MF();

      file1 = mf.getOriginalFilename(); // 원본 파일명
      size1 = mf.getSize(); // 파일 크기

      if (size1 > 0) { // 폼에서 새롭게 올리는 파일이 있는지 파일 크기로 체크 ★
        // 파일 저장 후 업로드된 파일명이 리턴됨, spring.jsp, spring_1.jpg...
        file1saved = Upload.saveFileSpring(mf, upDir);

        if (Tool.isImage(file1saved)) { // 이미지인지 검사
          // thumb 이미지 생성후 파일명 리턴됨, width: 250, height: 200
          thumb1 = Tool.preview(upDir, file1saved, 250, 200);
        }

      } else { // 파일이 삭제만 되고 새로 올리지 않는 경우
        file1 = "";
        file1saved = "";
        thumb1 = "";
        size1 = 0;
      }

      contentsVO.setFile1(file1);
      contentsVO.setFile1saved(file1saved);
      contentsVO.setThumb1(thumb1);
      contentsVO.setSize1(size1);
      // -------------------------------------------------------------------
      // 파일 전송 코드 종료
      // -------------------------------------------------------------------

      this.contentsProc.update_file(contentsVO); // Oracle 처리
      ra.addAttribute ("contentsno", contentsVO.getContentsno());
      ra.addAttribute("categono", contentsVO.getCategono());
      ra.addAttribute("word", word);
      ra.addAttribute("now_page", now_page);
      
      return "redirect:/contents/read";
    } else {
      ra.addAttribute("url", "/users/login_cookie_need"); 
      return "redirect:/contents/post2get"; // GET
    }
  }

  /**
   * 파일 삭제 폼
   * http://localhost:9091/contents/delete?contentsno=1
   * 
   * @return
   */
  @GetMapping(value = "/delete")
  public String delete(HttpSession session, Model model, RedirectAttributes ra,
                           @RequestParam(name="categono", defaultValue="0") int categono, 
                           @RequestParam(name="contentsno", defaultValue="0") int contentsno,
                           @RequestParam(name="word", defaultValue="") String word, 
                           @RequestParam(name="now_page", defaultValue="0") int now_page) {
    if (this.usersProc.isAdmin(session)) { // 관리자로 로그인한경우
      model.addAttribute("categono", categono);
      model.addAttribute("word", word);
      model.addAttribute("now_page", now_page);
      
      ArrayList<CategoVOMenu> menu = this.categoProc.menu();
      model.addAttribute("menu", menu);
      
      ContentsVO contentsVO = this.contentsProc.read(contentsno);
      model.addAttribute("contentsVO", contentsVO);
      
      CategoVO categoVO = this.categoProc.read(contentsVO.getCategono());
      model.addAttribute("categoVO", categoVO);
      
      return "contents/delete"; // forward
      
    } else {
      return "redirect:/users/login_cookie_need?url=/contents/update_text?contentsno=" + contentsno;
    }

  }
  
  /**
   * 삭제 처리 http://localhost:9091/contents/delete
   * 
   * @return
   */
  @PostMapping(value = "/delete")
  public String delete(RedirectAttributes ra,
                            @RequestParam(name="categono", defaultValue="0") int categono, 
                            @RequestParam(name="contentsno", defaultValue="0") int contentsno,
                            @RequestParam(name="word", defaultValue="") String word, 
                            @RequestParam(name="now_page", defaultValue="0") int now_page) {
    // -------------------------------------------------------------------
    // 파일 삭제 시작
    // -------------------------------------------------------------------
    // 삭제할 파일 정보를 읽어옴.
    ContentsVO contentsVO_read = contentsProc.read(contentsno);
        
    String file1saved = contentsVO_read.getFile1saved();
    String thumb1 = contentsVO_read.getThumb1();
    
    String uploadDir = Contents.getUploadDir();
    Tool.deleteFile(uploadDir, file1saved);  // 실제 저장된 파일삭제
    Tool.deleteFile(uploadDir, thumb1);     // preview 이미지 삭제
    // -------------------------------------------------------------------
    // 파일 삭제 종료
    // -------------------------------------------------------------------
//    this.contentsProc.delete_children(contentsno); // 자식 먼저 삭제
    
    this.contentsProc.delete(contentsno); // DBMS 삭제

    this.categoProc.updateCntByCategono(categono); // 중분류 cnt 갱신
    String name = this.categoProc.read(categono).getName();
    this.categoProc.updateCntByName(name);         // 대분류 cnt 갱신

        
    // -------------------------------------------------------------------------------------
    // 마지막 페이지의 마지막 레코드 삭제시의 페이지 번호 -1 처리
    // -------------------------------------------------------------------------------------    
    // 마지막 페이지의 마지막 10번째 레코드를 삭제후
    // 하나의 페이지가 3개의 레코드로 구성되는 경우 현재 9개의 레코드가 남아 있으면
    // 페이지수를 4 -> 3으로 감소 시켜야함, 마지막 페이지의 마지막 레코드 삭제시 나머지는 0 발생
    
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("categono", categono);
    map.put("word", word);
    
    if (this.contentsProc.list_by_categono_search_count(map) % Contents.RECORD_PER_PAGE == 0) {
      now_page = now_page - 1; // 삭제시 DBMS는 바로 적용되나 크롬은 새로고침등의 필요로 단계가 작동 해야함.
      if (now_page < 1) {
        now_page = 1; // 시작 페이지
      }
    }
    // -------------------------------------------------------------------------------------

    ra.addAttribute("categono", categono);
    ra.addAttribute("word", word);
    ra.addAttribute("now_page", now_page);
    
    return "redirect:/contents/list_by_categono";    
    
  }   
  
  /**
   * 추천 처리 http://localhost:9092/contents/good
   * 
   * @return
   */
  @PostMapping(value = "/good")
  @ResponseBody
  public String good(HttpSession session, Model model, @RequestBody String json_src){ 
    System.out.println("-> json_src: " + json_src); // json_src: {"contentsno":"5"}
    
    JSONObject src = new JSONObject(json_src); // String -> JSON
    int contentsno = (int)src.get("contentsno"); // 값 가져오기
    System.out.println("-> contentsno: " + contentsno);
        
    System.out.println(this.usersProc.isUsers(session));
    if (this.usersProc.isUsers(session)) { // 회원 로그인 확인
      // 추천을 한 상태인지 확인
      int userno = (int)session.getAttribute("userno");
      
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("contentsno", contentsno);
      map.put("userno", userno);
      
      int good_cnt = this.contentsgoodProc.hartCnt(map);
      System.out.println("-> good_cnt: " + good_cnt);
      
      if (good_cnt == 1) { // 이미지 추천을 한 회원인지 검사
        System.out.println("-> 추천 해제: " + contentsno + ' ' + userno);
        
        // Contentsgood 테이블에서 추천한 기록을 찾음
        ContentsgoodVO contentsgoodVO = this.contentsgoodProc.readByContentsnoUserno(map);
        
        this.contentsgoodProc.delete(contentsgoodVO.getContentsgoodno()); // 추천 삭제
        this.contentsProc.decreaseRecom(contentsno); // 카운트 감소
      } else {
        System.out.println("-> 추천: " + contentsno + ' ' + userno);
        
        ContentsgoodVO contentsgoodVO_new = new ContentsgoodVO();
        contentsgoodVO_new.setContentsno(contentsno);
        contentsgoodVO_new.setUserno(userno);
        
        this.contentsgoodProc.create(contentsgoodVO_new);
        this.contentsProc.increaseRecom(contentsno); // 카운트 증가
      }
      
      // 추천 여부가 변경되어 다시 새로운 값을 읽어옴.
      int hartCnt = this.contentsgoodProc.hartCnt(map);
      int recom = this.contentsProc.read(contentsno).getRecom();
            
      JSONObject result = new JSONObject();
      result.put("isUsers", 1); // 로그인: 1, 비회원: 0
      result.put("hartCnt", hartCnt); // 추천 여부, 추천:1, 비추천: 0
      result.put("recom", recom);   // 추천인수
      
      System.out.println("-> result.toString(): " + result.toString());
      return result.toString();
      
    } else { // 정상적인 로그인이 아닌 경우 로그인 유도
      JSONObject result = new JSONObject();
      result.put("isUsers", 0); // 로그인: 1, 비회원: 0
      
      System.out.println("-> result.toString(): " + result.toString());
      return result.toString();
    }

  }
   
  
}