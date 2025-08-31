package ganadinote.todolist.service;

import ganadinote.common.domain.Member;

public interface TodoMemberService {
    
    Member getMemberByCd(int mbrCd);
}