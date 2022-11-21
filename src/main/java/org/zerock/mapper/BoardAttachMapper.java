package org.zerock.mapper;

import java.util.List;

import org.zerock.domain.BoardAttachVO;

public interface BoardAttachMapper {
	public void insert(BoardAttachVO vo);
	public void delete(String uuid);
//특정 게시물의 번호로 첨부파일을 찾는 작업이 필요하므로 findByBno() 메서드를 정의
	public List<BoardAttachVO> findByBno(Long bno);
}