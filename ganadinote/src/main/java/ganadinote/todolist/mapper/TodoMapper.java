package ganadinote.todolist.mapper;

// [수정된 부분] TodoDTO를 'common'이 아닌 'domain' 패키지에서 가져오도록 변경합니다.
import ganadinote.todolist.domain.TodoDTO; 
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper // Spring Boot가 이 인터페이스를 Mybatis Mapper로 인식하게 하는 어노테이션입니다.
public interface TodoMapper {

    /**
     * 특정 회원의 모든 할 일 목록을 조회합니다.
     * @param mbrCd 조회할 회원의 ID
     * @return 할 일 목록 (List<TodoDTO>)
     */
    List<TodoDTO> getTodosByMbrCd(int mbrCd);
}