package org.zerock.service;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.mapper.BoardAttachMapper;
import org.zerock.mapper.BoardMapper;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/*@Log4j*/
//비즈니스 영역을 담당하는 객체임을 표시하기 위해 사용
@Service
@AllArgsConstructor
public class BoardServiceImpl implements BoardService {
	
	//BoardServiceImpl이 정상적으로 동작하기 위한 BoardMapper 객체 생성
	//spring 4.3 이상에서 단일 파라미터를 받는 생성자의 겨우 필요한 자동파라미터를 자동 주입해줌
	@Setter(onMethod_= @Autowired)
	private BoardMapper mapper;
	
	@Setter(onMethod_=@Autowired)
	private BoardAttachMapper attachMapper;
	
	@Override
	public BoardVO get(Long bno) {
		System.out.println("get......" + bno);
		return mapper.read(bno);
	}
	
	/*
	 * @Override public List<BoardVO> getlist() {
	 * System.out.println("getList....."); return mapper.getList(); }
	 */
	
	@Override
	public List<BoardVO> getlist(Criteria cri) {
		System.out.println("get List with Criteria: " +cri);
		return mapper.getListWithPaging(cri);
	}
	
	@Transactional
	@Override
	public boolean modify(BoardVO board) {
		System.out.println("modify....." + board);
		
		attachMapper.deleteAll(board.getBno());
		
		boolean modifyResult = mapper.update(board) == 1;
		
		if(modifyResult && board.getAttachList() != null && board.getAttachList().size() >0) {
			
			board.getAttachList().forEach(attach -> {
				
				attach.setBno(board.getBno());
				
				attachMapper.insert(attach);
			});
		}
		
		return modifyResult;
	}
	
	@Transactional
	@Override
	//필요하다면 예외처리나 void대신 int타입을 이용해서 사용 가능
	//ex) mapper.insertSelectKey()반환 값 int 사용하려고 int 리턴하는 쪽으로 작성
	public void register(BoardVO board) {
		System.out.println("register....." + board);
		mapper.insertSelectKey(board);
		
		if(board.getAttachList() == null || board.getAttachList().size() <=0) {
			return;
		}
		
		board.getAttachList().forEach(attach -> {
			attach.setBno(board.getBno());
			attachMapper.insert(attach);
			});
	}
	
	@Transactional
	@Override
	public boolean remove(Long bno) {
		System.out.println("remove....." + bno);
		
		attachMapper.deleteAll(bno);
		
		return mapper.delete(bno) == 1;
	}
	
	@Override
	public int getTotal(Criteria cri) {
		System.out.println("get total count");
		return mapper.getTotalCount(cri);
	}
	
	@Override
	public List<BoardAttachVO> getAttachList(Long bno) {
		System.out.println("get Attach list by bno" + bno);
		return attachMapper.findByBno(bno);
	}
	
	
}
