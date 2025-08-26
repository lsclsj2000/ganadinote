package ganadinote.auth.register.service;

import ganadinote.auth.register.dto.RegisterDTO;

public interface RegisterService {
    void register(RegisterDTO registerDTO);
    boolean isEmailDuplicate(String mbrEmail);
    boolean isNicknameDuplicate(String mbrNknm);
}