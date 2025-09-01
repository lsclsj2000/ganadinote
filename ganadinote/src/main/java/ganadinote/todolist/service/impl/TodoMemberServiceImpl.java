package ganadinote.todolist.service.impl;

import ganadinote.common.domain.Member;
import ganadinote.todolist.mapper.TodoMemberMapper;
import ganadinote.todolist.service.TodoMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TodoMemberServiceImpl implements TodoMemberService {

    private final TodoMemberMapper todoMemberMapper;

    @Autowired
    public TodoMemberServiceImpl(TodoMemberMapper todoMemberMapper) {
        this.todoMemberMapper = todoMemberMapper;
    }

    @Override
    public Member getMemberByCd(int mbrCd) {
        return todoMemberMapper.getMemberByCd(mbrCd);
    }
}