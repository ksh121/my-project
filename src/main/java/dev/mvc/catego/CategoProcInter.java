package dev.mvc.catego;

import java.util.ArrayList;
import java.util.Map;

import dev.mvc.catego.CategoVO;

public interface CategoProcInter { // 알고리즘 선언하는 곳, CateDAO 호출
  
  /**
   * 등록
   * @param categoVO
   * @return
   */
  public int create(CategoVO categoVO);
  
  /**
   * 전체 목록
   * @return
   */
  public ArrayList<CategoVO> list_all();
  
  /**
   * 조회
   * @param categono
   * @return
   */
  public CategoVO read(int categono);
  
  /**
   * 수정
   * @param categono
   * @return
   */
  public int update(CategoVO categoVO);
  
  /**
   * 삭제 처리
   * delete id="delete" parameterType="Integer"
   * @param int
   * @return 삭제된 레코드 갯수
   */
  public int delete(int categono);
  
  /**
   * 우선순위 높임 10등 -> 1등
   * @param categono
   * @return
   */
  public int update_seqno_forward(int categono);
  
  /**
   * 우선순위 낮춤 1등 -> 10등
   * @param cateno
   * @return
   */
  public int update_seqno_backward(int categono);
  
  /** 카테고리 공개 설정
   * @param categono
   * @return
   */
  public int update_visible_y(int categono);
  
  /**
   * 카테고리 비공개 설정
   * @param categono
   * @return
   */
  public int update_visible_n(int categono);
  
  /**
   * 공개된 대분류만 출력
   * @param categoVO
   * @return
   */
  public ArrayList<CategoVO> list_all_grp_y();
  
  /**
   * 특정 그룹의 중분류 출력
   * @param categoVO
   * @return
   */
  public ArrayList<CategoVO> list_all_name_y(String name);
  
  /**
   * 화면 상단 메뉴
   * @return
   */
  public ArrayList<CategoVOMenu> menu();
  
  /**
   * 카테고리 그룹 목록
   * @return
   */
  public ArrayList<String> grpset();
  
  /**
   * 검색, 전체 목록
   * @return
   */
  public ArrayList<CategoVO> list_search(String word);
  
  /**
   * 검색, 전체 레코드 갯수, 페이징 버튼 생성시 필요
   * @return
   */
  public int list_search_count(String word); 
  
  /**
   * 검색 + 페이징 목록
   * select id="list_search_paging" resultType="dev.mvc.cate.CateVO" parameterType="Map" 
   * @param word 검색어
   * @param now_page 현재 페이지, 시작 페이지 번호: 1 ★
   * @param record_per_page 페이지당 출력할 레코드 수
   * @return
   */
  public ArrayList<CategoVO> list_search_paging(String word, int now_page, int record_per_page);

  /** 
   * SPAN태그를 이용한 박스 모델의 지원, 1 페이지부터 시작 
   * 현재 페이지: 11 / 22   [이전] 11 12 13 14 15 16 17 18 19 20 [다음] 
   *
   * @param now_page  현재 페이지
   * @param word 검색어
   * @param list_url 페이지 버튼 클릭시 이동할 주소, @GetMapping(value="/list_search") 
   * @param search_count 검색 레코드수   
   * @param record_per_page 페이지당 레코드 수
   * @param page_per_block 블럭당 페이지 수
   * @return 페이징 생성 문자열
   */
  String pagingBox(int now_page, String word, String list_url, int search_count, int record_per_page,
      int page_per_block);
  
  public int updateCntByCategono(int categono);
  public int updateCntByName(String name);

  
}
