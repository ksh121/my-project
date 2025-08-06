package dev.mvc.catego;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.mvc.catego.CategoVOMenu;
import dev.mvc.catego.CategoVO;

// Service, Process, Manager: DAO 호출 및 알고리즘 구현
@Service("dev.mvc.catego.CategoProc")
public class CategoProc implements CategoProcInter {

  @Autowired
  private CategoDAOInter categoDAO;
    
  
  @Override
  public int create(CategoVO categoVO) { // 추상메소드는 중괄호가 없음 이건 일반 메소드(인스턴스 메소드)
    // TODO Auto-generated method stub
    int cnt = this.categoDAO.create(categoVO);
    return cnt;
  }
  
  @Override
  public ArrayList<CategoVO> list_all() {
    ArrayList<CategoVO> list = this.categoDAO.list_all();
    return list;
  }
  
  @Override
  public CategoVO read(int categono) {
    CategoVO categoVO = this.categoDAO.read(categono);
    return categoVO;
  }
  
  @Override
  public int update(CategoVO categoVO) {
    int cnt = this.categoDAO.update(categoVO);
    return cnt;
  }
  
  @Override
  public int delete(int categono) {
    int cnt = this.categoDAO.delete(categono);
    return cnt;
  }
  
  @Override
  public int update_seqno_forward(int categono) {
    int cnt = this.categoDAO.update_seqno_forward(categono);
    return cnt;
  }

  @Override
  public int update_seqno_backward(int categono) {
    int cnt = this.categoDAO.update_seqno_backward(categono);
    return cnt;
  }
  
  @Override
  public int update_visible_y(int categono) {
    int cnt = this.categoDAO.update_visible_y(categono);
    return cnt;
  }
  
  @Override
  public int update_visible_n(int categono) {
    int cnt = this.categoDAO.update_visible_n(categono);
    return cnt;
  }
  
  @Override
  public ArrayList<CategoVO> list_all_grp_y() {
    ArrayList<CategoVO> list = this.categoDAO.list_all_grp_y();
    return list;
  }
  
  @Override
  public ArrayList<CategoVO> list_all_name_y(String name) {
    ArrayList<CategoVO> list = this.categoDAO.list_all_name_y(name);
    return list;
  }
  
  @Override
  public ArrayList<CategoVOMenu> menu() {
    ArrayList<CategoVOMenu> menu = new ArrayList<CategoVOMenu>();
    ArrayList<CategoVO> grps = this.categoDAO.list_all_grp_y(); // 대분류 목록
    
    for(CategoVO categoVO:grps) {
      CategoVOMenu categoVOMenu = new CategoVOMenu();
     categoVOMenu.setName(categoVO.getName()); // 대분류 이름
     
     // 특정 대분류의 해당하는 중분류 추출
     ArrayList<CategoVO> list_name = this.categoDAO.list_all_name_y(categoVO.getName());
     categoVOMenu.setList_name(list_name);
     
     menu.add(categoVOMenu); // 하나의 그룹에 해당하는 중분류메뉴 객체 저장
    }
    
    return menu;
  }
  
  @Override
  public ArrayList<String> grpset() {
    ArrayList<String> grpset = this.categoDAO.grpset();    
    return grpset;
  }
  
  @Override
  public ArrayList<CategoVO> list_search(String word) {
    ArrayList<CategoVO> list = this.categoDAO.list_search(word);
    return list;
  }
  
  @Override
  public int list_search_count(String word) {
    int cnt = this.categoDAO.list_search_count(word);
    return cnt;
  }
  
  @Override
  public ArrayList<CategoVO> list_search_paging(String word, int now_page, int record_per_page) {
      int start_num = ((now_page - 1) * record_per_page) + 1;
      int end_num = (start_num + record_per_page) - 1;

      System.out.println("WHERE r >= " + start_num + " AND r <= " + end_num);

      Map<String, Object> map = new HashMap<>();
      map.put("word", word);
      map.put("start_num", start_num);
      map.put("end_num", end_num);

      // cnt 값을 포함하여 리스트를 가져옴
      ArrayList<CategoVO> list = this.categoDAO.list_search_paging(map);

      // 리스트의 cnt 값을 추가적으로 처리할 필요가 있으면 여기에서 추가 처리 가능
      for (CategoVO vo : list) {
          System.out.println("cnt: " + vo.getCnt());  // cnt 값을 출력
      }

      return list;
  }

  /** 
   * SPAN태그를 이용한 박스 모델의 지원, 1 페이지부터 시작 
   * 현재 페이지: 11 / 22   [이전] 11 12 13 14 15 16 17 18 19 20 [다음] 
   *
   * @param now_page  현재 페이지
   * @param word 검색어
   * @param list_url 목록 파일명, @GetMapping(value="/list_search")타임리프아님)
   * @param search_count 검색 레코드수   
   * @param record_per_page 페이지당 레코드 수
   * @param page_per_block 블럭당 페이지 수
   * @return 페이징 생성 문자열
   */ 
  @Override
  public String pagingBox(int now_page, String word, String list_url, int search_count, 
                                      int record_per_page, int page_per_block){    
    // 전체 페이지 수: (double)1/10 -> 0.1 -> 1 페이지, (double)12/10 -> 1.2 페이지 -> 2 페이지
    int total_page = (int)(Math.ceil((double)search_count / record_per_page)); // ceil : 올림 함수
    // 전체 그룹  수: (double)1/10 -> 0.1 -> 1 그룹, (double)12/10 -> 1.2 그룹-> 2 그룹
    int total_grp = (int)(Math.ceil((double)total_page / page_per_block)); 
    // 현재 그룹 번호: (double)13/10 -> 1.3 -> 2 그룹
    int now_grp = (int)(Math.ceil((double)now_page / page_per_block));  
    
    // 1 group: 1, 2, 3 ... 9, 10
    // 2 group: 11, 12 ... 19, 20
    // 3 group: 21, 22 ... 29, 30
    int start_page = ((now_grp - 1) * page_per_block) + 1; // 특정 그룹의 시작 페이지  
    int end_page = (now_grp * page_per_block);               // 특정 그룹의 마지막 페이지   
     
    StringBuffer str = new StringBuffer(); // String class 보다 문자열 추가등의 편집시 속도가 빠름 
    str.append("<div id='paging'>"); 
    // style이 java 파일에 명시되는 경우는 로직에 따라 css가 영향을 많이 받는 경우에 사용하는 방법
    
    
//    str.append("현재 페이지: " + nowPage + " / " + totalPage + "  "); 
 
    // 이전 10개 페이지로 이동
    // now_grp: 1 (1 ~ 10 page)
    // now_grp: 2 (11 ~ 20 page)
    // now_grp: 3 (21 ~ 30 page) 
    // 현재 2그룹일 경우: (2 - 1) * 10 = 1그룹의 마지막 페이지 10
    // 현재 3그룹일 경우: (3 - 1) * 10 = 2그룹의 마지막 페이지 20
    int _now_page = (now_grp - 1) * page_per_block;  
    if (now_grp >= 2){ // 현재 그룹번호가 2이상이면 페이지수가 11페이지 이상임으로 이전 그룹으로 갈수 있는 링크 생성 
      str.append("<span class='span_box_1'><a href='"+list_url+"?&word="+word+"&now_page="+_now_page+"'>이전</a></span>"); 
    } 
 
    // 중앙의 페이지 목록
    for(int i=start_page; i<=end_page; i++){ 
      if (i > total_page){ // 마지막 페이지를 넘어갔다면 페이 출력 종료
        break; 
      } 
  
      if (now_page == i){ // 목록에 출력하는 페이지가 현재페이지와 같다면 CSS 강조(차별을 둠)
        str.append("<span class='span_box_2'>"+i+"</span>"); // 현재 페이지, 강조 
      }else{
        // 현재 페이지가 아닌 페이지는 이동이 가능하도록 링크를 설정
        str.append("<span class='span_box_1'><a href='"+list_url+"?word="+word+"&now_page="+i+"'>"+i+"</a></span>");   
      } 
    } 
 
    // 10개 다음 페이지로 이동
    // nowGrp: 1 (1 ~ 10 page),  nowGrp: 2 (11 ~ 20 page),  nowGrp: 3 (21 ~ 30 page) 
    // 현재 페이지 5일경우 -> 현재 1그룹: (1 * 10) + 1 = 2그룹의 시작페이지 11
    // 현재 페이지 15일경우 -> 현재 2그룹: (2 * 10) + 1 = 3그룹의 시작페이지 21
    // 현재 페이지 25일경우 -> 현재 3그룹: (3 * 10) + 1 = 4그룹의 시작페이지 31
    _now_page = (now_grp * page_per_block)+1; //  최대 페이지수 + 1 
    if (now_grp < total_grp){ 
      str.append("<span class='span_box_1'><a href='"+list_url+"?&word="+word+"&now_page="+_now_page+"'>다음</a></span>"); 
    } 
    str.append("</div>"); 
     
    return str.toString(); 
  }
  
  @Override
  public int updateCntByCategono(int categono) {
    return this.categoDAO.updateCntByCategono(categono);
  }

  @Override
  public int updateCntByName(String name) {
    return this.categoDAO.updateCntByName(name);
  }

  

}
