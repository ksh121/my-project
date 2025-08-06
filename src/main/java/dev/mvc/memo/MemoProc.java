package dev.mvc.memo;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dev.mvc.memo.MemoProc")
public class MemoProc implements MemoProcInter {

  @Autowired
  private MemoDAOInter memoDAO;

  @Override
  public int create(MemoVO memoVO) {
    return memoDAO.create(memoVO);
  }

  @Override
  public ArrayList<MemoVO> list_all() {
    return memoDAO.list_all();
  }

  @Override
  public MemoVO read(int memono) {
    return memoDAO.read(memono);
  }

  @Override
  public int update(MemoVO memoVO) {
    return memoDAO.update(memoVO);
  }

  @Override
  public int delete(int memono) {
    return memoDAO.delete(memono);
  }
}
