package dev.mvc.memo;

import java.util.ArrayList;

public interface MemoProcInter {

  /**
   * 메모 작성
   * @param memoVO
   * @return 등록된 레코드 수
   */
  public int create(MemoVO memoVO);

  /**
   * 메모 전체 목록
   * @return 메모 목록
   */
  public ArrayList<MemoVO> list_all();

  /**
   * 메모 상세 조회
   * @param memono
   * @return 메모 정보
   */
  public MemoVO read(int memono);

  /**
   * 메모 수정
   * @param memoVO
   * @return 수정된 레코드 수
   */
  public int update(MemoVO memoVO);

  /**
   * 메모 삭제
   * @param memono
   * @return 삭제된 레코드 수
   */
  public int delete(int memono);
}
