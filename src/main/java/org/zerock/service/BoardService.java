package org.zerock.service;

import java.util.List;

import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;

//명백하게 반환해야 할 데이터가 있는(select를 해야하는) 메서드는 리턴 타입 지정 가능
public interface BoardService {
	public void register(BoardVO board);
	
	//처음부터 메서드의 리턴타입을 결정해서 진행 가능
	public BoardVO get(Long bno);
	
	public boolean modify(BoardVO board);
	
	public boolean remove(Long bno);
	
	//처음부터 메서드의 리턴타입을 결정해서 진행 가능
	//public List<BoardVO> getlist();
	public List<BoardVO> getlist(Criteria cri);
	
	public int getTotal(Criteria cri);
	
}