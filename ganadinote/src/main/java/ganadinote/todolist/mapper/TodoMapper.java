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
    
 // [추가 1!] 할 일 ID로 특정 할 일 1건의 정보를 조회하는 메소드
    Todo getTodoByCd(Long todoCd);
    
    // [추가 2!] 수정된 할 일 정보를 DB에 업데이트하는 메소드
    void updateTodo(Todo todo);
}