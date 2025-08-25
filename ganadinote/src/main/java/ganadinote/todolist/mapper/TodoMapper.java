package ganadinote.todolist.mapper;

import org.apache.ibatis.annotations.Mapper;
import ganadinote.common.domain.Todo;
import java.util.List;

@Mapper
public interface TodoMapper {

    // 기존에 있던 메소드
    List<Todo> getTodosByMbrCd(int mbrCd);
    
    // [추가!] 새로운 할 일을 DB에 삽입하는 메소드 선언
    void addTodo(Todo todo);
    
 // [추가!] 할 일 ID를 기반으로 데이터를 삭제하는 메소드
    void deleteTodo(Long todoCd);
}